package com.arcblaze.arccore.db.dao;

import java.util.Collection;
import java.util.Set;

import com.arcblaze.arccore.common.model.Company;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arccore.db.DatabaseUniqueConstraintException;

/**
 * Performs operations on companies in the system.
 */
public interface CompanyDao {
    /**
     * @param includeInactive
     *            whether inactive companies should be included in the count
     * 
     * @return the total number of companies in the system
     * 
     * @throws DatabaseException
     *             if there is a problem communicating with the database
     */
    int count(final boolean includeInactive) throws DatabaseException;

    /**
     * @param filter
     *            the search filter to use when counting results, possibly {@code null} to retrieve all companies
     * @param includeInactive
     *            whether inactive companies should be included in the count
     * 
     * @return the total number of companies in the system
     * 
     * @throws DatabaseException
     *             if there is a problem communicating with the database
     */
    int count(final String filter, final boolean includeInactive) throws DatabaseException;

    /**
     * @param companyId
     *            the unique id of the company to retrieve
     * 
     * @return the requested company, possibly {@code null} if not found
     * 
     * @throws IllegalArgumentException
     *             if the provided id is invalid
     * @throws DatabaseException
     *             if there is a problem communicating with the database
     */
    Company get(final Integer companyId) throws DatabaseException;

    /**
     * @param userId
     *            the unique id of the user for which a company will be retrieved
     * 
     * @return the requested company, should never be {@code null}
     * 
     * @throws IllegalArgumentException
     *             if the provided id is invalid
     * @throws DatabaseException
     *             if there is a problem communicating with the database
     */
    Company getForUser(final Integer userId) throws DatabaseException;

    /**
     * @param filter
     *            the search filter to use to restrict results, possibly {@code null} to retrieve all companies
     * @param includeInactive
     *            whether inactive companies should be included in the response
     * @param limit
     *            the maximum number of items to be retrieved
     * @param offset
     *            the offset into the items to be retrieved
     * 
     * @return all matching companies, possibly empty but never {@code null}
     * 
     * @throws DatabaseException
     *             if there is a problem communicating with the database
     */
    Set<Company> search(final String filter, final boolean includeInactive, final Integer limit, final Integer offset)
            throws DatabaseException;

    /**
     * @param limit
     *            the maximum number of items to be retrieved
     * @param offset
     *            the offset into the items to be retrieved
     * 
     * @return all companies, possibly empty but never {@code null}
     * 
     * @throws DatabaseException
     *             if there is a problem communicating with the database
     */
    Set<Company> getAll(final Integer limit, final Integer offset) throws DatabaseException;

    /**
     * @param companies
     *            the new companies to be added
     * 
     * @return the number of records inserted
     * 
     * @throws IllegalArgumentException
     *             if the provided parameter is invalid
     * @throws DatabaseUniqueConstraintException
     *             if there is a problem adding the company due to a unique constraint violation
     * @throws DatabaseException
     *             if there is a problem communicating with the database
     */
    int add(final Company... companies) throws DatabaseUniqueConstraintException, DatabaseException;

    /**
     * @param companies
     *            the new companies to be added
     * 
     * @return the number of records inserted
     * 
     * @throws IllegalArgumentException
     *             if the provided parameter is invalid
     * @throws DatabaseUniqueConstraintException
     *             if there is a problem adding the company due to a unique constraint violation
     * @throws DatabaseException
     *             if there is a problem communicating with the database
     */
    int add(final Collection<Company> companies) throws DatabaseUniqueConstraintException, DatabaseException;

    /**
     * Save property updates within the provided companies to the database.
     * 
     * @param companies
     *            the companies to be updated
     * 
     * @return the number of records modified
     * 
     * @throws IllegalArgumentException
     *             if the provided parameter is invalid
     * @throws DatabaseUniqueConstraintException
     *             if there is a problem adding the company due to a unique constraint violation
     * @throws DatabaseException
     *             if there is a problem communicating with the database
     */
    int update(final Company... companies) throws DatabaseUniqueConstraintException, DatabaseException;

    /**
     * Save property updates within the provided companies to the database.
     * 
     * @param companies
     *            the companies to be updated
     * 
     * @return the number of records modified
     * 
     * @throws IllegalArgumentException
     *             if the provided parameter is invalid
     * @throws DatabaseUniqueConstraintException
     *             if there is a problem adding the company due to a unique constraint violation
     * @throws DatabaseException
     *             if there is a problem communicating with the database
     */
    int update(final Collection<Company> companies) throws DatabaseUniqueConstraintException, DatabaseException;

    /**
     * @param companyIds
     *            the unique ids of the companies to make active
     * 
     * @return the number of records modified
     * 
     * @throws IllegalArgumentException
     *             if the provided parameter is invalid
     * @throws DatabaseException
     *             if there is a problem communicating with the database
     */
    int activate(final Integer... companyIds) throws DatabaseException;

    /**
     * @param companyIds
     *            the unique ids of the companies to make active
     * 
     * @return the number of records modified
     * 
     * @throws IllegalArgumentException
     *             if the provided parameter is invalid
     * @throws DatabaseException
     *             if there is a problem communicating with the database
     */
    int activate(final Collection<Integer> companyIds) throws DatabaseException;

    /**
     * @param companyIds
     *            the unique ids of the companies to make inactive
     * 
     * @return the number of records modified
     * 
     * @throws IllegalArgumentException
     *             if the provided parameter is invalid
     * @throws DatabaseException
     *             if there is a problem communicating with the database
     */
    int deactivate(final Integer... companyIds) throws DatabaseException;

    /**
     * @param companyIds
     *            the unique ids of the companies to make inactive
     * 
     * @return the number of records modified
     * 
     * @throws IllegalArgumentException
     *             if the provided parameter is invalid
     * @throws DatabaseException
     *             if there is a problem communicating with the database
     */
    int deactivate(final Collection<Integer> companyIds) throws DatabaseException;

    /**
     * @param companyIds
     *            the unique ids of the companies to be deleted
     * 
     * @return the number of records deleted
     * 
     * @throws IllegalArgumentException
     *             if the provided parameter is invalid
     * @throws DatabaseException
     *             if there is a problem communicating with the database
     */
    int delete(final Integer... companyIds) throws DatabaseException;

    /**
     * @param companyIds
     *            the unique ids of the companies to be deleted
     * 
     * @return the number of records deleted
     * 
     * @throws IllegalArgumentException
     *             if the provided parameter is invalid
     * @throws DatabaseException
     *             if there is a problem communicating with the database
     */
    int delete(final Collection<Integer> companyIds) throws DatabaseException;
}
