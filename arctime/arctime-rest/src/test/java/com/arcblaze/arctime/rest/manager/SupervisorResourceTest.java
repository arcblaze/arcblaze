package com.arcblaze.arctime.rest.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.SecurityContext;

import org.junit.Test;
import org.mockito.Mockito;

import com.arcblaze.arccore.common.config.Config;
import com.arcblaze.arccore.common.model.Company;
import com.arcblaze.arccore.common.model.User;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arctime.common.model.Supervisor;
import com.arcblaze.arctime.db.ArcTimeDaoFactory;
import com.arcblaze.arctime.db.util.TestDatabase;
import com.arcblaze.arctime.rest.manager.SupervisorResource.AddResponse;
import com.arcblaze.arctime.rest.manager.SupervisorResource.DeleteResponse;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

/**
 * Perform testing of the user resource capabilities.
 */
public class SupervisorResourceTest {
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

			final SupervisorResource resource = new SupervisorResource();
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
			user.setEmail("user");
			user.setFirstName("first");
			user.setLastName("last");
			final User spr1 = new User();
			spr1.setCompanyId(company.getId());
			spr1.setLogin("spr1");
			spr1.setHashedPass("hashed");
			spr1.setSalt("salt");
			spr1.setEmail("spr1");
			spr1.setFirstName("first");
			spr1.setLastName("last");
			final User spr2 = new User();
			spr2.setCompanyId(company.getId());
			spr2.setLogin("spr2");
			spr2.setHashedPass("hashed");
			spr2.setSalt("salt");
			spr2.setEmail("spr2");
			spr2.setFirstName("first");
			spr2.setLastName("last");
			daoFactory.getUserDao().add(user, spr1, spr2);

			daoFactory.getSupervisorDao().add(company.getId(), user.getId(),
					true, spr1.getId());
			daoFactory.getSupervisorDao().add(company.getId(), user.getId(),
					false, spr2.getId());

			final SecurityContext securityContext = Mockito
					.mock(SecurityContext.class);
			Mockito.when(securityContext.getUserPrincipal()).thenReturn(user);

			final SupervisorResource resource = new SupervisorResource();
			final Set<Supervisor> supervisors = resource.get(securityContext,
					config, daoFactory, timer, user.getId());

			assertNotNull(supervisors);
			assertEquals(2, supervisors.size());

			final Iterator<Supervisor> superIter = supervisors.iterator();
			Supervisor supervisor = superIter.next();
			assertEquals(spr1, supervisor);
			assertTrue(supervisor.isPrimary());
			supervisor = superIter.next();
			assertEquals(spr2, supervisor);
			assertFalse(supervisor.isPrimary());
		}
	}

	/**
	 * Test how the resource responds to adding a non-existent supervisor.
	 * 
	 * @throws DatabaseException
	 *             if there is a database problem
	 */
	@Test
	public void testAddNonExistent() throws DatabaseException {
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
			user.setEmail("user");
			user.setFirstName("first");
			user.setLastName("last");
			daoFactory.getUserDao().add(user);

			final SecurityContext securityContext = Mockito
					.mock(SecurityContext.class);
			Mockito.when(securityContext.getUserPrincipal()).thenReturn(user);

			final SupervisorResource resource = new SupervisorResource();
			final AddResponse response = resource.add(securityContext, config,
					daoFactory, timer, user.getId(), 1234, true);

			assertNotNull(response);
			assertTrue(response.success);

			final Set<Supervisor> supervisors = daoFactory.getSupervisorDao()
					.getSupervisors(company.getId(), user.getId());
			assertEquals(0, supervisors.size());
		}
	}

	/**
	 * Test how the resource responds to adding an existing supervisor.
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

			final Company company = new Company().setName("company").setActive(
					true);
			daoFactory.getCompanyDao().add(company);

			final User user = new User();
			user.setCompanyId(company.getId());
			user.setLogin("user");
			user.setHashedPass("hashed");
			user.setSalt("salt");
			user.setEmail("user");
			user.setFirstName("first");
			user.setLastName("last");
			final User spr = new User();
			spr.setCompanyId(company.getId());
			spr.setLogin("spr");
			spr.setHashedPass("hashed");
			spr.setSalt("salt");
			spr.setEmail("spr");
			spr.setFirstName("first");
			spr.setLastName("last");
			daoFactory.getUserDao().add(user, spr);

			final SecurityContext securityContext = Mockito
					.mock(SecurityContext.class);
			Mockito.when(securityContext.getUserPrincipal()).thenReturn(user);

			final SupervisorResource resource = new SupervisorResource();
			final AddResponse response = resource.add(securityContext, config,
					daoFactory, timer, user.getId(), spr.getId(), true);

			assertNotNull(response);
			assertTrue(response.success);

			final Set<Supervisor> supervisors = daoFactory.getSupervisorDao()
					.getSupervisors(company.getId(), user.getId());
			assertEquals(1, supervisors.size());
			assertEquals(spr.getId(), supervisors.iterator().next().getId());
		}
	}

	/**
	 * Test how the resource responds to deleting a non-existent supervisor.
	 * 
	 * @throws DatabaseException
	 *             if there is a database problem
	 */
	@Test
	public void testDeleteNonExistent() throws DatabaseException {
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
			user.setEmail("user");
			user.setFirstName("first");
			user.setLastName("last");
			daoFactory.getUserDao().add(user);

			final SecurityContext securityContext = Mockito
					.mock(SecurityContext.class);
			Mockito.when(securityContext.getUserPrincipal()).thenReturn(user);

			final SupervisorResource resource = new SupervisorResource();
			final DeleteResponse response = resource.delete(securityContext,
					config, daoFactory, timer, user.getId(),
					Collections.singleton(1234));

			assertNotNull(response);
			assertTrue(response.success);

			final Set<Supervisor> supervisors = daoFactory.getSupervisorDao()
					.getSupervisors(company.getId(), user.getId());
			assertEquals(0, supervisors.size());
		}
	}

	/**
	 * Test how the resource responds to deleting a supervisor.
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

			final Company company = new Company().setName("company").setActive(
					true);
			daoFactory.getCompanyDao().add(company);

			final User user = new User();
			user.setCompanyId(company.getId());
			user.setLogin("user");
			user.setHashedPass("hashed");
			user.setSalt("salt");
			user.setEmail("user");
			user.setFirstName("first");
			user.setLastName("last");
			final User spr = new User();
			spr.setCompanyId(company.getId());
			spr.setLogin("spr");
			spr.setHashedPass("hashed");
			spr.setSalt("salt");
			spr.setEmail("spr");
			spr.setFirstName("first");
			spr.setLastName("last");

			daoFactory.getUserDao().add(user, spr);
			daoFactory.getSupervisorDao().add(company.getId(), user.getId(),
					true, spr.getId());

			final SecurityContext securityContext = Mockito
					.mock(SecurityContext.class);
			Mockito.when(securityContext.getUserPrincipal()).thenReturn(user);

			final SupervisorResource resource = new SupervisorResource();
			final DeleteResponse response = resource.delete(securityContext,
					config, daoFactory, timer, user.getId(),
					Collections.singleton(spr.getId()));

			assertNotNull(response);
			assertTrue(response.success);

			final Set<Supervisor> supervisors = daoFactory.getSupervisorDao()
					.getSupervisors(company.getId(), user.getId());
			assertEquals(0, supervisors.size());
		}
	}
}
