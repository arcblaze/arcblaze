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
	 * @param includeInactive
	 *            whether assignments for inactive tasks should be included in
	 *            the return data
	 * 
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
			final Date day, final boolean includeInactive)
			throws DatabaseException;

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
	 * @param includeInactive
	 *            whether assignments for inactive users should be included in
	 *            the return data
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
			final Date day, final boolean includeInactive)
			throws DatabaseException;

	/**
	 * @param assignments
	 *            the new assignments to be added
	 * 
	 * @return the number if records inserted
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided assignments are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	int add(final Assignment... assignments) throws DatabaseException;

	/**
	 * @param assignments
	 *            the new assignments to be added
	 * 
	 * @return the number if records inserted
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided assignments are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	int add(final Collection<Assignment> assignments) throws DatabaseException;

	/**
	 * @param assignments
	 *            the assignments to be updated
	 * 
	 * @return the number if records modified
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided assignments are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	int update(final Assignment... assignments) throws DatabaseException;

	/**
	 * @param assignments
	 *            the assignments to be updated
	 * 
	 * @return the number if records modified
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided assignments are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	int update(final Collection<Assignment> assignments)
			throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which assignments should be
	 *            deleted
	 * @param assignmentIds
	 *            the unique ids of the assignments to be deleted
	 * 
	 * @return the number if records deleted
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided ids are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	int delete(final Integer companyId, final Integer... assignmentIds)
			throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which assignments should be
	 *            deleted
	 * @param assignmentIds
	 *            the unique ids of the assignments to be deleted
	 * 
	 * @return the number if records deleted
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided ids are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	int delete(final Integer companyId, final Collection<Integer> assignmentIds)
			throws DatabaseException;
}
