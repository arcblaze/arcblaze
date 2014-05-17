package com.arcblaze.arccore.db.dao.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

import com.arcblaze.arccore.common.model.Company;
import com.arcblaze.arccore.common.model.Role;
import com.arcblaze.arccore.common.model.User;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arccore.db.dao.CompanyDao;
import com.arcblaze.arccore.db.dao.RoleDao;
import com.arcblaze.arccore.db.dao.UserDao;
import com.arcblaze.arccore.db.util.TestDatabase;

/**
 * Perform database integration testing on the roles table.
 */
public class JdbcRoleDaoTest {
    /**
     * @throws DatabaseException
     *             if there is a problem with the database
     */
    @Test
    public void dbIntegrationTests() throws DatabaseException {
        try (final TestDatabase database = new TestDatabase()) {
            database.load("hsqldb/db.sql");

            final CompanyDao companyDao = new JdbcCompanyDao(database.getConnectionManager());
            final UserDao userDao = new JdbcUserDao(database.getConnectionManager());
            final RoleDao roleDao = new JdbcRoleDao(database.getConnectionManager());

            final Company company = new Company();
            company.setName("Test Company");
            company.setActive(true);
            companyDao.add(company);
            assertNotNull(company.getId());

            final User user = new User();
            user.setCompanyId(company.getId());
            user.setLogin("user");
            user.setHashedPass("hashed");
            user.setSalt("salt");
            user.setEmail("email");
            user.setFirstName("first");
            user.setLastName("last");

            userDao.add(user);
            assertNotNull(user.getId());

            Set<Role> roles = roleDao.get(user.getId());
            assertNotNull(roles);
            assertEquals(0, roles.size());

            final Role roleA = new Role("A");
            final Role roleB = new Role("B");

            roleDao.add(user.getId(), roleA, roleB);

            roles = roleDao.get(user.getId());
            assertNotNull(roles);
            assertEquals(2, roles.size());
            assertTrue(roles.contains(roleA));
            assertTrue(roles.contains(roleB));

            roleDao.populateUsers(user);
            assertEquals(2, user.getRoles().size());
            assertTrue(user.getRoles().contains(roleA));
            assertTrue(user.getRoles().contains(roleB));

            roleDao.delete(user.getId(), roleA);

            roles = roleDao.get(user.getId());
            assertNotNull(roles);
            assertEquals(1, roles.size());
            assertTrue(roles.contains(roleB));

            roleDao.delete(user.getId());

            roles = roleDao.get(user.getId());
            assertNotNull(roles);
            assertEquals(0, roles.size());
        }
    }
}
