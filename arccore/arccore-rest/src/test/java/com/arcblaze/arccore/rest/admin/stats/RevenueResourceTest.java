package com.arcblaze.arccore.rest.admin.stats;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;

import javax.ws.rs.BadRequestException;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;

import com.arcblaze.arccore.common.model.Company;
import com.arcblaze.arccore.common.model.Transaction;
import com.arcblaze.arccore.common.model.TransactionType;
import com.arcblaze.arccore.common.model.User;
import com.arcblaze.arccore.db.DaoFactory;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arccore.db.dao.UserDao;
import com.arcblaze.arccore.db.util.TestDatabase;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

/**
 * Perform testing of the system statistics.
 */
public class RevenueResourceTest {
    /**
     * Test how the resource responds when the provided date parameters are invalid.
     * 
     * @throws DatabaseException
     *             if there is a database issue
     */
    @Test(expected = BadRequestException.class)
    public void testInvalidDates() throws DatabaseException {
        try (final TestDatabase testDatabase = new TestDatabase()) {
            final DaoFactory daoFactory = testDatabase.getDaoFactory();
            final MetricRegistry metricRegistry = new MetricRegistry();
            final Timer timer = metricRegistry.timer("test");

            final RevenueResource resource = new RevenueResource();
            resource.getRevenue(daoFactory, timer, "abcd", "abcd");
        }
    }

    /**
     * Test how the resource responds when the provided date parameters are invalid.
     * 
     * @throws DatabaseException
     *             if there is a database issue
     */
    @Test(expected = BadRequestException.class)
    public void testInvalidEndDate() throws DatabaseException {
        try (final TestDatabase testDatabase = new TestDatabase()) {
            final DaoFactory daoFactory = testDatabase.getDaoFactory();
            final MetricRegistry metricRegistry = new MetricRegistry();
            final Timer timer = metricRegistry.timer("test");

            final RevenueResource resource = new RevenueResource();
            resource.getRevenue(daoFactory, timer, "2014-01-01", "abcd");
        }
    }

    /**
     * Test how the resource responds when the provided start date is null.
     * 
     * @throws DatabaseException
     *             if there is a database issue
     */
    @Test
    public void testNullStartDate() throws DatabaseException {
        final StringBuilder correct = new StringBuilder();
        correct.append("index\tamount\n");
        correct.append("1\t0.00\n");
        correct.append("2\t0.00\n");
        correct.append("3\t0.00\n");
        correct.append("4\t0.00\n");
        correct.append("5\t0.00\n");
        correct.append("6\t0.00\n");
        correct.append("7\t0.00\n");
        correct.append("8\t0.00\n");
        correct.append("9\t0.00\n");
        correct.append("10\t0.00\n");

        try (final TestDatabase testDatabase = new TestDatabase()) {
            testDatabase.load("hsqldb/db.sql");
            final DaoFactory daoFactory = testDatabase.getDaoFactory();
            final MetricRegistry metricRegistry = new MetricRegistry();
            final Timer timer = metricRegistry.timer("test");

            final RevenueResource resource = new RevenueResource();
            final String data = resource.getRevenue(daoFactory, timer, null, "2014-01-01");

            assertNotNull(data);
            assertEquals(correct.toString(), data);
        }
    }

    /**
     * Test how the resource responds when the provided end date is null.
     * 
     * @throws DatabaseException
     *             if there is a database issue
     */
    @Test
    public void testNullEndDate() throws DatabaseException {
        final StringBuilder correct = new StringBuilder();
        correct.append("index\tamount\n");
        correct.append("1\t0.00\n");
        correct.append("2\t0.00\n");
        correct.append("3\t0.00\n");
        correct.append("4\t0.00\n");

        try (final TestDatabase testDatabase = new TestDatabase()) {
            testDatabase.load("hsqldb/db.sql");
            final DaoFactory daoFactory = testDatabase.getDaoFactory();
            final MetricRegistry metricRegistry = new MetricRegistry();
            final Timer timer = metricRegistry.timer("test");

            final RevenueResource resource = new RevenueResource();
            final String data = resource.getRevenue(daoFactory, timer, "2014-01-01", null);

            assertNotNull(data);
            assertEquals(correct.toString(), data);
        }
    }

    /**
     * Test how the resource responds with no transaction data available.
     * 
     * @throws DatabaseException
     *             if there is a database issue
     */
    @Test
    public void testNoData() throws DatabaseException {
        final StringBuilder correct = new StringBuilder();
        correct.append("index\tamount\n");
        correct.append("1\t0.00\n");
        correct.append("2\t0.00\n");
        correct.append("3\t0.00\n");
        correct.append("4\t0.00\n");
        correct.append("5\t0.00\n");
        correct.append("6\t0.00\n");
        correct.append("7\t0.00\n");
        correct.append("8\t0.00\n");
        correct.append("9\t0.00\n");
        correct.append("10\t0.00\n");
        correct.append("11\t0.00\n");
        correct.append("12\t0.00\n");
        correct.append("13\t0.00\n");

        try (final TestDatabase testDatabase = new TestDatabase()) {
            testDatabase.load("hsqldb/db.sql");
            final DaoFactory daoFactory = testDatabase.getDaoFactory();
            final MetricRegistry metricRegistry = new MetricRegistry();
            final Timer timer = metricRegistry.timer("test");

            final RevenueResource resource = new RevenueResource();
            final String data = resource.getRevenue(daoFactory, timer, null, null);

            assertNotNull(data);
            assertEquals(correct.toString(), data);
        }
    }

    /**
     * Test how the resource responds with some transactions available.
     * 
     * @throws DatabaseException
     *             if there is a database issue
     */
    @Test
    public void testTransactionsAvailable() throws DatabaseException {
        try (final TestDatabase testDatabase = new TestDatabase()) {
            testDatabase.load("hsqldb/db.sql");
            final DaoFactory daoFactory = testDatabase.getDaoFactory();
            final MetricRegistry metricRegistry = new MetricRegistry();
            final Timer timer = metricRegistry.timer("test");

            final Company company = new Company().setName("company").setActive(true);
            daoFactory.getCompanyDao().add(company);

            final User user = new User();
            user.setCompanyId(company.getId());
            user.setLogin("user");
            user.setHashedPass("hashed");
            user.setSalt("salt");
            user.setEmail("email@whatever.com");
            user.setFirstName("first");
            user.setLastName("last");
            user.setActive(true);

            final UserDao userDao = daoFactory.getUserDao();
            userDao.add(user);

            final Transaction tx1 = new Transaction();
            tx1.setCompanyId(company.getId());
            tx1.setUserId(user.getId());
            tx1.setTimestamp(DateUtils.addMonths(new Date(), -1));
            tx1.setTransactionType(TransactionType.PAYMENT);
            tx1.setDescription("payment");
            tx1.setAmount("50.00");
            tx1.setNotes("payment notes");

            final Transaction tx2 = new Transaction();
            tx2.setCompanyId(company.getId());
            tx2.setUserId(user.getId());
            tx2.setTimestamp(new Date());
            tx2.setTransactionType(TransactionType.REFUND);
            tx2.setDescription("refund");
            tx2.setAmount("-10.00");
            tx2.setNotes("refund notes");

            daoFactory.getTransactionDao().add(tx1, tx2);

            final StringBuilder correct = new StringBuilder();
            correct.append("index\tamount\n");
            correct.append("1\t0.00\n");
            correct.append("2\t0.00\n");
            correct.append("3\t0.00\n");
            correct.append("4\t0.00\n");
            correct.append("5\t0.00\n");
            correct.append("6\t0.00\n");
            correct.append("7\t0.00\n");
            correct.append("8\t0.00\n");
            correct.append("9\t0.00\n");
            correct.append("10\t0.00\n");
            correct.append("11\t0.00\n");
            correct.append("12\t50.00\n");
            correct.append("13\t-10.00\n");

            final RevenueResource resource = new RevenueResource();
            final String data = resource.getRevenue(daoFactory, timer, null, null);

            assertNotNull(data);
            assertEquals(correct.toString(), data);
        }
    }

    /**
     * Test how the resource responds with some transactions available.
     * 
     * @throws DatabaseException
     *             if there is a database issue
     */
    @Test
    public void testTransactionsAvailableWithDates() throws DatabaseException {
        try (final TestDatabase testDatabase = new TestDatabase()) {
            testDatabase.load("hsqldb/db.sql");
            final DaoFactory daoFactory = testDatabase.getDaoFactory();
            final MetricRegistry metricRegistry = new MetricRegistry();
            final Timer timer = metricRegistry.timer("test");

            final Company company = new Company().setName("company").setActive(true);
            daoFactory.getCompanyDao().add(company);

            final User user = new User();
            user.setCompanyId(company.getId());
            user.setLogin("user");
            user.setHashedPass("hashed");
            user.setSalt("salt");
            user.setEmail("email@whatever.com");
            user.setFirstName("first");
            user.setLastName("last");
            user.setActive(true);

            final UserDao userDao = daoFactory.getUserDao();
            userDao.add(user);

            final Transaction tx1 = new Transaction();
            tx1.setCompanyId(company.getId());
            tx1.setUserId(user.getId());
            tx1.setTimestamp(DateUtils.addMonths(new Date(), -1));
            tx1.setTransactionType(TransactionType.PAYMENT);
            tx1.setDescription("payment");
            tx1.setAmount("50.00");
            tx1.setNotes("payment notes");

            final Transaction tx2 = new Transaction();
            tx2.setCompanyId(company.getId());
            tx2.setUserId(user.getId());
            tx2.setTimestamp(new Date());
            tx2.setTransactionType(TransactionType.REFUND);
            tx2.setDescription("refund");
            tx2.setAmount("-10.00");
            tx2.setNotes("refund notes");

            daoFactory.getTransactionDao().add(tx1, tx2);

            final StringBuilder correct = new StringBuilder();
            correct.append("index\tamount\n");
            correct.append("1\t0.00\n");
            correct.append("2\t0.00\n");
            correct.append("3\t50.00\n");
            correct.append("4\t-10.00\n");

            final String begin = DateFormatUtils.format(DateUtils.addMonths(new Date(), -3), "yyyy-MM-dd");
            final String end = DateFormatUtils.format(DateUtils.addDays(new Date(), 2), "yyyy-MM-dd");

            final RevenueResource resource = new RevenueResource();
            final String data = resource.getRevenue(daoFactory, timer, begin, end);

            assertNotNull(data);
            assertEquals(correct.toString(), data);
        }
    }
}
