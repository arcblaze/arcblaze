package com.arcblaze.arctime.db.dao.jdbc;

import static org.apache.commons.lang.Validate.notNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import com.arcblaze.arccore.db.ConnectionManager;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arccore.db.dao.jdbc.JdbcUserDao;
import com.arcblaze.arctime.common.model.Supervisor;
import com.arcblaze.arctime.db.dao.SupervisorDao;

/**
 * Manages supervisors within the back-end database.
 */
public class JdbcSupervisorDao extends JdbcUserDao implements SupervisorDao {
	/** Used to retrieve database connection objects. */
	private final ConnectionManager connectionManager;

	/**
	 * @param connectionManager
	 *            used to retrieve database connection objects
	 */
	public JdbcSupervisorDao(final ConnectionManager connectionManager) {
		super(connectionManager);
		this.connectionManager = connectionManager;
	}

	@Override
	protected Supervisor fromResultSet(final ResultSet rs,
			final boolean includePass) throws SQLException {
		final Supervisor supervisor = new Supervisor(super.fromResultSet(rs,
				includePass));
		supervisor.setPrimary(rs.getBoolean("is_primary"));
		return supervisor;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<Supervisor> getSupervisors(final Integer companyId,
			final Integer userId) throws DatabaseException {
		notNull(companyId, "Invalid null company id");
		notNull(userId, "Invalid null user id");

		final String sql = "SELECT * FROM supervisors s JOIN users e ON "
				+ "(s.supervisor_id = e.id AND s.company_id = e.company_id) "
				+ "WHERE s.company_id = ? AND s.user_id = ?";

		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, companyId);
			ps.setInt(2, userId);
			try (final ResultSet rs = ps.executeQuery()) {
				final Set<Supervisor> supervisors = new TreeSet<>();
				while (rs.next())
					supervisors.add(fromResultSet(rs, false));
				return supervisors;
			}
		} catch (final SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final Integer companyId, final Integer userId,
			final boolean primary, final Integer... supervisorIds)
			throws DatabaseException {
		this.add(companyId, userId, primary, supervisorIds == null ? null
				: Arrays.asList(supervisorIds));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final Integer companyId, final Integer userId,
			final boolean primary, final Collection<Integer> supervisorIds)
			throws DatabaseException {
		if (supervisorIds == null || supervisorIds.isEmpty())
			return;
		notNull(companyId, "Invalid null company id");
		notNull(userId, "Invalid null user id");

		final String sql = "INSERT INTO supervisors (company_id, user_id, "
				+ "supervisor_id, is_primary) VALUES (?, ?, ?, ?)";

		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql)) {
			for (final Integer supervisorId : supervisorIds) {
				ps.setInt(1, companyId);
				ps.setInt(2, userId);
				ps.setInt(3, supervisorId);
				ps.setBoolean(4, primary);
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
	public void delete(final Integer companyId, final Integer userId,
			final Integer... supervisorIds) throws DatabaseException {
		this.delete(companyId, userId,
				supervisorIds == null ? null : Arrays.asList(supervisorIds));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete(final Integer companyId, final Integer userId,
			final Collection<Integer> supervisorIds) throws DatabaseException {
		if (supervisorIds == null || supervisorIds.isEmpty())
			return;
		notNull(companyId, "Invalid null company id");
		notNull(userId, "Invalid null user id");

		final String sql = "DELETE FROM supervisors WHERE company_id = ? AND "
				+ "user_id = ? AND supervisor_id = ?";

		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql)) {
			for (final Integer supervisorId : supervisorIds) {
				ps.setInt(1, companyId);
				ps.setInt(2, userId);
				ps.setInt(3, supervisorId);
				ps.executeUpdate();
			}
		} catch (final SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}
}
