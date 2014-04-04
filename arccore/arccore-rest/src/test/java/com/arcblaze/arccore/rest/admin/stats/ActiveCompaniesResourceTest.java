package com.arcblaze.arccore.rest.admin.stats;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;

import javax.ws.rs.BadRequestException;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;

import com.arcblaze.arccore.common.model.Company;
import com.arcblaze.arccore.db.DaoFactory;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arccore.db.dao.CompanyActivityDao;
import com.arcblaze.arccore.db.util.TestDatabase;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

/**
 * Perform testing of the system statistics.
 */
public class ActiveCompaniesResourceTest {
	/**
	 * Test how the resource responds when the provided date parameters are
	 * invalid.
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

			final ActiveCompaniesResource resource = new ActiveCompaniesResource();
			resource.activeCompanies(daoFactory, timer, "abcd", "abcd");
		}
	}

	/**
	 * Test how the resource responds when the provided date parameters are
	 * invalid.
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

			final ActiveCompaniesResource resource = new ActiveCompaniesResource();
			resource.activeCompanies(daoFactory, timer, "2014-01-01", "abcd");
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
		correct.append("index\tcount\n");
		correct.append("1\t0\n");
		correct.append("2\t0\n");
		correct.append("3\t0\n");
		correct.append("4\t0\n");
		correct.append("5\t0\n");
		correct.append("6\t0\n");
		correct.append("7\t0\n");
		correct.append("8\t0\n");
		correct.append("9\t0\n");
		correct.append("10\t0\n");

		try (final TestDatabase testDatabase = new TestDatabase()) {
			testDatabase.load("hsqldb/db.sql");
			final DaoFactory daoFactory = testDatabase.getDaoFactory();
			final MetricRegistry metricRegistry = new MetricRegistry();
			final Timer timer = metricRegistry.timer("test");

			final ActiveCompaniesResource resource = new ActiveCompaniesResource();
			final String data = resource.activeCompanies(daoFactory, timer,
					null, "2014-01-01");

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
		correct.append("index\tcount\n");
		correct.append("1\t0\n");
		correct.append("2\t0\n");
		correct.append("3\t0\n");
		correct.append("4\t0\n");

		try (final TestDatabase testDatabase = new TestDatabase()) {
			testDatabase.load("hsqldb/db.sql");
			final DaoFactory daoFactory = testDatabase.getDaoFactory();
			final MetricRegistry metricRegistry = new MetricRegistry();
			final Timer timer = metricRegistry.timer("test");

			final ActiveCompaniesResource resource = new ActiveCompaniesResource();
			final String data = resource.activeCompanies(daoFactory, timer,
					"2014-01-01", null);

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
		correct.append("index\tcount\n");
		correct.append("1\t0\n");
		correct.append("2\t0\n");
		correct.append("3\t0\n");
		correct.append("4\t0\n");
		correct.append("5\t0\n");
		correct.append("6\t0\n");
		correct.append("7\t0\n");
		correct.append("8\t0\n");
		correct.append("9\t0\n");
		correct.append("10\t0\n");
		correct.append("11\t0\n");
		correct.append("12\t0\n");
		correct.append("13\t0\n");

		try (final TestDatabase testDatabase = new TestDatabase()) {
			testDatabase.load("hsqldb/db.sql");
			final DaoFactory daoFactory = testDatabase.getDaoFactory();
			final MetricRegistry metricRegistry = new MetricRegistry();
			final Timer timer = metricRegistry.timer("test");

			final ActiveCompaniesResource resource = new ActiveCompaniesResource();
			final String data = resource.activeCompanies(daoFactory, timer,
					null, null);

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
	public void testWithDataAvailable() throws DatabaseException {
		try (final TestDatabase testDatabase = new TestDatabase()) {
			testDatabase.load("hsqldb/db.sql");
			final DaoFactory daoFactory = testDatabase.getDaoFactory();
			final MetricRegistry metricRegistry = new MetricRegistry();
			final Timer timer = metricRegistry.timer("test");

			final Company company = new Company().setName("company").setActive(
					true);
			daoFactory.getCompanyDao().add(company);

			final CompanyActivityDao dao = daoFactory.getCompanyActivityDao();
			final Date now = new Date();
			dao.setActiveCompanies(DateUtils.addMonths(now, -5), 30);
			dao.setActiveCompanies(DateUtils.addMonths(now, -4), 33);
			dao.setActiveCompanies(DateUtils.addMonths(now, -3), 29);
			dao.setActiveCompanies(DateUtils.addMonths(now, -2), 34);
			dao.setActiveCompanies(DateUtils.addMonths(now, -1), 36);
			dao.setActiveCompanies(DateUtils.addMonths(now, -0), 39);

			final StringBuilder correct = new StringBuilder();
			correct.append("index\tcount\n");
			correct.append("1\t0\n");
			correct.append("2\t0\n");
			correct.append("3\t0\n");
			correct.append("4\t0\n");
			correct.append("5\t0\n");
			correct.append("6\t0\n");
			correct.append("7\t0\n");
			correct.append("8\t30\n");
			correct.append("9\t33\n");
			correct.append("10\t29\n");
			correct.append("11\t34\n");
			correct.append("12\t36\n");
			correct.append("13\t39\n");

			final ActiveCompaniesResource resource = new ActiveCompaniesResource();
			final String data = resource.activeCompanies(daoFactory, timer,
					null, null);

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
	public void testWithDataAvailableAndDates() throws DatabaseException {
		try (final TestDatabase testDatabase = new TestDatabase()) {
			testDatabase.load("hsqldb/db.sql");
			final DaoFactory daoFactory = testDatabase.getDaoFactory();
			final MetricRegistry metricRegistry = new MetricRegistry();
			final Timer timer = metricRegistry.timer("test");

			final Company company = new Company().setName("company").setActive(
					true);
			daoFactory.getCompanyDao().add(company);

			final CompanyActivityDao dao = daoFactory.getCompanyActivityDao();
			final Date now = new Date();
			dao.setActiveCompanies(DateUtils.addMonths(now, -5), 30);
			dao.setActiveCompanies(DateUtils.addMonths(now, -4), 33);
			dao.setActiveCompanies(DateUtils.addMonths(now, -3), 29);
			dao.setActiveCompanies(DateUtils.addMonths(now, -2), 34);
			dao.setActiveCompanies(DateUtils.addMonths(now, -1), 36);

			final StringBuilder correct = new StringBuilder();
			correct.append("index\tcount\n");
			correct.append("1\t0\n");
			correct.append("2\t0\n");
			correct.append("3\t0\n");
			correct.append("4\t0\n");
			correct.append("5\t0\n");
			correct.append("6\t0\n");
			correct.append("7\t0\n");
			correct.append("8\t30\n");
			correct.append("9\t33\n");
			correct.append("10\t29\n");
			correct.append("11\t34\n");
			correct.append("12\t36\n");
			correct.append("13\t0\n");

			final String begin = DateFormatUtils.format(
					DateUtils.addMonths(now, -12), "yyyy-MM-dd");
			final String end = DateFormatUtils.format(now, "yyyy-MM-dd");

			final ActiveCompaniesResource resource = new ActiveCompaniesResource();
			final String data = resource.activeCompanies(daoFactory, timer,
					begin, end);

			assertNotNull(data);
			assertEquals(correct.toString(), data);
		}
	}
}
