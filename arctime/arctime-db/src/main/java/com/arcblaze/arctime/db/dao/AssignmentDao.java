package com.arcblaze.arctime.db.dao;

import java.util.Collection;
import java.util.Date;
import java.util.Set;

import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arctime.common.model.Assignment;

/**
 * Performs operations on assignments in the system.
 */
public interface AssignmentDao {
	/**
	 * @param assignmentId
	 *            the unique id of the assignment to retrieve
	 * 
	 * @return the requested assignment, possibly {@code null} if not found
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	Assignment get(final Integer assignmentId) throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which assignments should be
	 *            retrieved
	 * @param userId
	 *            the unique id of the user for which assignments will be
	 *            retrieved
	 * @param day
	 *            the day for which assignments must be valid, may be
	 *            {@code null} if date is not important
	 * 
	 * @return all available assignments for the user during the specified day,
	 *         possibly empty but never {@code null}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id or day is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	Set<Assignment> getForUser(final Integer companyId, final Integer userId,
			final Date day) throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which assignments should be
	 *            retrieved
	 * @param taskId
	 *            the unique id of the task for which assignments will be
	 *            retrieved
	 * @param day
	 *            the day for which assignments must be valid, may be
	 *            {@code null} if date is not important
	 * 
	 * @return all available assignments for the user during the specified day,
	 *         possibly empty but never {@code null}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id or day is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	Set<Assignment> getForTask(final Integer companyId, final Integer taskId,
			final Date day) throws DatabaseException;

	/**
	 * @param assignments
	 *            the new assignments to be added
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided assignments are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void add(final Assignment... assignments) throws DatabaseException;

	/**
	 * @param assignments
	 *            the new assignments to be added
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided assignments are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void add(final Collection<Assignment> assignments) throws DatabaseException;

	/**
	 * @param assignments
	 *            the assignments to be updated
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided assignments are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void update(final Assignment... assignments) throws DatabaseException;

	/**
	 * @param assignments
	 *            the assignments to be updated
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided assignments are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void update(final Collection<Assignment> assignments)
			throws DatabaseException;

	/**
	 * @param assignmentIds
	 *            the unique ids of the assignments to be deleted
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided ids are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void delete(final Integer... assignmentIds) throws DatabaseException;

	/**
	 * @param assignmentIds
	 *            the unique ids of the assignments to be deleted
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided ids are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void delete(final Collection<Integer> assignmentIds)
			throws DatabaseException;
}
