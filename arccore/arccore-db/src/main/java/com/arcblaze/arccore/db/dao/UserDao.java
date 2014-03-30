package com.arcblaze.arccore.db.dao;

import java.util.Collection;
import java.util.Set;

import com.arcblaze.arccore.common.model.User;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arccore.db.DatabaseUniqueConstraintException;

/**
 * Performs operations on users in the system.
 */
public interface UserDao {
	/**
	 * @param includeInactive
	 *            whether inactive user accounts should be included in the count
	 * 
	 * @return the total number of user accounts in the system
	 * 
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	int count(final boolean includeInactive) throws DatabaseException;

	/**
	 * @param login
	 *            the login value provided by the user
	 * 
	 * @return the requested user, possibly {@code null} if not found
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided login is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	User getLogin(final String login) throws DatabaseException;

	/**
	 * @param userId
	 *            the unique id of the user to retrieve
	 * 
	 * @return the requested user, possibly {@code null} if not found
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	User get(final Integer userId) throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which users will be retrieved
	 * 
	 * @return the requested users, possibly empty but never {@code null}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	Set<User> getForCompany(final Integer companyId) throws DatabaseException;

	/**
	 * @return all available users, possibly empty but never {@code null}
	 * 
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	Set<User> getAll() throws DatabaseException;

	/**
	 * @param users
	 *            the new users to be added
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided parameter is invalid
	 * @throws DatabaseUniqueConstraintException
	 *             if there is a problem adding the user due to a unique
	 *             constraint violation
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void add(final User... users) throws DatabaseUniqueConstraintException,
			DatabaseException;

	/**
	 * @param users
	 *            the new users to be added
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided parameter is invalid
	 * @throws DatabaseUniqueConstraintException
	 *             if there is a problem adding the user due to a unique
	 *             constraint violation
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void add(final Collection<User> users)
			throws DatabaseUniqueConstraintException, DatabaseException;

	/**
	 * Save property updates within the provided users to the database. Note
	 * that this does not save any password changes, the
	 * {@link #setPassword(Integer, String, String)} method is used for that.
	 * 
	 * @param users
	 *            the users to be updated
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided parameter is invalid
	 * @throws DatabaseUniqueConstraintException
	 *             if there is a problem adding the user due to a unique
	 *             constraint violation
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void update(final User... users) throws DatabaseUniqueConstraintException,
			DatabaseException;

	/**
	 * Save property updates within the provided users to the database. Note
	 * that this does not save any password changes, the
	 * {@link #setPassword(Integer, String, String)} method is used for that.
	 * 
	 * @param users
	 *            the users to be updated
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided parameter is invalid
	 * @throws DatabaseUniqueConstraintException
	 *             if there is a problem adding the user due to a unique
	 *             constraint violation
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void update(final Collection<User> users)
			throws DatabaseUniqueConstraintException, DatabaseException;

	/**
	 * Save property updates within the provided users to the database. Note
	 * that this does not save any password changes.
	 * 
	 * @param userId
	 *            the unique id of the user whose password is being reset
	 * @param hashedPass
	 *            the new hashed password value to set for the user
	 * @param salt
	 *            the salt value to use when hashing the user's password
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided parameters are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void setPassword(final Integer userId, final String hashedPass,
			final String salt) throws DatabaseException;

	/**
	 * @param userIds
	 *            the unique ids of the users to be deleted
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided parameter is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void delete(final Integer... userIds) throws DatabaseException;

	/**
	 * @param userIds
	 *            the unique ids of the users to be deleted
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided parameter is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void delete(final Collection<Integer> userIds) throws DatabaseException;
}
