package com.arcblaze.arctime.db.dao;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.SortedMap;

import com.arcblaze.arccore.common.model.Transaction;
import com.arcblaze.arccore.db.DatabaseException;

/**
 * Performs operations on transactions in the system.
 */
public interface TransactionDao {
	/**
	 * @param companyId
	 *            the unique id of the company for which the sum of amounts will
	 *            be retrieved
	 * @param begin
	 *            the beginning boundary of the time frame where transactions
	 *            should be retrieved (inclusive)
	 * @param end
	 *            the ending boundary of the time frame where transactions
	 *            should be retrieved (exclusive)
	 * 
	 * @return the sum of amounts for all transactions within the specified date
	 *         range, possibly zero if no transactions were available during
	 *         that time period
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided parameters are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	BigDecimal amountBetween(final Integer companyId, final Date begin,
			final Date end) throws DatabaseException;

	/**
	 * @param begin
	 *            the beginning boundary of the time frame where transactions
	 *            should be retrieved (inclusive)
	 * @param end
	 *            the ending boundary of the time frame where transactions
	 *            should be retrieved (exclusive)
	 * 
	 * @return the sum of amounts for all transactions within the specified date
	 *         range, possibly zero if no transactions were available during
	 *         that time period
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided parameters are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	BigDecimal amountBetween(final Date begin, final Date end)
			throws DatabaseException;

	/**
	 * @param begin
	 *            the beginning boundary of the time frame where transactions
	 *            should be retrieved (inclusive)
	 * @param end
	 *            the ending boundary of the time frame where transactions
	 *            should be retrieved (exclusive)
	 * 
	 * @return a map containing the first day of each month along with the sum
	 *         of transaction amounts during that month, for every month between
	 *         the begin and end dates, whether data is available for that month
	 *         or not
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided parameters are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	SortedMap<Date, BigDecimal> getSumByMonth(final Date begin, final Date end)
			throws DatabaseException;

	/**
	 * @param id
	 *            the unique id of the transaction to be retrieved
	 * 
	 * @return the requested transaction, possibly {@code null} if not found
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	Transaction get(final Integer id) throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which the transactions will
	 *            be retrieved
	 * 
	 * @return all available transactions associated with the specified company,
	 *         possibly empty but never {@code null}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	Set<Transaction> getForCompany(final Integer companyId)
			throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which the transactions will
	 *            be retrieved
	 * @param begin
	 *            the beginning boundary of the time frame where transactions
	 *            should be retrieved (inclusive)
	 * @param end
	 *            the ending boundary of the time frame where transactions
	 *            should be retrieved (exclusive)
	 * 
	 * @return all available transactions associated with the specified company,
	 *         possibly empty but never {@code null}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id or date range is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	Set<Transaction> getForCompany(final Integer companyId, final Date begin,
			final Date end) throws DatabaseException;

	/**
	 * @param transactions
	 *            the new transactions to be added
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided transactions are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void add(final Transaction... transactions) throws DatabaseException;

	/**
	 * @param transactions
	 *            the new transactions to be added
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided transactions are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void add(final Collection<Transaction> transactions)
			throws DatabaseException;

	/**
	 * @param transactions
	 *            the transactions to be updated
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided transactions are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void update(final Transaction... transactions) throws DatabaseException;

	/**
	 * @param transactions
	 *            the transactions to be updated
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided transactions are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void update(final Collection<Transaction> transactions)
			throws DatabaseException;

	/**
	 * @param transactionIds
	 *            the unique ids of the transactions to be deleted
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided ids are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void delete(final Integer... transactionIds) throws DatabaseException;

	/**
	 * @param transactionIds
	 *            the unique ids of the transactions to be deleted
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided ids are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void delete(final Collection<Integer> transactionIds)
			throws DatabaseException;
}
