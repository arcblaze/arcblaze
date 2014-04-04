package com.arcblaze.arccore.rest.admin.stats;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.Iterator;

import org.junit.Test;

import com.arcblaze.arccore.common.model.Company;
import com.arcblaze.arccore.common.model.Transaction;
import com.arcblaze.arccore.common.model.TransactionType;
import com.arcblaze.arccore.common.model.User;
import com.arcblaze.arccore.db.DaoFactory;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arccore.db.util.TestDatabase;
import com.arcblaze.arccore.rest.admin.stats.SystemStatsResource.Stats;
import com.arcblaze.arccore.rest.admin.stats.SystemStatsResource.SystemStat;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

/**
 * Perform testing of the system statistics.
 */
public class SystemStatsResourceTest {
	/**
	 * Test how the resource responds with no transaction data available.
	 * 
	 * @throws DatabaseException
	 *             if there is a database issue
	 */
	@Test
	public void testNoData() throws DatabaseException {
		try (final TestDatabase testDatabase = new TestDatabase()) {
			testDatabase.load("hsqldb/db.sql");
			final DaoFactory daoFactory = testDatabase.getDaoFactory();
			final MetricRegistry metricRegistry = new MetricRegistry();
			final Timer timer = metricRegistry.timer("test");

			final SystemStatsResource resource = new SystemStatsResource();
			final Stats stats = resource.getStats(daoFactory, timer);

			assertNotNull(stats);
			assertEquals(4, stats.statList.size());

			final Iterator<SystemStat> iter = stats.statList.iterator();

			SystemStat stat = iter.next();
			assertEquals("Revenue YTD", stat.name);
			assertEquals("$0.00", stat.value);

			stat = iter.next();
			assertEquals("Revenue Year", stat.name);
			assertEquals("$0.00", stat.value);

			stat = iter.next();
			assertEquals("Active Users", stat.name);
			assertEquals("0", stat.value);

			stat = iter.next();
			assertEquals("Active Companies", stat.name);
			assertEquals("0", stat.value);
		}
	}

	/**
	 * Test how the resource responds with a few transactions.
	 * 
	 * @throws DatabaseException
	 *             if there is a database issue
	 */
	@Test
	public void testStatsAvailable() throws DatabaseException {
		try (final TestDatabase testDatabase = new TestDatabase()) {
			testDatabase.load("hsqldb/db.sql");
			final DaoFactory daoFactory = testDatabase.getDaoFactory();
			final MetricRegistry metricRegistry = new MetricRegistry();
			final Timer timer = metricRegistry.timer("test");

			final Company company = new Company().setName("company").setActive(
					true);
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
			daoFactory.getUserDao().add(user);

			final Transaction tx1 = new Transaction();
			tx1.setCompanyId(company.getId());
			tx1.setUserId(user.getId());
			tx1.setTimestamp(new Date());
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

			final SystemStatsResource resource = new SystemStatsResource();
			final Stats stats = resource.getStats(daoFactory, timer);

			assertNotNull(stats);
			assertEquals(4, stats.statList.size());

			final Iterator<SystemStat> iter = stats.statList.iterator();

			SystemStat stat = iter.next();
			assertEquals("Revenue YTD", stat.name);
			assertEquals("$40.00", stat.value);

			stat = iter.next();
			assertEquals("Revenue Year", stat.name);
			assertEquals("$40.00", stat.value);

			stat = iter.next();
			assertEquals("Active Users", stat.name);
			assertEquals("1", stat.value);

			stat = iter.next();
			assertEquals("Active Companies", stat.name);
			assertEquals("1", stat.value);
		}
	}
}
