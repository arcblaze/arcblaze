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
	 *            the unique id of the user for which a company will be
	 *            retrieved
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
	 * @return all available companies, possibly empty but never {@code null}
	 * 
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	Set<Company> getAll() throws DatabaseException;

	/**
	 * @param companies
	 *            the new companies to be added
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided parameter is invalid
	 * @throws DatabaseUniqueConstraintException
	 *             if there is a problem adding the company due to a unique
	 *             constraint violation
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void add(final Company... companies)
			throws DatabaseUniqueConstraintException, DatabaseException;

	/**
	 * @param companies
	 *            the new companies to be added
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided parameter is invalid
	 * @throws DatabaseUniqueConstraintException
	 *             if there is a problem adding the company due to a unique
	 *             constraint violation
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void add(final Collection<Company> companies)
			throws DatabaseUniqueConstraintException, DatabaseException;

	/**
	 * Save property updates within the provided companies to the database.
	 * 
	 * @param companies
	 *            the companies to be updated
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided parameter is invalid
	 * @throws DatabaseUniqueConstraintException
	 *             if there is a problem adding the company due to a unique
	 *             constraint violation
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void update(final Company... companies)
			throws DatabaseUniqueConstraintException, DatabaseException;

	/**
	 * Save property updates within the provided companies to the database.
	 * 
	 * @param companies
	 *            the companies to be updated
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided parameter is invalid
	 * @throws DatabaseUniqueConstraintException
	 *             if there is a problem adding the company due to a unique
	 *             constraint violation
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void update(final Collection<Company> companies)
			throws DatabaseUniqueConstraintException, DatabaseException;

	/**
	 * @param companyIds
	 *            the unique ids of the companies to make active
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided parameter is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void activate(final Integer... companyIds) throws DatabaseException;

	/**
	 * @param companyIds
	 *            the unique ids of the companies to make active
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided parameter is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void activate(final Collection<Integer> companyIds)
			throws DatabaseException;

	/**
	 * @param companyIds
	 *            the unique ids of the companies to make inactive
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided parameter is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void deactivate(final Integer... companyIds) throws DatabaseException;

	/**
	 * @param companyIds
	 *            the unique ids of the companies to make inactive
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided parameter is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void deactivate(final Collection<Integer> companyIds)
			throws DatabaseException;

	/**
	 * @param companyIds
	 *            the unique ids of the companies to be deleted
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided parameter is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void delete(final Integer... companyIds) throws DatabaseException;

	/**
	 * @param companyIds
	 *            the unique ids of the companies to be deleted
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided parameter is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void delete(final Collection<Integer> companyIds) throws DatabaseException;
}
