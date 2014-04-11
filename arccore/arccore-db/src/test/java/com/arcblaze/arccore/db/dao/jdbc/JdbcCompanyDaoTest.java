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

			final CompanyDao companyDao = new JdbcCompanyDao(
					database.getConnectionManager());
			final UserDao userDao = new JdbcUserDao(
					database.getConnectionManager());

			Set<Company> companies = companyDao.getAll();
			assertNotNull(companies);
			assertEquals(0, companies.size());

			assertEquals(0, companyDao.count(true));

			final Company company = new Company();
			company.setName("Company");
			company.setActive(true);

			companyDao.add(company);
			assertNotNull(company.getId());

			assertEquals(1, companyDao.count(true));
			assertEquals(1, companyDao.count(false));

			companies = companyDao.getAll();
			assertNotNull(companies);
			assertEquals(1, companies.size());
			final Company getAllCompany = companies.iterator().next();
			assertEquals(company, getAllCompany);

			Company getCompany = companyDao.get(company.getId());
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

			getCompany = companyDao.getForUser(user.getId());
			assertEquals(company, getCompany);

			company.setName("New Name");
			companyDao.update(company);
			getCompany = companyDao.get(company.getId());
			assertEquals(company, getCompany);

			company.setActive(false);
			companyDao.update(company);
			getCompany = companyDao.get(company.getId());
			assertEquals(company, getCompany);

			assertEquals(1, companyDao.count(true));
			assertEquals(0, companyDao.count(false));

			companyDao.activate(company.getId());
			assertEquals(1, companyDao.count(true));
			assertEquals(1, companyDao.count(false));

			companyDao.deactivate(company.getId());
			assertEquals(1, companyDao.count(true));
			assertEquals(0, companyDao.count(false));

			companyDao.delete(company.getId());
			getCompany = companyDao.get(company.getId());
			assertNull(getCompany);

			companies = companyDao.getAll();
			assertNotNull(companies);
			assertEquals(0, companies.size());
			assertEquals(0, companyDao.count(true));
		}
	}
}
