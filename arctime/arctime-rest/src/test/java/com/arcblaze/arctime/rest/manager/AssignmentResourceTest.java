package com.arcblaze.arctime.rest.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.Set;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.SecurityContext;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;
import org.mockito.Mockito;

import com.arcblaze.arccore.common.config.Config;
import com.arcblaze.arccore.common.model.Company;
import com.arcblaze.arccore.common.model.User;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arctime.common.model.Assignment;
import com.arcblaze.arctime.common.model.Task;
import com.arcblaze.arctime.db.ArcTimeDaoFactory;
import com.arcblaze.arctime.db.util.TestDatabase;
import com.arcblaze.arctime.rest.manager.AssignmentResource.AddResponse;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

/**
 * Perform testing of the user resource capabilities.
 */
public class AssignmentResourceTest {
	/**
	 * Test how the resource responds to being given an invalid id.
	 * 
	 * @throws DatabaseException
	 *             if there is a database problem
	 */
	@Test(expected = BadRequestException.class)
	public void testGetForTaskInvalidId() throws DatabaseException {
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

			final AssignmentResource resource = new AssignmentResource();
			resource.getForTask(securityContext, config, daoFactory, timer,
					null, null);
		}
	}

	/**
	 * Test how the resource responds to being given a non-existent id.
	 * 
	 * @throws DatabaseException
	 *             if there is a database problem
	 */
	@Test
	public void testGetForTaskNonExistentId() throws DatabaseException {
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

			final AssignmentResource resource = new AssignmentResource();
			final Set<Assignment> assignments = resource.getForTask(
					securityContext, config, daoFactory, timer, 1234, null);

			assertNotNull(assignments);
			assertEquals(0, assignments.size());
		}
	}

	/**
	 * Test how the resource responds to being given a valid task id with no
	 * day.
	 * 
	 * @throws DatabaseException
	 *             if there is a database problem
	 */
	@Test
	public void testGetForTaskNoDay() throws DatabaseException {
		final Config config = new Config();
		try (final TestDatabase testDatabase = new TestDatabase()) {
			testDatabase.load("hsqldb/arctime-db.sql");
			final User login = new User().setId(1).setCompanyId(1)
					.setLogin("user");
			final SecurityContext securityContext = Mockito
					.mock(SecurityContext.class);
			Mockito.when(securityContext.getUserPrincipal()).thenReturn(login);
			final ArcTimeDaoFactory daoFactory = testDatabase.getDaoFactory();
			final MetricRegistry metricRegistry = new MetricRegistry();
			final Timer timer = metricRegistry.timer("test");

			final Company company = new Company().setName("company");
			daoFactory.getCompanyDao().add(company);

			final Task task = new Task();
			task.setCompanyId(company.getId());
			task.setDescription("active");
			task.setJobCode("active");
			task.setAdministrative(false);
			daoFactory.getTaskDao().add(task);

			final User user = new User();
			user.setCompanyId(company.getId());
			user.setLogin("user");
			user.setHashedPass("hashed");
			user.setSalt("salt");
			user.setEmail("email");
			user.setFirstName("first");
			user.setLastName("last");
			daoFactory.getUserDao().add(user);

			final Assignment assignment = new Assignment();
			assignment.setCompanyId(company.getId());
			assignment.setTaskId(task.getId());
			assignment.setUserId(user.getId());
			assignment.setBegin(DateUtils.addDays(new Date(), -30));
			assignment.setEnd(DateUtils.addDays(new Date(), 30));
			assignment.setLaborCat("labor cat");
			assignment.setItemName("item name");
			daoFactory.getAssignmentDao().add(assignment);

			final AssignmentResource resource = new AssignmentResource();
			final Set<Assignment> assignments = resource.getForTask(
					securityContext, config, daoFactory, timer, task.getId(),
					null);

			assertNotNull(assignments);
			assertEquals(1, assignments.size());
			assertTrue(assignments.contains(assignment));
		}
	}

	/**
	 * Test how the resource responds to being given a valid task id with no
	 * day.
	 * 
	 * @throws DatabaseException
	 *             if there is a database problem
	 */
	@Test
	public void testGetForTaskWithDayInRange() throws DatabaseException {
		final Config config = new Config();
		try (final TestDatabase testDatabase = new TestDatabase()) {
			testDatabase.load("hsqldb/arctime-db.sql");
			final User login = new User().setId(1).setCompanyId(1)
					.setLogin("user");
			final SecurityContext securityContext = Mockito
					.mock(SecurityContext.class);
			Mockito.when(securityContext.getUserPrincipal()).thenReturn(login);
			final ArcTimeDaoFactory daoFactory = testDatabase.getDaoFactory();
			final MetricRegistry metricRegistry = new MetricRegistry();
			final Timer timer = metricRegistry.timer("test");

			final Company company = new Company().setName("company");
			daoFactory.getCompanyDao().add(company);

			final Task task = new Task();
			task.setCompanyId(company.getId());
			task.setDescription("active");
			task.setJobCode("active");
			task.setAdministrative(false);
			daoFactory.getTaskDao().add(task);

			final User user = new User();
			user.setCompanyId(company.getId());
			user.setLogin("user");
			user.setHashedPass("hashed");
			user.setSalt("salt");
			user.setEmail("email");
			user.setFirstName("first");
			user.setLastName("last");
			daoFactory.getUserDao().add(user);

			final Assignment assignment = new Assignment();
			assignment.setCompanyId(company.getId());
			assignment.setTaskId(task.getId());
			assignment.setUserId(user.getId());
			assignment.setBegin(DateUtils.addDays(new Date(), -30));
			assignment.setEnd(DateUtils.addDays(new Date(), 30));
			assignment.setLaborCat("labor cat");
			assignment.setItemName("item name");
			daoFactory.getAssignmentDao().add(assignment);

			final AssignmentResource resource = new AssignmentResource();
			final Set<Assignment> assignments = resource.getForTask(
					securityContext, config, daoFactory, timer, task.getId(),
					DateFormatUtils.format(new Date(), "yyyy-MM-dd"));

			assertNotNull(assignments);
			assertEquals(1, assignments.size());
			assertTrue(assignments.contains(assignment));
		}
	}

	/**
	 * Test how the resource responds to being given a valid task id with no
	 * day.
	 * 
	 * @throws DatabaseException
	 *             if there is a database problem
	 */
	@Test
	public void testGetForTaskWithDayOutOfRange() throws DatabaseException {
		final Config config = new Config();
		try (final TestDatabase testDatabase = new TestDatabase()) {
			testDatabase.load("hsqldb/arctime-db.sql");
			final User login = new User().setId(1).setCompanyId(1)
					.setLogin("user");
			final SecurityContext securityContext = Mockito
					.mock(SecurityContext.class);
			Mockito.when(securityContext.getUserPrincipal()).thenReturn(login);
			final ArcTimeDaoFactory daoFactory = testDatabase.getDaoFactory();
			final MetricRegistry metricRegistry = new MetricRegistry();
			final Timer timer = metricRegistry.timer("test");

			final Company company = new Company().setName("company");
			daoFactory.getCompanyDao().add(company);

			final Task task = new Task();
			task.setCompanyId(company.getId());
			task.setDescription("active");
			task.setJobCode("active");
			task.setAdministrative(false);
			daoFactory.getTaskDao().add(task);

			final User user = new User();
			user.setCompanyId(company.getId());
			user.setLogin("user");
			user.setHashedPass("hashed");
			user.setSalt("salt");
			user.setEmail("email");
			user.setFirstName("first");
			user.setLastName("last");
			daoFactory.getUserDao().add(user);

			final Assignment assignment = new Assignment();
			assignment.setCompanyId(company.getId());
			assignment.setTaskId(task.getId());
			assignment.setUserId(user.getId());
			assignment.setBegin(DateUtils.addDays(new Date(), -30));
			assignment.setEnd(DateUtils.addDays(new Date(), 30));
			assignment.setLaborCat("labor cat");
			assignment.setItemName("item name");
			daoFactory.getAssignmentDao().add(assignment);

			final AssignmentResource resource = new AssignmentResource();
			final Set<Assignment> assignments = resource.getForTask(
					securityContext, config, daoFactory, timer, task.getId(),
					DateFormatUtils.format(DateUtils.addDays(new Date(), 45),
							"yyyy-MM-dd"));

			assertNotNull(assignments);
			assertEquals(0, assignments.size());
		}
	}

	/**
	 * Test how the resource responds to being given an invalid id.
	 * 
	 * @throws DatabaseException
	 *             if there is a database problem
	 */
	@Test(expected = BadRequestException.class)
	public void testGetForUserInvalidId() throws DatabaseException {
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

			final AssignmentResource resource = new AssignmentResource();
			resource.getForUser(securityContext, config, daoFactory, timer,
					null, null);
		}
	}

	/**
	 * Test how the resource responds to being given a non-existent id.
	 * 
	 * @throws DatabaseException
	 *             if there is a database problem
	 */
	@Test
	public void testGetForUserNonExistentId() throws DatabaseException {
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

			final AssignmentResource resource = new AssignmentResource();
			final Set<Assignment> assignments = resource.getForUser(
					securityContext, config, daoFactory, timer, 1234, null);

			assertNotNull(assignments);
			assertEquals(0, assignments.size());
		}
	}

	/**
	 * Test how the resource responds to being given a valid user id with no
	 * day.
	 * 
	 * @throws DatabaseException
	 *             if there is a database problem
	 */
	@Test
	public void testGetForUserNoDay() throws DatabaseException {
		final Config config = new Config();
		try (final TestDatabase testDatabase = new TestDatabase()) {
			testDatabase.load("hsqldb/arctime-db.sql");
			final User login = new User().setId(1).setCompanyId(1)
					.setLogin("user");
			final SecurityContext securityContext = Mockito
					.mock(SecurityContext.class);
			Mockito.when(securityContext.getUserPrincipal()).thenReturn(login);
			final ArcTimeDaoFactory daoFactory = testDatabase.getDaoFactory();
			final MetricRegistry metricRegistry = new MetricRegistry();
			final Timer timer = metricRegistry.timer("test");

			final Company company = new Company().setName("company");
			daoFactory.getCompanyDao().add(company);

			final Task task = new Task();
			task.setCompanyId(company.getId());
			task.setDescription("active");
			task.setJobCode("active");
			task.setAdministrative(false);
			daoFactory.getTaskDao().add(task);

			final User user = new User();
			user.setCompanyId(company.getId());
			user.setLogin("user");
			user.setHashedPass("hashed");
			user.setSalt("salt");
			user.setEmail("email");
			user.setFirstName("first");
			user.setLastName("last");
			daoFactory.getUserDao().add(user);

			final Assignment assignment = new Assignment();
			assignment.setCompanyId(company.getId());
			assignment.setTaskId(task.getId());
			assignment.setUserId(user.getId());
			assignment.setBegin(DateUtils.addDays(new Date(), -30));
			assignment.setEnd(DateUtils.addDays(new Date(), 30));
			assignment.setLaborCat("labor cat");
			assignment.setItemName("item name");
			daoFactory.getAssignmentDao().add(assignment);

			final AssignmentResource resource = new AssignmentResource();
			final Set<Assignment> assignments = resource.getForUser(
					securityContext, config, daoFactory, timer, user.getId(),
					null);

			assertNotNull(assignments);
			assertEquals(1, assignments.size());
			assertTrue(assignments.contains(assignment));
		}
	}

	/**
	 * Test how the resource responds to being given a valid user id with no
	 * day.
	 * 
	 * @throws DatabaseException
	 *             if there is a database problem
	 */
	@Test
	public void testGetForUserWithDayInRange() throws DatabaseException {
		final Config config = new Config();
		try (final TestDatabase testDatabase = new TestDatabase()) {
			testDatabase.load("hsqldb/arctime-db.sql");
			final User login = new User().setId(1).setCompanyId(1)
					.setLogin("user");
			final SecurityContext securityContext = Mockito
					.mock(SecurityContext.class);
			Mockito.when(securityContext.getUserPrincipal()).thenReturn(login);
			final ArcTimeDaoFactory daoFactory = testDatabase.getDaoFactory();
			final MetricRegistry metricRegistry = new MetricRegistry();
			final Timer timer = metricRegistry.timer("test");

			final Company company = new Company().setName("company");
			daoFactory.getCompanyDao().add(company);

			final Task task = new Task();
			task.setCompanyId(company.getId());
			task.setDescription("active");
			task.setJobCode("active");
			task.setAdministrative(false);
			daoFactory.getTaskDao().add(task);

			final User user = new User();
			user.setCompanyId(company.getId());
			user.setLogin("user");
			user.setHashedPass("hashed");
			user.setSalt("salt");
			user.setEmail("email");
			user.setFirstName("first");
			user.setLastName("last");
			daoFactory.getUserDao().add(user);

			final Assignment assignment = new Assignment();
			assignment.setCompanyId(company.getId());
			assignment.setTaskId(task.getId());
			assignment.setUserId(user.getId());
			assignment.setBegin(DateUtils.addDays(new Date(), -30));
			assignment.setEnd(DateUtils.addDays(new Date(), 30));
			assignment.setLaborCat("labor cat");
			assignment.setItemName("item name");
			daoFactory.getAssignmentDao().add(assignment);

			final AssignmentResource resource = new AssignmentResource();
			final Set<Assignment> assignments = resource.getForUser(
					securityContext, config, daoFactory, timer, user.getId(),
					DateFormatUtils.format(new Date(), "yyyy-MM-dd"));

			assertNotNull(assignments);
			assertEquals(1, assignments.size());
			assertTrue(assignments.contains(assignment));
		}
	}

	/**
	 * Test how the resource responds to being given a valid user id with no
	 * day.
	 * 
	 * @throws DatabaseException
	 *             if there is a database problem
	 */
	@Test
	public void testGetForUserWithDayOutOfRange() throws DatabaseException {
		final Config config = new Config();
		try (final TestDatabase testDatabase = new TestDatabase()) {
			testDatabase.load("hsqldb/arctime-db.sql");
			final User login = new User().setId(1).setCompanyId(1)
					.setLogin("user");
			final SecurityContext securityContext = Mockito
					.mock(SecurityContext.class);
			Mockito.when(securityContext.getUserPrincipal()).thenReturn(login);
			final ArcTimeDaoFactory daoFactory = testDatabase.getDaoFactory();
			final MetricRegistry metricRegistry = new MetricRegistry();
			final Timer timer = metricRegistry.timer("test");

			final Company company = new Company().setName("company");
			daoFactory.getCompanyDao().add(company);

			final Task task = new Task();
			task.setCompanyId(company.getId());
			task.setDescription("active");
			task.setJobCode("active");
			task.setAdministrative(false);
			daoFactory.getTaskDao().add(task);

			final User user = new User();
			user.setCompanyId(company.getId());
			user.setLogin("user");
			user.setHashedPass("hashed");
			user.setSalt("salt");
			user.setEmail("email");
			user.setFirstName("first");
			user.setLastName("last");
			daoFactory.getUserDao().add(user);

			final Assignment assignment = new Assignment();
			assignment.setCompanyId(company.getId());
			assignment.setTaskId(task.getId());
			assignment.setUserId(user.getId());
			assignment.setBegin(DateUtils.addDays(new Date(), -30));
			assignment.setEnd(DateUtils.addDays(new Date(), 30));
			assignment.setLaborCat("labor cat");
			assignment.setItemName("item name");
			daoFactory.getAssignmentDao().add(assignment);

			final AssignmentResource resource = new AssignmentResource();
			final Set<Assignment> assignments = resource.getForUser(
					securityContext, config, daoFactory, timer, user.getId(),
					DateFormatUtils.format(DateUtils.addDays(new Date(), 45),
							"yyyy-MM-dd"));

			assertNotNull(assignments);
			assertEquals(0, assignments.size());
		}
	}

	/**
	 * Test how the resource responds to being given an assignment for a
	 * non-existent task.
	 * 
	 * @throws DatabaseException
	 *             if there is a database problem
	 */
	@Test
	public void testAddWithNonExistentTask() throws DatabaseException {
		final Config config = new Config();
		try (final TestDatabase testDatabase = new TestDatabase()) {
			testDatabase.load("hsqldb/arctime-db.sql");
			final User login = new User().setId(1).setCompanyId(1)
					.setLogin("user");
			final SecurityContext securityContext = Mockito
					.mock(SecurityContext.class);
			Mockito.when(securityContext.getUserPrincipal()).thenReturn(login);
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

			final Assignment assignment = new Assignment();
			assignment.setCompanyId(company.getId());
			assignment.setTaskId(1234);
			assignment.setUserId(user.getId());
			assignment.setBegin(DateUtils.addDays(new Date(), -30));
			assignment.setEnd(DateUtils.addDays(new Date(), 30));
			assignment.setLaborCat("labor cat");
			assignment.setItemName("item name");

			final AssignmentResource resource = new AssignmentResource();
			final AddResponse response = resource.add(securityContext, config,
					daoFactory, timer, assignment);

			assertNotNull(response);
			assertEquals(assignment, response.assignment);
		}
	}

	/**
	 * Test how the resource responds to being given an assignment for a
	 * non-existent user.
	 * 
	 * @throws DatabaseException
	 *             if there is a database problem
	 */
	@Test
	public void testAddWithNonExistentUser() throws DatabaseException {
		final Config config = new Config();
		try (final TestDatabase testDatabase = new TestDatabase()) {
			testDatabase.load("hsqldb/arctime-db.sql");
			final User login = new User().setId(1).setCompanyId(1)
					.setLogin("user");
			final SecurityContext securityContext = Mockito
					.mock(SecurityContext.class);
			Mockito.when(securityContext.getUserPrincipal()).thenReturn(login);
			final ArcTimeDaoFactory daoFactory = testDatabase.getDaoFactory();
			final MetricRegistry metricRegistry = new MetricRegistry();
			final Timer timer = metricRegistry.timer("test");

			final Company company = new Company().setName("company");
			daoFactory.getCompanyDao().add(company);

			final Task task = new Task();
			task.setCompanyId(company.getId());
			task.setDescription("active");
			task.setJobCode("active");
			task.setAdministrative(false);
			daoFactory.getTaskDao().add(task);

			final Assignment assignment = new Assignment();
			assignment.setCompanyId(company.getId());
			assignment.setTaskId(task.getId());
			assignment.setUserId(1234);
			assignment.setBegin(DateUtils.addDays(new Date(), -30));
			assignment.setEnd(DateUtils.addDays(new Date(), 30));
			assignment.setLaborCat("labor cat");
			assignment.setItemName("item name");

			final AssignmentResource resource = new AssignmentResource();
			final AddResponse response = resource.add(securityContext, config,
					daoFactory, timer, assignment);

			assertNotNull(response);
			assertEquals(assignment, response.assignment);
		}
	}

	/**
	 * Test how the resource responds to being given a valid assignment.
	 * 
	 * @throws DatabaseException
	 *             if there is a database problem
	 */
	@Test
	public void testAdd() throws DatabaseException {
		final Config config = new Config();
		try (final TestDatabase testDatabase = new TestDatabase()) {
			testDatabase.load("hsqldb/arctime-db.sql");
			final User login = new User().setId(1).setCompanyId(1)
					.setLogin("user");
			final SecurityContext securityContext = Mockito
					.mock(SecurityContext.class);
			Mockito.when(securityContext.getUserPrincipal()).thenReturn(login);
			final ArcTimeDaoFactory daoFactory = testDatabase.getDaoFactory();
			final MetricRegistry metricRegistry = new MetricRegistry();
			final Timer timer = metricRegistry.timer("test");

			final Company company = new Company().setName("company");
			daoFactory.getCompanyDao().add(company);

			final Task task = new Task();
			task.setCompanyId(company.getId());
			task.setDescription("active");
			task.setJobCode("active");
			task.setAdministrative(false);
			daoFactory.getTaskDao().add(task);

			final User user = new User();
			user.setCompanyId(company.getId());
			user.setLogin("user");
			user.setHashedPass("hashed");
			user.setSalt("salt");
			user.setEmail("email");
			user.setFirstName("first");
			user.setLastName("last");
			daoFactory.getUserDao().add(user);

			final Assignment assignment = new Assignment();
			assignment.setCompanyId(company.getId());
			assignment.setTaskId(task.getId());
			assignment.setUserId(user.getId());
			assignment.setBegin(DateUtils.addDays(new Date(), -30));
			assignment.setEnd(DateUtils.addDays(new Date(), 30));
			assignment.setLaborCat("labor cat");
			assignment.setItemName("item name");

			final AssignmentResource resource = new AssignmentResource();
			final AddResponse response = resource.add(securityContext, config,
					daoFactory, timer, assignment);

			assertNotNull(response);
			assertEquals(assignment, response.assignment);
		}
	}
}
