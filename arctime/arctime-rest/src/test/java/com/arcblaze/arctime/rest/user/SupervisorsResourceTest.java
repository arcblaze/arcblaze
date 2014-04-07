package com.arcblaze.arctime.rest.user;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.ws.rs.core.SecurityContext;

import org.junit.Test;
import org.mockito.Mockito;

import com.arcblaze.arccore.common.config.Config;
import com.arcblaze.arccore.common.model.Company;
import com.arcblaze.arccore.common.model.User;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arctime.db.ArcTimeDaoFactory;
import com.arcblaze.arctime.db.dao.SupervisorDao;
import com.arcblaze.arctime.db.util.TestDatabase;
import com.arcblaze.arctime.rest.user.SupervisorsResource.Supervisors;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

/**
 * Perform testing of the profile update capabilities.
 */
public class SupervisorsResourceTest {
	/**
	 * Test how the resource responds with no updated password specified.
	 * 
	 * @throws DatabaseException
	 *             if there is a database issue
	 */
	@Test
	public void testNoSupervisors() throws DatabaseException {
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

			final SupervisorsResource resource = new SupervisorsResource();
			final Supervisors supervisors = resource.get(security, config,
					daoFactory, timer);

			assertNotNull(supervisors);
			assertNotNull(supervisors.supervisors);
			assertTrue(supervisors.supervisors.isEmpty());
		}
	}

	/**
	 * Test how the resource responds with no updated password specified.
	 * 
	 * @throws DatabaseException
	 *             if there is a database issue
	 */
	@Test
	public void testMultipleSupervisors() throws DatabaseException {
		final Config config = new Config();
		try (final TestDatabase testDatabase = new TestDatabase()) {
			testDatabase.load("hsqldb/arctime-db.sql");
			final ArcTimeDaoFactory daoFactory = testDatabase.getDaoFactory();
			final MetricRegistry metricRegistry = new MetricRegistry();
			final Timer timer = metricRegistry.timer("test");

			final Company company = new Company().setName("company").setActive(
					true);
			daoFactory.getCompanyDao().add(company);

			final User supervisor1 = new User();
			supervisor1.setCompanyId(company.getId());
			supervisor1.setLogin("s1");
			supervisor1.setHashedPass("hashed");
			supervisor1.setSalt("salt");
			supervisor1.setEmail("s1@whatever.com");
			supervisor1.setFirstName("first");
			supervisor1.setLastName("last");
			supervisor1.setActive(true);

			final User supervisor2 = new User();
			supervisor2.setCompanyId(company.getId());
			supervisor2.setLogin("s2");
			supervisor2.setHashedPass("hashed");
			supervisor2.setSalt("salt");
			supervisor2.setEmail("s2@whatever.com");
			supervisor2.setFirstName("first");
			supervisor2.setLastName("last");
			supervisor2.setActive(true);

			final User user = new User();
			user.setCompanyId(company.getId());
			user.setLogin("user");
			user.setHashedPass("hashed");
			user.setSalt("salt");
			user.setEmail("email@whatever.com");
			user.setFirstName("first");
			user.setLastName("last");
			user.setActive(true);

			daoFactory.getUserDao().add(supervisor1, supervisor2, user);

			final SupervisorDao supervisorDao = daoFactory.getSupervisorDao();
			supervisorDao.add(company.getId(), user.getId(), true,
					supervisor1.getId());
			supervisorDao.add(company.getId(), user.getId(), false,
					supervisor2.getId());

			final SecurityContext security = Mockito
					.mock(SecurityContext.class);
			Mockito.when(security.getUserPrincipal()).thenReturn(user);

			final SupervisorsResource resource = new SupervisorsResource();
			final Supervisors response = resource.get(security, config,
					daoFactory, timer);

			assertNotNull(response);
			assertNotNull(response.supervisors);
			assertEquals(2, response.supervisors.size());
			assertTrue(response.supervisors.contains(supervisor1));
			assertTrue(response.supervisors.contains(supervisor2));
		}
	}
}
