package com.arcblaze.arctime.rest.user;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.util.Date;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.SecurityContext;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;
import org.mockito.Mockito;

import com.arcblaze.arccore.common.model.Company;
import com.arcblaze.arccore.common.model.User;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arctime.common.model.PayPeriod;
import com.arcblaze.arctime.common.model.PayPeriodType;
import com.arcblaze.arctime.common.model.Timesheet;
import com.arcblaze.arctime.common.model.util.HolidayConfigurationException;
import com.arcblaze.arctime.db.ArcTimeDaoFactory;
import com.arcblaze.arctime.db.dao.TimesheetDao;
import com.arcblaze.arctime.db.util.TestDatabase;
import com.arcblaze.arctime.rest.user.TimesheetFixResource.FixResponse;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

/**
 * Perform testing of the current timesheet retrieval capabilities.
 */
public class TimesheetFixResourceTest {
	/**
	 * Test how the resource responds with a null id.
	 * 
	 * @throws DatabaseException
	 *             if there is a database issue
	 */
	@Test(expected = BadRequestException.class)
	public void testNullId() throws DatabaseException {
		try (final TestDatabase testDatabase = new TestDatabase()) {
			testDatabase.load("hsqldb/arctime-db.sql");
			final ArcTimeDaoFactory daoFactory = testDatabase.getDaoFactory();
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

			final SecurityContext security = Mockito
					.mock(SecurityContext.class);
			Mockito.when(security.getUserPrincipal()).thenReturn(user);

			final TimesheetFixResource resource = new TimesheetFixResource();
			resource.fix(security, daoFactory, timer, null);
		}
	}

	/**
	 * Test how the resource responds with no timesheet available.
	 * 
	 * @throws DatabaseException
	 *             if there is a database issue
	 */
	@Test(expected = NotFoundException.class)
	public void testNoTimesheetAvailable() throws DatabaseException {
		try (final TestDatabase testDatabase = new TestDatabase()) {
			testDatabase.load("hsqldb/arctime-db.sql");
			final ArcTimeDaoFactory daoFactory = testDatabase.getDaoFactory();
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

			final SecurityContext security = Mockito
					.mock(SecurityContext.class);
			Mockito.when(security.getUserPrincipal()).thenReturn(user);

			final TimesheetFixResource resource = new TimesheetFixResource();
			resource.fix(security, daoFactory, timer, 1);
		}
	}

	/**
	 * Test how the resource responds when the timesheet has not been completed.
	 * 
	 * @throws DatabaseException
	 *             if there is a database issue
	 * @throws ParseException
	 *             if there is a problem parsing dates
	 * @throws HolidayConfigurationException
	 *             if there is a holiday configuration issue
	 */
	@Test
	public void testTimesheetNotCompleted() throws DatabaseException,
			ParseException, HolidayConfigurationException {
		try (final TestDatabase testDatabase = new TestDatabase()) {
			testDatabase.load("hsqldb/arctime-db.sql");
			final ArcTimeDaoFactory daoFactory = testDatabase.getDaoFactory();
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

			final PayPeriod payPeriod = new PayPeriod()
					.setCompanyId(company.getId())
					.setType(PayPeriodType.WEEKLY).setBegin(new Date());
			payPeriod.setEnd(DateUtils.addDays(payPeriod.getBegin(), 7));
			daoFactory.getPayPeriodDao().add(payPeriod);

			final Timesheet timesheet = new Timesheet();
			timesheet.setCompanyId(company.getId());
			timesheet.setUserId(user.getId());
			timesheet.setBegin(payPeriod.getBegin());
			final TimesheetDao dao = daoFactory.getTimesheetDao();
			dao.add(timesheet);

			final SecurityContext security = Mockito
					.mock(SecurityContext.class);
			Mockito.when(security.getUserPrincipal()).thenReturn(user);

			final TimesheetFixResource resource = new TimesheetFixResource();
			final FixResponse response = resource.fix(security, daoFactory,
					timer, timesheet.getId());
			assertNotNull(response);
			assertTrue(response.success);

			final Timesheet ts = dao.get(company.getId(), timesheet.getId());
			assertNotNull(ts);
			assertEquals(company.getId(), ts.getCompanyId());
			assertNotNull(ts.getBegin());
			assertFalse(ts.isCompleted());
			assertFalse(ts.isApproved());
			assertFalse(ts.isVerified());
			assertFalse(ts.isExported());
			assertNull(ts.getApprover());
			assertNull(ts.getApproverId());
			assertNull(ts.getVerifier());
			assertNull(ts.getVerifierId());
			assertNull(ts.getExporter());
			assertNull(ts.getExporterId());
			assertNotNull(ts.getAuditLogs());
			assertTrue(ts.getAuditLogs().isEmpty());
			assertNotNull(ts.getHolidays());
			assertTrue(ts.getHolidays().isEmpty());
			assertNotNull(ts.getTasks());
			assertTrue(ts.getTasks().isEmpty());
		}
	}

	/**
	 * Test how the resource responds when the timesheet has been completed.
	 * 
	 * @throws DatabaseException
	 *             if there is a database issue
	 * @throws ParseException
	 *             if there is a problem parsing dates
	 * @throws HolidayConfigurationException
	 *             if there is a holiday configuration issue
	 */
	@Test
	public void testTimesheetCompleted() throws DatabaseException,
			ParseException, HolidayConfigurationException {
		try (final TestDatabase testDatabase = new TestDatabase()) {
			testDatabase.load("hsqldb/arctime-db.sql");
			final ArcTimeDaoFactory daoFactory = testDatabase.getDaoFactory();
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

			final PayPeriod payPeriod = new PayPeriod()
					.setCompanyId(company.getId())
					.setType(PayPeriodType.WEEKLY).setBegin(new Date());
			payPeriod.setEnd(DateUtils.addDays(payPeriod.getBegin(), 7));
			daoFactory.getPayPeriodDao().add(payPeriod);

			final Timesheet timesheet = new Timesheet();
			timesheet.setCompanyId(company.getId());
			timesheet.setUserId(user.getId());
			timesheet.setBegin(payPeriod.getBegin());
			final TimesheetDao dao = daoFactory.getTimesheetDao();
			dao.add(timesheet);

			dao.complete(company.getId(), true, timesheet.getId());

			final SecurityContext security = Mockito
					.mock(SecurityContext.class);
			Mockito.when(security.getUserPrincipal()).thenReturn(user);

			final TimesheetFixResource resource = new TimesheetFixResource();
			final FixResponse response = resource.fix(security, daoFactory,
					timer, timesheet.getId());
			assertNotNull(response);
			assertTrue(response.success);

			final Timesheet ts = dao.get(company.getId(), timesheet.getId());
			assertNotNull(ts);
			assertEquals(company.getId(), ts.getCompanyId());
			assertNotNull(ts.getBegin());
			assertFalse(ts.isCompleted());
			assertFalse(ts.isApproved());
			assertFalse(ts.isVerified());
			assertFalse(ts.isExported());
			assertNull(ts.getApprover());
			assertNull(ts.getApproverId());
			assertNull(ts.getVerifier());
			assertNull(ts.getVerifierId());
			assertNull(ts.getExporter());
			assertNull(ts.getExporterId());
			assertNotNull(ts.getAuditLogs());
			assertTrue(ts.getAuditLogs().isEmpty());
			assertNotNull(ts.getHolidays());
			assertTrue(ts.getHolidays().isEmpty());
			assertNotNull(ts.getTasks());
			assertTrue(ts.getTasks().isEmpty());
		}
	}
}
