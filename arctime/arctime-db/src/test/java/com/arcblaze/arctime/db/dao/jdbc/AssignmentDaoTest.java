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
import com.arcblaze.arctime.db.dao.AssignmentDao;
import com.arcblaze.arctime.db.dao.TaskDao;
import com.arcblaze.arctime.model.Assignment;
import com.arcblaze.arctime.model.Task;

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
			database.load("hsqldb/db.sql");

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

			final Task task = new Task();
			task.setCompanyId(company.getId());
			task.setDescription("Task");
			task.setJobCode("job code");
			task.setAdministrative(false);
			task.setActive(true);
			taskDao.add(task);

			final User user = new User();
			user.setCompanyId(company.getId());
			user.setLogin("user");
			user.setHashedPass("hashed");
			user.setSalt("salt");
			user.setEmail("email");
			user.setFirstName("first");
			user.setLastName("last");
			userDao.add(user);

			Set<Assignment> assignments = assignmentDao.getForUser(
					user.getId(), null);
			assertNotNull(assignments);
			assertEquals(0, assignments.size());

			final Assignment assignment = new Assignment();
			assignment.setCompanyId(company.getId());
			assignment.setTaskId(task.getId());
			assignment.setUserId(user.getId());
			assignment.setLaborCat("labor cat");
			assignment.setItemName("item name");
			assignment.setBegin(DateUtils.truncate(
					DateUtils.addDays(new Date(), -10), Calendar.DATE));
			assignment.setEnd(DateUtils.truncate(
					DateUtils.addDays(new Date(), 10), Calendar.DATE));

			assignmentDao.add(assignment);
			assertNotNull(assignment.getId());
			assertEquals(company.getId(), assignment.getCompanyId());

			assignments = assignmentDao.getForUser(user.getId(), null);
			assertNotNull(assignments);
			assertEquals(1, assignments.size());
			assertTrue(assignments.contains(assignment));

			assignments = assignmentDao.getForUser(user.getId(), new Date());
			assertNotNull(assignments);
			assertEquals(1, assignments.size());
			assertTrue(assignments.contains(assignment));

			assignments = assignmentDao.getForUser(user.getId(),
					DateUtils.addDays(new Date(), -15));
			assertNotNull(assignments);
			assertEquals(0, assignments.size());

			assignments = assignmentDao.getForUser(user.getId(),
					DateUtils.addDays(new Date(), 15));
			assertNotNull(assignments);
			assertEquals(0, assignments.size());

			Assignment getAssignment = assignmentDao.get(assignment.getId());
			assertEquals(assignment, getAssignment);

			assignment.setItemName("New Item Name");
			assignmentDao.update(assignment);
			getAssignment = assignmentDao.get(assignment.getId());
			assertEquals(assignment, getAssignment);

			assignmentDao.delete(company.getId(), assignment.getId());
			getAssignment = assignmentDao.get(assignment.getId());
			assertNull(getAssignment);

			assignments = assignmentDao.getForUser(user.getId(), null);
			assertNotNull(assignments);
			assertEquals(0, assignments.size());
		}
	}
}
