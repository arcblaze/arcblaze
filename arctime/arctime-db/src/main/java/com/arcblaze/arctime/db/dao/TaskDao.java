package com.arcblaze.arctime.db.dao;

import java.util.Collection;
import java.util.Date;
import java.util.Set;

import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arctime.model.PayPeriod;
import com.arcblaze.arctime.model.Task;

/**
 * Performs operations on tasks in the system.
 */
public interface TaskDao {
	/**
	 * @param taskId
	 *            the unique id of the task to retrieve
	 * 
	 * @return the requested task, possibly {@code null} if not found
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	Task get(final Integer taskId) throws DatabaseException;

	/**
	 * @param userId
	 *            the unique id of the user for which tasks will be retrieved
	 * @param payPeriod
	 *            the pay period for which to retrieve tasks
	 * 
	 * @return all available tasks for the user during the provided pay period,
	 *         possibly empty but never {@code null}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided ids or pay period are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	Set<Task> getForPayPeriod(final Integer userId, final PayPeriod payPeriod)
			throws DatabaseException;

	/**
	 * @param userId
	 *            the unique id of the user for which tasks will be retrieved
	 * @param day
	 *            the day for which task assignments must be valid, may be
	 *            {@code null} if date is not important
	 * @param includeAdmin
	 *            whether administrative tasks should be included
	 * 
	 * @return all available tasks for the user on the given day, possibly empty
	 *         but never {@code null}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id or day is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	Set<Task> getForUser(final Integer userId, final Date day,
			final boolean includeAdmin) throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which tasks will be retrieved
	 * 
	 * @return all available tasks, possibly empty but never {@code null}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	Set<Task> getAll(final Integer companyId) throws DatabaseException;

	/**
	 * @param tasks
	 *            the new tasks to be added
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided tasks are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void add(final Task... tasks) throws DatabaseException;

	/**
	 * @param tasks
	 *            the new tasks to be added
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided tasks are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void add(final Collection<Task> tasks) throws DatabaseException;

	/**
	 * @param tasks
	 *            the tasks to be updated
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided tasks are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void update(final Task... tasks) throws DatabaseException;

	/**
	 * @param tasks
	 *            the tasks to be updated
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided tasks are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void update(final Collection<Task> tasks) throws DatabaseException;

	/**
	 * @param taskIds
	 *            the unique ids of the tasks to be deleted
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided ids are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void delete(final Integer... taskIds) throws DatabaseException;

	/**
	 * @param taskIds
	 *            the unique ids of the tasks to be deleted
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided ids are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void delete(final Collection<Integer> taskIds) throws DatabaseException;
}
