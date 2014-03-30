package com.arcblaze.arccore.db.dao.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Set;

import org.junit.Test;

import com.arcblaze.arccore.common.model.Company;
import com.arcblaze.arccore.common.model.User;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arccore.db.dao.CompanyDao;
import com.arcblaze.arccore.db.dao.UserDao;
import com.arcblaze.arccore.db.util.TestDatabase;

/**
 * Perform database integration testing.
 */
public class JdbcCompanyDaoTest {
	/**
	 * @throws DatabaseException
	 *             if there is a problem with the database
	 */
	@Test
	public void dbIntegrationTests() throws DatabaseException {
		try (final TestDatabase database = new TestDatabase()) {
			database.load("hsqldb/db.sql");

			final CompanyDao dao = new JdbcCompanyDao(
					database.getConnectionManager());
			final UserDao userDao = new JdbcUserDao(
					database.getConnectionManager());

			Set<Company> companies = dao.getAll();
			assertNotNull(companies);
			assertEquals(0, companies.size());

			final Company company = new Company();
			company.setName("Company");
			company.setActive(true);

			dao.add(company);
			assertNotNull(company.getId());

			companies = dao.getAll();
			assertNotNull(companies);
			assertEquals(1, companies.size());
			final Company getAllCompany = companies.iterator().next();
			assertEquals(company, getAllCompany);

			Company getCompany = dao.get(company.getId());
			assertEquals(company, getCompany);

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

			getCompany = dao.getForUser(user.getId());
			assertEquals(company, getCompany);

			company.setName("New Name");
			dao.update(company);
			getCompany = dao.get(company.getId());
			assertEquals(company, getCompany);

			dao.delete(company.getId());
			getCompany = dao.get(company.getId());
			assertNull(getCompany);

			companies = dao.getAll();
			assertNotNull(companies);
			assertEquals(0, companies.size());
		}
	}
}
