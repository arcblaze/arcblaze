package com.arcblaze.arccore.db.dao.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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

			Set<Company> companies = companyDao.getAll(null, null);
			assertNotNull(companies);
			assertEquals(0, companies.size());

			assertEquals(0, companyDao.count(true));

			final Company company1 = new Company().setName("Company 1");
			final Company company2 = new Company().setName("Company 2");
			final Company company3 = new Company().setName("Company 3")
					.setActive(false);

			companyDao.add(company1, company2, company3);
			assertNotNull(company1.getId());
			assertNotNull(company2.getId());
			assertNotNull(company3.getId());

			assertEquals(3, companyDao.count(true));
			assertEquals(2, companyDao.count(false));

			companies = companyDao.getAll(null, null);
			assertNotNull(companies);
			assertEquals(3, companies.size());
			assertTrue(companies.contains(company1));
			assertTrue(companies.contains(company2));
			assertTrue(companies.contains(company3));

			companies = companyDao.getAll(1, 1);
			assertNotNull(companies);
			assertEquals(1, companies.size());
			assertTrue(companies.contains(company2));

			Company getCompany = companyDao.get(company1.getId());
			assertEquals(company1, getCompany);

			final User user = new User();
			user.setCompanyId(company1.getId());
			user.setLogin("user");
			user.setHashedPass("hashed");
			user.setSalt("salt");
			user.setEmail("email");
			user.setFirstName("first");
			user.setLastName("last");
			userDao.add(user);
			assertNotNull(user.getId());

			getCompany = companyDao.getForUser(user.getId());
			assertEquals(company1, getCompany);

			company1.setName("New Name");
			companyDao.update(company1);
			getCompany = companyDao.get(company1.getId());
			assertEquals(company1, getCompany);

			company1.setActive(false);
			companyDao.update(company1);
			getCompany = companyDao.get(company1.getId());
			assertEquals(company1, getCompany);

			assertEquals(3, companyDao.count(true));
			assertEquals(1, companyDao.count(false));

			companyDao.activate(company1.getId());
			assertEquals(3, companyDao.count(true));
			assertEquals(2, companyDao.count(false));

			companyDao.deactivate(company1.getId());
			assertEquals(3, companyDao.count(true));
			assertEquals(1, companyDao.count(false));

			companyDao.delete(company1.getId());
			getCompany = companyDao.get(company1.getId());
			assertNull(getCompany);

			companies = companyDao.getAll(null, null);
			assertNotNull(companies);
			assertEquals(2, companies.size());
			assertEquals(2, companyDao.count(true));

			companyDao.delete(company2.getId(), company3.getId());

			companies = companyDao.getAll(null, null);
			assertNotNull(companies);
			assertEquals(0, companies.size());
			assertEquals(0, companyDao.count(true));
		}
	}
}
