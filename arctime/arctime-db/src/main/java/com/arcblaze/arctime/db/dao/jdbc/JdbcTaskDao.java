package com.arcblaze.arctime.db.dao.jdbc;

import static org.apache.commons.lang.Validate.notNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.arcblaze.arccore.db.ConnectionManager;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arctime.common.model.Assignment;
import com.arcblaze.arctime.common.model.PayPeriod;
import com.arcblaze.arctime.common.model.Task;
import com.arcblaze.arctime.db.dao.TaskDao;

/**
 * Manages tasks within the back-end database.
 */
public class JdbcTaskDao implements TaskDao {
	/** Used to retrieve database connection objects. */
	private final ConnectionManager connectionManager;

	/**
	 * @param connectionManager
	 *            used to retrieve database connection objects
	 */
	public JdbcTaskDao(final ConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
	}

	protected Task fromResultSet(final ResultSet rs) throws SQLException {
		final Task task = new Task();
		task.setId(rs.getInt("id"));
		task.setCompanyId(rs.getInt("company_id"));
		task.setDescription(rs.getString("description"));
		task.setJobCode(rs.getString("job_code"));
		task.setAdministrative(rs.getBoolean("admin"));
		task.setActive(rs.getBoolean("active"));
		return task;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Task get(final Integer companyId, final Integer taskId)
			throws DatabaseException {
		notNull(companyId, "Invalid null company id");
		notNull(taskId, "Invalid null task id");

		final String sql = "SELECT * FROM tasks "
				+ "WHERE company_id = ? AND id = ?";

		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, companyId);
			ps.setInt(2, taskId);
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
	public Set<Task> getAll(final Integer companyId) throws DatabaseException {
		notNull(companyId, "Invalid null company id");

		final String sql = "SELECT * FROM tasks WHERE company_id = ?";

		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, companyId);
			final Set<Task> tasks = new TreeSet<>();
			try (final ResultSet rs = ps.executeQuery()) {
				while (rs.next())
					tasks.add(fromResultSet(rs));
			}
			return tasks;
		} catch (final SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<Task> getForPayPeriod(final Integer userId,
			final PayPeriod payPeriod) throws DatabaseException {
		notNull(userId, "Invalid null user id");
		notNull(payPeriod, "Invalid null pay period");

		final String sql = "SELECT t.company_id, t.id, description, job_code, "
				+ "admin, active FROM tasks t LEFT JOIN assignments a ON "
				+ "(a.task_id = t.id) WHERE (a.user_id = ? OR t.admin = true) "
				+ "AND (begin IS NULL OR begin <= ?) "
				+ "AND (end IS NULL OR end >= ?)";

		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, userId);
			ps.setDate(2, new java.sql.Date(payPeriod.getEnd().getTime()));
			ps.setDate(3, new java.sql.Date(payPeriod.getBegin().getTime()));
			final Set<Task> tasks = new TreeSet<>();
			try (final ResultSet rs = ps.executeQuery()) {
				while (rs.next())
					tasks.add(fromResultSet(rs));
			}
			return tasks;
		} catch (final SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * Used to enrich timesheets with task information.
	 */
	protected Map<Integer, Set<Task>> getForPayPeriod(final Connection conn,
			final PayPeriod payPeriod, final Set<Integer> userIds)
			throws DatabaseException {
		if (userIds == null || userIds.isEmpty())
			return Collections.emptyMap();
		notNull(conn, "Invalid null connection");
		notNull(payPeriod, "Invalid null pay period");

		final String sql = "SELECT t.id, t.id AS task_id, t.company_id, "
				+ "description, job_code, admin, active, a.id AS assmnt_id, "
				+ "user_id, labor_cat, item_name, begin, end "
				+ "FROM tasks t LEFT JOIN assignments a ON (a.task_id = t.id) "
				+ "WHERE (a.user_id = ? OR t.admin = TRUE) AND "
				+ "(begin IS NULL OR begin <= ?) AND "
				+ "(end IS NULL OR end >= ?) AND active = TRUE";

		try (final PreparedStatement ps = conn.prepareStatement(sql)) {
			final Map<Integer, Set<Task>> userTaskMap = new TreeMap<>();
			for (final Integer userId : userIds) {
				ps.setInt(1, userId);
				ps.setDate(2, new java.sql.Date(payPeriod.getEnd().getTime()));
				ps.setDate(3, new java.sql.Date(payPeriod.getBegin().getTime()));

				final Map<Integer, Task> taskMap = new HashMap<>();
				try (final ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						final int taskId = rs.getInt("task_id");
						Task task = taskMap.get(taskId);
						if (task == null) {
							task = fromResultSet(rs);
							task.setId(taskId);
							taskMap.put(taskId, task);
						}

						Assignment assignment = null;
						final int assignmentId = rs.getInt("assmnt_id");
						if (!rs.wasNull()) {
							assignment = JdbcAssignmentDao.fromResultSet(rs);
							assignment.setId(assignmentId);
							task.addAssignments(assignment);
						}

						Set<Task> tasks = userTaskMap.get(userId);
						if (tasks == null) {
							tasks = new TreeSet<>();
							userTaskMap.put(userId, tasks);
						}
						tasks.add(task);
					}
				}
			}
			return userTaskMap;
		} catch (final SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<Task> getForUser(final Integer userId, final Date day,
			final boolean includeAdmin) throws DatabaseException {
		notNull(userId, "Invalid null user id");

		final StringBuilder sql = new StringBuilder();
		sql.append("SELECT t.company_id, t.id, description, job_code, admin, "
				+ "active FROM tasks t LEFT JOIN assignments a ON "
				+ "(a.task_id = t.id) WHERE ");

		if (includeAdmin)
			sql.append("(a.user_id = ? OR t.admin = true)");
		else
			sql.append("a.user_id = ? AND t.admin = false");

		if (day != null) {
			sql.append(" AND (begin IS NULL OR begin <= ?)");
			sql.append(" AND (end IS NULL OR end >= ?)");
		}

		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql
						.toString())) {
			ps.setInt(1, userId);
			if (day != null) {
				ps.setTimestamp(2, new Timestamp(day.getTime()));
				ps.setTimestamp(3, new Timestamp(day.getTime()));
			}

			final Set<Task> tasks = new TreeSet<>();
			try (final ResultSet rs = ps.executeQuery()) {
				while (rs.next())
					tasks.add(fromResultSet(rs));
			}
			return tasks;
		} catch (final SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final Task... tasks) throws DatabaseException {
		this.add(tasks == null ? null : Arrays.asList(tasks));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final Collection<Task> tasks) throws DatabaseException {
		if (tasks == null || tasks.isEmpty())
			return;

		final String sql = "INSERT INTO tasks (company_id, description, "
				+ "job_code, admin, active) VALUES (?, ?, ?, ?, ?)";

		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql,
						Statement.RETURN_GENERATED_KEYS)) {
			for (final Task task : tasks) {
				int index = 1;
				ps.setInt(index++, task.getCompanyId());
				ps.setString(index++, task.getDescription());
				ps.setString(index++, task.getJobCode());
				ps.setBoolean(index++, task.isAdministrative());
				ps.setBoolean(index++, task.isActive());
				ps.executeUpdate();

				try (final ResultSet rs = ps.getGeneratedKeys()) {
					if (rs.next())
						task.setId(rs.getInt(1));
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
	public void update(final Task... tasks) throws DatabaseException {
		this.update(tasks == null ? null : Arrays.asList(tasks));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(final Collection<Task> tasks) throws DatabaseException {
		if (tasks == null || tasks.isEmpty())
			return;

		final String sql = "UPDATE tasks SET description = ?, job_code = ?, "
				+ "admin = ?, active = ? WHERE id = ?";

		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql)) {
			for (final Task task : tasks) {
				int index = 1;
				ps.setString(index++, task.getDescription());
				ps.setString(index++, task.getJobCode());
				ps.setBoolean(index++, task.isAdministrative());
				ps.setBoolean(index++, task.isActive());
				ps.setInt(index++, task.getId());
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
	public void delete(final Integer companyId, final Integer... ids)
			throws DatabaseException {
		this.delete(companyId, ids == null ? null : Arrays.asList(ids));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete(final Integer companyId, final Collection<Integer> ids)
			throws DatabaseException {
		if (ids == null || ids.isEmpty())
			return;

		final String sql = "DELETE FROM tasks WHERE company_id = ? AND id = ?";

		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql)) {
			for (final Integer id : ids) {
				ps.setInt(1, companyId);
				ps.setInt(2, id);
				ps.executeUpdate();
			}
		} catch (final SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}
}
