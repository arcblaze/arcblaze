package com.arcblaze.arccore.db.dao;

import java.util.Collection;
import java.util.Map;
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
     * @param filter
     *            the search filter to use when restricting the result count
     * @param includeInactive
     *            whether inactive user accounts should be included in the count
     * 
     * @return the total number of user accounts in the system
     * 
     * @throws DatabaseException
     *             if there is a problem communicating with the database
     */
    int count(final String filter, final boolean includeInactive) throws DatabaseException;

    /**
     * @param includeInactive
     *            whether inactive user accounts should be included
     * 
     * @return a map specifying the total number of user accounts in the system per company
     * 
     * @throws DatabaseException
     *             if there is a problem communicating with the database
     */
    Map<Integer, Integer> countPerCompany(final boolean includeInactive) throws DatabaseException;

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
     *            the unique id of the company for which a user will be retrieved
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
    User get(final Integer companyId, final Integer userId) throws DatabaseException;

    /**
     * @param ids
     *            the unique ids of the user objects to retrieve
     * 
     * @return a map of user id to user, possibly empty but never {@code null}
     * 
     * @throws IllegalArgumentException
     *             if the provided parameter is invalid
     * @throws DatabaseException
     *             if there is a problem communicating with the database
     */
    Map<Integer, User> get(final Set<Integer> ids) throws DatabaseException;

    /**
     * @param companyId
     *            the unique id of the company for which users will be retrieved
     * @param includeInactive
     *            whether inactive users should be included in the response
     * @param limit
     *            the maximum number of items to be retrieved
     * @param offset
     *            the offset into the items to be retrieved
     * 
     * @return all available users, possibly empty but never {@code null}
     * 
     * @throws DatabaseException
     *             if there is a problem communicating with the database
     */
    Set<User> getAll(final Integer companyId, final boolean includeInactive, final Integer limit, final Integer offset)
            throws DatabaseException;

    /**
     * @param includeInactive
     *            whether inactive users should be included in the response
     * @param limit
     *            the maximum number of items to be retrieved
     * @param offset
     *            the offset into the items to be retrieved
     * 
     * @return all available users, possibly empty but never {@code null}
     * 
     * @throws DatabaseException
     *             if there is a problem communicating with the database
     */
    Set<User> getAll(final boolean includeInactive, final Integer limit, final Integer offset) throws DatabaseException;

    /**
     * @param companyId
     *            the unique id of the company for which users will be retrieved
     * @param filter
     *            the search filter to use when restricting results
     * @param includeInactive
     *            whether inactive users should be included in the response
     * @param limit
     *            the maximum number of items to be retrieved
     * @param offset
     *            the offset into the items to be retrieved
     * 
     * @return all available users, possibly empty but never {@code null}
     * 
     * @throws DatabaseException
     *             if there is a problem communicating with the database
     */
    Set<User> search(final Integer companyId, final String filter, final boolean includeInactive, final Integer limit,
            final Integer offset) throws DatabaseException;

    /**
     * @param filter
     *            the search filter to use when restricting results
     * @param includeInactive
     *            whether inactive users should be included in the response
     * @param limit
     *            the maximum number of items to be retrieved
     * @param offset
     *            the offset into the items to be retrieved
     * 
     * @return all available users, possibly empty but never {@code null}
     * 
     * @throws DatabaseException
     *             if there is a problem communicating with the database
     */
    Set<User> search(final String filter, final boolean includeInactive, final Integer limit, final Integer offset)
            throws DatabaseException;

    /**
     * @param users
     *            the new users to be added
     * 
     * @return the number of records inserted
     * 
     * @throws IllegalArgumentException
     *             if the provided parameter is invalid
     * @throws DatabaseUniqueConstraintException
     *             if there is a problem adding the user due to a unique constraint violation
     * @throws DatabaseException
     *             if there is a problem communicating with the database
     */
    int add(final User... users) throws DatabaseUniqueConstraintException, DatabaseException;

    /**
     * @param users
     *            the new users to be added
     * 
     * @return the number of records inserted
     * 
     * @throws IllegalArgumentException
     *             if the provided parameter is invalid
     * @throws DatabaseUniqueConstraintException
     *             if there is a problem adding the user due to a unique constraint violation
     * @throws DatabaseException
     *             if there is a problem communicating with the database
     */
    int add(final Collection<User> users) throws DatabaseUniqueConstraintException, DatabaseException;

    /**
     * @param companyId
     *            the unique id of the company for which users will be activated
     * @param userIds
     *            the unique ids of the users to make active
     * 
     * @return the number of records modified
     * 
     * @throws IllegalArgumentException
     *             if either of the provided parameters are invalid
     * @throws DatabaseException
     *             if there is a problem communicating with the database
     */
    int activate(final Integer companyId, final Integer... userIds) throws DatabaseException;

    /**
     * @param companyId
     *            the unique id of the company for which users will be activated
     * @param userIds
     *            the unique ids of the users to make active
     * 
     * @return the number of records modified
     * 
     * @throws IllegalArgumentException
     *             if either of the provided parameters are invalid
     * @throws DatabaseException
     *             if there is a problem communicating with the database
     */
    int activate(final Integer companyId, final Collection<Integer> userIds) throws DatabaseException;

    /**
     * @param companyId
     *            the unique id of the company for which users will be deactivated
     * @param userIds
     *            the unique ids of the users to make inactive
     * 
     * @return the number of records modified
     * 
     * @throws IllegalArgumentException
     *             if either of the provided parameters are invalid
     * @throws DatabaseException
     *             if there is a problem communicating with the database
     */
    int deactivate(final Integer companyId, final Integer... userIds) throws DatabaseException;

    /**
     * @param companyId
     *            the unique id of the company for which users will be deactivated
     * @param userIds
     *            the unique ids of the users to make inactive
     * 
     * @return the number of records modified
     * 
     * @throws IllegalArgumentException
     *             if either of the provided parameters are invalid
     * @throws DatabaseException
     *             if there is a problem communicating with the database
     */
    int deactivate(final Integer companyId, final Collection<Integer> userIds) throws DatabaseException;

    /**
     * Save property updates within the provided users to the database. Note that this does not save any password
     * changes, the {@link #setPassword(Integer, String, String)} method is used for that.
     * 
     * @param users
     *            the users to be updated
     * 
     * @return the number of records modified
     * 
     * @throws IllegalArgumentException
     *             if the provided parameter is invalid
     * @throws DatabaseUniqueConstraintException
     *             if there is a problem adding the user due to a unique constraint violation
     * @throws DatabaseException
     *             if there is a problem communicating with the database
     */
    int update(final User... users) throws DatabaseUniqueConstraintException, DatabaseException;

    /**
     * Save property updates within the provided users to the database. Note that this does not save any password
     * changes, the {@link #setPassword(Integer, String, String)} method is used for that.
     * 
     * @param users
     *            the users to be updated
     * 
     * @return the number of records modified
     * 
     * @throws IllegalArgumentException
     *             if the provided parameter is invalid
     * @throws DatabaseUniqueConstraintException
     *             if there is a problem adding the user due to a unique constraint violation
     * @throws DatabaseException
     *             if there is a problem communicating with the database
     */
    int update(final Collection<User> users) throws DatabaseUniqueConstraintException, DatabaseException;

    /**
     * Save property updates within the provided users to the database. Note that this does not save any password
     * changes.
     * 
     * @param userId
     *            the unique id of the user whose password is being reset
     * @param hashedPass
     *            the new hashed password value to set for the user
     * @param salt
     *            the salt value to use when hashing the user's password
     * 
     * @return the number of records modified
     * 
     * @throws IllegalArgumentException
     *             if the provided parameters are invalid
     * @throws DatabaseException
     *             if there is a problem communicating with the database
     */
    int setPassword(final Integer userId, final String hashedPass, final String salt) throws DatabaseException;

    /**
     * @param userIds
     *            the unique ids of the users to be deleted
     * 
     * @return the number of records modified
     * 
     * @throws IllegalArgumentException
     *             if the provided parameter is invalid
     * @throws DatabaseException
     *             if there is a problem communicating with the database
     */
    int delete(final Integer... userIds) throws DatabaseException;

    /**
     * @param userIds
     *            the unique ids of the users to be deleted
     * 
     * @return the number of records deleted
     * 
     * @throws IllegalArgumentException
     *             if the provided parameter is invalid
     * @throws DatabaseException
     *             if there is a problem communicating with the database
     */
    int delete(final Collection<Integer> userIds) throws DatabaseException;

    /**
     * @param companyId
     *            the unique id of the company for which users will be deleted
     * @param userIds
     *            the unique ids of the users to be deleted
     * 
     * @return the number of records deleted
     * 
     * @throws IllegalArgumentException
     *             if the provided parameter is invalid
     * @throws DatabaseException
     *             if there is a problem communicating with the database
     */
    int delete(final Integer companyId, final Collection<Integer> userIds) throws DatabaseException;
}
