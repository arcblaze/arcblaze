package com.arcblaze.arccore.db.dao;

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
	 * @return the total number of transactions in the system
	 * 
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	int count() throws DatabaseException;

	/**
	 * @param filter
	 *            the search filter to use when counting results, possibly
	 *            {@code null} to retrieve all transactions
	 * 
	 * @return the total number of transactions in the system
	 * 
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	int count(final String filter) throws DatabaseException;

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
	 * @param limit
	 *            the maximum number of items to be retrieved
	 * @param offset
	 *            the offset into the items to be retrieved
	 * 
	 * @return all available transactions associated with the specified company,
	 *         possibly empty but never {@code null}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	Set<Transaction> getForCompany(final Integer companyId,
			final Integer limit, final Integer offset) throws DatabaseException;

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
	 * @param limit
	 *            the maximum number of items to be retrieved
	 * @param offset
	 *            the offset into the items to be retrieved
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
			final Date end, final Integer limit, final Integer offset)
			throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which the transactions will
	 *            be retrieved
	 * @param filter
	 *            the search filter to use to restrict results
	 * @param limit
	 *            the maximum number of items to be retrieved
	 * @param offset
	 *            the offset into the items to be retrieved
	 * 
	 * @return all matching transactions associated with the specified company,
	 *         possibly empty but never {@code null}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id or date range is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	Set<Transaction> searchForCompany(final Integer companyId,
			final String filter, final Integer limit, final Integer offset)
			throws DatabaseException;

	/**
	 * @param filter
	 *            the search filter to use to restrict results
	 * @param limit
	 *            the maximum number of items to be retrieved
	 * @param offset
	 *            the offset into the items to be retrieved
	 * 
	 * @return all matching transactions, possibly empty but never {@code null}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id or date range is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	Set<Transaction> search(final String filter, final Integer limit,
			final Integer offset) throws DatabaseException;

	/**
	 * @param transactions
	 *            the new transactions to be added
	 * 
	 * @return the number of records inserted
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided transactions are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	int add(final Transaction... transactions) throws DatabaseException;

	/**
	 * @param transactions
	 *            the new transactions to be added
	 * 
	 * @return the number of records inserted
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided transactions are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	int add(final Collection<Transaction> transactions)
			throws DatabaseException;

	/**
	 * @param transactions
	 *            the transactions to be updated
	 * 
	 * @return the number of records modified
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided transactions are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	int update(final Transaction... transactions) throws DatabaseException;

	/**
	 * @param transactions
	 *            the transactions to be updated
	 * 
	 * @return the number of records modified
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided transactions are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	int update(final Collection<Transaction> transactions)
			throws DatabaseException;

	/**
	 * @param transactionIds
	 *            the unique ids of the transactions to be deleted
	 * 
	 * @return the number of records deleted
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided ids are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	int delete(final Integer... transactionIds) throws DatabaseException;

	/**
	 * @param transactionIds
	 *            the unique ids of the transactions to be deleted
	 * 
	 * @return the number of records deleted
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided ids are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	int delete(final Collection<Integer> transactionIds)
			throws DatabaseException;
}
