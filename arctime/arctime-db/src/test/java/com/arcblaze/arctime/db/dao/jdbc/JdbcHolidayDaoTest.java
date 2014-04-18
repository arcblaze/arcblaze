package com.arcblaze.arctime.db.dao.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;

import com.arcblaze.arccore.common.model.Company;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arccore.db.dao.CompanyDao;
import com.arcblaze.arccore.db.dao.jdbc.JdbcCompanyDao;
import com.arcblaze.arccore.db.util.TestDatabase;
import com.arcblaze.arctime.common.model.Holiday;
import com.arcblaze.arctime.common.model.PayPeriod;
import com.arcblaze.arctime.common.model.util.HolidayConfigurationException;
import com.arcblaze.arctime.db.dao.HolidayDao;

/**
 * Perform database integration testing.
 */
public class JdbcHolidayDaoTest {
	protected Set<Holiday> getFederalHolidays(final Integer companyId)
			throws HolidayConfigurationException {
		final Map<String, String> map = new HashMap<>();
		map.put("New Years", "January 1st Observance");
		map.put("Martin Luther King Junior Day", "3rd Monday in January");
		map.put("President's Day", "3rd Monday in February");
		map.put("Memorial Day", "Last Monday in May");
		map.put("Independence Day", "July 4th Observance");
		map.put("Labor Day", "1st Monday in September");
		map.put("Columbus Day", "2nd Monday in October");
		map.put("Veterans Day", "November 11th Observance");
		map.put("Thanksgiving Day", "4th Thursday in November");
		map.put("Christmas Day", "December 25th Observance");

		final Set<Holiday> holidays = new TreeSet<>();
		for (final Map.Entry<String, String> entry : map.entrySet())
			holidays.add(new Holiday().setCompanyId(companyId)
					.setDescription(entry.getKey()).setConfig(entry.getValue()));
		return holidays;
	}

	/**
	 * @throws DatabaseException
	 *             if there is a problem with the database
	 * @throws HolidayConfigurationException
	 *             if there is a problem parsing holiday configurations
	 */
	@Test
	public void dbIntegrationTests() throws DatabaseException,
			HolidayConfigurationException {
		try (final TestDatabase database = new TestDatabase()) {
			database.load("hsqldb/arctime-db.sql");

			final CompanyDao companyDao = new JdbcCompanyDao(
					database.getConnectionManager());
			final HolidayDao holidayDao = new JdbcHolidayDao(
					database.getConnectionManager());

			final Company company = new Company().setName("Company").setActive(
					true);
			companyDao.add(company);

			Set<Holiday> holidays = holidayDao.getAll(company.getId());
			assertNotNull(holidays);
			assertEquals(0, holidays.size());

			holidays = getFederalHolidays(company.getId());
			holidayDao.add(holidays);
			for (final Holiday holiday : holidays) {
				assertNotNull(holiday.getId());
				assertEquals(company.getId(), holiday.getCompanyId());
			}

			Set<Holiday> getAllHolidays = holidayDao.getAll(company.getId());
			assertNotNull(getAllHolidays);
			assertEquals(holidays.size(), getAllHolidays.size());

			final Holiday first = holidays.iterator().next();
			Holiday getHoliday = holidayDao.get(company.getId(), first.getId());
			assertEquals(first, getHoliday);

			first.setDescription("New Description");
			holidayDao.update(first);
			getHoliday = holidayDao.get(company.getId(), first.getId());
			assertEquals(first, getHoliday);

			holidayDao.delete(company.getId(), first.getId());
			getHoliday = holidayDao.get(company.getId(), first.getId());
			assertNull(getHoliday);

			getAllHolidays = holidayDao.getAll(company.getId());
			assertNotNull(getAllHolidays);
			assertEquals(holidays.size() - 1, getAllHolidays.size());

			final Holiday second = getAllHolidays.iterator().next();
			final PayPeriod payPeriod = new PayPeriod().setBegin(
					DateUtils.addDays(second.getDay(), -3)).setEnd(
					DateUtils.addDays(second.getDay(), 3));
			final Set<Holiday> forPayPeriod = holidayDao.getForPayPeriod(
					company.getId(), payPeriod);
			assertNotNull(forPayPeriod);
			assertEquals(1, forPayPeriod.size());
			assertEquals(second, forPayPeriod.iterator().next());
		}
	}
}
