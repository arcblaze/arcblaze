package com.arcblaze.arctime.db;

import com.arcblaze.arccore.common.config.Config;
import com.arcblaze.arccore.db.ConnectionManager;
import com.arcblaze.arccore.db.DaoFactory;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arccore.db.DatabaseType;
import com.arcblaze.arctime.db.dao.AssignmentDao;
import com.arcblaze.arctime.db.dao.AuditLogDao;
import com.arcblaze.arctime.db.dao.BillDao;
import com.arcblaze.arctime.db.dao.HolidayDao;
import com.arcblaze.arctime.db.dao.PayPeriodDao;
import com.arcblaze.arctime.db.dao.TaskDao;
import com.arcblaze.arctime.db.dao.TimesheetDao;
import com.arcblaze.arctime.db.dao.jdbc.JdbcAssignmentDao;
import com.arcblaze.arctime.db.dao.jdbc.JdbcAuditLogDao;
import com.arcblaze.arctime.db.dao.jdbc.JdbcBillDao;
import com.arcblaze.arctime.db.dao.jdbc.JdbcHolidayDao;
import com.arcblaze.arctime.db.dao.jdbc.JdbcPayPeriodDao;
import com.arcblaze.arctime.db.dao.jdbc.JdbcTaskDao;
import com.arcblaze.arctime.db.dao.jdbc.JdbcTimesheetDao;

/**
 * Used to retrieve DAO instances to work with the configured back-end database.
 */
public class ArcTimeDaoFactory extends DaoFactory {
	private AssignmentDao cachedAssignmentDao = null;
	private AuditLogDao cachedAuditLogDao = null;
	private BillDao cachedBillDao = null;
	private HolidayDao cachedHolidayDao = null;
	private PayPeriodDao cachedPayPeriodDao = null;
	private TaskDao cachedTaskDao = null;
	private TimesheetDao cachedTimesheetDao = null;

	/**
	 * @param config
	 *            the system configuration information
	 */
	public ArcTimeDaoFactory(final Config config) {
		super(config);
	}

	/**
	 * @param connectionManager
	 *            a preconfigured connection manager
	 */
	public ArcTimeDaoFactory(final ConnectionManager connectionManager) {
		super(connectionManager);
	}

	/**
	 * @return an {@link AssignmentDao} based on the currently configured
	 *         database
	 */
	public AssignmentDao getAssignmentDao() {
		if (this.cachedAssignmentDao == null) {
			if (DatabaseType.JDBC.equals(getDatabaseType()))
				this.cachedAssignmentDao = new JdbcAssignmentDao(
						getConnectionManager());
			else
				throw new RuntimeException("Invalid database type: "
						+ getDatabaseType());
		}

		return this.cachedAssignmentDao;
	}

	/**
	 * @return an {@link AuditLogDao} based on the currently configured database
	 */
	public AuditLogDao getAuditLogDao() {
		if (this.cachedAuditLogDao == null) {
			if (DatabaseType.JDBC.equals(getDatabaseType()))
				this.cachedAuditLogDao = new JdbcAuditLogDao(
						getConnectionManager());
			else
				throw new RuntimeException("Invalid database type: "
						+ getDatabaseType());
		}

		return this.cachedAuditLogDao;
	}

	/**
	 * @return a {@link BillDao} based on the currently configured database
	 */
	public BillDao getBillDao() {
		if (this.cachedBillDao == null) {
			if (DatabaseType.JDBC.equals(getDatabaseType()))
				this.cachedBillDao = new JdbcBillDao(getConnectionManager());
			else
				throw new RuntimeException("Invalid database type: "
						+ getDatabaseType());
		}

		return this.cachedBillDao;
	}

	/**
	 * @return a {@link HolidayDao} based on the currently configured database
	 */
	public HolidayDao getHolidayDao() {
		if (this.cachedHolidayDao == null) {
			if (DatabaseType.JDBC.equals(getDatabaseType()))
				this.cachedHolidayDao = new JdbcHolidayDao(
						getConnectionManager());
			else
				throw new RuntimeException("Invalid database type: "
						+ getDatabaseType());
		}

		return this.cachedHolidayDao;
	}

	/**
	 * @return a {@link PayPeriodDao} based on the currently configured database
	 */
	public PayPeriodDao getPayPeriodDao() {
		if (this.cachedPayPeriodDao == null) {
			if (DatabaseType.JDBC.equals(getDatabaseType()))
				this.cachedPayPeriodDao = new JdbcPayPeriodDao(
						getConnectionManager());
			else
				throw new RuntimeException("Invalid database type: "
						+ getDatabaseType());
		}

		return this.cachedPayPeriodDao;
	}

	/**
	 * @return a {@link TaskDao} based on the currently configured database
	 */
	public TaskDao getTaskDao() {
		if (this.cachedTaskDao == null) {
			if (DatabaseType.JDBC.equals(getDatabaseType()))
				this.cachedTaskDao = new JdbcTaskDao(getConnectionManager());
			else
				throw new RuntimeException("Invalid database type: "
						+ getDatabaseType());
		}

		return this.cachedTaskDao;
	}

	/**
	 * @return a {@link TimesheetDao} based on the currently configured database
	 */
	public TimesheetDao getTimesheetDao() {
		if (this.cachedTimesheetDao == null) {
			if (DatabaseType.JDBC.equals(getDatabaseType()))
				this.cachedTimesheetDao = new JdbcTimesheetDao(
						getConnectionManager());
			else
				throw new RuntimeException("Invalid database type: "
						+ getDatabaseType());
		}

		return this.cachedTimesheetDao;
	}

	/**
	 * Close any resources associated with the internal DAOs.
	 * 
	 * @throws DatabaseException
	 *             if there is a problem closing resources
	 */
	@Override
	public void close() throws DatabaseException {
		super.close();
		this.cachedAssignmentDao = null;
		this.cachedAuditLogDao = null;
		this.cachedBillDao = null;
		this.cachedHolidayDao = null;
		this.cachedTaskDao = null;
		this.cachedTimesheetDao = null;
	}
}
