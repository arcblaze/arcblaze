package com.arcblaze.arccore.db.dao.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;

import com.arcblaze.arccore.common.model.Company;
import com.arcblaze.arccore.common.model.Transaction;
import com.arcblaze.arccore.common.model.TransactionType;
import com.arcblaze.arccore.common.model.User;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arccore.db.dao.CompanyDao;
import com.arcblaze.arccore.db.dao.TransactionDao;
import com.arcblaze.arccore.db.dao.UserDao;
import com.arcblaze.arccore.db.util.TestDatabase;

/**
 * Perform database integration testing.
 */
public class JdbcTransactionDaoTest {
    /**
     * @throws DatabaseException
     *             if there is a problem with the database
     */
    @Test
    public void dbIntegrationTests() throws DatabaseException {
        try (final TestDatabase database = new TestDatabase()) {
            database.load("hsqldb/db.sql");

            final CompanyDao companyDao = new JdbcCompanyDao(database.getConnectionManager());
            final UserDao userDao = new JdbcUserDao(database.getConnectionManager());
            final TransactionDao transactionDao = new JdbcTransactionDao(database.getConnectionManager());

            final Company company = new Company().setName("company");
            companyDao.add(company);

            final User user = new User();
            user.setCompanyId(company.getId());
            user.setLogin("user");
            user.setHashedPass("hashed");
            user.setSalt("salt");
            user.setEmail("email");
            user.setFirstName("first");
            user.setLastName("last");
            userDao.add(user);

            Set<Transaction> transactions = transactionDao.getForCompany(company.getId(), null, null);
            assertNotNull(transactions);
            assertEquals(0, transactions.size());

            final Transaction t1 = new Transaction();
            t1.setCompanyId(company.getId());
            t1.setUserId(user.getId());
            t1.setTimestamp(DateUtils.addDays(new Date(), -5));
            t1.setTransactionType(TransactionType.PAYMENT);
            t1.setDescription("Received payment");
            t1.setAmount("30.00");

            final Transaction t2 = new Transaction();
            t2.setCompanyId(company.getId());
            t2.setUserId(user.getId());
            t2.setTimestamp(DateUtils.addDays(new Date(), -10));
            t2.setTransactionType(TransactionType.REFUND);
            t2.setDescription("Refunded some money");
            t2.setAmount("-10.00");
            t2.setNotes("Some notes");

            transactionDao.add(t1, t2);
            assertNotNull(t1.getId());
            assertNotNull(t2.getId());

            transactions = transactionDao.getForCompany(company.getId(), null, null);
            assertNotNull(transactions);
            assertEquals(2, transactions.size());
            assertTrue(transactions.contains(t1));
            assertTrue(transactions.contains(t2));

            transactions = transactionDao.getForCompany(company.getId(), 1, 0);
            assertNotNull(transactions);
            assertEquals(1, transactions.size());
            assertTrue(transactions.contains(t1));

            transactions = transactionDao.getForCompany(company.getId(), null, 1);
            assertNotNull(transactions);
            assertEquals(1, transactions.size());
            assertTrue(transactions.contains(t2));

            transactions = transactionDao.getForCompany(company.getId(), 1, null);
            assertNotNull(transactions);
            assertEquals(1, transactions.size());
            assertTrue(transactions.contains(t1));

            transactions = transactionDao.searchForCompany(company.getId(), null, null, null);
            assertNotNull(transactions);
            assertEquals(2, transactions.size());
            assertTrue(transactions.contains(t1));
            assertTrue(transactions.contains(t2));

            transactions = transactionDao.searchForCompany(company.getId(), "payment", null, null);
            assertNotNull(transactions);
            assertEquals(1, transactions.size());
            assertTrue(transactions.contains(t1));

            transactions = transactionDao.searchForCompany(company.getId(), "notes", null, null);
            assertNotNull(transactions);
            assertEquals(1, transactions.size());
            assertTrue(transactions.contains(t2));

            transactions = transactionDao.search("payment", null, null);
            assertNotNull(transactions);
            assertEquals(1, transactions.size());
            assertTrue(transactions.contains(t1));

            transactions = transactionDao.search("notes", null, null);
            assertNotNull(transactions);
            assertEquals(1, transactions.size());
            assertTrue(transactions.contains(t2));

            transactions = transactionDao.search("non-existent", null, null);
            assertNotNull(transactions);
            assertEquals(0, transactions.size());

            assertEquals(2, transactionDao.count());
            assertEquals(1, transactionDao.count("payment"));
            assertEquals(1, transactionDao.count("notes"));
            assertEquals(0, transactionDao.count("non-existent"));

            final Date threeMonthsAgo = DateUtils.addMonths(DateUtils.truncate(new Date(), Calendar.MONTH), -2);
            final Date tomorrow = DateUtils.addDays(new Date(), 1);
            BigDecimal amount = transactionDao.amountBetween(threeMonthsAgo, tomorrow);
            assertEquals("20.00", amount.toPlainString());
            amount = transactionDao.amountBetween(company.getId(), threeMonthsAgo, tomorrow);
            assertEquals("20.00", amount.toPlainString());
            final SortedMap<Date, BigDecimal> map = transactionDao.getSumByMonth(threeMonthsAgo, tomorrow);
            assertEquals(3, map.size());
            final Iterator<Entry<Date, BigDecimal>> iter = map.entrySet().iterator();
            assertEquals("0.00", iter.next().getValue().toPlainString());
            assertEquals("0.00", iter.next().getValue().toPlainString());
            assertEquals("20.00", iter.next().getValue().toPlainString());

            Transaction getTransaction = transactionDao.get(t1.getId());
            assertEquals(t1, getTransaction);

            t1.setDescription("New Description");
            t1.setAmount("40");
            t1.setNotes("New Notes");
            transactionDao.update(t1);
            getTransaction = transactionDao.get(t1.getId());
            assertEquals(t1, getTransaction);

            transactionDao.delete(t1.getId(), t2.getId());
            getTransaction = transactionDao.get(t1.getId());
            assertNull(getTransaction);

            transactions = transactionDao.getForCompany(company.getId(), null, null);
            assertNotNull(transactions);
            assertEquals(0, transactions.size());
        }
    }
}
