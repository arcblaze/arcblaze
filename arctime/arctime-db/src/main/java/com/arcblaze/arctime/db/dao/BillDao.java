package com.arcblaze.arctime.db.dao;

import java.util.Collection;
import java.util.Set;

import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arctime.common.model.Bill;

/**
 * Performs operations on bills in the system.
 */
public interface BillDao {
	/**
	 * @param id
	 *            the unique id of the bill to be retrieved
	 * 
	 * @return the requested bill, possibly {@code null} if not found
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	Bill get(final Integer id) throws DatabaseException;

	/**
	 * @param timesheetId
	 *            the unique id of the timesheet for which the bills will be
	 *            retrieved
	 * 
	 * @return all available bills contained within the specified timesheet,
	 *         possibly empty but never {@code null}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	Set<Bill> getForTimesheet(final Integer timesheetId)
			throws DatabaseException;

	/**
	 * @param bills
	 *            the new bills to be added
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided bills are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void add(final Bill... bills) throws DatabaseException;

	/**
	 * @param bills
	 *            the new bills to be added
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided bills are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void add(final Collection<Bill> bills) throws DatabaseException;

	/**
	 * @param bills
	 *            the bills to be updated
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided bills are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void update(final Bill... bills) throws DatabaseException;

	/**
	 * @param bills
	 *            the bills to be updated
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided bills are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void update(final Collection<Bill> bills) throws DatabaseException;

	/**
	 * @param billIds
	 *            the unique ids of the bills to be deleted
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided ids are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void delete(final Integer... billIds) throws DatabaseException;

	/**
	 * @param billIds
	 *            the unique ids of the bills to be deleted
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided ids are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void delete(final Collection<Integer> billIds) throws DatabaseException;
}
