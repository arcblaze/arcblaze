package com.arcblaze.arctime.db.dao.jdbc;

import static org.apache.commons.lang.Validate.notNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import com.arcblaze.arccore.common.model.User;
import com.arcblaze.arccore.db.ConnectionManager;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arctime.common.model.Assignment;
import com.arcblaze.arctime.common.model.PayPeriod;
import com.arcblaze.arctime.common.model.Task;
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

		final Set<String> labels = new HashSet<>();
		final ResultSetMetaData meta = rs.getMetaData();
		for (int i = 1; i < meta.getColumnCount(); i++)
			labels.add(meta.getColumnLabel(i).toLowerCase());

		if (labels.contains("login")) {
			final User user = new User();
			user.setId(rs.getInt("user_id"));
			user.setLogin(rs.getString("login"));
			user.setEmail(rs.getString("email"));
			user.setFirstName(rs.getString("first_name"));
			user.setLastName(rs.getString("last_name"));
			assignment.setUser(user);
		}

		if (labels.contains("job_code")) {
			final Task task = new Task();
			task.setId(rs.getInt("task_id"));
			task.setDescription(rs.getString("description"));
			task.setJobCode(rs.getString("job_code"));
			task.setAdministrative(rs.getBoolean("admin"));
			assignment.setTask(task);
		}

		return assignment;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Assignment get(final Integer assignmentId) throws DatabaseException {
		notNull(assignmentId, "Invalid null assignment id");

		final String sql = "SELECT a.*, u.id AS user_id, u.login, u.email, "
				+ "u.first_name, u.last_name, t.id AS task_id, t.description, "
				+ "t.job_code, t.admin FROM assignments a "
				+ "JOIN users u ON (a.user_id = u.id) "
				+ "JOIN tasks t ON (a.task_id = t.id) WHERE a.id = ?";

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
	public Set<Assignment> getForUser(final Integer companyId,
			final Integer userId, final Date day) throws DatabaseException {
		notNull(userId, "Invalid null user id");

		final StringBuilder sql = new StringBuilder();
		sql.append("SELECT a.*, u.id AS user_id, u.login, u.email, ");
		sql.append("u.first_name, u.last_name, t.id AS task_id, ");
		sql.append("t.description, t.job_code, t.admin FROM assignments a ");
		sql.append("JOIN users u ON (a.user_id = u.id) ");
		sql.append("JOIN tasks t ON (a.task_id = t.id) WHERE a.user_id = ? ");
		sql.append("AND a.company_id = ? ");
		if (day != null) {
			sql.append(" AND (begin IS NULL OR begin <= ?)");
			sql.append(" AND (end IS NULL OR end >= ?)");
		}

		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql
						.toString())) {
			ps.setInt(1, userId);
			ps.setInt(2, companyId);
			if (day != null) {
				ps.setTimestamp(3, new Timestamp(day.getTime()));
				ps.setTimestamp(4, new Timestamp(day.getTime()));
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
	 * {@inheritDoc}
	 */
	@Override
	public Set<Assignment> getForTask(final Integer companyId,
			final Integer taskId, final Date day) throws DatabaseException {
		notNull(taskId, "Invalid null task id");

		final StringBuilder sql = new StringBuilder();
		sql.append("SELECT a.*, u.id AS user_id, u.login, u.email, ");
		sql.append("u.first_name, u.last_name, t.id AS task_id, ");
		sql.append("t.description, t.job_code, t.admin FROM assignments a ");
		sql.append("JOIN users u ON (a.user_id = u.id) ");
		sql.append("JOIN tasks t ON (a.task_id = t.id) WHERE a.task_id = ? ");
		sql.append("AND a.company_id = ? ");
		if (day != null) {
			sql.append(" AND (begin IS NULL OR begin <= ?)");
			sql.append(" AND (end IS NULL OR end >= ?)");
		}

		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql
						.toString())) {
			ps.setInt(1, taskId);
			ps.setInt(2, companyId);
			if (day != null) {
				ps.setTimestamp(3, new Timestamp(day.getTime()));
				ps.setTimestamp(4, new Timestamp(day.getTime()));
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

		final StringBuilder sql = new StringBuilder();
		sql.append("SELECT a.*, u.id AS user_id, u.login, u.email, ");
		sql.append("u.first_name, u.last_name, t.id AS task_id, ");
		sql.append("t.description, t.job_code, t.admin FROM assignments a ");
		sql.append("JOIN users u ON (a.user_id = u.id) ");
		sql.append("JOIN tasks t ON (a.task_id = t.id) WHERE a.user_id = ? ");
		sql.append("AND (begin IS NULL OR begin <= ?) ");
		sql.append("AND (end IS NULL OR end >= ?)");

		try (final PreparedStatement ps = conn.prepareStatement(sql.toString())) {
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
	public int add(final Assignment... assignments) throws DatabaseException {
		return this
				.add(assignments == null ? null : Arrays.asList(assignments));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int add(final Collection<Assignment> assignments)
			throws DatabaseException {
		if (assignments == null || assignments.isEmpty())
			return 0;

		final StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO assignments (company_id, task_id, user_id, ");
		sql.append("labor_cat, item_name, begin, end) ");
		sql.append("SELECT u.company_id, t.id, u.id, ?, ?, ?, ? ");
		sql.append("FROM users u JOIN tasks t ON (u.company_id = t.company_id) ");
		sql.append("WHERE u.company_id = ? AND t.id = ? AND u.id = ?");

		int count = 0;
		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(
						sql.toString(), Statement.RETURN_GENERATED_KEYS)) {
			for (final Assignment assignment : assignments) {
				int index = 1;
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
				ps.setInt(index++, assignment.getCompanyId());
				ps.setInt(index++, assignment.getTaskId());
				ps.setInt(index++, assignment.getUserId());
				count += ps.executeUpdate();

				try (final ResultSet rs = ps.getGeneratedKeys()) {
					if (rs.next())
						assignment.setId(rs.getInt(1));
				}
			}
		} catch (final SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
		return count;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int update(final Assignment... assignments) throws DatabaseException {
		return this.update(assignments == null ? null : Arrays
				.asList(assignments));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int update(final Collection<Assignment> assignments)
			throws DatabaseException {
		if (assignments == null || assignments.isEmpty())
			return 0;

		final String sql = "UPDATE assignments SET task_id = ?, user_id = ?, "
				+ "labor_cat = ?, item_name = ?, begin = ?, end = ? "
				+ "WHERE id = ?";

		int count = 0;
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
				count += ps.executeUpdate();
			}
		} catch (final SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
		return count;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int delete(final Integer companyId, final Integer... ids)
			throws DatabaseException {
		return this.delete(companyId, ids == null ? null : Arrays.asList(ids));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int delete(final Integer companyId, final Collection<Integer> ids)
			throws DatabaseException {
		if (ids == null || ids.isEmpty())
			return 0;

		final String sql = "DELETE FROM assignments "
				+ "WHERE company_id = ? AND id = ?";

		int count = 0;
		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql)) {
			for (final Integer id : ids) {
				ps.setInt(1, companyId);
				ps.setInt(2, id);
				count += ps.executeUpdate();
			}
		} catch (final SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
		return count;
	}
}
