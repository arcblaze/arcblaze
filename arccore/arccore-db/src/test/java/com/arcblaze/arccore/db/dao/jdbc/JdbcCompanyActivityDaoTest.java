package com.arcblaze.arccore.db.dao.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.SortedMap;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;

import com.arcblaze.arccore.common.model.Company;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arccore.db.dao.CompanyActivityDao;
import com.arcblaze.arccore.db.dao.CompanyDao;
import com.arcblaze.arccore.db.util.TestDatabase;

/**
 * Perform database integration testing on the user activity table.
 */
public class JdbcCompanyActivityDaoTest {
	/**
	 * @throws DatabaseException
	 *             if there is a problem with the database
	 */
	@Test
	public void dbIntegrationTests() throws DatabaseException {
		try (final TestDatabase database = new TestDatabase()) {
			database.load("hsqldb/db.sql");

			final CompanyDao companyDao = new JdbcCompanyDao(
					database.getConnectionManager());
			final CompanyActivityDao activityDao = new JdbcCompanyActivityDao(
					database.getConnectionManager());

			final Company company = new Company();
			company.setName("Test Company");
			company.setActive(true);
			companyDao.add(company);
			assertNotNull(company.getId());

			final Date begin = DateUtils.addMonths(
					DateUtils.truncate(new Date(), Calendar.MONTH), -3);
			final Date end = DateUtils.addDays(new Date(), 1);
			SortedMap<Date, Integer> map = activityDao.getActiveByMonth(begin,
					end);
			assertEquals(4, map.size());
			for (final Integer value : map.values())
				assertEquals(new Integer(0), value);

			activityDao.setActiveCompanies(begin, 5);
			activityDao.setActiveCompanies(DateUtils.addDays(begin, 3), 6);
			activityDao.setActiveCompanies(DateUtils.addDays(begin, 32), 8);
			activityDao.setActiveCompanies(DateUtils.addDays(begin, 33), 9);
			activityDao.setActiveCompanies(DateUtils.addDays(begin, 66), 7);
			activityDao.setActiveCompanies(DateUtils.addDays(begin, 67), 6);

			map = activityDao.getActiveByMonth(begin, end);
			assertEquals(4, map.size());

			final Iterator<Entry<Date, Integer>> iter = map.entrySet()
					.iterator();
			Entry<Date, Integer> entry = iter.next();
			assertEquals(begin, entry.getKey());
			assertEquals(new Integer(6), entry.getValue());

			entry = iter.next();
			assertEquals(DateUtils.addMonths(begin, 1), entry.getKey());
			assertEquals(new Integer(9), entry.getValue());

			entry = iter.next();
			assertEquals(DateUtils.addMonths(begin, 2), entry.getKey());
			assertEquals(new Integer(7), entry.getValue());

			entry = iter.next();
			assertEquals(DateUtils.addMonths(begin, 3), entry.getKey());
			assertEquals(new Integer(0), entry.getValue());
		}
	}
}
