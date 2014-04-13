package com.arcblaze.arctime.rest.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.SecurityContext;

import org.apache.commons.lang.time.DateFormatUtils;
import org.junit.Test;
import org.mockito.Mockito;

import com.arcblaze.arccore.common.config.Config;
import com.arcblaze.arccore.common.model.Company;
import com.arcblaze.arccore.common.model.IdSet;
import com.arcblaze.arccore.common.model.User;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arctime.common.model.Holiday;
import com.arcblaze.arctime.common.model.util.HolidayConfigurationException;
import com.arcblaze.arctime.db.ArcTimeDaoFactory;
import com.arcblaze.arctime.db.dao.HolidayDao;
import com.arcblaze.arctime.db.util.TestDatabase;
import com.arcblaze.arctime.rest.manager.HolidayResource.AddResponse;
import com.arcblaze.arctime.rest.manager.HolidayResource.UpdateResponse;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

/**
 * Perform testing of the holiday resource capabilities.
 */
public class HolidayResourceTest {
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
	 * Test how the resource responds to being given an invalid id.
	 * 
	 * @throws DatabaseException
	 *             if there is a database problem
	 * @throws HolidayConfigurationException
	 *             if there is a bad holiday config
	 */
	@Test(expected = BadRequestException.class)
	public void testOneInvalidId() throws DatabaseException,
			HolidayConfigurationException {
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

			final HolidayResource resource = new HolidayResource();
			resource.get(securityContext, config, daoFactory, timer, null);
		}
	}

	/**
	 * Test how the resource responds to returning a valid holiday.
	 * 
	 * @throws DatabaseException
	 *             if there is a database problem
	 * @throws HolidayConfigurationException
	 *             if there is a bad holiday config
	 */
	@Test
	public void testOne() throws DatabaseException,
			HolidayConfigurationException {
		final Config config = new Config();
		try (final TestDatabase testDatabase = new TestDatabase()) {
			testDatabase.load("hsqldb/arctime-db.sql");
			final ArcTimeDaoFactory daoFactory = testDatabase.getDaoFactory();
			final MetricRegistry metricRegistry = new MetricRegistry();
			final Timer timer = metricRegistry.timer("test");

			final Company company = new Company().setName("company").setActive(
					true);
			daoFactory.getCompanyDao().add(company);

			final Holiday holiday = new Holiday().setCompanyId(company.getId())
					.setDescription("holiday")
					.setConfig(DateFormatUtils.format(new Date(), "MMM d"));
			daoFactory.getHolidayDao().add(holiday);

			final User user = new User().setId(1).setCompanyId(1)
					.setLogin("user");
			final SecurityContext securityContext = Mockito
					.mock(SecurityContext.class);
			Mockito.when(securityContext.getUserPrincipal()).thenReturn(user);

			final HolidayResource resource = new HolidayResource();
			final Holiday response = resource.get(securityContext, config,
					daoFactory, timer, holiday.getId());

			assertNotNull(response);
			assertEquals(holiday, response);
		}
	}

	/**
	 * Test how the resource responds to retrieving all holidays when none are
	 * available.
	 * 
	 * @throws DatabaseException
	 *             if there is a database problem
	 * @throws HolidayConfigurationException
	 *             if there is a bad holiday config
	 */
	@Test
	public void testAllNoneAvailable() throws DatabaseException,
			HolidayConfigurationException {
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

			final HolidayResource resource = new HolidayResource();
			final Set<Holiday> holidays = resource.all(securityContext, config,
					daoFactory, timer);

			assertNotNull(holidays);
			assertTrue(holidays.isEmpty());
		}
	}

	/**
	 * Test how the resource responds to retrieving all holidays.
	 * 
	 * @throws DatabaseException
	 *             if there is a database problem
	 * @throws HolidayConfigurationException
	 *             if there is a bad holiday config
	 */
	@Test
	public void testAll() throws DatabaseException,
			HolidayConfigurationException {
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

			final Company company = new Company().setName("company");
			daoFactory.getCompanyDao().add(company);

			daoFactory.getHolidayDao().add(getFederalHolidays(company.getId()));

			final HolidayResource resource = new HolidayResource();
			final Set<Holiday> holidays = resource.all(securityContext, config,
					daoFactory, timer);

			assertNotNull(holidays);
			assertEquals(10, holidays.size());
		}
	}

	/**
	 * Test how the resource responds to adding a new holiday.
	 * 
	 * @throws DatabaseException
	 *             if there is a database problem
	 * @throws HolidayConfigurationException
	 *             if there is a holiday definition problem
	 */
	@Test
	public void testValidate() throws DatabaseException,
			HolidayConfigurationException {
		final MetricRegistry metricRegistry = new MetricRegistry();
		final Timer timer = metricRegistry.timer("test");

		final HolidayResource resource = new HolidayResource();
		assertFalse(resource.validate(timer, "").valid);
		assertFalse(resource.validate(timer, "J").valid);
		assertFalse(resource.validate(timer, "Jan").valid);
		assertTrue(resource.validate(timer, "Jan 1").valid);
		assertTrue(resource.validate(timer, "Jan 1st").valid);
		assertTrue(resource.validate(timer, "Jan 1st Observance").valid);
		assertTrue(resource.validate(timer, "jan 1st observance").valid);
		assertTrue(resource.validate(timer, "3rd Monday in January").valid);
		assertTrue(resource.validate(timer, "3rd monday in january").valid);
		assertTrue(resource.validate(timer, "last monday in january").valid);
		assertTrue(resource.validate(timer, "first monday in january").valid);
	}

	/**
	 * Test how the resource responds to adding a new holiday.
	 * 
	 * @throws DatabaseException
	 *             if there is a database problem
	 * @throws HolidayConfigurationException
	 *             if there is a holiday definition problem
	 */
	@Test
	public void testAdd() throws DatabaseException,
			HolidayConfigurationException {
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

			final Company company = new Company().setName("company");
			daoFactory.getCompanyDao().add(company);

			final Holiday holiday = new Holiday().setCompanyId(company.getId())
					.setConfig("Jan 1st").setDescription("New Years");

			final HolidayResource resource = new HolidayResource();
			final AddResponse response = resource.add(securityContext, config,
					daoFactory, timer, holiday);

			assertNotNull(response);
			assertTrue(response.success);
			assertNotNull(response.holiday);
			assertEquals(holiday, response.holiday);

			assertEquals(1, daoFactory.getHolidayDao().getAll(company.getId())
					.size());
		}
	}

	/**
	 * Test how the resource responds to updating a holiday without an id.
	 * 
	 * @throws DatabaseException
	 *             if there is a database problem
	 * @throws HolidayConfigurationException
	 *             if there is a holiday definition problem
	 */
	@Test(expected = BadRequestException.class)
	public void testUpdateNoId() throws DatabaseException,
			HolidayConfigurationException {
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

			final Company company = new Company().setName("company");
			daoFactory.getCompanyDao().add(company);

			final Holiday holiday = new Holiday().setCompanyId(company.getId())
					.setConfig("Jan 1st").setDescription("New Years");

			final HolidayResource resource = new HolidayResource();
			resource.update(securityContext, config, daoFactory, timer, holiday);
		}
	}

	/**
	 * Test how the resource responds to updating a holiday.
	 * 
	 * @throws DatabaseException
	 *             if there is a database problem
	 * @throws HolidayConfigurationException
	 *             if there is a holiday definition problem
	 */
	@Test
	public void testUpdate() throws DatabaseException,
			HolidayConfigurationException {
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

			final Company company = new Company().setName("company");
			daoFactory.getCompanyDao().add(company);

			final Holiday holiday = new Holiday().setCompanyId(company.getId())
					.setConfig("Jan 1st").setDescription("New Years");

			final HolidayDao dao = daoFactory.getHolidayDao();
			dao.add(holiday);

			holiday.setDescription("New Years Day");

			final HolidayResource resource = new HolidayResource();
			final UpdateResponse response = resource.update(securityContext,
					config, daoFactory, timer, holiday);

			assertNotNull(response);
			assertTrue(response.success);
			assertNotNull(response.holiday);
			assertEquals(holiday, response.holiday);

			final Holiday fromDb = dao.get(company.getId(), holiday.getId());
			assertEquals(holiday, fromDb);
		}
	}

	/**
	 * Test how the resource responds to deleting holidays.
	 * 
	 * @throws DatabaseException
	 *             if there is a database problem
	 * @throws HolidayConfigurationException
	 *             if there is a holiday definition problem
	 */
	@Test
	public void testDelete() throws DatabaseException,
			HolidayConfigurationException {
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

			final Company company = new Company().setName("company");
			daoFactory.getCompanyDao().add(company);

			final Holiday holiday = new Holiday().setCompanyId(company.getId())
					.setConfig("Jan 1st").setDescription("New Years");
			daoFactory.getHolidayDao().add(holiday);

			final HolidayResource resource = new HolidayResource();
			resource.delete(securityContext, config, daoFactory, timer,
					new IdSet(holiday.getId()));

			assertEquals(0, daoFactory.getHolidayDao().getAll(company.getId())
					.size());
		}
	}
}
