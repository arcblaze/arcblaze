package com.arcblaze.arccore.rest.login;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import javax.mail.MessagingException;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;

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
import com.arcblaze.arccore.mail.sender.ResetPasswordMailSender;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

/**
 * Perform testing of the password reset capabilities.
 */
public class ResetPasswordResourceTest {
	/**
	 * Test how the resource responds when the provided login value is null.
	 */
	@Test(expected = BadRequestException.class)
	public void testNullUser() {
		final Config config = new Config();
		try (final TestDatabase testDatabase = new TestDatabase()) {
			final DaoFactory daoFactory = testDatabase.getDaoFactory();
			final Password password = new Password();
			final MetricRegistry metricRegistry = new MetricRegistry();
			final Timer timer = metricRegistry.timer("test");

			final ResetPasswordResource resource = new ResetPasswordResource();
			resource.reset(config, daoFactory, password, timer, null);
		}
	}

	/**
	 * Test how the resource responds when the provided login value is blank.
	 */
	@Test(expected = BadRequestException.class)
	public void testBlankUser() {
		final Config config = new Config();
		try (final TestDatabase testDatabase = new TestDatabase()) {
			final DaoFactory daoFactory = testDatabase.getDaoFactory();
			final Password password = new Password();
			final MetricRegistry metricRegistry = new MetricRegistry();
			final Timer timer = metricRegistry.timer("test");

			final ResetPasswordResource resource = new ResetPasswordResource();
			resource.reset(config, daoFactory, password, timer, "  ");
		}
	}

	/**
	 * Test how the resource responds when the user doesn't exist.
	 * 
	 * @throws DatabaseException
	 *             if there is a database problem
	 */
	@Test(expected = NotFoundException.class)
	public void testNonExistentUser() throws DatabaseException {
		final Config config = new Config();
		try (final TestDatabase testDatabase = new TestDatabase()) {
			testDatabase.load("hsqldb/db.sql");
			final DaoFactory daoFactory = testDatabase.getDaoFactory();
			final Password password = new Password();
			final MetricRegistry metricRegistry = new MetricRegistry();
			final Timer timer = metricRegistry.timer("test");

			final ResetPasswordResource resource = new ResetPasswordResource();
			resource.reset(config, daoFactory, password, timer, "non-existent");
		}
	}

	/**
	 * Test how the resource responds when an existing login is provided as
	 * input.
	 * 
	 * @throws DatabaseException
	 *             if there is a database problem
	 */
	@Test
	public void testExistingUserByLogin() throws DatabaseException {
		final Config config = new Config();
		try (final TestDatabase testDatabase = new TestDatabase()) {
			testDatabase.load("hsqldb/db.sql");
			final DaoFactory daoFactory = testDatabase.getDaoFactory();
			final MetricRegistry metricRegistry = new MetricRegistry();
			final Timer timer = metricRegistry.timer("test");

			final Company company = new Company();
			company.setName("company");
			company.setActive(true);

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

			final ResetPasswordMailSender mockMailSender = Mockito
					.mock(ResetPasswordMailSender.class);

			final Password mockPassword = Mockito.mock(Password.class);
			Mockito.when(mockPassword.random()).thenReturn("new-password");
			Mockito.when(mockPassword.random(10)).thenReturn("new-salt");
			Mockito.when(mockPassword.hash("new-password", "new-salt"))
					.thenReturn("hashed-password");

			final ResetPasswordResource resource = new ResetPasswordResource(
					mockMailSender);
			resource.reset(config, daoFactory, mockPassword, timer,
					user.getLogin());

			// Make sure the password was updated.
			final User updated = userDao.getLogin(user.getLogin());
			assertEquals("hashed-password", updated.getHashedPass());
			assertEquals("new-salt", updated.getSalt());
		}
	}

	/**
	 * Test how the resource responds when an existing email address is provided
	 * as input.
	 * 
	 * @throws DatabaseException
	 *             if there is a database problem
	 */
	@Test
	public void testExistingUserByEmail() throws DatabaseException {
		final Config config = new Config();
		try (final TestDatabase testDatabase = new TestDatabase()) {
			testDatabase.load("hsqldb/db.sql");
			final DaoFactory daoFactory = testDatabase.getDaoFactory();
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

			final UserDao userDao = daoFactory.getUserDao();
			userDao.add(user);

			final ResetPasswordMailSender mockMailSender = Mockito
					.mock(ResetPasswordMailSender.class);

			final Password mockPassword = Mockito.mock(Password.class);
			Mockito.when(mockPassword.random()).thenReturn("new-password");
			Mockito.when(mockPassword.random(10)).thenReturn("new-salt");
			Mockito.when(mockPassword.hash("new-password", "new-salt"))
					.thenReturn("hashed-password");

			ResetPasswordResource resource = new ResetPasswordResource(
					mockMailSender);
			resource.reset(config, daoFactory, mockPassword, timer,
					user.getEmail());

			// Make sure the password was updated.
			final User updated = userDao.getLogin(user.getLogin());
			assertEquals("hashed-password", updated.getHashedPass());
			assertEquals("new-salt", updated.getSalt());
		}
	}

	/**
	 * Test how the resource responds when there is a problem sending the email.
	 * 
	 * @throws DatabaseException
	 *             if there is a database problem
	 * @throws MessagingException
	 *             if there is an email-sending problem
	 */
	@Test
	public void testExistingUserEmailError() throws DatabaseException,
			MessagingException {
		final Config config = new Config();
		try (final TestDatabase testDatabase = new TestDatabase()) {
			testDatabase.load("hsqldb/db.sql");
			final DaoFactory daoFactory = testDatabase.getDaoFactory();
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

			final UserDao userDao = daoFactory.getUserDao();
			userDao.add(user);

			final ResetPasswordMailSender mailSender = new ResetPasswordMailSender(
					config, user, "");
			final ResetPasswordMailSender mockMailSender = Mockito
					.spy(mailSender);
			Mockito.doThrow(MessagingException.class).when(mockMailSender)
					.send();

			final Password mockPassword = Mockito.mock(Password.class);
			Mockito.when(mockPassword.random()).thenReturn("new-password");
			Mockito.when(mockPassword.random(10)).thenReturn("new-salt");
			Mockito.when(mockPassword.hash("new-password", "new-salt"))
					.thenReturn("hashed-password");

			try {
				final ResetPasswordResource resource = new ResetPasswordResource(
						mockMailSender);
				resource.reset(config, daoFactory, mockPassword, timer,
						user.getLogin());
				fail("Expected an email-sending error");
			} catch (final InternalServerErrorException expected) {
				// This is expected.
			}

			// Make sure the password and salt values were reverted back to the
			// original.
			final User updated = userDao.getLogin(user.getLogin());
			assertEquals("hashed", updated.getHashedPass());
			assertEquals("salt", updated.getSalt());
		}
	}
}
