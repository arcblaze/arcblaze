package com.arcblaze.arctime.db.dao;

import java.util.Collection;
import java.util.Date;

import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arctime.common.model.PayPeriod;

/**
 * Performs operations on pay periods in the system.
 */
public interface PayPeriodDao {
    /**
     * @param companyId
     *            the unique id of the company for which pay period information should be searched
     * @param begin
     *            the begin date of the pay period to be searched
     * 
     * @return whether the requested pay period exists
     * 
     * @throws IllegalArgumentException
     *             if the provided id or begin date is invalid
     * @throws DatabaseException
     *             if there is a problem communicating with the database
     */
    boolean exists(final Integer companyId, final Date begin) throws DatabaseException;

    /**
     * @param companyId
     *            the unique id of the company for which pay period information should be retrieved
     * @param begin
     *            the begin date of the pay period to be retrieved
     * 
     * @return the requested pay period, potentially {@code null} if the requested pay period does not exist
     * 
     * @throws IllegalArgumentException
     *             if the provided id or begin date is invalid
     * @throws DatabaseException
     *             if there is a problem communicating with the database
     */
    PayPeriod get(final Integer companyId, final Date begin) throws DatabaseException;

    /**
     * @param companyId
     *            the unique id of the company for which pay period information should be retrieved
     * @param day
     *            the date for which the containing pay period should be returned
     * 
     * @return the pay period containing the specified day, guaranteed to return a pay period if there are any available
     *         for the specified company such that a pay period containing the specified day can be derived, otherwise
     *         returns {@code null}
     * 
     * @throws IllegalArgumentException
     *             if the provided company or date is invalid
     * @throws DatabaseException
     *             if there is a problem communicating with the database
     */
    PayPeriod getContaining(final Integer companyId, final Date day) throws DatabaseException;

    /**
     * @param companyId
     *            the unique id of the company for which pay period information should be retrieved
     * 
     * @return the pay period containing today, guaranteed to return a pay period if there are any available for the
     *         specified company such that the current pay period can be derived, otherwise returns {@code null}
     * 
     * @throws IllegalArgumentException
     *             if the provided id is invalid
     * @throws DatabaseException
     *             if there is a problem communicating with the database
     */
    PayPeriod getCurrent(final Integer companyId) throws DatabaseException;

    /**
     * @param companyId
     *            the unique id of the company for which pay period information should be retrieved
     * 
     * @return the pay period with the lowest begin date for the specified company, possibly {@code null} if no pay
     *         period information is available
     * 
     * @throws IllegalArgumentException
     *             if the provided id is invalid
     * @throws DatabaseException
     *             if there is a problem communicating with the database
     */
    PayPeriod getEarliest(final Integer companyId) throws DatabaseException;

    /**
     * @param companyId
     *            the unique id of the company for which pay period information should be retrieved
     * 
     * @return the pay period with the highest begin date for the specified company, possibly {@code null} if no pay
     *         period information is available
     * 
     * @throws IllegalArgumentException
     *             if the provided id is invalid
     * @throws DatabaseException
     *             if there is a problem communicating with the database
     */
    PayPeriod getLatest(final Integer companyId) throws DatabaseException;

    /**
     * @param payPeriods
     *            the new pay periods to be inserted
     * 
     * @return the number if records inserted
     * 
     * @throws IllegalArgumentException
     *             if the provided pay periods are invalid
     * @throws DatabaseException
     *             if there is a problem communicating with the database
     */
    int add(final PayPeriod... payPeriods) throws DatabaseException;

    /**
     * @param payPeriods
     *            the new pay periods to be inserted
     * 
     * @return the number if records inserted
     * 
     * @throws IllegalArgumentException
     *             if the provided pay periods are invalid
     * @throws DatabaseException
     *             if there is a problem communicating with the database
     */
    int add(final Collection<PayPeriod> payPeriods) throws DatabaseException;
}
