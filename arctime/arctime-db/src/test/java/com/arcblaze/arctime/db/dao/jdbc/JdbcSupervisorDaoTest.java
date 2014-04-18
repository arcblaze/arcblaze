package com.arcblaze.arctime.db.dao.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.Set;

import org.junit.Test;

import com.arcblaze.arccore.common.model.Company;
import com.arcblaze.arccore.common.model.User;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arccore.db.dao.CompanyDao;
import com.arcblaze.arccore.db.dao.UserDao;
import com.arcblaze.arccore.db.dao.jdbc.JdbcCompanyDao;
import com.arcblaze.arccore.db.dao.jdbc.JdbcUserDao;
import com.arcblaze.arccore.db.util.TestDatabase;
import com.arcblaze.arctime.common.model.Supervisor;
import com.arcblaze.arctime.db.dao.SupervisorDao;

/**
 * Perform database integration testing.
 */
public class JdbcSupervisorDaoTest {
	/**
	 * @throws DatabaseException
	 *             if there is a problem with the database
	 */
	@Test
	public void dbIntegrationTests() throws DatabaseException {
		try (final TestDatabase database = new TestDatabase()) {
			database.load("hsqldb/arctime-db.sql");

			final CompanyDao companyDao = new JdbcCompanyDao(
					database.getConnectionManager());
			final UserDao userDao = new JdbcUserDao(
					database.getConnectionManager());
			final SupervisorDao supervisorDao = new JdbcSupervisorDao(
					database.getConnectionManager());

			final Company company1 = new Company().setName("company1");
			final Company company2 = new Company().setName("company2");
			companyDao.add(company1, company2);

			final User supervisor1 = new User();
			supervisor1.setCompanyId(company1.getId());
			supervisor1.setLogin("s1");
			supervisor1.setHashedPass("hashed");
			supervisor1.setSalt("salt");
			supervisor1.setEmail("s1@whatever.com");
			supervisor1.setFirstName("first");
			supervisor1.setLastName("last");
			supervisor1.setActive(true);

			final User supervisor2 = new User();
			supervisor2.setCompanyId(company1.getId());
			supervisor2.setLogin("s2");
			supervisor2.setHashedPass("hashed");
			supervisor2.setSalt("salt");
			supervisor2.setEmail("s2@whatever.com");
			supervisor2.setFirstName("first");
			supervisor2.setLastName("last");
			supervisor2.setActive(true);

			// In a different company.
			final User supervisor3 = new User();
			supervisor3.setCompanyId(company2.getId());
			supervisor3.setLogin("s3");
			supervisor3.setHashedPass("hashed");
			supervisor3.setSalt("salt");
			supervisor3.setEmail("s3@whatever.com");
			supervisor3.setFirstName("first");
			supervisor3.setLastName("last");
			supervisor3.setActive(true);

			final User user = new User();
			user.setCompanyId(company1.getId());
			user.setLogin("user");
			user.setHashedPass("hashed");
			user.setSalt("salt");
			user.setEmail("email@whatever.com");
			user.setFirstName("first");
			user.setLastName("last");
			user.setActive(true);

			userDao.add(supervisor1, supervisor2, supervisor3, user);

			Set<Supervisor> supervisors = supervisorDao.getSupervisors(
					company1.getId(), user.getId());
			assertNotNull(supervisors);
			assertTrue(supervisors.isEmpty());

			supervisorDao.add(company1.getId(), user.getId(), true,
					supervisor1.getId());
			supervisorDao.add(company1.getId(), user.getId(), false,
					supervisor2.getId());
			supervisorDao.add(company2.getId(), user.getId(), false,
					supervisor3.getId());

			supervisors = supervisorDao.getSupervisors(company1.getId(),
					user.getId());
			assertNotNull(supervisors);
			assertEquals(2, supervisors.size());

			final Iterator<Supervisor> iter = supervisors.iterator();
			Supervisor supervisor = iter.next();
			assertEquals(supervisor1.getId(), supervisor.getId());
			assertTrue(supervisor.isPrimary());
			supervisor = iter.next();
			assertEquals(supervisor2.getId(), supervisor.getId());
			assertFalse(supervisor.isPrimary());

			supervisorDao.delete(company1.getId(), user.getId(),
					supervisor2.getId());

			supervisors = supervisorDao.getSupervisors(company1.getId(),
					user.getId());
			assertNotNull(supervisors);
			assertEquals(1, supervisors.size());

			supervisor = supervisors.iterator().next();
			assertEquals(supervisor1.getId(), supervisor.getId());
			assertTrue(supervisor.isPrimary());

			supervisorDao.delete(company1.getId(), user.getId(),
					supervisor1.getId());

			supervisors = supervisorDao.getSupervisors(company1.getId(),
					user.getId());
			assertNotNull(supervisors);
			assertTrue(supervisors.isEmpty());
		}
	}
}
