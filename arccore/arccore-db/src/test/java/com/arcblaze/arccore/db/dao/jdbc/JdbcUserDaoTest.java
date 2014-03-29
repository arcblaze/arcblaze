package com.arcblaze.arccore.db.dao.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Set;

import org.junit.Test;

import com.arcblaze.arccore.common.model.User;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arccore.db.DatabaseUniqueConstraintException;
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

			final UserDao userDao = new JdbcUserDao(
					database.getConnectionManager());

			Set<User> users = userDao.getAll();
			assertNotNull(users);
			assertEquals(0, users.size());

			final User user = new User();
			user.setLogin("user");
			user.setHashedPass("hashed");
			user.setSalt("salt");
			user.setEmail("email");
			user.setFirstName("first");
			user.setLastName("last");

			userDao.add(user);
			assertNotNull(user.getId());

			try {
				final User bad = new User();
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

			users = userDao.getAll();
			assertNotNull(users);
			assertEquals(1, users.size());
			assertTrue(users.contains(user));

			User getAllUser = null;
			for (final User fromDb : users)
				if (fromDb.getId() == user.getId())
					getAllUser = fromDb;
			assertNotNull(getAllUser);
			assertEquals(user, getAllUser);

			User getUser = userDao.get(user.getId());
			assertEquals(user, getUser);

			User loginUser = userDao.getLogin(user.getLogin());
			assertEquals(user, loginUser);

			loginUser = userDao.getLogin(user.getEmail());
			assertEquals(user, loginUser);

			loginUser = userDao.getLogin(user.getEmail().toUpperCase());
			assertEquals(user, loginUser);

			user.setEmail("New Email");
			userDao.update(user);
			getUser = userDao.get(user.getId());
			assertEquals(user, getUser);

			userDao.delete(user.getId());
			getUser = userDao.get(user.getId());
			assertNull(getUser);

			users = userDao.getAll();
			assertNotNull(users);
			assertEquals(0, users.size());
		}
	}
}