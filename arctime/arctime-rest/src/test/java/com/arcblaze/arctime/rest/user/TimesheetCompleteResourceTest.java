package com.arcblaze.arctime.rest.user;

import static com.arcblaze.arctime.common.model.Enrichment.AUDIT_LOGS;
import static com.arcblaze.arctime.common.model.Enrichment.BILLS;
import static com.arcblaze.arctime.common.model.Enrichment.HOLIDAYS;
import static com.arcblaze.arctime.common.model.Enrichment.PAY_PERIODS;
import static com.arcblaze.arctime.common.model.Enrichment.TASKS;
import static com.arcblaze.arctime.common.model.Enrichment.USERS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Date;
import java.util.Set;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.SecurityContext;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;
import org.mockito.Mockito;

import com.arcblaze.arccore.common.config.Config;
import com.arcblaze.arccore.common.model.Company;
import com.arcblaze.arccore.common.model.User;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arctime.common.model.Assignment;
import com.arcblaze.arctime.common.model.AuditLog;
import com.arcblaze.arctime.common.model.Bill;
import com.arcblaze.arctime.common.model.PayPeriod;
import com.arcblaze.arctime.common.model.PayPeriodType;
import com.arcblaze.arctime.common.model.Task;
import com.arcblaze.arctime.common.model.Timesheet;
import com.arcblaze.arctime.common.model.util.HolidayConfigurationException;
import com.arcblaze.arctime.db.ArcTimeDaoFactory;
import com.arcblaze.arctime.db.dao.TimesheetDao;
import com.arcblaze.arctime.db.util.TestDatabase;
import com.arcblaze.arctime.rest.user.TimesheetCompleteResource.CompleteResponse;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

/**
 * Perform testing of the current timesheet retrieval capabilities.
 */
public class TimesheetCompleteResourceTest {
	/**
	 * Test how the resource responds with no pay periods available.
	 * 
	 * @throws DatabaseException
	 *             if there is a database issue
	 */
	@Test(expected = BadRequestException.class)
	public void testNullId() throws DatabaseException {
		final Config config = new Config();
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

			final TimesheetCompleteResource resource = new TimesheetCompleteResource();
			resource.complete(security, config, daoFactory, timer, null, "");
		}
	}

	/**
	 * Test how the resource responds with no pay periods available.
	 * 
	 * @throws DatabaseException
	 *             if there is a database issue
	 */
	@Test(expected = BadRequestException.class)
	public void testInvalidData() throws DatabaseException {
		final Config config = new Config();
		try (final TestDatabase testDatabase = new TestDatabase()) {
			testDatabase.load("hsqldb/arctime-db.sql");
			final ArcTimeDaoFactory daoFactory = testDatabase.getDaoFactory();
			final MetricRegistry metricRegistry = new MetricRegistry();
			final Timer timer = metricRegistry.timer("test");

			final Company company = new Company().setName("company").setActive(
					true);
			daoFactory.getCompanyDao().add(company);

			final PayPeriod payPeriod = new PayPeriod()
					.setCompanyId(company.getId())
					.setType(PayPeriodType.WEEKLY).setBegin(new Date());
			payPeriod.setEnd(DateUtils.addDays(payPeriod.getBegin(), 7));
			daoFactory.getPayPeriodDao().add(payPeriod);

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

			final Timesheet timesheet = new Timesheet();
			timesheet.setCompanyId(company.getId());
			timesheet.setUserId(user.getId());
			timesheet.setBegin(payPeriod.getBegin());
			daoFactory.getTimesheetDao().add(timesheet);

			final SecurityContext security = Mockito
					.mock(SecurityContext.class);
			Mockito.when(security.getUserPrincipal()).thenReturn(user);

			final TimesheetCompleteResource resource = new TimesheetCompleteResource();
			resource.complete(security, config, daoFactory, timer,
					timesheet.getId(), "data");
		}
	}

	/**
	 * Test how the resource responds with no pay periods available.
	 * 
	 * @throws DatabaseException
	 *             if there is a database issue
	 * @throws HolidayConfigurationException
	 *             if there is a holiday problem
	 */
	@Test
	public void testEmptyData() throws DatabaseException,
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

			final PayPeriod payPeriod = new PayPeriod()
					.setCompanyId(company.getId())
					.setType(PayPeriodType.WEEKLY).setBegin(new Date());
			payPeriod.setEnd(DateUtils.addDays(payPeriod.getBegin(), 7));
			daoFactory.getPayPeriodDao().add(payPeriod);

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

			final Timesheet timesheet = new Timesheet();
			timesheet.setCompanyId(company.getId());
			timesheet.setUserId(user.getId());
			timesheet.setBegin(payPeriod.getBegin());
			final TimesheetDao dao = daoFactory.getTimesheetDao();
			dao.add(timesheet);

			final SecurityContext security = Mockito
					.mock(SecurityContext.class);
			Mockito.when(security.getUserPrincipal()).thenReturn(user);

			final TimesheetCompleteResource resource = new TimesheetCompleteResource();
			final CompleteResponse response = resource.complete(security,
					config, daoFactory, timer, timesheet.getId(), "");

			final Timesheet next = response.next;
			assertNotNull(next);
			assertEquals(company.getId(), next.getCompanyId());
			assertEquals(user.getId(), next.getUserId());
			assertNotNull(next.getPayPeriod());
			assertTrue(next.getPayPeriod().contains(
					DateUtils.addDays(payPeriod.getEnd(), 2)));
			assertNotNull(next.getBegin());
			assertEquals(next.getBegin(), next.getPayPeriod().getBegin());
			assertFalse(next.isCompleted());
			assertFalse(next.isApproved());
			assertFalse(next.isVerified());
			assertFalse(next.isExported());
			assertNull(next.getApprover());
			assertNull(next.getApproverId());
			assertNull(next.getVerifier());
			assertNull(next.getVerifierId());
			assertNull(next.getExporter());
			assertNull(next.getExporterId());
			assertNotNull(next.getAuditLogs());
			assertTrue(next.getAuditLogs().isEmpty());
			assertNotNull(next.getHolidays());
			assertTrue(next.getHolidays().isEmpty());
			assertNotNull(next.getTasks());
			assertTrue(next.getTasks().isEmpty());

			final Timesheet ts = dao.get(company.getId(), timesheet.getId(),
					AUDIT_LOGS, BILLS, HOLIDAYS, PAY_PERIODS, TASKS, USERS);
			assertNotNull(ts);
			assertEquals(company.getId(), ts.getCompanyId());
			assertEquals(user, ts.getUser());
			assertEquals(user.getId(), ts.getUserId());
			assertNotNull(ts.getPayPeriod());
			assertTrue(ts.getPayPeriod().contains(new Date()));
			assertNotNull(ts.getBegin());
			assertEquals(ts.getBegin(), ts.getPayPeriod().getBegin());
			assertTrue(ts.isCompleted());
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
			assertEquals(1, ts.getAuditLogs().size());
			assertNotNull(ts.getHolidays());
			assertTrue(ts.getHolidays().isEmpty());
			assertNotNull(ts.getTasks());
			assertTrue(ts.getTasks().isEmpty());
		}
	}

	/**
	 * Test how the resource responds with no pay periods available.
	 * 
	 * @throws DatabaseException
	 *             if there is a database issue
	 * @throws HolidayConfigurationException
	 *             if there is a holiday problem
	 */
	@Test
	public void testEmptyDataWithPopulatedTimesheet() throws DatabaseException,
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

			final PayPeriod payPeriod = new PayPeriod()
					.setCompanyId(company.getId())
					.setType(PayPeriodType.WEEKLY).setBegin(new Date());
			payPeriod.setEnd(DateUtils.addDays(payPeriod.getBegin(), 7));
			daoFactory.getPayPeriodDao().add(payPeriod);

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

			final Timesheet timesheet = new Timesheet();
			timesheet.setCompanyId(company.getId());
			timesheet.setUserId(user.getId());
			timesheet.setBegin(payPeriod.getBegin());
			final TimesheetDao dao = daoFactory.getTimesheetDao();
			dao.add(timesheet);

			final Task t1 = new Task().setCompanyId(company.getId())
					.setAdministrative(true).setDescription("Task 1")
					.setJobCode("Job Code 1").setActive(true);
			final Task t2 = new Task().setCompanyId(company.getId())
					.setAdministrative(false).setDescription("Task 2")
					.setJobCode("Job Code 2").setActive(true);
			final Task t3 = new Task().setCompanyId(company.getId())
					.setAdministrative(false).setDescription("Task 3")
					.setJobCode("Job Code 3").setActive(true);
			final Task t4 = new Task().setCompanyId(company.getId())
					.setAdministrative(false).setDescription("Task 4")
					.setJobCode("Job Code 4").setActive(false);
			daoFactory.getTaskDao().add(t1, t2, t3, t4);

			// This assignment includes the full pay period.
			final Assignment a1 = new Assignment()
					.setCompanyId(company.getId()).setUserId(user.getId())
					.setTaskId(t2.getId()).setItemName("Task 2:Item 1")
					.setLaborCat("LCAT 1")
					.setBegin(DateUtils.addDays(payPeriod.getBegin(), -5))
					.setEnd(DateUtils.addDays(payPeriod.getEnd(), 5));

			// This assignment includes the beginning of the pay period.
			final Assignment a2 = new Assignment()
					.setCompanyId(company.getId()).setUserId(user.getId())
					.setTaskId(t2.getId()).setItemName("Task 2:Item 2")
					.setLaborCat("LCAT 2")
					.setBegin(DateUtils.addDays(payPeriod.getBegin(), -5))
					.setEnd(DateUtils.addDays(payPeriod.getBegin(), 1));

			// This assignment includes the ending of the pay period.
			final Assignment a3 = new Assignment()
					.setCompanyId(company.getId()).setUserId(user.getId())
					.setTaskId(t2.getId()).setItemName("Task 2:Item 3")
					.setLaborCat("LCAT 3")
					.setBegin(DateUtils.addDays(payPeriod.getEnd(), -1))
					.setEnd(DateUtils.addDays(payPeriod.getEnd(), 5));

			// This assignment is before the pay period.
			final Assignment a4 = new Assignment()
					.setCompanyId(company.getId()).setUserId(user.getId())
					.setTaskId(t3.getId()).setItemName("Task 3:Item 1")
					.setLaborCat("LCAT 1")
					.setBegin(DateUtils.addDays(payPeriod.getBegin(), -10))
					.setEnd(DateUtils.addDays(payPeriod.getBegin(), -5));

			// This assignment is after the pay period.
			final Assignment a5 = new Assignment()
					.setCompanyId(company.getId()).setUserId(user.getId())
					.setTaskId(t3.getId()).setItemName("Task 3:Item 2")
					.setLaborCat("LCAT 2")
					.setBegin(DateUtils.addDays(payPeriod.getEnd(), 5))
					.setEnd(DateUtils.addDays(payPeriod.getEnd(), 10));

			// This assignment includes the full pay period.
			final Assignment a6 = new Assignment()
					.setCompanyId(company.getId()).setUserId(user.getId())
					.setTaskId(t4.getId()).setItemName("Task 4:Item 1")
					.setLaborCat("LCAT 1")
					.setBegin(DateUtils.addDays(payPeriod.getBegin(), -5))
					.setEnd(DateUtils.addDays(payPeriod.getEnd(), 5));

			daoFactory.getAssignmentDao().add(a1, a2, a3, a4, a5, a6);

			// Bill against the admin task.
			final Bill b1 = new Bill().setUserId(user.getId())
					.setTaskId(t1.getId()).setDay(payPeriod.getBegin())
					.setHours("8.0").setTimestamp(new Date());

			// Bill against LCAT 1 on task 2
			final Bill b2 = new Bill().setUserId(user.getId())
					.setTaskId(t2.getId()).setAssignmentId(a1.getId())
					.setDay(payPeriod.getBegin()).setHours("8.0")
					.setTimestamp(new Date());

			// Bill against LCAT 2 on task 2
			final Bill b3 = new Bill().setUserId(user.getId())
					.setTaskId(t2.getId()).setAssignmentId(a2.getId())
					.setDay(payPeriod.getBegin()).setHours("2.25")
					.setTimestamp(new Date());

			// Bill against LCAT 3 on task 2, but outside the assignment dates
			final Bill b4 = new Bill().setUserId(user.getId())
					.setTaskId(t2.getId()).setAssignmentId(a3.getId())
					.setDay(payPeriod.getBegin()).setHours("8.5")
					.setTimestamp(new Date());

			// Bill against task 3, before the timesheet pay period
			final Bill b5 = new Bill().setUserId(user.getId())
					.setTaskId(t3.getId()).setAssignmentId(a4.getId())
					.setDay(a4.getBegin()).setHours("3.5")
					.setTimestamp(new Date());

			// Bill against task 3, after the timesheet pay period
			final Bill b6 = new Bill().setUserId(user.getId())
					.setTaskId(t3.getId()).setAssignmentId(a5.getId())
					.setDay(a5.getBegin()).setHours("3.5")
					.setTimestamp(new Date());

			// Bill against task 4, which is inactive
			final Bill b7 = new Bill().setUserId(user.getId())
					.setTaskId(t4.getId()).setAssignmentId(a6.getId())
					.setDay(payPeriod.getEnd()).setHours("3.5")
					.setTimestamp(new Date());

			daoFactory.getBillDao().add(b1, b2, b3, b4, b5, b6, b7);

			final SecurityContext security = Mockito
					.mock(SecurityContext.class);
			Mockito.when(security.getUserPrincipal()).thenReturn(user);

			final TimesheetCompleteResource resource = new TimesheetCompleteResource();
			final CompleteResponse response = resource.complete(security,
					config, daoFactory, timer, timesheet.getId(), "");

			final Timesheet next = response.next;
			assertNotNull(next);
			assertEquals(company.getId(), next.getCompanyId());
			assertEquals(user.getId(), next.getUserId());
			assertNotNull(next.getPayPeriod());
			assertTrue(next.getPayPeriod().contains(
					DateUtils.addDays(payPeriod.getEnd(), 2)));
			assertNotNull(next.getBegin());
			assertEquals(next.getBegin(), next.getPayPeriod().getBegin());
			assertFalse(next.isCompleted());
			assertFalse(next.isApproved());
			assertFalse(next.isVerified());
			assertFalse(next.isExported());
			assertNull(next.getApprover());
			assertNull(next.getApproverId());
			assertNull(next.getVerifier());
			assertNull(next.getVerifierId());
			assertNull(next.getExporter());
			assertNull(next.getExporterId());
			assertNotNull(next.getAuditLogs());
			assertTrue(next.getAuditLogs().isEmpty());
			assertNotNull(next.getHolidays());
			assertTrue(next.getHolidays().isEmpty());
			assertNotNull(next.getTasks());
			assertEquals(3, next.getTasks().size());

			final Timesheet ts = dao.get(company.getId(), timesheet.getId(),
					AUDIT_LOGS, BILLS, HOLIDAYS, PAY_PERIODS, TASKS, USERS);
			assertNotNull(ts);
			assertEquals(company.getId(), ts.getCompanyId());
			assertEquals(user, ts.getUser());
			assertEquals(user.getId(), ts.getUserId());
			assertNotNull(ts.getPayPeriod());
			assertTrue(ts.getPayPeriod().contains(new Date()));
			assertNotNull(ts.getBegin());
			assertEquals(ts.getBegin(), ts.getPayPeriod().getBegin());
			assertTrue(ts.isCompleted());
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
			assertEquals(4, ts.getAuditLogs().size());
			assertNotNull(ts.getHolidays());
			assertTrue(ts.getHolidays().isEmpty());
			assertNotNull(ts.getTasks());
			assertEquals(2, ts.getTasks().size());

			final Set<AuditLog> auditLogs = ts.getAuditLogs();
			for (AuditLog auditLog : auditLogs) {
				assertEquals(company.getId(), auditLog.getCompanyId());
				assertEquals(ts.getId(), auditLog.getTimesheetId());
			}

			final Set<Task> tasks = ts.getTasks();
			assertEquals(2, tasks.size());
			assertTrue(tasks.contains(t1));
			assertTrue(tasks.contains(t2));
			for (final Task t : tasks) {
				if (t.getId() == 1) {
					assertTrue(t.getAssignments().isEmpty());
					assertTrue(t.getBills().isEmpty());
				} else if (t.getId() == 2) {
					assertEquals(3, t.getAssignments().size());
					assertTrue(t.getBills().isEmpty());
					for (final Assignment a : t.getAssignments()) {
						assertEquals(company.getId(), a.getCompanyId());
						assertTrue(a.getId() >= 1 && a.getId() <= 3);
					}
				}
			}
		}
	}

	/**
	 * Test how the resource responds with no pay periods available.
	 * 
	 * @throws DatabaseException
	 *             if there is a database issue
	 * @throws HolidayConfigurationException
	 *             if there is a holiday problem
	 */
	@Test
	public void testCompleteDataIntoTimesheet() throws DatabaseException,
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

			final PayPeriod payPeriod = new PayPeriod()
					.setCompanyId(company.getId())
					.setType(PayPeriodType.WEEKLY).setBegin(new Date());
			payPeriod.setEnd(DateUtils.addDays(payPeriod.getBegin(), 7));
			daoFactory.getPayPeriodDao().add(payPeriod);

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

			final Timesheet timesheet = new Timesheet();
			timesheet.setCompanyId(company.getId());
			timesheet.setUserId(user.getId());
			timesheet.setBegin(payPeriod.getBegin());
			final TimesheetDao dao = daoFactory.getTimesheetDao();
			dao.add(timesheet);

			final Task t1 = new Task().setCompanyId(company.getId())
					.setAdministrative(true).setDescription("Task 1")
					.setJobCode("Job Code 1").setActive(true);
			final Task t2 = new Task().setCompanyId(company.getId())
					.setAdministrative(false).setDescription("Task 2")
					.setJobCode("Job Code 2").setActive(true);
			final Task t3 = new Task().setCompanyId(company.getId())
					.setAdministrative(false).setDescription("Task 3")
					.setJobCode("Job Code 3").setActive(true);
			final Task t4 = new Task().setCompanyId(company.getId())
					.setAdministrative(false).setDescription("Task 4")
					.setJobCode("Job Code 4").setActive(false);
			daoFactory.getTaskDao().add(t1, t2, t3, t4);

			// This assignment includes the full pay period.
			final Assignment a1 = new Assignment()
					.setCompanyId(company.getId()).setUserId(user.getId())
					.setTaskId(t2.getId()).setItemName("Task 2:Item 1")
					.setLaborCat("LCAT 1")
					.setBegin(DateUtils.addDays(payPeriod.getBegin(), -5))
					.setEnd(DateUtils.addDays(payPeriod.getEnd(), 5));

			// This assignment includes the beginning of the pay period.
			final Assignment a2 = new Assignment()
					.setCompanyId(company.getId()).setUserId(user.getId())
					.setTaskId(t2.getId()).setItemName("Task 2:Item 2")
					.setLaborCat("LCAT 2")
					.setBegin(DateUtils.addDays(payPeriod.getBegin(), -5))
					.setEnd(DateUtils.addDays(payPeriod.getBegin(), 1));

			// This assignment includes the ending of the pay period.
			final Assignment a3 = new Assignment()
					.setCompanyId(company.getId()).setUserId(user.getId())
					.setTaskId(t2.getId()).setItemName("Task 2:Item 3")
					.setLaborCat("LCAT 3")
					.setBegin(DateUtils.addDays(payPeriod.getEnd(), -1))
					.setEnd(DateUtils.addDays(payPeriod.getEnd(), 5));

			// This assignment is before the pay period.
			final Assignment a4 = new Assignment()
					.setCompanyId(company.getId()).setUserId(user.getId())
					.setTaskId(t3.getId()).setItemName("Task 3:Item 1")
					.setLaborCat("LCAT 1")
					.setBegin(DateUtils.addDays(payPeriod.getBegin(), -10))
					.setEnd(DateUtils.addDays(payPeriod.getBegin(), -5));

			// This assignment is after the pay period.
			final Assignment a5 = new Assignment()
					.setCompanyId(company.getId()).setUserId(user.getId())
					.setTaskId(t3.getId()).setItemName("Task 3:Item 2")
					.setLaborCat("LCAT 2")
					.setBegin(DateUtils.addDays(payPeriod.getEnd(), 5))
					.setEnd(DateUtils.addDays(payPeriod.getEnd(), 10));

			// This assignment includes the full pay period.
			final Assignment a6 = new Assignment()
					.setCompanyId(company.getId()).setUserId(user.getId())
					.setTaskId(t4.getId()).setItemName("Task 4:Item 1")
					.setLaborCat("LCAT 1")
					.setBegin(DateUtils.addDays(payPeriod.getBegin(), -5))
					.setEnd(DateUtils.addDays(payPeriod.getEnd(), 5));

			daoFactory.getAssignmentDao().add(a1, a2, a3, a4, a5, a6);

			// Bill against the admin task.
			final Bill b1 = new Bill().setUserId(user.getId())
					.setTaskId(t1.getId()).setDay(payPeriod.getBegin())
					.setHours("8.0").setTimestamp(new Date());

			// Bill against LCAT 1 on task 2
			final Bill b2 = new Bill().setUserId(user.getId())
					.setTaskId(t2.getId()).setAssignmentId(a1.getId())
					.setDay(payPeriod.getBegin()).setHours("8.0")
					.setTimestamp(new Date());

			// Bill against LCAT 2 on task 2
			final Bill b3 = new Bill().setUserId(user.getId())
					.setTaskId(t2.getId()).setAssignmentId(a2.getId())
					.setDay(payPeriod.getBegin()).setHours("2.25")
					.setTimestamp(new Date());

			// Bill against LCAT 3 on task 2, but outside the assignment dates
			final Bill b4 = new Bill().setUserId(user.getId())
					.setTaskId(t2.getId()).setAssignmentId(a3.getId())
					.setDay(payPeriod.getBegin()).setHours("8.5")
					.setTimestamp(new Date());

			// Bill against task 3, before the timesheet pay period
			final Bill b5 = new Bill().setUserId(user.getId())
					.setTaskId(t3.getId()).setAssignmentId(a4.getId())
					.setDay(a4.getBegin()).setHours("3.5")
					.setTimestamp(new Date());

			// Bill against task 3, after the timesheet pay period
			final Bill b6 = new Bill().setUserId(user.getId())
					.setTaskId(t3.getId()).setAssignmentId(a5.getId())
					.setDay(a5.getBegin()).setHours("3.5")
					.setTimestamp(new Date());

			// Bill against task 4, which is inactive
			final Bill b7 = new Bill().setUserId(user.getId())
					.setTaskId(t4.getId()).setAssignmentId(a6.getId())
					.setDay(payPeriod.getEnd()).setHours("3.5")
					.setTimestamp(new Date());

			final String timesheetData = Bill.toTimesheetData(Arrays.asList(b1,
					b2, b3, b4, b5, b6, b7));

			final SecurityContext security = Mockito
					.mock(SecurityContext.class);
			Mockito.when(security.getUserPrincipal()).thenReturn(user);

			final TimesheetCompleteResource resource = new TimesheetCompleteResource();
			final CompleteResponse response = resource
					.complete(security, config, daoFactory, timer,
							timesheet.getId(), timesheetData);

			final Timesheet next = response.next;
			assertNotNull(next);
			assertEquals(company.getId(), next.getCompanyId());
			assertEquals(user.getId(), next.getUserId());
			assertNotNull(next.getPayPeriod());
			assertTrue(next.getPayPeriod().contains(
					DateUtils.addDays(payPeriod.getEnd(), 2)));
			assertNotNull(next.getBegin());
			assertEquals(next.getBegin(), next.getPayPeriod().getBegin());
			assertFalse(next.isCompleted());
			assertFalse(next.isApproved());
			assertFalse(next.isVerified());
			assertFalse(next.isExported());
			assertNull(next.getApprover());
			assertNull(next.getApproverId());
			assertNull(next.getVerifier());
			assertNull(next.getVerifierId());
			assertNull(next.getExporter());
			assertNull(next.getExporterId());
			assertNotNull(next.getAuditLogs());
			assertTrue(next.getAuditLogs().isEmpty());
			assertNotNull(next.getHolidays());
			assertTrue(next.getHolidays().isEmpty());
			assertNotNull(next.getTasks());
			assertEquals(3, next.getTasks().size());

			final Timesheet ts = dao.get(company.getId(), timesheet.getId(),
					AUDIT_LOGS, BILLS, HOLIDAYS, PAY_PERIODS, TASKS, USERS);
			assertNotNull(ts);
			assertEquals(company.getId(), ts.getCompanyId());
			assertEquals(user, ts.getUser());
			assertEquals(user.getId(), ts.getUserId());
			assertNotNull(ts.getPayPeriod());
			assertTrue(ts.getPayPeriod().contains(new Date()));
			assertNotNull(ts.getBegin());
			assertEquals(ts.getBegin(), ts.getPayPeriod().getBegin());
			assertTrue(ts.isCompleted());
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
			assertEquals(5, ts.getAuditLogs().size());
			assertNotNull(ts.getHolidays());
			assertTrue(ts.getHolidays().isEmpty());
			assertNotNull(ts.getTasks());
			assertEquals(2, ts.getTasks().size());

			final Set<AuditLog> auditLogs = ts.getAuditLogs();
			for (AuditLog auditLog : auditLogs) {
				assertEquals(company.getId(), auditLog.getCompanyId());
				assertEquals(ts.getId(), auditLog.getTimesheetId());
			}

			final Set<Task> tasks = ts.getTasks();
			assertEquals(2, tasks.size());
			assertTrue(tasks.contains(t1));
			assertTrue(tasks.contains(t2));
			for (final Task t : tasks) {
				if (t.getId() == 1) {
					assertTrue(t.getAssignments().isEmpty());
					assertTrue(t.getBills().isEmpty());
				} else if (t.getId() == 2) {
					assertEquals(3, t.getAssignments().size());
					assertTrue(t.getBills().isEmpty());
					for (final Assignment a : t.getAssignments()) {
						assertEquals(company.getId(), a.getCompanyId());
						assertTrue(a.getId() >= 1 && a.getId() <= 3);
					}
				}
			}
		}
	}
}
