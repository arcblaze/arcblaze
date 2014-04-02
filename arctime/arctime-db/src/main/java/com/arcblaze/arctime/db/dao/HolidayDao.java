package com.arcblaze.arctime.db.dao;

import java.util.Collection;
import java.util.Set;

import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arctime.model.Holiday;
import com.arcblaze.arctime.model.PayPeriod;
import com.arcblaze.arctime.model.util.HolidayConfigurationException;

/**
 * Performs operations on holidays in the system.
 */
public interface HolidayDao {
	/**
	 * @param id
	 *            the unique id of the holiday to be retrieved
	 * 
	 * @return the requested holiday, possibly {@code null} if not found
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 * @throws HolidayConfigurationException
	 *             if there is a problem parsing the holiday configuration
	 *             information
	 */
	Holiday get(final Integer id) throws DatabaseException,
			HolidayConfigurationException;

	/**
	 * @param companyId
	 *            the unique id of the company for which the holidays should be
	 *            retrieved
	 * 
	 * @return all available holidays, possibly empty but never {@code null}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 * @throws HolidayConfigurationException
	 *             if there is a problem parsing the holiday configuration
	 *             information
	 */
	Set<Holiday> getAll(final Integer companyId) throws DatabaseException,
			HolidayConfigurationException;

	/**
	 * @param companyId
	 *            the unique id of the company for which the holidays should be
	 *            retrieved
	 * @param payPeriod
	 *            the {@link PayPeriod} for which holidays should be retrieved
	 * 
	 * @return all available holidays that fall within the provided
	 *         {@link PayPeriod}, possibly empty but never {@code null}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id or pay period is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 * @throws HolidayConfigurationException
	 *             if there is a problem parsing the holiday configuration
	 *             values
	 * 
	 */
	Set<Holiday> getForPayPeriod(final Integer companyId,
			final PayPeriod payPeriod) throws DatabaseException,
			HolidayConfigurationException;

	/**
	 * @param holidays
	 *            the new holidays to be added
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided holidays are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void add(final Holiday... holidays) throws DatabaseException;

	/**
	 * @param holidays
	 *            the new holidays to be added
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided holidays are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void add(final Collection<Holiday> holidays) throws DatabaseException;

	/**
	 * @param holidays
	 *            the holidays to be updated
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided holidays are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void update(final Holiday... holidays) throws DatabaseException;

	/**
	 * @param holidays
	 *            the holidays to be updated
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided holidays are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void update(final Collection<Holiday> holidays) throws DatabaseException;

	/**
	 * @param holidayIds
	 *            the unique ids of the holidays to be deleted
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided ids are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void delete(final Integer... holidayIds) throws DatabaseException;

	/**
	 * @param holidayIds
	 *            the unique ids of the holidays to be deleted
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided ids are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void delete(final Collection<Integer> holidayIds) throws DatabaseException;
}