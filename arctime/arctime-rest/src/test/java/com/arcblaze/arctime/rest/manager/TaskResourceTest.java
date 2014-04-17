package com.arcblaze.arctime.rest.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.SecurityContext;

import org.junit.Test;
import org.mockito.Mockito;

import com.arcblaze.arccore.common.config.Config;
import com.arcblaze.arccore.common.model.Company;
import com.arcblaze.arccore.common.model.IdSet;
import com.arcblaze.arccore.common.model.User;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arctime.common.model.Task;
import com.arcblaze.arctime.db.ArcTimeDaoFactory;
import com.arcblaze.arctime.db.dao.TaskDao;
import com.arcblaze.arctime.db.util.TestDatabase;
import com.arcblaze.arctime.rest.manager.TaskResource.AddResponse;
import com.arcblaze.arctime.rest.manager.TaskResource.UpdateResponse;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

/**
 * Perform testing of the task resource capabilities.
 */
public class TaskResourceTest {
	/**
	 * Test how the resource responds to being given an invalid id.
	 * 
	 * @throws DatabaseException
	 *             if there is a database problem
	 */
	@Test(expected = BadRequestException.class)
	public void testGetInvalidId() throws DatabaseException {
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

			final TaskResource resource = new TaskResource();
			resource.get(securityContext, config, daoFactory, timer, null);
		}
	}

	/**
	 * Test how the resource responds to returning a valid task.
	 * 
	 * @throws DatabaseException
	 *             if there is a database problem
	 */
	@Test
	public void testGet() throws DatabaseException {
		final Config config = new Config();
		try (final TestDatabase testDatabase = new TestDatabase()) {
			testDatabase.load("hsqldb/arctime-db.sql");
			final ArcTimeDaoFactory daoFactory = testDatabase.getDaoFactory();
			final MetricRegistry metricRegistry = new MetricRegistry();
			final Timer timer = metricRegistry.timer("test");

			final Company company = new Company().setName("company").setActive(
					true);
			daoFactory.getCompanyDao().add(company);

			final Task task = new Task();
			task.setCompanyId(company.getId());
			task.setDescription("task");
			task.setJobCode("jobCode");
			daoFactory.getTaskDao().add(task);

			final User user = new User().setId(1).setCompanyId(1)
					.setLogin("user");
			final SecurityContext securityContext = Mockito
					.mock(SecurityContext.class);
			Mockito.when(securityContext.getUserPrincipal()).thenReturn(user);

			final TaskResource resource = new TaskResource();
			final Task response = resource.get(securityContext, config,
					daoFactory, timer, task.getId());

			assertNotNull(response);
			assertEquals(task, response);
		}
	}

	/**
	 * Test how the resource responds to retrieving all tasks when none are
	 * available.
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

			final TaskResource resource = new TaskResource();
			final Set<Task> tasks = resource.all(securityContext, config,
					daoFactory, timer, true, true);

			assertNotNull(tasks);
			assertTrue(tasks.isEmpty());
		}
	}

	/**
	 * Test how the resource responds to retrieving all tasks.
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

			final Task active = new Task();
			active.setCompanyId(company.getId());
			active.setDescription("active");
			active.setJobCode("active");
			active.setAdministrative(false);
			final Task inactive = new Task();
			inactive.setCompanyId(company.getId());
			inactive.setDescription("inactive");
			inactive.setJobCode("inactive");
			inactive.setAdministrative(false);
			inactive.setActive(false);
			final Task admin = new Task();
			admin.setCompanyId(company.getId());
			admin.setDescription("admin");
			admin.setJobCode("admin");
			admin.setAdministrative(true);
			daoFactory.getTaskDao().add(active, inactive, admin);

			final User user = new User().setId(1).setCompanyId(1)
					.setLogin("user");
			final SecurityContext securityContext = Mockito
					.mock(SecurityContext.class);
			Mockito.when(securityContext.getUserPrincipal()).thenReturn(user);

			final TaskResource resource = new TaskResource();
			Set<Task> tasks = resource.all(securityContext, config, daoFactory,
					timer, true, true);
			assertNotNull(tasks);
			assertEquals(3, tasks.size());
			assertTrue(tasks.contains(active));
			assertTrue(tasks.contains(inactive));
			assertTrue(tasks.contains(admin));

			tasks = resource.all(securityContext, config, daoFactory, timer,
					true, false);
			assertNotNull(tasks);
			assertEquals(2, tasks.size());
			assertTrue(tasks.contains(active));
			assertFalse(tasks.contains(inactive));
			assertTrue(tasks.contains(admin));

			tasks = resource.all(securityContext, config, daoFactory, timer,
					false, true);
			assertNotNull(tasks);
			assertEquals(2, tasks.size());
			assertTrue(tasks.contains(active));
			assertTrue(tasks.contains(inactive));
			assertFalse(tasks.contains(admin));

			tasks = resource.all(securityContext, config, daoFactory, timer,
					false, false);
			assertNotNull(tasks);
			assertEquals(1, tasks.size());
			assertTrue(tasks.contains(active));
			assertFalse(tasks.contains(inactive));
			assertFalse(tasks.contains(admin));
		}
	}

	/**
	 * Test how the resource responds to adding a new task.
	 * 
	 * @throws DatabaseException
	 *             if there is a database problem
	 */
	@Test
	public void testAdd() throws DatabaseException {
		final Config config = new Config();
		try (final TestDatabase testDatabase = new TestDatabase()) {
			testDatabase.load("hsqldb/arctime-db.sql");
			final ArcTimeDaoFactory daoFactory = testDatabase.getDaoFactory();
			final MetricRegistry metricRegistry = new MetricRegistry();
			final Timer timer = metricRegistry.timer("test");

			final Company company = new Company().setName("company");
			daoFactory.getCompanyDao().add(company);

			final Task task = new Task();
			task.setCompanyId(company.getId());
			task.setDescription("task");
			task.setJobCode("jobCode");

			final User user = new User().setId(1).setCompanyId(1)
					.setLogin("user");
			final SecurityContext securityContext = Mockito
					.mock(SecurityContext.class);
			Mockito.when(securityContext.getUserPrincipal()).thenReturn(user);

			final TaskResource resource = new TaskResource();
			final AddResponse response = resource.add(securityContext, config,
					daoFactory, timer, task);

			assertNotNull(response);
			assertTrue(response.success);
			assertNotNull(response.task);
			assertEquals(task, response.task);

			assertEquals(1,
					daoFactory.getTaskDao().getAll(company.getId(), true, true)
							.size());
		}
	}

	/**
	 * Test how the resource responds to activating tasks.
	 * 
	 * @throws DatabaseException
	 *             if there is a database problem
	 */
	@Test
	public void testActivate() throws DatabaseException {
		final Config config = new Config();
		try (final TestDatabase testDatabase = new TestDatabase()) {
			testDatabase.load("hsqldb/arctime-db.sql");
			final ArcTimeDaoFactory daoFactory = testDatabase.getDaoFactory();
			final MetricRegistry metricRegistry = new MetricRegistry();
			final Timer timer = metricRegistry.timer("test");

			final Company company = new Company().setName("company");
			daoFactory.getCompanyDao().add(company);

			final Task task = new Task();
			task.setCompanyId(company.getId());
			task.setDescription("task");
			task.setJobCode("jobCode");
			task.setActive(false);
			daoFactory.getTaskDao().add(task);

			final User user = new User().setId(1).setCompanyId(1)
					.setLogin("user");
			final SecurityContext securityContext = Mockito
					.mock(SecurityContext.class);
			Mockito.when(securityContext.getUserPrincipal()).thenReturn(user);

			final TaskResource resource = new TaskResource();
			resource.activate(securityContext, config, daoFactory, timer,
					new IdSet(task.getId()));

			assertTrue(daoFactory.getTaskDao()
					.get(company.getId(), task.getId()).isActive());
		}
	}

	/**
	 * Test how the resource responds to deactivating tasks.
	 * 
	 * @throws DatabaseException
	 *             if there is a database problem
	 */
	@Test
	public void testDectivate() throws DatabaseException {
		final Config config = new Config();
		try (final TestDatabase testDatabase = new TestDatabase()) {
			testDatabase.load("hsqldb/arctime-db.sql");
			final ArcTimeDaoFactory daoFactory = testDatabase.getDaoFactory();
			final MetricRegistry metricRegistry = new MetricRegistry();
			final Timer timer = metricRegistry.timer("test");

			final Company company = new Company().setName("company");
			daoFactory.getCompanyDao().add(company);

			final Task task = new Task();
			task.setCompanyId(company.getId());
			task.setDescription("task");
			task.setJobCode("jobCode");
			task.setActive(true);
			daoFactory.getTaskDao().add(task);

			final User user = new User().setId(1).setCompanyId(1)
					.setLogin("user");
			final SecurityContext securityContext = Mockito
					.mock(SecurityContext.class);
			Mockito.when(securityContext.getUserPrincipal()).thenReturn(user);

			final TaskResource resource = new TaskResource();
			resource.deactivate(securityContext, config, daoFactory, timer,
					new IdSet(task.getId()));

			assertFalse(daoFactory.getTaskDao()
					.get(company.getId(), task.getId()).isActive());
		}
	}

	/**
	 * Test how the resource responds to updating a task without an id.
	 * 
	 * @throws DatabaseException
	 *             if there is a database problem
	 */
	@Test(expected = BadRequestException.class)
	public void testUpdateNoId() throws DatabaseException {
		final Config config = new Config();
		try (final TestDatabase testDatabase = new TestDatabase()) {
			testDatabase.load("hsqldb/arctime-db.sql");
			final ArcTimeDaoFactory daoFactory = testDatabase.getDaoFactory();
			final MetricRegistry metricRegistry = new MetricRegistry();
			final Timer timer = metricRegistry.timer("test");

			final Company company = new Company().setName("company");
			daoFactory.getCompanyDao().add(company);

			final Task task = new Task();
			task.setCompanyId(company.getId());
			task.setDescription("task");
			task.setJobCode("jobCode");

			final User user = new User().setId(1).setCompanyId(1)
					.setLogin("user");
			final SecurityContext securityContext = Mockito
					.mock(SecurityContext.class);
			Mockito.when(securityContext.getUserPrincipal()).thenReturn(user);

			final TaskResource resource = new TaskResource();
			resource.update(securityContext, config, daoFactory, timer, task);
		}
	}

	/**
	 * Test how the resource responds to updating a task.
	 * 
	 * @throws DatabaseException
	 *             if there is a database problem
	 */
	@Test
	public void testUpdate() throws DatabaseException {
		final Config config = new Config();
		try (final TestDatabase testDatabase = new TestDatabase()) {
			testDatabase.load("hsqldb/arctime-db.sql");
			final ArcTimeDaoFactory daoFactory = testDatabase.getDaoFactory();
			final MetricRegistry metricRegistry = new MetricRegistry();
			final Timer timer = metricRegistry.timer("test");

			final Company company = new Company().setName("company");
			daoFactory.getCompanyDao().add(company);

			final Task task = new Task();
			task.setCompanyId(company.getId());
			task.setDescription("task");
			task.setJobCode("jobCode");

			final TaskDao dao = daoFactory.getTaskDao();
			dao.add(task);

			final User user = new User().setId(1).setCompanyId(1)
					.setLogin("user");
			final SecurityContext securityContext = Mockito
					.mock(SecurityContext.class);
			Mockito.when(securityContext.getUserPrincipal()).thenReturn(user);

			task.setDescription("modified");

			final TaskResource resource = new TaskResource();
			final UpdateResponse response = resource.update(securityContext,
					config, daoFactory, timer, task);

			assertNotNull(response);
			assertTrue(response.success);
			assertNotNull(response.task);
			assertEquals(task, response.task);

			final Task fromDb = dao.get(company.getId(), task.getId());
			assertEquals(task, fromDb);
		}
	}

	/**
	 * Test how the resource responds to deleting tasks.
	 * 
	 * @throws DatabaseException
	 *             if there is a database problem
	 */
	@Test
	public void testDelete() throws DatabaseException {
		final Config config = new Config();
		try (final TestDatabase testDatabase = new TestDatabase()) {
			testDatabase.load("hsqldb/arctime-db.sql");
			final ArcTimeDaoFactory daoFactory = testDatabase.getDaoFactory();
			final MetricRegistry metricRegistry = new MetricRegistry();
			final Timer timer = metricRegistry.timer("test");

			final Company company = new Company().setName("company");
			daoFactory.getCompanyDao().add(company);

			final Task task = new Task();
			task.setCompanyId(company.getId());
			task.setDescription("task");
			task.setJobCode("jobCode");
			daoFactory.getTaskDao().add(task);

			final User user = new User().setId(1).setCompanyId(1)
					.setLogin("user");
			final SecurityContext securityContext = Mockito
					.mock(SecurityContext.class);
			Mockito.when(securityContext.getUserPrincipal()).thenReturn(user);

			final TaskResource resource = new TaskResource();
			resource.delete(securityContext, config, daoFactory, timer,
					new IdSet(task.getId()));

			assertEquals(0,
					daoFactory.getTaskDao().getAll(company.getId(), true, true)
							.size());
		}
	}
}
