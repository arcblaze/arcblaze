package com.arcblaze.arccore.db.dao.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Set;

import org.junit.Test;

import com.arcblaze.arccore.common.model.Company;
import com.arcblaze.arccore.common.model.User;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arccore.db.DatabaseUniqueConstraintException;
import com.arcblaze.arccore.db.dao.CompanyDao;
import com.arcblaze.arccore.db.dao.UserDao;
import com.arcblaze.arccore.db.util.TestDatabase;

/**
 * Perform database integration testing on the users table.
 */
public class JdbcUserDaoTest {
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

            final Company company = new Company();
            company.setName("Test Company");
            company.setActive(true);
            companyDao.add(company);
            assertNotNull(company.getId());

            Set<User> users = userDao.getAll(true, null, null);
            assertNotNull(users);
            assertEquals(0, users.size());

            assertEquals(0, userDao.count(true));

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
            user2.setActive(false);

            userDao.add(user1, user2);
            assertNotNull(user1.getId());
            assertNotNull(user2.getId());

            try {
                final User bad = new User();
                bad.setCompanyId(company.getId());
                bad.setLogin("user"); // same as other user
                bad.setHashedPass("hashed");
                bad.setEmail("email2");
                bad.setFirstName("first");
                bad.setLastName("last");
                userDao.add(bad);
                fail("No unique constraint was thrown");
            } catch (final DatabaseUniqueConstraintException notUnique) {
                // Expected
            }

            try {
                final User bad = new User();
                bad.setCompanyId(company.getId());
                bad.setLogin("user2");
                bad.setHashedPass("hashed");
                bad.setEmail("EMAIL"); // same as other user
                bad.setFirstName("first");
                bad.setLastName("last");
                userDao.add(bad);
                fail("No unique constraint was thrown");
            } catch (final DatabaseUniqueConstraintException notUnique) {
                // Expected
            }

            assertEquals(2, userDao.count(true));
            assertEquals(1, userDao.count(false));

            users = userDao.getAll(true, null, null);
            assertNotNull(users);
            assertEquals(2, users.size());
            assertTrue(users.contains(user1));
            assertTrue(users.contains(user2));

            users = userDao.getAll(true, 1, 0);
            assertNotNull(users);
            assertEquals(1, users.size());
            assertTrue(users.contains(user1));

            users = userDao.getAll(true, 1, 1);
            assertNotNull(users);
            assertEquals(1, users.size());
            assertTrue(users.contains(user2));

            users = userDao.getAll(company.getId(), true, null, null);
            assertNotNull(users);
            assertEquals(2, users.size());
            assertTrue(users.contains(user1));
            assertTrue(users.contains(user2));

            users = userDao.getAll(company.getId(), true, 1, 0);
            assertNotNull(users);
            assertEquals(1, users.size());
            assertTrue(users.contains(user1));

            users = userDao.getAll(company.getId(), true, 1, 1);
            assertNotNull(users);
            assertEquals(1, users.size());
            assertTrue(users.contains(user2));

            users = userDao.search(company.getId(), "user1", true, null, null);
            assertNotNull(users);
            assertEquals(1, users.size());
            assertTrue(users.contains(user1));

            users = userDao.search(company.getId(), "user", true, null, null);
            assertNotNull(users);
            assertEquals(2, users.size());
            assertTrue(users.contains(user1));
            assertTrue(users.contains(user2));

            users = userDao.search(company.getId(), "user", true, 1, 0);
            assertNotNull(users);
            assertEquals(1, users.size());
            assertTrue(users.contains(user1));

            users = userDao.search(company.getId(), "non-existent", true, null, null);
            assertNotNull(users);
            assertEquals(0, users.size());

            users = userDao.search("user1", true, null, null);
            assertNotNull(users);
            assertEquals(1, users.size());
            assertTrue(users.contains(user1));

            users = userDao.search("user", true, null, null);
            assertNotNull(users);
            assertEquals(2, users.size());
            assertTrue(users.contains(user1));
            assertTrue(users.contains(user2));

            users = userDao.search("user", true, 1, 0);
            assertNotNull(users);
            assertEquals(1, users.size());
            assertTrue(users.contains(user1));

            users = userDao.search("non-existent", true, null, null);
            assertNotNull(users);
            assertEquals(0, users.size());

            User getUser = userDao.get(user1.getId());
            assertEquals(user1, getUser);

            User loginUser = userDao.getLogin(user1.getLogin());
            assertEquals(user1, loginUser);

            loginUser = userDao.getLogin(user1.getEmail());
            assertEquals(user1, loginUser);

            loginUser = userDao.getLogin(user1.getEmail().toUpperCase());
            assertEquals(user1, loginUser);

            user1.setEmail("New Email");
            userDao.update(user1);
            getUser = userDao.get(user1.getId());
            assertEquals(user1, getUser);

            user1.setActive(false);
            userDao.update(user1);
            getUser = userDao.get(user1.getId());
            assertEquals(user1, getUser);

            assertEquals(2, userDao.count(true));
            assertEquals(0, userDao.count(false));
            assertEquals(2, userDao.getAll(company.getId(), true, null, null).size());
            assertEquals(0, userDao.getAll(company.getId(), false, null, null).size());

            userDao.activate(company.getId(), user1.getId());
            assertEquals(2, userDao.count(true));
            assertEquals(1, userDao.count(false));
            assertEquals(2, userDao.getAll(company.getId(), true, null, null).size());
            assertEquals(1, userDao.getAll(company.getId(), false, null, null).size());

            userDao.deactivate(company.getId(), user1.getId());
            assertEquals(2, userDao.count(true));
            assertEquals(0, userDao.count(false));
            assertEquals(2, userDao.getAll(company.getId(), true, null, null).size());
            assertEquals(0, userDao.getAll(company.getId(), false, null, null).size());

            userDao.delete(user1.getId(), user2.getId());
            getUser = userDao.get(user1.getId());
            assertNull(getUser);

            users = userDao.getAll(true, null, null);
            assertNotNull(users);
            assertEquals(0, users.size());
            assertEquals(0, userDao.count(true));
        }
    }
}
