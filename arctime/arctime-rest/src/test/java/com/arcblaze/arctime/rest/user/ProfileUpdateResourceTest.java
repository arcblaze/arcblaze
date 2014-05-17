package com.arcblaze.arctime.rest.user;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.SecurityContext;

import org.junit.Test;
import org.mockito.Mockito;

import com.arcblaze.arccore.common.config.Config;
import com.arcblaze.arccore.common.model.Company;
import com.arcblaze.arccore.common.model.Password;
import com.arcblaze.arccore.common.model.User;
import com.arcblaze.arccore.db.DaoFactory;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arccore.db.dao.UserDao;
import com.arcblaze.arccore.db.util.TestDatabase;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

/**
 * Perform testing of the profile update capabilities.
 */
public class ProfileUpdateResourceTest {
    /**
     * Test how the resource responds when the provided parameters are invalid.
     */
    @Test(expected = BadRequestException.class)
    public void testInvalidParameters1() {
        final Config config = new Config();
        try (final TestDatabase testDatabase = new TestDatabase()) {
            final User user = new User().setId(1).setCompanyId(1).setLogin("user");
            final SecurityContext securityContext = Mockito.mock(SecurityContext.class);
            Mockito.when(securityContext.getUserPrincipal()).thenReturn(user);
            final DaoFactory daoFactory = testDatabase.getDaoFactory();
            final Password password = new Password();
            final MetricRegistry metricRegistry = new MetricRegistry();
            final Timer timer = metricRegistry.timer("test");

            final ProfileUpdateResource resource = new ProfileUpdateResource();
            resource.update(securityContext, config, daoFactory, password, timer, "", "a", "b", "c", "d");
        }
    }

    /**
     * Test how the resource responds when the provided parameters are invalid.
     */
    @Test(expected = BadRequestException.class)
    public void testInvalidParameters2() {
        final Config config = new Config();
        try (final TestDatabase testDatabase = new TestDatabase()) {
            final User user = new User().setId(1).setCompanyId(1).setLogin("user");
            final SecurityContext securityContext = Mockito.mock(SecurityContext.class);
            Mockito.when(securityContext.getUserPrincipal()).thenReturn(user);
            final DaoFactory daoFactory = testDatabase.getDaoFactory();
            final Password password = new Password();
            final MetricRegistry metricRegistry = new MetricRegistry();
            final Timer timer = metricRegistry.timer("test");

            final ProfileUpdateResource resource = new ProfileUpdateResource();
            resource.update(securityContext, config, daoFactory, password, timer, "a", "", "b", "c", "d");
        }
    }

    /**
     * Test how the resource responds when the provided parameters are invalid.
     */
    @Test(expected = BadRequestException.class)
    public void testInvalidParameters3() {
        final Config config = new Config();
        try (final TestDatabase testDatabase = new TestDatabase()) {
            final User user = new User().setId(1).setCompanyId(1).setLogin("user");
            final SecurityContext securityContext = Mockito.mock(SecurityContext.class);
            Mockito.when(securityContext.getUserPrincipal()).thenReturn(user);
            final DaoFactory daoFactory = testDatabase.getDaoFactory();
            final Password password = new Password();
            final MetricRegistry metricRegistry = new MetricRegistry();
            final Timer timer = metricRegistry.timer("test");

            final ProfileUpdateResource resource = new ProfileUpdateResource();
            resource.update(securityContext, config, daoFactory, password, timer, "a", "b", "", "c", "d");
        }
    }

    /**
     * Test how the resource responds when the provided parameters are invalid.
     */
    @Test(expected = BadRequestException.class)
    public void testInvalidParameters4() {
        final Config config = new Config();
        try (final TestDatabase testDatabase = new TestDatabase()) {
            final User user = new User().setId(1).setCompanyId(1).setLogin("user");
            final SecurityContext securityContext = Mockito.mock(SecurityContext.class);
            Mockito.when(securityContext.getUserPrincipal()).thenReturn(user);
            final DaoFactory daoFactory = testDatabase.getDaoFactory();
            final Password password = new Password();
            final MetricRegistry metricRegistry = new MetricRegistry();
            final Timer timer = metricRegistry.timer("test");

            final ProfileUpdateResource resource = new ProfileUpdateResource();
            resource.update(securityContext, config, daoFactory, password, timer, "a", "b", "c", "", "d");
        }
    }

    /**
     * Test how the resource responds when the provided parameters are invalid.
     */
    @Test(expected = BadRequestException.class)
    public void testInvalidParameters5() {
        final Config config = new Config();
        try (final TestDatabase testDatabase = new TestDatabase()) {
            final User user = new User().setId(1).setCompanyId(1).setLogin("user");
            final SecurityContext securityContext = Mockito.mock(SecurityContext.class);
            Mockito.when(securityContext.getUserPrincipal()).thenReturn(user);
            final DaoFactory daoFactory = testDatabase.getDaoFactory();
            final Password password = new Password();
            final MetricRegistry metricRegistry = new MetricRegistry();
            final Timer timer = metricRegistry.timer("test");

            final ProfileUpdateResource resource = new ProfileUpdateResource();
            resource.update(securityContext, config, daoFactory, password, timer, "a", "b", null, "c", null);
        }
    }

    /**
     * Test how the resource responds with no updated password specified.
     * 
     * @throws DatabaseException
     *             if there is a database issue
     */
    @Test
    public void testValidBlankPassword() throws DatabaseException {
        final Config config = new Config();
        try (final TestDatabase testDatabase = new TestDatabase()) {
            testDatabase.load("hsqldb/arctime-db.sql");
            final SecurityContext securityContext = Mockito.mock(SecurityContext.class);
            final DaoFactory daoFactory = testDatabase.getDaoFactory();
            final Password password = new Password();
            final MetricRegistry metricRegistry = new MetricRegistry();
            final Timer timer = metricRegistry.timer("test");

            final Company company = new Company().setName("company").setActive(true);
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

            final UserDao userDao = daoFactory.getUserDao();
            userDao.add(user);

            Mockito.when(securityContext.getUserPrincipal()).thenReturn(user);

            final ProfileUpdateResource resource = new ProfileUpdateResource();
            resource.update(securityContext, config, daoFactory, password, timer, "a", "b", "c", "d", null);

            final User updated = userDao.getLogin("d");
            assertEquals(user.getId(), updated.getId());
            assertEquals(company.getId(), updated.getCompanyId());
            assertEquals("c", updated.getLogin());
            assertEquals("d", updated.getEmail());
            assertEquals("a", updated.getFirstName());
            assertEquals("b", updated.getLastName());
            assertEquals(user.getHashedPass(), updated.getHashedPass());
        }
    }

    /**
     * Test how the resource responds with a valid password.
     * 
     * @throws DatabaseException
     *             if there is a database issue
     */
    @Test
    public void testValidWithPassword() throws DatabaseException {
        final Config config = new Config();
        try (final TestDatabase testDatabase = new TestDatabase()) {
            testDatabase.load("hsqldb/arctime-db.sql");
            final SecurityContext securityContext = Mockito.mock(SecurityContext.class);
            final DaoFactory daoFactory = testDatabase.getDaoFactory();
            final MetricRegistry metricRegistry = new MetricRegistry();
            final Timer timer = metricRegistry.timer("test");

            final Company company = new Company().setName("company").setActive(true);
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

            final UserDao userDao = daoFactory.getUserDao();
            userDao.add(user);

            Mockito.when(securityContext.getUserPrincipal()).thenReturn(user);

            Password mockPassword = Mockito.mock(Password.class);
            Mockito.when(mockPassword.random(10)).thenReturn("new-salt");
            Mockito.when(mockPassword.hash("password", "new-salt")).thenReturn("hashed-password");

            final ProfileUpdateResource resource = new ProfileUpdateResource();
            resource.update(securityContext, config, daoFactory, mockPassword, timer, "a", "b", "c", "d", "password");

            final User updated = userDao.getLogin("c");
            assertEquals(user.getId(), updated.getId());
            assertEquals(company.getId(), updated.getCompanyId());
            assertEquals("c", updated.getLogin());
            assertEquals("d", updated.getEmail());
            assertEquals("a", updated.getFirstName());
            assertEquals("b", updated.getLastName());

            // Password should be an updated value.
            assertEquals("hashed-password", updated.getHashedPass());
        }
    }

    /**
     * Test how the resource responds when the updated user login value already exists.
     * 
     * @throws DatabaseException
     *             if there is a database issue
     */
    @Test(expected = BadRequestException.class)
    public void testLoginAlreadyExists() throws DatabaseException {
        final Config config = new Config();
        try (final TestDatabase testDatabase = new TestDatabase()) {
            testDatabase.load("hsqldb/arctime-db.sql");
            final SecurityContext securityContext = Mockito.mock(SecurityContext.class);
            final DaoFactory daoFactory = testDatabase.getDaoFactory();
            final Password password = new Password();
            final MetricRegistry metricRegistry = new MetricRegistry();
            final Timer timer = metricRegistry.timer("test");

            final Company company = new Company().setName("company").setActive(true);
            daoFactory.getCompanyDao().add(company);

            final User existing = new User();
            existing.setCompanyId(company.getId());
            existing.setLogin("existing");
            existing.setHashedPass("hashed");
            existing.setSalt("salt");
            existing.setEmail("existing@whatever.com");
            existing.setFirstName("first");
            existing.setLastName("last");
            existing.setActive(true);

            final User user = new User();
            user.setCompanyId(company.getId());
            user.setLogin("user");
            user.setHashedPass("hashed");
            user.setSalt("salt");
            user.setEmail("email@whatever.com");
            user.setFirstName("first");
            user.setLastName("last");
            user.setActive(true);

            daoFactory.getUserDao().add(existing, user);

            Mockito.when(securityContext.getUserPrincipal()).thenReturn(user);

            final ProfileUpdateResource resource = new ProfileUpdateResource();
            resource.update(securityContext, config, daoFactory, password, timer, "a", "b", "existing", "e", "password");
        }
    }
}
