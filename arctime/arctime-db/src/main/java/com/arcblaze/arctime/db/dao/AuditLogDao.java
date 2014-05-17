package com.arcblaze.arctime.db.dao;

import java.util.Collection;
import java.util.Set;

import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arctime.common.model.AuditLog;

/**
 * Performs operations on audit logs in the system.
 */
public interface AuditLogDao {
    /**
     * @param timesheetId
     *            the unique id of the timesheet for which audit logs will be retrieved
     * 
     * @return all available audit logs for the provided timesheet, possibly empty but never {@code null}
     * 
     * @throws IllegalArgumentException
     *             if the provided ids are invalid
     * @throws DatabaseException
     *             if there is a problem communicating with the database
     */
    Set<AuditLog> getForTimesheet(final Integer timesheetId) throws DatabaseException;

    /**
     * @param auditLogs
     *            the new audit logs to be added
     * 
     * @return the number if records inserted
     * 
     * @throws IllegalArgumentException
     *             if the provided audit logs are invalid
     * @throws DatabaseException
     *             if there is a problem communicating with the database
     */
    int add(final AuditLog... auditLogs) throws DatabaseException;

    /**
     * @param auditLogs
     *            the new audit logs to be added
     * 
     * @return the number if records inserted
     * 
     * @throws IllegalArgumentException
     *             if the provided audit logs are invalid
     * @throws DatabaseException
     *             if there is a problem communicating with the database
     */
    int add(final Collection<AuditLog> auditLogs) throws DatabaseException;

    /**
     * @param timesheetIds
     *            the unique ids of the timesheets for which audit logs will be deleted
     * 
     * @return the number if records deleted
     * 
     * @throws IllegalArgumentException
     *             if the provided ids are invalid
     * @throws DatabaseException
     *             if there is a problem communicating with the database
     */
    int delete(final Integer... timesheetIds) throws DatabaseException;

    /**
     * @param timesheetIds
     *            the unique ids of the timesheets for which audit logs will be deleted
     * 
     * @return the number if records deleted
     * 
     * @throws IllegalArgumentException
     *             if the provided ids are invalid
     * @throws DatabaseException
     *             if there is a problem communicating with the database
     */
    int delete(final Collection<Integer> timesheetIds) throws DatabaseException;
}
