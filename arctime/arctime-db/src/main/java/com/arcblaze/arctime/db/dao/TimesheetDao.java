package com.arcblaze.arctime.db.dao;

import java.util.Collection;
import java.util.Set;

import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arctime.model.Enrichment;
import com.arcblaze.arctime.model.PayPeriod;
import com.arcblaze.arctime.model.Timesheet;
import com.arcblaze.arctime.model.util.HolidayConfigurationException;

/**
 * Performs operations on timesheets in the system.
 */
public interface TimesheetDao {
	/**
	 * @param companyId
	 *            the unique id of the company for which timesheet information
	 *            will be retrieved
	 * @param timesheetId
	 *            the unique id of the timesheet to be retrieved
	 * @param enrichments
	 *            the types of additional data to include in the returned
	 *            timesheets
	 * 
	 * @return the requested timesheet, possibly {@code null} if not found
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided parameters are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 * @throws HolidayConfigurationException
	 *             if there is a problem parsing holiday configurations during
	 *             enrichment
	 */
	Timesheet get(final Integer companyId, final Integer timesheetId,
			final Enrichment... enrichments) throws DatabaseException,
			HolidayConfigurationException;

	/**
	 * @param companyId
	 *            the unique id of the company for which timesheet information
	 *            will be retrieved
	 * @param timesheetId
	 *            the unique id of the timesheet to be retrieved
	 * @param enrichments
	 *            the types of additional data to include in the returned
	 *            timesheets
	 * 
	 * @return the requested timesheet, possibly {@code null} if not found
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided parameters are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 * @throws HolidayConfigurationException
	 *             if there is a problem parsing holiday configurations during
	 *             enrichment
	 */
	Timesheet get(final Integer companyId, final Integer timesheetId,
			final Set<Enrichment> enrichments) throws DatabaseException,
			HolidayConfigurationException;

	/**
	 * @param companyId
	 *            the unique id of the company for which timesheet information
	 *            will be retrieved
	 * @param timesheetIds
	 *            the unique ids of the timesheets to be retrieved
	 * @param enrichments
	 *            the types of additional data to include in the returned
	 *            timesheets
	 * 
	 * @return the requested timesheets, possibly empty but never {@code null}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided parameters are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 * @throws HolidayConfigurationException
	 *             if there is a problem parsing holiday configurations during
	 *             enrichment
	 */
	Set<Timesheet> get(final Integer companyId,
			final Set<Integer> timesheetIds, final Enrichment... enrichments)
			throws DatabaseException, HolidayConfigurationException;

	/**
	 * @param companyId
	 *            the unique id of the company for which timesheet information
	 *            will be retrieved
	 * @param timesheetIds
	 *            the unique ids of the timesheets to be retrieved
	 * @param enrichments
	 *            the types of additional data to include in the returned
	 *            timesheets
	 * 
	 * @return the requested timesheets, possibly empty but never {@code null}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided parameters are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 * @throws HolidayConfigurationException
	 *             if there is a problem parsing holiday configurations during
	 *             enrichment
	 */
	Set<Timesheet> get(final Integer companyId,
			final Set<Integer> timesheetIds, final Set<Enrichment> enrichments)
			throws DatabaseException, HolidayConfigurationException;

	/**
	 * @param userId
	 *            the unique id of the user that owns the timesheet to be
	 *            retrieved
	 * @param payPeriod
	 *            the pay period for which timesheet data will be returned
	 * @param enrichments
	 *            the types of additional data to include in the returned
	 *            timesheets
	 * 
	 * @return the requested timesheet, possibly {@code null} if not found
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided parameters are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 * @throws HolidayConfigurationException
	 *             if there is a problem parsing holiday configurations during
	 *             enrichment
	 */
	Timesheet getForUser(final Integer userId, final PayPeriod payPeriod,
			final Enrichment... enrichments) throws DatabaseException,
			HolidayConfigurationException;

	/**
	 * @param userId
	 *            the unique id of the user that owns the timesheet to be
	 *            retrieved
	 * @param payPeriod
	 *            the pay period for which timesheet data will be returned
	 * @param enrichments
	 *            the types of additional data to include in the returned
	 *            timesheets
	 * 
	 * @return the requested timesheet, possibly {@code null} if not found
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided parameters are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 * @throws HolidayConfigurationException
	 *             if there is a problem parsing holiday configurations during
	 *             enrichment
	 */
	Timesheet getForUser(final Integer userId, final PayPeriod payPeriod,
			final Set<Enrichment> enrichments) throws DatabaseException,
			HolidayConfigurationException;

	/**
	 * @param userId
	 *            the unique id of the user that owns the timesheet to be
	 *            retrieved
	 * @param enrichments
	 *            the types of additional data to include in the returned
	 *            timesheets
	 * 
	 * @return the requested timesheet, possibly {@code null} if not found
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided parameters are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 * @throws HolidayConfigurationException
	 *             if there is a problem parsing holiday configurations during
	 *             enrichment
	 */
	Timesheet getLatestForUser(final Integer userId,
			final Enrichment... enrichments) throws DatabaseException,
			HolidayConfigurationException;

	/**
	 * @param userId
	 *            the unique id of the user that owns the timesheet to be
	 *            retrieved
	 * @param enrichments
	 *            the types of additional data to include in the returned
	 *            timesheets
	 * 
	 * @return the requested timesheet, possibly {@code null} if not found
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id is invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 * @throws HolidayConfigurationException
	 *             if there is a problem parsing holiday configurations during
	 *             enrichment
	 */
	Timesheet getLatestForUser(final Integer userId,
			final Set<Enrichment> enrichments) throws DatabaseException,
			HolidayConfigurationException;

	/**
	 * @param timesheets
	 *            the new timesheets to be added
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided timesheets are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void add(final Timesheet... timesheets) throws DatabaseException;

	/**
	 * @param timesheets
	 *            the new timesheets to be added
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided parameters are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void add(final Collection<Timesheet> timesheets) throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which timesheet information
	 *            will be updated
	 * @param completed
	 *            the new value for the completed status
	 * @param timesheetIds
	 *            the unique ids of the timesheets for which completion status
	 *            will be updated
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided parameters are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void complete(final Integer companyId, final boolean completed,
			final Integer... timesheetIds) throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which timesheet information
	 *            will be updated
	 * @param completed
	 *            the new value for the completed status
	 * @param timesheetIds
	 *            the unique ids of the timesheets for which completion status
	 *            will be updated
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided parameters are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void complete(final Integer companyId, final boolean completed,
			final Collection<Integer> timesheetIds) throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which timesheet information
	 *            will be updated
	 * @param approverId
	 *            the unique id of the supervisor user that approved the
	 *            timesheets
	 * @param approved
	 *            the new value for the approved status
	 * @param timesheetIds
	 *            the unique ids of the timesheets for which approval status
	 *            will be updated
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided parameters are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void approve(final Integer companyId, final Integer approverId,
			final boolean approved, final Integer... timesheetIds)
			throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which timesheet information
	 *            will be updated
	 * @param approverId
	 *            the unique id of the supervisor user that approved the
	 *            timesheets
	 * @param approved
	 *            the new value for the approved status
	 * @param timesheetIds
	 *            the unique ids of the timesheets for which approval status
	 *            will be updated
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided parameters are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void approve(final Integer companyId, final Integer approverId,
			final boolean approved, final Collection<Integer> timesheetIds)
			throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which timesheet information
	 *            will be updated
	 * @param verifierId
	 *            the unique id of the payroll user that verified the timesheets
	 * @param verified
	 *            the new value for the verified status
	 * @param timesheetIds
	 *            the unique ids of the timesheets for which verification status
	 *            will be updated
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided parameters are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void verify(final Integer companyId, final Integer verifierId,
			final boolean verified, final Integer... timesheetIds)
			throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which timesheet information
	 *            will be updated
	 * @param verifierId
	 *            the unique id of the payroll user that verified the timesheets
	 * @param verified
	 *            the new value for the verified status
	 * @param timesheetIds
	 *            the unique ids of the timesheets for which verification status
	 *            will be updated
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided parameters are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void verify(final Integer companyId, final Integer verifierId,
			final boolean verified, final Collection<Integer> timesheetIds)
			throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which timesheet information
	 *            will be updated
	 * @param exporterId
	 *            the unique id of the payroll user that exported the timesheets
	 * @param exported
	 *            the new value for the export status
	 * @param timesheetIds
	 *            the unique ids of the timesheets for which export status will
	 *            be updated
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided parameters are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void export(final Integer companyId, final Integer exporterId,
			final boolean exported, final Integer... timesheetIds)
			throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which timesheet information
	 *            will be updated
	 * @param exporterId
	 *            the unique id of the payroll user that exported the timesheets
	 * @param exported
	 *            the new value for the export status
	 * @param timesheetIds
	 *            the unique ids of the timesheets for which export status will
	 *            be updated
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided parameters are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void export(final Integer companyId, final Integer exporterId,
			final boolean exported, final Collection<Integer> timesheetIds)
			throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which timesheet information
	 *            will be updated
	 * @param timesheetIds
	 *            the unique ids of the timesheets to be deleted
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided parameters are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void delete(final Integer companyId, final Integer... timesheetIds)
			throws DatabaseException;

	/**
	 * @param companyId
	 *            the unique id of the company for which timesheet information
	 *            will be updated
	 * @param timesheetIds
	 *            the unique ids of the timesheets to be deleted
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided parameters are invalid
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	void delete(final Integer companyId, final Collection<Integer> timesheetIds)
			throws DatabaseException;
}