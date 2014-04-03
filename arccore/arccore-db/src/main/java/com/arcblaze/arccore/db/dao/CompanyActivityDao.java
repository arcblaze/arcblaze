package com.arcblaze.arccore.db.dao;

import java.util.Date;
import java.util.SortedMap;

import com.arcblaze.arccore.db.DatabaseException;

/**
 * Performs operations on companies in the system.
 */
public interface CompanyActivityDao {
	/**
	 * @param begin
	 *            the beginning of the date range for which active company
	 *            information should be calculated per month (inclusive)
	 * @param end
	 *            the ending of the date range for which active company
	 *            information should be calculated per month (exclusive)
	 * 
	 * @return a map containing the first day of each month along with the max
	 *         active company count during that month, for every month between
	 *         the begin and end dates, whether data is available for that month
	 *         or not
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided begin or end dates are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	SortedMap<Date, Integer> getActiveByMonth(final Date begin, final Date end)
			throws DatabaseException;

	/**
	 * @param day
	 *            the day for which the active company count applies
	 * @param count
	 *            the count value indicating the number of active companies for
	 *            the specified day
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided day or count values are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void setActiveCompanies(final Date day, final Integer count)
			throws DatabaseException;
}
