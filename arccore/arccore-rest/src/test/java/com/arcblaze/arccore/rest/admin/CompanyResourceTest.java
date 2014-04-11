package com.arcblaze.arccore.rest.admin;

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
import com.arcblaze.arccore.db.DaoFactory;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arccore.db.util.TestDatabase;
import com.arcblaze.arccore.rest.admin.CompanyResource.ActivateResponse;
import com.arcblaze.arccore.rest.admin.CompanyResource.AddResponse;
import com.arcblaze.arccore.rest.admin.CompanyResource.DeactivateResponse;
import com.arcblaze.arccore.rest.admin.CompanyResource.UpdateResponse;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

/**
 * Perform testing of the company resource capabilities.
 */
public class CompanyResourceTest {
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
			testDatabase.load("hsqldb/db.sql");
			final User user = new User().setId(1).setCompanyId(1)
					.setLogin("user");
			final SecurityContext securityContext = Mockito
					.mock(SecurityContext.class);
			Mockito.when(securityContext.getUserPrincipal()).thenReturn(user);
			final DaoFactory daoFactory = testDatabase.getDaoFactory();
			final MetricRegistry metricRegistry = new MetricRegistry();
			final Timer timer = metricRegistry.timer("test");

			final CompanyResource resource = new CompanyResource();
			resource.get(securityContext, config, daoFactory, timer, null);
		}
	}

	/**
	 * Test how the resource responds to returning a valid company.
	 * 
	 * @throws DatabaseException
	 *             if there is a database problem
	 */
	@Test
	public void testGet() throws DatabaseException {
		final Config config = new Config();
		try (final TestDatabase testDatabase = new TestDatabase()) {
			testDatabase.load("hsqldb/db.sql");
			final DaoFactory daoFactory = testDatabase.getDaoFactory();
			final MetricRegistry metricRegistry = new MetricRegistry();
			final Timer timer = metricRegistry.timer("test");

			final Company company = new Company().setName("company").setActive(
					true);
			daoFactory.getCompanyDao().add(company);

			final User user = new User().setId(1).setCompanyId(1)
					.setLogin("user");
			final SecurityContext securityContext = Mockito
					.mock(SecurityContext.class);
			Mockito.when(securityContext.getUserPrincipal()).thenReturn(user);

			final CompanyResource resource = new CompanyResource();
			final Company response = resource.get(securityContext, config,
					daoFactory, timer, company.getId());

			assertNotNull(response);
			assertEquals(company, response);
		}
	}

	/**
	 * Test how the resource responds to retrieving all companies when none are
	 * available.
	 * 
	 * @throws DatabaseException
	 *             if there is a database problem
	 */
	@Test
	public void testAllNoneAvailable() throws DatabaseException {
		final Config config = new Config();
		try (final TestDatabase testDatabase = new TestDatabase()) {
			testDatabase.load("hsqldb/db.sql");
			final User user = new User().setId(1).setCompanyId(1)
					.setLogin("user");
			final SecurityContext securityContext = Mockito
					.mock(SecurityContext.class);
			Mockito.when(securityContext.getUserPrincipal()).thenReturn(user);
			final DaoFactory daoFactory = testDatabase.getDaoFactory();
			final MetricRegistry metricRegistry = new MetricRegistry();
			final Timer timer = metricRegistry.timer("test");

			final CompanyResource resource = new CompanyResource();
			final Set<Company> companies = resource.all(securityContext,
					config, daoFactory, timer);

			assertNotNull(companies);
			assertTrue(companies.isEmpty());
		}
	}

	/**
	 * Test how the resource responds to retrieving all companies.
	 * 
	 * @throws DatabaseException
	 *             if there is a database problem
	 */
	@Test
	public void testAll() throws DatabaseException {
		final Config config = new Config();
		try (final TestDatabase testDatabase = new TestDatabase()) {
			testDatabase.load("hsqldb/db.sql");
			final User user = new User().setId(1).setCompanyId(1)
					.setLogin("user");
			final SecurityContext securityContext = Mockito
					.mock(SecurityContext.class);
			Mockito.when(securityContext.getUserPrincipal()).thenReturn(user);
			final DaoFactory daoFactory = testDatabase.getDaoFactory();
			final MetricRegistry metricRegistry = new MetricRegistry();
			final Timer timer = metricRegistry.timer("test");

			final Company company = new Company().setName("company");
			daoFactory.getCompanyDao().add(company);

			final CompanyResource resource = new CompanyResource();
			final Set<Company> companies = resource.all(securityContext,
					config, daoFactory, timer);

			assertNotNull(companies);
			assertEquals(1, companies.size());
			assertTrue(companies.contains(company));
		}
	}

	/**
	 * Test how the resource responds to adding a new company.
	 * 
	 * @throws DatabaseException
	 *             if there is a database problem
	 */
	@Test
	public void testAdd() throws DatabaseException {
		final Config config = new Config();
		try (final TestDatabase testDatabase = new TestDatabase()) {
			testDatabase.load("hsqldb/db.sql");
			final User user = new User().setId(1).setCompanyId(1)
					.setLogin("user");
			final SecurityContext securityContext = Mockito
					.mock(SecurityContext.class);
			Mockito.when(securityContext.getUserPrincipal()).thenReturn(user);
			final DaoFactory daoFactory = testDatabase.getDaoFactory();
			final MetricRegistry metricRegistry = new MetricRegistry();
			final Timer timer = metricRegistry.timer("test");

			final Company company = new Company().setName("company");

			final CompanyResource resource = new CompanyResource();
			final AddResponse response = resource.add(securityContext, config,
					daoFactory, timer, company);

			assertNotNull(response);
			assertTrue(response.success);
			assertNotNull(response.company);
			assertEquals(company, response.company);

			assertEquals(1, daoFactory.getCompanyDao().getAll().size());
		}
	}

	/**
	 * Test how the resource responds to activating a company that isn't found.
	 * 
	 * @throws DatabaseException
	 *             if there is a database problem
	 */
	@Test
	public void testActivateNotFound() throws DatabaseException {
		final Config config = new Config();
		try (final TestDatabase testDatabase = new TestDatabase()) {
			testDatabase.load("hsqldb/db.sql");
			final User user = new User().setId(1).setCompanyId(1)
					.setLogin("user");
			final SecurityContext securityContext = Mockito
					.mock(SecurityContext.class);
			Mockito.when(securityContext.getUserPrincipal()).thenReturn(user);
			final DaoFactory daoFactory = testDatabase.getDaoFactory();
			final MetricRegistry metricRegistry = new MetricRegistry();
			final Timer timer = metricRegistry.timer("test");

			final CompanyResource resource = new CompanyResource();
			final ActivateResponse response = resource.activate(
					securityContext, config, daoFactory, timer, new IdSet(1));

			assertNotNull(response);
			assertTrue(response.success);
		}
	}

	/**
	 * Test how the resource responds to activating a company.
	 * 
	 * @throws DatabaseException
	 *             if there is a database problem
	 */
	@Test
	public void testActivate() throws DatabaseException {
		final Config config = new Config();
		try (final TestDatabase testDatabase = new TestDatabase()) {
			testDatabase.load("hsqldb/db.sql");
			final User user = new User().setId(1).setCompanyId(1)
					.setLogin("user");
			final SecurityContext securityContext = Mockito
					.mock(SecurityContext.class);
			Mockito.when(securityContext.getUserPrincipal()).thenReturn(user);
			final DaoFactory daoFactory = testDatabase.getDaoFactory();
			final MetricRegistry metricRegistry = new MetricRegistry();
			final Timer timer = metricRegistry.timer("test");

			final Company company = new Company().setName("company").setActive(
					false);
			daoFactory.getCompanyDao().add(company);

			final CompanyResource resource = new CompanyResource();
			final ActivateResponse response = resource.activate(
					securityContext, config, daoFactory, timer, new IdSet(
							company.getId()));

			assertNotNull(response);
			assertTrue(response.success);

			assertTrue(daoFactory.getCompanyDao().get(company.getId())
					.isActive());
		}
	}

	/**
	 * Test how the resource responds to deactivating a company that isn't
	 * found.
	 * 
	 * @throws DatabaseException
	 *             if there is a database problem
	 */
	@Test
	public void testDeactivateNotFound() throws DatabaseException {
		final Config config = new Config();
		try (final TestDatabase testDatabase = new TestDatabase()) {
			testDatabase.load("hsqldb/db.sql");
			final User user = new User().setId(1).setCompanyId(1)
					.setLogin("user");
			final SecurityContext securityContext = Mockito
					.mock(SecurityContext.class);
			Mockito.when(securityContext.getUserPrincipal()).thenReturn(user);
			final DaoFactory daoFactory = testDatabase.getDaoFactory();
			final MetricRegistry metricRegistry = new MetricRegistry();
			final Timer timer = metricRegistry.timer("test");

			final CompanyResource resource = new CompanyResource();
			final DeactivateResponse response = resource.deactivate(
					securityContext, config, daoFactory, timer, new IdSet(1));

			assertNotNull(response);
			assertTrue(response.success);
		}
	}

	/**
	 * Test how the resource responds to deactivating a company.
	 * 
	 * @throws DatabaseException
	 *             if there is a database problem
	 */
	@Test
	public void testDeactivate() throws DatabaseException {
		final Config config = new Config();
		try (final TestDatabase testDatabase = new TestDatabase()) {
			testDatabase.load("hsqldb/db.sql");
			final User user = new User().setId(1).setCompanyId(1)
					.setLogin("user");
			final SecurityContext securityContext = Mockito
					.mock(SecurityContext.class);
			Mockito.when(securityContext.getUserPrincipal()).thenReturn(user);
			final DaoFactory daoFactory = testDatabase.getDaoFactory();
			final MetricRegistry metricRegistry = new MetricRegistry();
			final Timer timer = metricRegistry.timer("test");

			final Company company = new Company().setName("company").setActive(
					true);
			daoFactory.getCompanyDao().add(company);

			final CompanyResource resource = new CompanyResource();
			final DeactivateResponse response = resource.deactivate(
					securityContext, config, daoFactory, timer, new IdSet(
							company.getId()));

			assertNotNull(response);
			assertTrue(response.success);

			assertFalse(daoFactory.getCompanyDao().get(company.getId())
					.isActive());
		}
	}

	/**
	 * Test how the resource responds to updating a null company.
	 * 
	 * @throws DatabaseException
	 *             if there is a database problem
	 */
	@Test(expected = BadRequestException.class)
	public void testUpdateNull() throws DatabaseException {
		final Config config = new Config();
		try (final TestDatabase testDatabase = new TestDatabase()) {
			testDatabase.load("hsqldb/db.sql");
			final User user = new User().setId(1).setCompanyId(1)
					.setLogin("user");
			final SecurityContext securityContext = Mockito
					.mock(SecurityContext.class);
			Mockito.when(securityContext.getUserPrincipal()).thenReturn(user);
			final DaoFactory daoFactory = testDatabase.getDaoFactory();
			final MetricRegistry metricRegistry = new MetricRegistry();
			final Timer timer = metricRegistry.timer("test");

			final CompanyResource resource = new CompanyResource();
			resource.update(securityContext, config, daoFactory, timer, null);
		}
	}

	/**
	 * Test how the resource responds to updating a company with no id.
	 * 
	 * @throws DatabaseException
	 *             if there is a database problem
	 */
	@Test(expected = BadRequestException.class)
	public void testUpdateNoId() throws DatabaseException {
		final Config config = new Config();
		try (final TestDatabase testDatabase = new TestDatabase()) {
			testDatabase.load("hsqldb/db.sql");
			final User user = new User().setId(1).setCompanyId(1)
					.setLogin("user");
			final SecurityContext securityContext = Mockito
					.mock(SecurityContext.class);
			Mockito.when(securityContext.getUserPrincipal()).thenReturn(user);
			final DaoFactory daoFactory = testDatabase.getDaoFactory();
			final MetricRegistry metricRegistry = new MetricRegistry();
			final Timer timer = metricRegistry.timer("test");

			final CompanyResource resource = new CompanyResource();
			resource.update(securityContext, config, daoFactory, timer,
					new Company().setName("company"));
		}
	}

	/**
	 * Test how the resource responds to updating a null company.
	 * 
	 * @throws DatabaseException
	 *             if there is a database problem
	 */
	@Test
	public void testUpdate() throws DatabaseException {
		final Config config = new Config();
		try (final TestDatabase testDatabase = new TestDatabase()) {
			testDatabase.load("hsqldb/db.sql");
			final User user = new User().setId(1).setCompanyId(1)
					.setLogin("user");
			final SecurityContext securityContext = Mockito
					.mock(SecurityContext.class);
			Mockito.when(securityContext.getUserPrincipal()).thenReturn(user);
			final DaoFactory daoFactory = testDatabase.getDaoFactory();
			final MetricRegistry metricRegistry = new MetricRegistry();
			final Timer timer = metricRegistry.timer("test");

			final Company company = new Company().setName("company");
			daoFactory.getCompanyDao().add(company);

			company.setName("new name");

			final CompanyResource resource = new CompanyResource();
			final UpdateResponse response = resource.update(securityContext,
					config, daoFactory, timer, company);

			assertNotNull(response);
			assertTrue(response.success);
			assertEquals(company, response.company);
			assertEquals("new name",
					daoFactory.getCompanyDao().get(company.getId()).getName());
		}
	}

	/**
	 * Test how the resource responds to deleting companies.
	 * 
	 * @throws DatabaseException
	 *             if there is a database problem
	 */
	@Test
	public void testDelete() throws DatabaseException {
		final Config config = new Config();
		try (final TestDatabase testDatabase = new TestDatabase()) {
			testDatabase.load("hsqldb/db.sql");
			final User user = new User().setId(1).setCompanyId(1)
					.setLogin("user");
			final SecurityContext securityContext = Mockito
					.mock(SecurityContext.class);
			Mockito.when(securityContext.getUserPrincipal()).thenReturn(user);
			final DaoFactory daoFactory = testDatabase.getDaoFactory();
			final MetricRegistry metricRegistry = new MetricRegistry();
			final Timer timer = metricRegistry.timer("test");

			final Company company = new Company().setName("company");
			daoFactory.getCompanyDao().add(company);

			final CompanyResource resource = new CompanyResource();
			resource.delete(securityContext, config, daoFactory, timer,
					new IdSet(company.getId()));

			assertEquals(0, daoFactory.getCompanyDao().getAll().size());
		}
	}
}
