package com.arcblaze.arctime.db.dao.jdbc;

import static org.apache.commons.lang.Validate.notNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.arcblaze.arccore.db.ConnectionManager;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arctime.common.model.AuditLog;
import com.arcblaze.arctime.db.dao.AuditLogDao;

/**
 * Manages audit logs within the back-end database.
 */
public class JdbcAuditLogDao implements AuditLogDao {
	/** Used to retrieve database connection objects. */
	private final ConnectionManager connectionManager;

	/**
	 * @param connectionManager
	 *            used to retrieve database connection objects
	 */
	public JdbcAuditLogDao(final ConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
	}

	protected AuditLog fromResultSet(ResultSet rs) throws SQLException {
		AuditLog auditLog = new AuditLog();
		auditLog.setCompanyId(rs.getInt("company_id"));
		auditLog.setTimesheetId(rs.getInt("timesheet_id"));
		auditLog.setLog(rs.getString("log"));
		auditLog.setTimestamp(rs.getTimestamp("timestamp"));
		return auditLog;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<AuditLog> getForTimesheet(final Integer timesheetId)
			throws DatabaseException {
		notNull(timesheetId, "Invalid null timesheet id");

		final String sql = "SELECT * FROM audit_logs WHERE timesheet_id = ?";

		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, timesheetId);
			try (final ResultSet rs = ps.executeQuery()) {
				final Set<AuditLog> auditLogs = new TreeSet<>();
				while (rs.next())
					auditLogs.add(fromResultSet(rs));
				return auditLogs;
			}
		} catch (final SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * Used for performing enrichment on timesheets.
	 */
	protected Map<Integer, Set<AuditLog>> getForTimesheets(
			final Connection conn, final Set<Integer> timesheetIds)
			throws DatabaseException {
		if (timesheetIds == null || timesheetIds.isEmpty())
			return Collections.emptyMap();
		notNull(conn, "Invalid null connection");

		final String sql = "SELECT * FROM audit_logs WHERE timesheet_id = ?";

		try (final PreparedStatement ps = conn.prepareStatement(sql)) {
			final Map<Integer, Set<AuditLog>> auditLogMap = new TreeMap<>();
			for (final Integer timesheetId : timesheetIds) {
				ps.setInt(1, timesheetId);
				try (final ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						final AuditLog auditLog = fromResultSet(rs);

						Set<AuditLog> auditLogs = auditLogMap.get(auditLog
								.getTimesheetId());
						if (auditLogs == null) {
							auditLogs = new TreeSet<>();
							auditLogMap.put(timesheetId, auditLogs);
						}
						auditLogs.add(auditLog);
					}
				}
			}

			return auditLogMap;
		} catch (final SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(AuditLog... auditLogs) throws DatabaseException {
		this.add(auditLogs == null ? null : Arrays.asList(auditLogs));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final Collection<AuditLog> auditLogs)
			throws DatabaseException {
		if (auditLogs == null || auditLogs.isEmpty())
			return;

		String sql = "INSERT INTO audit_logs (company_id, timesheet_id, log, "
				+ "timestamp) VALUES (?, ?, ?, ?)";

		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql)) {
			for (final AuditLog auditLog : auditLogs) {
				int index = 1;
				ps.setInt(index++, auditLog.getCompanyId());
				ps.setInt(index++, auditLog.getTimesheetId());
				ps.setString(index++, auditLog.getLog());
				ps.setTimestamp(index++, new Timestamp(auditLog.getTimestamp()
						.getTime()));
				ps.executeUpdate();
			}
		} catch (final SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete(final Integer... timesheetIds) throws DatabaseException {
		this.delete(timesheetIds == null ? null : Arrays.asList(timesheetIds));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete(final Collection<Integer> timesheetIds)
			throws DatabaseException {
		if (timesheetIds == null || timesheetIds.isEmpty())
			return;

		final String sql = "DELETE FROM audit_logs WHERE timesheet_id = ?";

		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql)) {
			for (final Integer timesheetId : timesheetIds) {
				ps.setInt(1, timesheetId);
				ps.executeUpdate();
			}
		} catch (final SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}
}
