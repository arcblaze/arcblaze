package com.arcblaze.arccore.db.dao;

import java.util.Collection;
import java.util.Set;

import com.arcblaze.arccore.common.model.Role;
import com.arcblaze.arccore.common.model.User;
import com.arcblaze.arccore.db.DatabaseException;

/**
 * Performs operations on user roles in the system.
 */
public interface RoleDao {
	/**
	 * @param userId
	 *            the unique id of the user for which roles will be identified
	 * 
	 * @return the identified roles for the provided user, possibly empty but
	 *         never {@code null}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	Set<Role> get(final Integer userId) throws DatabaseException;

	/**
	 * @param users
	 *            the {@link User} objects for which roles will be retrieved and
	 *            inserted
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided parameter is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void populateUsers(final User... users) throws DatabaseException;

	/**
	 * @param users
	 *            the {@link User} objects for which roles will be retrieved and
	 *            inserted
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided parameter is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void populateUsers(final Collection<User> users) throws DatabaseException;

	/**
	 * @param userId
	 *            the unique id of the user for which roles will be added
	 * @param roles
	 *            the new roles to be given to the specified user
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void add(final Integer userId, final Role... roles)
			throws DatabaseException;

	/**
	 * @param userId
	 *            the unique id of the user for which roles will be added
	 * @param roles
	 *            the new roles to be given to the specified user
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void add(final Integer userId, final Collection<Role> roles)
			throws DatabaseException;

	/**
	 * @param userId
	 *            the unique id of the user for which roles will be removed
	 * @param roles
	 *            the roles to be removed from the specified user
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void delete(final Integer userId, final Role... roles)
			throws DatabaseException;

	/**
	 * @param userId
	 *            the unique id of the user for which roles will be removed
	 * @param roles
	 *            the roles to be removed from the specified user
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void delete(final Integer userId, final Collection<Role> roles)
			throws DatabaseException;

	/**
	 * @param userId
	 *            the unique id of the user for which roles will be removed
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void delete(final Integer userId) throws DatabaseException;
}
