package com.arcblaze.arctime.db.dao.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;

import com.arcblaze.arccore.common.model.Company;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arccore.db.dao.CompanyDao;
import com.arcblaze.arccore.db.dao.jdbc.JdbcCompanyDao;
import com.arcblaze.arccore.db.util.TestDatabase;
import com.arcblaze.arctime.db.dao.PayPeriodDao;
import com.arcblaze.arctime.model.PayPeriod;
import com.arcblaze.arctime.model.PayPeriodType;
import com.arcblaze.arctime.model.util.HolidayConfigurationException;

/**
 * Perform database integration testing.
 */
public class PayPeriodDaoTest {
	private final static String[] FMT = { "yyyyMMdd HHmmss", "yyyyMMdd" };

	/**
	 * @throws DatabaseException
	 *             if there is a problem with the database
	 * @throws HolidayConfigurationException
	 *             if there is a problem parsing holiday configurations
	 * @throws ParseException
	 *             if there is a date-parsing issue
	 */
	@Test
	public void dbIntegrationTests() throws DatabaseException,
			HolidayConfigurationException, ParseException {
		try (final TestDatabase database = new TestDatabase()) {
			database.load("hsqldb/db.sql");

			final CompanyDao companyDao = new JdbcCompanyDao(
					database.getConnectionManager());
			final PayPeriodDao payPeriodDao = new JdbcPayPeriodDao(
					database.getConnectionManager());

			final Company company = new Company().setName("Company").setActive(
					true);
			companyDao.add(company);

			// No pay periods exist yet, so these will all be null.
			assertNull(payPeriodDao.getLatest(company.getId()));
			assertNull(payPeriodDao.getCurrent(company.getId()));
			assertNull(payPeriodDao.getContaining(company.getId(), new Date()));

			final PayPeriod first = new PayPeriod()
					.setCompanyId(company.getId())
					.setType(PayPeriodType.WEEKLY)
					.setBegin(DateUtils.parseDate("20140101", FMT))
					.setEnd(DateUtils.parseDate("20140107", FMT));
			payPeriodDao.add(first);

			final PayPeriod get = payPeriodDao.get(company.getId(),
					first.getBegin());
			assertNotNull(get);
			assertEquals(first, get);

			PayPeriod latest = payPeriodDao.getLatest(company.getId());
			assertNotNull(latest);
			assertEquals(first, latest);

			for (final String day : Arrays.asList("20140101", "20140102",
					"20140107")) {
				final PayPeriod containing = payPeriodDao.getContaining(
						company.getId(), DateUtils.parseDate(day, FMT));
				assertEquals(first, containing);
			}

			final PayPeriod prev = first.getPrevious();
			PayPeriod before = payPeriodDao.get(company.getId(),
					prev.getBegin());
			assertNull(before); // Not automatically created.
			before = payPeriodDao.getContaining(company.getId(),
					DateUtils.parseDate("20131230", FMT));
			assertNotNull(before); // Automatically created.
			assertEquals(prev, before);

			final PayPeriod next = first.getNext();
			PayPeriod after = payPeriodDao
					.get(company.getId(), next.getBegin());
			assertNull(after); // Not automatically created.
			after = payPeriodDao.getContaining(company.getId(),
					DateUtils.parseDate("20140109", FMT));
			assertNotNull(after); // Automatically created.
			assertEquals(next, after);

			latest = payPeriodDao.getLatest(company.getId());
			assertNotNull(latest);
			assertEquals(next, latest);

			final Date early = DateUtils.parseDate("20130901", FMT);
			before = payPeriodDao.get(company.getId(), early);
			assertNull(before); // Not automatically created.
			before = payPeriodDao.getContaining(company.getId(), early);
			assertNotNull(before); // Automatically created.
			assertTrue(before.contains(early));

			final Date late = DateUtils.parseDate("20140301", FMT);
			after = payPeriodDao.get(company.getId(), late);
			assertNull(after); // Not automatically created.
			after = payPeriodDao.getContaining(company.getId(), late);
			assertNotNull(after); // Automatically created.
			assertTrue(after.contains(late));

			final PayPeriod current = payPeriodDao.getCurrent(company.getId());
			assertNotNull(current);
			assertTrue(current.contains(new Date()));
		}
	}
}
