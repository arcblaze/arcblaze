package com.arcblaze.arctime.db.dao.jdbc;

import static org.apache.commons.lang.Validate.notNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import com.arcblaze.arccore.db.ConnectionManager;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arctime.common.model.Assignment;
import com.arcblaze.arctime.common.model.PayPeriod;
import com.arcblaze.arctime.db.dao.AssignmentDao;

/**
 * Manages assignments within the back-end database.
 */
public class JdbcAssignmentDao implements AssignmentDao {
	/** Used to retrieve database connection objects. */
	private final ConnectionManager connectionManager;

	/**
	 * @param connectionManager
	 *            used to retrieve database connection objects
	 */
	public JdbcAssignmentDao(final ConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
	}

	protected static Assignment fromResultSet(final ResultSet rs)
			throws SQLException {
		final Assignment assignment = new Assignment();
		assignment.setId(rs.getInt("id"));
		assignment.setCompanyId(rs.getInt("company_id"));
		assignment.setTaskId(rs.getInt("task_id"));
		assignment.setUserId(rs.getInt("user_id"));
		assignment.setLaborCat(rs.getString("labor_cat"));
		assignment.setItemName(rs.getString("item_name"));
		final Date begin = rs.getDate("begin");
		if (begin != null)
			assignment.setBegin(begin);
		final Date end = rs.getDate("end");
		if (end != null)
			assignment.setEnd(end);
		return assignment;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Assignment get(final Integer assignmentId) throws DatabaseException {
		notNull(assignmentId, "Invalid null assignment id");

		final String sql = "SELECT * FROM assignments WHERE id = ?";

		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, assignmentId);
			try (final ResultSet rs = ps.executeQuery();) {
				if (rs.next())
					return fromResultSet(rs);
			}
			return null;
		} catch (final SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<Assignment> getForUser(final Integer userId, final Date day)
			throws DatabaseException {
		notNull(userId, "Invalid null user id");

		String sql = "SELECT * FROM assignments WHERE user_id = ?";
		if (day != null) {
			sql += " AND (begin IS NULL OR begin <= ?)";
			sql += " AND (end IS NULL OR end >= ?)";
		}

		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, userId);
			if (day != null) {
				ps.setTimestamp(2, new Timestamp(day.getTime()));
				ps.setTimestamp(3, new Timestamp(day.getTime()));
			}

			final Set<Assignment> assignments = new TreeSet<>();
			try (final ResultSet rs = ps.executeQuery()) {
				while (rs.next())
					assignments.add(fromResultSet(rs));
			}

			return assignments;
		} catch (final SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * Used for timesheet enrichment.
	 */
	protected Set<Assignment> getForPayPeriod(final Connection conn,
			final Integer userId, final PayPeriod payPeriod)
			throws DatabaseException {
		notNull(conn, "Invalid null connection");
		notNull(userId, "Invalid null user id");
		notNull(payPeriod, "Invalid null pay period");

		final String sql = "SELECT * FROM assignments WHERE user_id = ? AND "
				+ "(begin IS NULL OR begin <= ?) AND "
				+ "(end IS NULL OR end >= ?)";

		try (final PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, userId);
			ps.setTimestamp(2, new Timestamp(payPeriod.getEnd().getTime()));
			ps.setTimestamp(3, new Timestamp(payPeriod.getBegin().getTime()));
			final Set<Assignment> assignments = new TreeSet<>();
			try (final ResultSet rs = ps.executeQuery()) {
				while (rs.next())
					assignments.add(fromResultSet(rs));
			}
			return assignments;
		} catch (final SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final Assignment... assignments) throws DatabaseException {
		this.add(assignments == null ? null : Arrays.asList(assignments));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final Collection<Assignment> assignments)
			throws DatabaseException {
		if (assignments == null || assignments.isEmpty())
			return;

		final String sql = "INSERT INTO assignments (company_id, task_id, "
				+ "user_id, labor_cat, item_name, begin, end) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?)";

		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql,
						Statement.RETURN_GENERATED_KEYS)) {
			for (final Assignment assignment : assignments) {
				int index = 1;
				ps.setInt(index++, assignment.getCompanyId());
				ps.setInt(index++, assignment.getTaskId());
				ps.setInt(index++, assignment.getUserId());
				ps.setString(index++, assignment.getLaborCat());
				ps.setString(index++, assignment.getItemName());
				if (assignment.getBegin() == null)
					ps.setNull(index++, Types.DATE);
				else
					ps.setTimestamp(index++, new Timestamp(assignment
							.getBegin().getTime()));
				if (assignment.getEnd() == null)
					ps.setNull(index++, Types.DATE);
				else
					ps.setTimestamp(index++, new Timestamp(assignment.getEnd()
							.getTime()));
				ps.executeUpdate();

				try (final ResultSet rs = ps.getGeneratedKeys()) {
					if (rs.next())
						assignment.setId(rs.getInt(1));
				}
			}
		} catch (final SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(final Assignment... assignments)
			throws DatabaseException {
		this.update(assignments == null ? null : Arrays.asList(assignments));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(final Collection<Assignment> assignments)
			throws DatabaseException {
		if (assignments == null || assignments.isEmpty())
			return;

		final String sql = "UPDATE assignments SET task_id = ?, user_id = ?, "
				+ "labor_cat = ?, item_name = ?, begin = ?, end = ? "
				+ "WHERE id = ?";

		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql)) {
			for (final Assignment assignment : assignments) {
				int index = 1;
				ps.setInt(index++, assignment.getTaskId());
				ps.setInt(index++, assignment.getUserId());
				ps.setString(index++, assignment.getLaborCat());
				ps.setString(index++, assignment.getItemName());
				if (assignment.getBegin() == null)
					ps.setNull(index++, Types.DATE);
				else
					ps.setTimestamp(index++, new Timestamp(assignment
							.getBegin().getTime()));
				if (assignment.getEnd() == null)
					ps.setNull(index++, Types.DATE);
				else
					ps.setTimestamp(index++, new Timestamp(assignment.getEnd()
							.getTime()));
				ps.setInt(index++, assignment.getId());
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
	public void delete(final Integer... ids) throws DatabaseException {
		this.delete(ids == null ? null : Arrays.asList(ids));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete(final Collection<Integer> ids) throws DatabaseException {
		if (ids == null || ids.isEmpty())
			return;

		final String sql = "DELETE FROM assignments WHERE id = ?";

		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql)) {
			for (final Integer id : ids) {
				ps.setInt(1, id);
				ps.executeUpdate();
			}
		} catch (final SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}
}
