package com.arcblaze.arctime.db.dao.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;

import com.arcblaze.arccore.common.model.Company;
import com.arcblaze.arccore.common.model.User;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arccore.db.dao.CompanyDao;
import com.arcblaze.arccore.db.dao.UserDao;
import com.arcblaze.arccore.db.dao.jdbc.JdbcCompanyDao;
import com.arcblaze.arccore.db.dao.jdbc.JdbcUserDao;
import com.arcblaze.arccore.db.util.TestDatabase;
import com.arcblaze.arctime.common.model.Assignment;
import com.arcblaze.arctime.common.model.Task;
import com.arcblaze.arctime.db.dao.AssignmentDao;
import com.arcblaze.arctime.db.dao.TaskDao;

/**
 * Perform database integration testing.
 */
public class AssignmentDaoTest {
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
			final TaskDao taskDao = new JdbcTaskDao(
					database.getConnectionManager());
			final AssignmentDao assignmentDao = new JdbcAssignmentDao(
					database.getConnectionManager());

			final Company company = new Company().setName("Company").setActive(
					true);
			companyDao.add(company);

			final Task task1 = new Task();
			task1.setCompanyId(company.getId());
			task1.setDescription("Task 1");
			task1.setJobCode("job code 1");
			task1.setAdministrative(false);
			task1.setActive(true);
			final Task task2 = new Task();
			task2.setCompanyId(company.getId());
			task2.setDescription("Task 2");
			task2.setJobCode("job code 2");
			task2.setAdministrative(false);
			task2.setActive(true);
			taskDao.add(task1, task2);

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
			userDao.add(user1, user2);

			Set<Assignment> assignments = assignmentDao.getForUser(
					company.getId(), user1.getId(), null);
			assertNotNull(assignments);
			assertEquals(0, assignments.size());

			final Assignment assmnt1 = new Assignment();
			assmnt1.setCompanyId(company.getId());
			assmnt1.setTaskId(task1.getId());
			assmnt1.setUserId(user1.getId());
			assmnt1.setLaborCat("labor cat");
			assmnt1.setItemName("item name");
			assmnt1.setBegin(DateUtils.truncate(
					DateUtils.addDays(new Date(), -10), Calendar.DATE));
			assmnt1.setEnd(DateUtils.truncate(
					DateUtils.addDays(new Date(), 10), Calendar.DATE));

			final Assignment assmnt2 = new Assignment();
			assmnt2.setCompanyId(company.getId());
			assmnt2.setTaskId(task1.getId());
			assmnt2.setUserId(user2.getId());
			assmnt2.setLaborCat("labor cat");
			assmnt2.setItemName("item name");
			assmnt2.setBegin(DateUtils.truncate(
					DateUtils.addDays(new Date(), -10), Calendar.DATE));
			assmnt2.setEnd(DateUtils.truncate(
					DateUtils.addDays(new Date(), 10), Calendar.DATE));

			final Assignment assmnt3 = new Assignment();
			assmnt3.setCompanyId(company.getId());
			assmnt3.setTaskId(task2.getId());
			assmnt3.setUserId(user1.getId());
			assmnt3.setLaborCat("labor cat");
			assmnt3.setItemName("item name");
			assmnt3.setBegin(DateUtils.truncate(
					DateUtils.addDays(new Date(), -10), Calendar.DATE));
			assmnt3.setEnd(DateUtils.truncate(
					DateUtils.addDays(new Date(), 10), Calendar.DATE));

			final Assignment assmnt4 = new Assignment();
			assmnt4.setCompanyId(company.getId());
			assmnt4.setTaskId(task2.getId());
			assmnt4.setUserId(user2.getId());
			assmnt4.setLaborCat("labor cat");
			assmnt4.setItemName("item name");
			assmnt4.setBegin(DateUtils.truncate(
					DateUtils.addDays(new Date(), -10), Calendar.DATE));
			assmnt4.setEnd(DateUtils.truncate(
					DateUtils.addDays(new Date(), 10), Calendar.DATE));

			assignmentDao.add(assmnt1, assmnt2, assmnt3, assmnt4);
			assertNotNull(assmnt1.getId());
			assertEquals(company.getId(), assmnt1.getCompanyId());
			assertNotNull(assmnt2.getId());
			assertEquals(company.getId(), assmnt2.getCompanyId());
			assertNotNull(assmnt3.getId());
			assertEquals(company.getId(), assmnt3.getCompanyId());
			assertNotNull(assmnt4.getId());
			assertEquals(company.getId(), assmnt4.getCompanyId());

			assignments = assignmentDao.getForUser(company.getId(),
					user1.getId(), null);
			assertNotNull(assignments);
			assertEquals(2, assignments.size());
			assertTrue(assignments.contains(assmnt1));
			assertTrue(assignments.contains(assmnt3));

			assignments = assignmentDao.getForUser(company.getId(),
					user1.getId(), new Date());
			assertNotNull(assignments);
			assertEquals(2, assignments.size());
			assertTrue(assignments.contains(assmnt1));
			assertTrue(assignments.contains(assmnt3));

			assignments = assignmentDao.getForUser(company.getId(),
					user2.getId(), null);
			assertNotNull(assignments);
			assertEquals(2, assignments.size());
			assertTrue(assignments.contains(assmnt2));
			assertTrue(assignments.contains(assmnt4));

			assignments = assignmentDao.getForUser(company.getId(),
					user2.getId(), new Date());
			assertNotNull(assignments);
			assertEquals(2, assignments.size());
			assertTrue(assignments.contains(assmnt2));
			assertTrue(assignments.contains(assmnt4));

			assignments = assignmentDao.getForTask(company.getId(),
					task1.getId(), null);
			assertNotNull(assignments);
			assertEquals(2, assignments.size());
			assertTrue(assignments.contains(assmnt1));
			assertTrue(assignments.contains(assmnt2));

			assignments = assignmentDao.getForTask(company.getId(),
					task1.getId(), new Date());
			assertNotNull(assignments);
			assertEquals(2, assignments.size());
			assertTrue(assignments.contains(assmnt1));
			assertTrue(assignments.contains(assmnt2));

			assignments = assignmentDao.getForTask(company.getId(),
					task2.getId(), null);
			assertNotNull(assignments);
			assertEquals(2, assignments.size());
			assertTrue(assignments.contains(assmnt3));
			assertTrue(assignments.contains(assmnt4));

			assignments = assignmentDao.getForTask(company.getId(),
					task2.getId(), new Date());
			assertNotNull(assignments);
			assertEquals(2, assignments.size());
			assertTrue(assignments.contains(assmnt3));
			assertTrue(assignments.contains(assmnt4));

			assignments = assignmentDao.getForUser(company.getId(),
					user1.getId(), DateUtils.addDays(new Date(), -15));
			assertNotNull(assignments);
			assertEquals(0, assignments.size());

			assignments = assignmentDao.getForUser(company.getId(),
					user1.getId(), DateUtils.addDays(new Date(), 15));
			assertNotNull(assignments);
			assertEquals(0, assignments.size());

			assignments = assignmentDao.getForTask(company.getId(),
					task1.getId(), DateUtils.addDays(new Date(), -15));
			assertNotNull(assignments);
			assertEquals(0, assignments.size());

			assignments = assignmentDao.getForTask(company.getId(),
					task1.getId(), DateUtils.addDays(new Date(), 15));
			assertNotNull(assignments);
			assertEquals(0, assignments.size());

			Assignment getAssignment = assignmentDao.get(assmnt1.getId());
			assertEquals(assmnt1, getAssignment);

			assmnt1.setItemName("New Item Name");
			assignmentDao.update(assmnt1);
			getAssignment = assignmentDao.get(assmnt1.getId());
			assertEquals(assmnt1, getAssignment);

			assignmentDao.delete(company.getId(), assmnt1.getId());
			assignmentDao.delete(company.getId(), assmnt2.getId());
			getAssignment = assignmentDao.get(assmnt1.getId());
			assertNull(getAssignment);

			assignments = assignmentDao.getForTask(company.getId(),
					task1.getId(), null);
			assertNotNull(assignments);
			assertEquals(0, assignments.size());

			assignments = assignmentDao.getForTask(company.getId(),
					task2.getId(), null);
			assertNotNull(assignments);
			assertEquals(2, assignments.size());

			assignments = assignmentDao.getForUser(company.getId(),
					user1.getId(), null);
			assertNotNull(assignments);
			assertEquals(1, assignments.size());

			assignments = assignmentDao.getForUser(company.getId(),
					user2.getId(), null);
			assertNotNull(assignments);
			assertEquals(1, assignments.size());

			assignmentDao.delete(company.getId(), assmnt3.getId());
			assignmentDao.delete(company.getId(), assmnt4.getId());

			assignments = assignmentDao.getForTask(company.getId(),
					task1.getId(), null);
			assertNotNull(assignments);
			assertEquals(0, assignments.size());

			assignments = assignmentDao.getForTask(company.getId(),
					task2.getId(), null);
			assertNotNull(assignments);
			assertEquals(0, assignments.size());

			assignments = assignmentDao.getForUser(company.getId(),
					user1.getId(), null);
			assertNotNull(assignments);
			assertEquals(0, assignments.size());

			assignments = assignmentDao.getForUser(company.getId(),
					user2.getId(), null);
			assertNotNull(assignments);
			assertEquals(0, assignments.size());
		}
	}
}
