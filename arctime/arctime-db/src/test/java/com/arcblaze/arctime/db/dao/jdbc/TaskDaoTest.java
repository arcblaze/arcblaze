package com.arcblaze.arctime.db.dao.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
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
public class TaskDaoTest {
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
			final TaskDao taskDao = new JdbcTaskDao(
					database.getConnectionManager());
			final UserDao userDao = new JdbcUserDao(
					database.getConnectionManager());
			final AssignmentDao assignmentDao = new JdbcAssignmentDao(
					database.getConnectionManager());

			final Company company = new Company().setName("Company").setActive(
					true);
			companyDao.add(company);

			Set<Task> tasks = taskDao.getAll(company.getId());
			assertNotNull(tasks);
			assertEquals(0, tasks.size());

			final Task task = new Task();
			task.setCompanyId(company.getId());
			task.setDescription("My Task");
			task.setJobCode("job code");
			task.setAdministrative(false);
			task.setActive(true);

			taskDao.add(task);
			assertNotNull(task.getId());
			assertEquals(company.getId(), task.getCompanyId());

			Set<Task> getAllTasks = taskDao.getAll(company.getId());
			assertNotNull(getAllTasks);
			assertEquals(1, getAllTasks.size());

			Task getTask = taskDao.get(company.getId(), task.getId());
			assertEquals(task, getTask);

			task.setDescription("New Description");
			taskDao.update(task);
			getTask = taskDao.get(company.getId(), task.getId());
			assertEquals(task, getTask);

			taskDao.delete(company.getId(), task.getId());
			getTask = taskDao.get(company.getId(), task.getId());
			assertNull(getTask);

			getAllTasks = taskDao.getAll(company.getId());
			assertNotNull(getAllTasks);
			assertEquals(0, getAllTasks.size());

			// Test assignments

			final Task adminTask = new Task();
			adminTask.setCompanyId(company.getId());
			adminTask.setDescription("Admin Task");
			adminTask.setJobCode("admin");
			adminTask.setAdministrative(true);
			adminTask.setActive(true);

			taskDao.add(Arrays.asList(adminTask, task));

			final User user = new User();
			user.setCompanyId(company.getId());
			user.setLogin("user");
			user.setHashedPass("hashed");
			user.setSalt("salt");
			user.setEmail("email");
			user.setFirstName("first");
			user.setLastName("last");
			userDao.add(user);

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

			Set<Task> assignedTasks = taskDao.getForUser(user.getId(), null,
					true);
			assertNotNull(assignedTasks);
			assertEquals(2, assignedTasks.size());
			assertTrue(assignedTasks.contains(task));
			assertTrue(assignedTasks.contains(adminTask));

			assignedTasks = taskDao.getForUser(user.getId(), null, false);
			assertNotNull(assignedTasks);
			assertEquals(1, assignedTasks.size());
			assertTrue(assignedTasks.contains(task));

			assignedTasks = taskDao.getForUser(user.getId(), new Date(), true);
			assertNotNull(assignedTasks);
			assertEquals(2, assignedTasks.size());
			assertTrue(assignedTasks.contains(task));
			assertTrue(assignedTasks.contains(adminTask));

			assignedTasks = taskDao.getForUser(user.getId(), new Date(), false);
			assertNotNull(assignedTasks);
			assertEquals(1, assignedTasks.size());
			assertTrue(assignedTasks.contains(task));

			assignedTasks = taskDao.getForUser(user.getId(),
					DateUtils.addDays(new Date(), -15), true);
			assertNotNull(assignedTasks);
			assertEquals(1, assignedTasks.size());
			assertTrue(assignedTasks.contains(adminTask));

			assignedTasks = taskDao.getForUser(user.getId(),
					DateUtils.addDays(new Date(), -15), false);
			assertNotNull(assignedTasks);
			assertEquals(0, assignedTasks.size());

			assignedTasks = taskDao.getForUser(user.getId(),
					DateUtils.addDays(new Date(), 15), true);
			assertNotNull(assignedTasks);
			assertEquals(1, assignedTasks.size());
			assertTrue(assignedTasks.contains(adminTask));

			assignedTasks = taskDao.getForUser(user.getId(),
					DateUtils.addDays(new Date(), 15), false);
			assertNotNull(assignedTasks);
			assertEquals(0, assignedTasks.size());
		}
	}
}
