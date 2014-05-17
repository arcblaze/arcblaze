package com.arcblaze.arctime.rest.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.ws.rs.core.SecurityContext;

import org.junit.Test;
import org.mockito.Mockito;

import com.arcblaze.arccore.common.config.Config;
import com.arcblaze.arccore.common.model.Company;
import com.arcblaze.arccore.common.model.User;
import com.arcblaze.arccore.db.DaoFactory;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arccore.db.util.TestDatabase;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

/**
 * Perform testing of the company resource capabilities.
 */
public class CompanyResourceTest {
    /**
     * Test how the resource responds to returning the current user's company.
     * 
     * @throws DatabaseException
     *             if there is a database problem
     */
    @Test
    public void testMineNoCompanies() throws DatabaseException {
        final Config config = new Config();
        try (final TestDatabase testDatabase = new TestDatabase()) {
            testDatabase.load("hsqldb/arctime-db.sql");
            final User user = new User().setId(1).setCompanyId(1).setLogin("user");
            final SecurityContext securityContext = Mockito.mock(SecurityContext.class);
            Mockito.when(securityContext.getUserPrincipal()).thenReturn(user);
            final DaoFactory daoFactory = testDatabase.getDaoFactory();
            final MetricRegistry metricRegistry = new MetricRegistry();
            final Timer timer = metricRegistry.timer("test");

            final CompanyResource resource = new CompanyResource();
            final Company company = resource.mine(securityContext, config, daoFactory, timer);

            assertNull(company);
        }
    }

    /**
     * Test how the resource responds to returning the current user's company.
     * 
     * @throws DatabaseException
     *             if there is a database problem
     */
    @Test
    public void testMine() throws DatabaseException {
        final Config config = new Config();
        try (final TestDatabase testDatabase = new TestDatabase()) {
            testDatabase.load("hsqldb/arctime-db.sql");
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

            final SecurityContext securityContext = Mockito.mock(SecurityContext.class);
            Mockito.when(securityContext.getUserPrincipal()).thenReturn(user);

            final CompanyResource resource = new CompanyResource();
            final Company mine = resource.mine(securityContext, config, daoFactory, timer);

            assertNotNull(mine);
            assertEquals(company, mine);
        }
    }
}
