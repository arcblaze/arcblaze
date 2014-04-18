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
import com.arcblaze.arccore.db.dao.UserDao;
import com.arcblaze.arctime.db.ArcTimeDaoFactory;
import com.arcblaze.arctime.db.util.TestDatabase;
import com.arcblaze.arctime.rest.manager.UserResource.AddResponse;
import com.arcblaze.arctime.rest.manager.UserResource.UpdateResponse;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

/**
 * Perform testing of the user resource capabilities.
 */
public class UserResourceTest {
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

			final UserResource resource = new UserResource();
			resource.get(securityContext, config, daoFactory, timer, null);
		}
	}

	/**
	 * Test how the resource responds to returning a valid user.
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

			final User user = new User();
			user.setCompanyId(company.getId());
			user.setLogin("user");
			user.setHashedPass("hashed");
			user.setSalt("salt");
			user.setEmail("email");
			user.setFirstName("first");
			user.setLastName("last");
			daoFactory.getUserDao().add(user);

			final SecurityContext securityContext = Mockito
					.mock(SecurityContext.class);
			Mockito.when(securityContext.getUserPrincipal()).thenReturn(user);

			final UserResource resource = new UserResource();
			final User response = resource.get(securityContext, config,
					daoFactory, timer, user.getId());

			assertNotNull(response);
			assertEquals(user, response);
		}
	}

	/**
	 * Test how the resource responds to retrieving all users when none are
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

			final UserResource resource = new UserResource();
			final Set<User> users = resource.all(securityContext, config,
					daoFactory, timer, true, null, null);

			assertNotNull(users);
			assertTrue(users.isEmpty());
		}
	}

	/**
	 * Test how the resource responds to retrieving all users.
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

			final User user1 = new User();
			user1.setCompanyId(company.getId());
			user1.setLogin("user1");
			user1.setHashedPass("hashed");
			user1.setSalt("salt");
			user1.setEmail("email1");
			user1.setFirstName("first");
			user1.setLastName("last");
			final User user2 = new User();
			user2.setCompanyId(company.getId());
			user2.setLogin("user2");
			user2.setHashedPass("hashed");
			user2.setSalt("salt");
			user2.setEmail("email2");
			user2.setFirstName("first");
			user2.setLastName("last");
			daoFactory.getUserDao().add(user1, user2);

			final SecurityContext securityContext = Mockito
					.mock(SecurityContext.class);
			Mockito.when(securityContext.getUserPrincipal()).thenReturn(user1);

			final UserResource resource = new UserResource();
			Set<User> users = resource.all(securityContext, config, daoFactory,
					timer, true, null, null);

			assertNotNull(users);
			assertEquals(2, users.size());
			assertTrue(users.contains(user1));
			assertTrue(users.contains(user2));

			users = resource.all(securityContext, config, daoFactory, timer,
					true, 2, 0);

			assertNotNull(users);
			assertEquals(2, users.size());
			assertTrue(users.contains(user1));
			assertTrue(users.contains(user2));

			users = resource.all(securityContext, config, daoFactory, timer,
					true, 1, 0);

			assertNotNull(users);
			assertEquals(1, users.size());
			assertTrue(users.contains(user1));

			users = resource.all(securityContext, config, daoFactory, timer,
					true, 1, 1);

			assertNotNull(users);
			assertEquals(1, users.size());
			assertTrue(users.contains(user2));
		}
	}

	/**
	 * Test how the resource responds to retrieving all users, with the inactive
	 * included parameter specified.
	 * 
	 * @throws DatabaseException
	 *             if there is a database problem
	 */
	@Test
	public void testAllWithIncludeInactive() throws DatabaseException {
		final Config config = new Config();
		try (final TestDatabase testDatabase = new TestDatabase()) {
			testDatabase.load("hsqldb/arctime-db.sql");
			final ArcTimeDaoFactory daoFactory = testDatabase.getDaoFactory();
			final MetricRegistry metricRegistry = new MetricRegistry();
			final Timer timer = metricRegistry.timer("test");

			final Company company = new Company().setName("company");
			daoFactory.getCompanyDao().add(company);

			final User active = new User();
			active.setCompanyId(company.getId());
			active.setLogin("active");
			active.setHashedPass("hashed");
			active.setSalt("salt");
			active.setEmail("active");
			active.setFirstName("first");
			active.setLastName("last");
			active.setActive(true);
			final User inactive = new User();
			inactive.setCompanyId(company.getId());
			inactive.setLogin("inactive");
			inactive.setHashedPass("hashed");
			inactive.setSalt("salt");
			inactive.setEmail("inactive");
			inactive.setFirstName("first");
			inactive.setLastName("last");
			inactive.setActive(false);
			daoFactory.getUserDao().add(active, inactive);

			final SecurityContext securityContext = Mockito
					.mock(SecurityContext.class);
			Mockito.when(securityContext.getUserPrincipal()).thenReturn(active);

			final UserResource resource = new UserResource();
			Set<User> users = resource.all(securityContext, config, daoFactory,
					timer, true, null, null);

			assertNotNull(users);
			assertEquals(2, users.size());
			assertTrue(users.contains(active));
			assertTrue(users.contains(inactive));

			users = resource.all(securityContext, config, daoFactory, timer,
					false, null, null);

			assertNotNull(users);
			assertEquals(1, users.size());
			assertTrue(users.contains(active));
		}
	}

	/**
	 * Test how the resource responds to adding a new user.
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

			final User user = new User();
			user.setCompanyId(company.getId());
			user.setLogin("user");
			user.setHashedPass("hashed");
			user.setSalt("salt");
			user.setEmail("email");
			user.setFirstName("first");
			user.setLastName("last");

			final SecurityContext securityContext = Mockito
					.mock(SecurityContext.class);
			Mockito.when(securityContext.getUserPrincipal()).thenReturn(user);

			final UserResource resource = new UserResource();
			final AddResponse response = resource.add(securityContext, config,
					daoFactory, timer, user);

			assertNotNull(response);
			assertTrue(response.success);
			assertNotNull(response.user);
			assertEquals(user, response.user);

			assertEquals(
					1,
					daoFactory.getUserDao()
							.getAll(company.getId(), true, null, null).size());
		}
	}

	/**
	 * Test how the resource responds to activating users.
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

			final User user = new User();
			user.setCompanyId(company.getId());
			user.setLogin("user");
			user.setHashedPass("hashed");
			user.setSalt("salt");
			user.setEmail("email");
			user.setFirstName("first");
			user.setLastName("last");
			user.setActive(false);
			daoFactory.getUserDao().add(user);

			final SecurityContext securityContext = Mockito
					.mock(SecurityContext.class);
			Mockito.when(securityContext.getUserPrincipal()).thenReturn(user);

			final UserResource resource = new UserResource();
			resource.activate(securityContext, config, daoFactory, timer,
					new IdSet(user.getId()));

			assertTrue(daoFactory.getUserDao().get(user.getId()).isActive());
		}
	}

	/**
	 * Test how the resource responds to deactivating users.
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

			final User user = new User();
			user.setCompanyId(company.getId());
			user.setLogin("user");
			user.setHashedPass("hashed");
			user.setSalt("salt");
			user.setEmail("email");
			user.setFirstName("first");
			user.setLastName("last");
			user.setActive(true);
			daoFactory.getUserDao().add(user);

			final SecurityContext securityContext = Mockito
					.mock(SecurityContext.class);
			Mockito.when(securityContext.getUserPrincipal()).thenReturn(user);

			final UserResource resource = new UserResource();
			resource.deactivate(securityContext, config, daoFactory, timer,
					new IdSet(user.getId()));

			assertFalse(daoFactory.getUserDao().get(user.getId()).isActive());
		}
	}

	/**
	 * Test how the resource responds to updating a user without an id.
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

			final User user = new User();
			user.setCompanyId(company.getId());
			user.setLogin("user");
			user.setHashedPass("hashed");
			user.setSalt("salt");
			user.setEmail("email");
			user.setFirstName("first");
			user.setLastName("last");

			final SecurityContext securityContext = Mockito
					.mock(SecurityContext.class);
			Mockito.when(securityContext.getUserPrincipal()).thenReturn(user);

			final UserResource resource = new UserResource();
			resource.update(securityContext, config, daoFactory, timer, user);
		}
	}

	/**
	 * Test how the resource responds to updating a user.
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

			final User user = new User();
			user.setCompanyId(company.getId());
			user.setLogin("user");
			user.setHashedPass("hashed");
			user.setSalt("salt");
			user.setEmail("email");
			user.setFirstName("first");
			user.setLastName("last");

			final UserDao dao = daoFactory.getUserDao();
			dao.add(user);

			final SecurityContext securityContext = Mockito
					.mock(SecurityContext.class);
			Mockito.when(securityContext.getUserPrincipal()).thenReturn(user);

			user.setFirstName("modified");

			final UserResource resource = new UserResource();
			final UpdateResponse response = resource.update(securityContext,
					config, daoFactory, timer, user);

			assertNotNull(response);
			assertTrue(response.success);
			assertNotNull(response.user);
			assertEquals(user, response.user);

			final User fromDb = dao.get(company.getId(), user.getId());
			assertEquals(user, fromDb);
		}
	}

	/**
	 * Test how the resource responds to deleting users.
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

			final User user = new User();
			user.setCompanyId(company.getId());
			user.setLogin("user");
			user.setHashedPass("hashed");
			user.setSalt("salt");
			user.setEmail("email");
			user.setFirstName("first");
			user.setLastName("last");
			daoFactory.getUserDao().add(user);

			final SecurityContext securityContext = Mockito
					.mock(SecurityContext.class);
			Mockito.when(securityContext.getUserPrincipal()).thenReturn(user);

			final UserResource resource = new UserResource();
			resource.delete(securityContext, config, daoFactory, timer,
					new IdSet(user.getId()));

			assertEquals(
					0,
					daoFactory.getUserDao()
							.getAll(company.getId(), true, null, null).size());
		}
	}
}
