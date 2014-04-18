package com.arcblaze.arctime.rest.finance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.Set;

import javax.ws.rs.core.SecurityContext;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;
import org.mockito.Mockito;

import com.arcblaze.arccore.common.config.Config;
import com.arcblaze.arccore.common.model.Company;
import com.arcblaze.arccore.common.model.Transaction;
import com.arcblaze.arccore.common.model.TransactionType;
import com.arcblaze.arccore.common.model.User;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arctime.db.ArcTimeDaoFactory;
import com.arcblaze.arctime.db.util.TestDatabase;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

/**
 * Perform testing of the transaction resource capabilities.
 */
public class TransactionResourceTest {
	/**
	 * Test how the resource responds to retrieving all transactions when none
	 * are available.
	 * 
	 * @throws DatabaseException
	 *             if there is a database problem
	 */
	@Test
	public void testAllNoneAvailable() throws DatabaseException {
		final Config config = new Config();
		try (final TestDatabase testDatabase = new TestDatabase()) {
			testDatabase.load("hsqldb/arctime-db.sql");
			final User user = new User().setId(1).setCompanyId(1)
					.setLogin("user");
			final SecurityContext securityContext = Mockito
					.mock(SecurityContext.class);
			Mockito.when(securityContext.getUserPrincipal()).thenReturn(user);
			final ArcTimeDaoFactory daoFactory = testDatabase.getDaoFactory();
			final MetricRegistry metricRegistry = new MetricRegistry();
			final Timer timer = metricRegistry.timer("test");

			final TransactionResource resource = new TransactionResource();
			final Set<Transaction> transactions = resource.get(securityContext,
					config, daoFactory, timer);

			assertNotNull(transactions);
			assertTrue(transactions.isEmpty());
		}
	}

	/**
	 * Test how the resource responds to retrieving all transactions.
	 * 
	 * @throws DatabaseException
	 *             if there is a database problem
	 */
	@Test
	public void testAll() throws DatabaseException {
		final Config config = new Config();
		try (final TestDatabase testDatabase = new TestDatabase()) {
			testDatabase.load("hsqldb/arctime-db.sql");
			final ArcTimeDaoFactory daoFactory = testDatabase.getDaoFactory();
			final MetricRegistry metricRegistry = new MetricRegistry();
			final Timer timer = metricRegistry.timer("test");

			final Company company = new Company().setName("company");
			daoFactory.getCompanyDao().add(company);

			final User user = new User();
			user.setCompanyId(company.getId());
			user.setLogin("user");
			user.setHashedPass("hashed");
			user.setSalt("salt");
			user.setEmail("email");
			user.setFirstName("first");
			user.setLastName("last");
			daoFactory.getUserDao().add(user);

			final Transaction tx1 = new Transaction();
			tx1.setCompanyId(company.getId());
			tx1.setUserId(user.getId());
			tx1.setTimestamp(DateUtils.addDays(new Date(), -5));
			tx1.setTransactionType(TransactionType.PAYMENT);
			tx1.setDescription("description");
			tx1.setAmount("50.00");
			tx1.setNotes("notes");
			final Transaction tx2 = new Transaction();
			tx2.setCompanyId(company.getId());
			tx2.setUserId(user.getId());
			tx1.setTimestamp(DateUtils.addDays(new Date(), -50));
			tx2.setTransactionType(TransactionType.REFUND);
			tx2.setDescription("description");
			tx2.setAmount("-5.00");
			daoFactory.getTransactionDao().add(tx1, tx2);

			final SecurityContext securityContext = Mockito
					.mock(SecurityContext.class);
			Mockito.when(securityContext.getUserPrincipal()).thenReturn(user);

			final TransactionResource resource = new TransactionResource();
			final Set<Transaction> transactions = resource.get(securityContext,
					config, daoFactory, timer);

			assertNotNull(transactions);
			assertEquals(2, transactions.size());
			assertTrue(transactions.contains(tx1));
			assertTrue(transactions.contains(tx2));
		}
	}
}
