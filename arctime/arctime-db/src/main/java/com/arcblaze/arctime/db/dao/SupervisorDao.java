package com.arcblaze.arctime.db.dao;

import java.util.Collection;
import java.util.Set;

import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arctime.common.model.Supervisor;

/**
 * Performs operations on user supervisors in the system.
 */
public interface SupervisorDao {
    /**
     * @param companyId
     *            the unique id of the company for which supervisors will be retrieved
     * @param userId
     *            the unique id of the user whose supervisors are to be retrieved
     * 
     * @return all the supervisors for the specified user, possibly empty but never {@code null}
     * 
     * @throws IllegalArgumentException
     *             if the provided ids are invalid
     * @throws DatabaseException
     *             if there is a problem communicating with the database
     */
    public Set<Supervisor> getSupervisors(final Integer companyId, final Integer userId) throws DatabaseException;

    /**
     * @param companyId
     *            the unique id of the company for which supervisors are being added
     * @param userId
     *            the unique id of the user who will gain the new supervisors
     * @param primary
     *            whether the supervisors are to be added as primary supervisors
     * @param supervisorIds
     *            the unique ids of the users that are to become supervisors
     * 
     * @return the number if records inserted
     * 
     * @throws IllegalArgumentException
     *             if the provided ids are invalid
     * @throws DatabaseException
     *             if there is a problem communicating with the database
     */
    public int add(final Integer companyId, final Integer userId, final boolean primary, final Integer... supervisorIds)
            throws DatabaseException;

    /**
     * @param companyId
     *            the unique id of the company for which supervisors are being added
     * @param userId
     *            the unique id of the user who will gain the new supervisors
     * @param primary
     *            whether the supervisors are to be added as primary supervisors
     * @param supervisorIds
     *            the unique ids of the users that are to become supervisors
     * 
     * @return the number if records inserted
     * 
     * @throws IllegalArgumentException
     *             if the provided ids are invalid
     * @throws DatabaseException
     *             if there is a problem communicating with the database
     */
    public int add(final Integer companyId, final Integer userId, final boolean primary,
            final Collection<Integer> supervisorIds) throws DatabaseException;

    /**
     * @param companyId
     *            the unique id of the company for which supervisors are being removed
     * @param userId
     *            the unique id of the user who will lose the specified supervisors
     * @param supervisorIds
     *            the unique ids of the users that are to be removed as supervisors
     * 
     * @return the number if records deleted
     * 
     * @throws IllegalArgumentException
     *             if the provided ids are invalid
     * @throws DatabaseException
     *             if there is a problem communicating with the database
     */
    public int delete(final Integer companyId, final Integer userId, final Integer... supervisorIds)
            throws DatabaseException;

    /**
     * @param companyId
     *            the unique id of the company for which supervisors are being removed
     * @param userId
     *            the unique id of the user who will lose the specified supervisors
     * @param supervisorIds
     *            the unique ids of the users that are to be removed as supervisors
     * 
     * @return the number if records deleted
     * 
     * @throws IllegalArgumentException
     *             if the provided ids are invalid
     * @throws DatabaseException
     *             if there is a problem communicating with the database
     */
    public int delete(final Integer companyId, final Integer userId, final Collection<Integer> supervisorIds)
            throws DatabaseException;
}
