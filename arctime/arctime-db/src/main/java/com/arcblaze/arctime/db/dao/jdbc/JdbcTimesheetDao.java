package com.arcblaze.arctime.db.dao.jdbc;

import static org.apache.commons.lang.Validate.isTrue;
import static org.apache.commons.lang.Validate.notNull;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.arcblaze.arccore.common.model.User;
import com.arcblaze.arccore.db.ConnectionManager;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arccore.db.dao.jdbc.JdbcUserDao;
import com.arcblaze.arctime.common.model.Assignment;
import com.arcblaze.arctime.common.model.AuditLog;
import com.arcblaze.arctime.common.model.Bill;
import com.arcblaze.arctime.common.model.Enrichment;
import com.arcblaze.arctime.common.model.Holiday;
import com.arcblaze.arctime.common.model.PayPeriod;
import com.arcblaze.arctime.common.model.Task;
import com.arcblaze.arctime.common.model.Timesheet;
import com.arcblaze.arctime.common.model.util.HolidayConfigurationException;
import com.arcblaze.arctime.db.dao.TimesheetDao;

/**
 * Manages timesheets within the back-end database.
 */
public class JdbcTimesheetDao implements TimesheetDao {
	/** Used to retrieve database connection objects. */
	private final ConnectionManager connectionManager;

	/**
	 * @param connectionManager
	 *            used to retrieve database connection objects
	 */
	public JdbcTimesheetDao(final ConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
	}

	protected Timesheet fromResultSet(final ResultSet rs) throws SQLException {
		final Timesheet timesheet = new Timesheet();
		timesheet.setId(rs.getInt("id"));
		timesheet.setCompanyId(rs.getInt("company_id"));
		timesheet.setUserId(rs.getInt("user_id"));
		timesheet.setBegin(rs.getDate("pp_begin"));
		timesheet.setCompleted(rs.getBoolean("completed"));
		timesheet.setApproved(rs.getBoolean("approved"));
		timesheet.setVerified(rs.getBoolean("verified"));
		timesheet.setExported(rs.getBoolean("exported"));

		final int approverId = rs.getInt("approver_id");
		if (!rs.wasNull())
			timesheet.setApproverId(approverId);
		final int verifierId = rs.getInt("verifier_id");
		if (!rs.wasNull())
			timesheet.setVerifierId(verifierId);
		final int exporterId = rs.getInt("exporter_id");
		if (!rs.wasNull())
			timesheet.setExporterId(exporterId);
		return timesheet;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Timesheet get(final Integer companyId, final Integer timesheetId,
			final Enrichment... enrichments) throws DatabaseException,
			HolidayConfigurationException {
		final Set<Enrichment> enrichmentSet = enrichments == null ? null
				: new LinkedHashSet<>(Arrays.asList(enrichments));
		return this.get(companyId, timesheetId, enrichmentSet);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Timesheet get(final Integer companyId, final Integer timesheetId,
			final Set<Enrichment> enrichments) throws DatabaseException,
			HolidayConfigurationException {
		notNull(companyId, "Invalid null company id");
		notNull(timesheetId, "Invalid null timesheet id");

		final String sql = "SELECT t.* FROM timesheets t JOIN users e ON "
				+ "(t.user_id = e.id AND e.company_id = ?) WHERE t.id = ?";

		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, companyId);
			ps.setInt(2, timesheetId);
			try (final ResultSet rs = ps.executeQuery();) {
				if (rs.next()) {
					final Timesheet timesheet = fromResultSet(rs);
					enrich(conn, companyId, Collections.singleton(timesheet),
							enrichments);
					return timesheet;
				}
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
	public Set<Timesheet> get(final Integer companyId,
			final Set<Integer> timesheetIds, final Enrichment... enrichments)
			throws DatabaseException, HolidayConfigurationException {
		final Set<Enrichment> enrichmentSet = enrichments == null ? null
				: new LinkedHashSet<>(Arrays.asList(enrichments));
		return this.get(companyId, timesheetIds, enrichmentSet);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<Timesheet> get(final Integer companyId,
			final Set<Integer> timesheetIds, final Set<Enrichment> enrichments)
			throws DatabaseException, HolidayConfigurationException {
		if (timesheetIds == null || timesheetIds.isEmpty())
			return Collections.emptySet();
		notNull(companyId, "Invalid null company id");

		final String sql = "SELECT t.* FROM timesheets t JOIN users e ON "
				+ "(t.user_id = e.id AND e.company_id = ?) WHERE t.id = ?";

		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql)) {
			final Set<Timesheet> timesheets = new TreeSet<>();
			for (final Integer timesheetId : timesheetIds) {
				ps.setInt(1, companyId);
				ps.setInt(2, timesheetId);
				try (final ResultSet rs = ps.executeQuery()) {
					while (rs.next())
						timesheets.add(fromResultSet(rs));
				}
			}
			enrich(conn, companyId, timesheets, enrichments);
			return timesheets;
		} catch (final SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Timesheet getForUser(final Integer userId,
			final PayPeriod payPeriod, final Enrichment... enrichments)
			throws DatabaseException, HolidayConfigurationException {
		final Set<Enrichment> enrichmentSet = enrichments == null ? null
				: new LinkedHashSet<>(Arrays.asList(enrichments));
		return this.getForUser(userId, payPeriod, enrichmentSet);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Timesheet getForUser(final Integer userId,
			final PayPeriod payPeriod, final Set<Enrichment> enrichments)
			throws DatabaseException, HolidayConfigurationException {
		notNull(userId, "Invalid null user id");
		notNull(payPeriod, "Invalid null pay period");

		final String sql = "SELECT t.* FROM timesheets t JOIN users e ON "
				+ "(t.user_id = e.id) WHERE t.user_id = ? AND t.pp_begin = ?";

		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, userId);
			ps.setDate(2, new Date(payPeriod.getBegin().getTime()));

			try (final ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					final Timesheet timesheet = fromResultSet(rs);
					enrich(conn, timesheet.getCompanyId(),
							Collections.singleton(timesheet), enrichments);
					return timesheet;
				}
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
	public Timesheet getLatestForUser(final Integer userId,
			final Enrichment... enrichments) throws DatabaseException,
			HolidayConfigurationException {
		final Set<Enrichment> enrichmentSet = enrichments == null ? null
				: new LinkedHashSet<>(Arrays.asList(enrichments));
		return getLatestForUser(userId, enrichmentSet);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Timesheet getLatestForUser(final Integer userId,
			Set<Enrichment> enrichments) throws DatabaseException,
			HolidayConfigurationException {
		notNull(userId, "Invalid null user id");

		final String sql = "(SELECT t.* FROM timesheets t JOIN pay_periods p ON "
				+ "(t.pp_begin = p.begin AND t.company_id = p.company_id) "
				+ "JOIN bills b ON (b.day >= p.begin AND b.day <= p.end "
				+ "AND b.user_id = t.user_id) WHERE t.user_id = ? "
				+ "GROUP BY t.id ORDER BY completed, pp_begin DESC LIMIT 1) "
				+ "UNION (SELECT * FROM timesheets WHERE user_id = ? "
				+ "ORDER BY completed, pp_begin DESC LIMIT 1)";

		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, userId);
			ps.setInt(2, userId);
			try (final ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					final Timesheet timesheet = fromResultSet(rs);
					enrich(conn, timesheet.getCompanyId(),
							Collections.singleton(timesheet), enrichments);
					return timesheet;
				}
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
	public int add(final Timesheet... timesheets) throws DatabaseException {
		return this.add(timesheets == null ? null : Arrays.asList(timesheets));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int add(final Collection<Timesheet> timesheets)
			throws DatabaseException {
		if (timesheets == null || timesheets.isEmpty())
			return 0;

		final String sql = "INSERT INTO timesheets (company_id, user_id, "
				+ "pp_begin, completed, approved, verified, exported) VALUES "
				+ "(?, ?, ?, ?, ?, ?, ?)";

		int count = 0;
		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql,
						Statement.RETURN_GENERATED_KEYS)) {
			for (Timesheet timesheet : timesheets) {
				int index = 1;
				ps.setInt(index++, timesheet.getCompanyId());
				ps.setInt(index++, timesheet.getUserId());
				ps.setDate(index++, new Date(timesheet.getBegin().getTime()));
				ps.setBoolean(index++, timesheet.isCompleted());
				ps.setBoolean(index++, timesheet.isApproved());
				ps.setBoolean(index++, timesheet.isVerified());
				ps.setBoolean(index++, timesheet.isExported());
				count += ps.executeUpdate();

				try (final ResultSet rs = ps.getGeneratedKeys()) {
					if (rs.next())
						timesheet.setId(rs.getInt(1));
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
	public int complete(final Integer companyId, final boolean completed,
			final Integer... timesheetIds) throws DatabaseException {
		return this.complete(companyId, completed, timesheetIds == null ? null
				: Arrays.asList(timesheetIds));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int complete(final Integer companyId, final boolean completed,
			final Collection<Integer> timesheetIds) throws DatabaseException {
		if (timesheetIds == null || timesheetIds.isEmpty())
			return 0;
		notNull(companyId, "Invalid null company id");

		final String sql = "UPDATE timesheets SET completed = ? "
				+ "WHERE id = ? AND company_id = ?";

		int count = 0;
		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql)) {
			for (final Integer timesheetId : timesheetIds) {
				int index = 1;
				ps.setBoolean(index++, completed);
				ps.setInt(index++, timesheetId);
				ps.setInt(index++, companyId);
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
	public int approve(final Integer companyId, final Integer approverId,
			final boolean approved, final Integer... timesheetIds)
			throws DatabaseException {
		return this.approve(companyId, approverId, approved,
				timesheetIds == null ? null : Arrays.asList(timesheetIds));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int approve(final Integer companyId, final Integer approverId,
			final boolean approved, final Collection<Integer> timesheetIds)
			throws DatabaseException {
		if (timesheetIds == null || timesheetIds.isEmpty())
			return 0;
		notNull(companyId, "Invalid null company id");
		isTrue(!approved || approverId != null, "Invalid null approver id");

		final String sql = "UPDATE timesheets SET approved = ?, approver_id = ? "
				+ "WHERE id = ? AND company_id = ?";

		int count = 0;
		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql)) {
			for (final Integer timesheetId : timesheetIds) {
				int index = 1;
				ps.setBoolean(index++, approved);
				// approverId will always have a value when approved is true,
				// based on the above Validate.isTrue
				if (approved && approverId != null)
					ps.setInt(index++, approverId);
				else
					ps.setNull(index++, Types.INTEGER);
				ps.setInt(index++, timesheetId);
				ps.setInt(index++, companyId);
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
	public int verify(final Integer companyId, final Integer verifierId,
			final boolean verified, final Integer... timesheetIds)
			throws DatabaseException {
		return this.verify(companyId, verifierId, verified,
				timesheetIds == null ? null : Arrays.asList(timesheetIds));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int verify(final Integer companyId, final Integer verifierId,
			final boolean verified, final Collection<Integer> timesheetIds)
			throws DatabaseException {
		if (timesheetIds == null || timesheetIds.isEmpty())
			return 0;
		notNull(companyId, "Invalid null company id");
		isTrue(!verified || verifierId != null, "Invalid null verifier id");

		final String sql = "UPDATE timesheets SET verified = ?, verifier_id = ? "
				+ "WHERE id = ? AND company_id = ?";

		int count = 0;
		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql)) {
			for (final Integer timesheetId : timesheetIds) {
				int index = 1;
				ps.setBoolean(index++, verified);
				// verifierId will always have a value when verified is true,
				// based on the above Validate.isTrue
				if (verified && verifierId != null)
					ps.setInt(index++, verifierId);
				else
					ps.setNull(index++, Types.INTEGER);
				ps.setInt(index++, timesheetId);
				ps.setInt(index++, companyId);
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
	public int export(final Integer companyId, final Integer exporterId,
			final boolean exported, final Integer... timesheetIds)
			throws DatabaseException {
		return this.export(companyId, exporterId, exported,
				timesheetIds == null ? null : Arrays.asList(timesheetIds));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int export(final Integer companyId, final Integer exporterId,
			final boolean exported, final Collection<Integer> timesheetIds)
			throws DatabaseException {
		if (timesheetIds == null || timesheetIds.isEmpty())
			return 0;
		notNull(companyId, "Invalid null company id");
		isTrue(!exported || exporterId != null, "Invalid null exporter id");

		final String sql = "UPDATE timesheets SET exported = ?, exporter_id = ? "
				+ "WHERE id = ? AND company_id = ?";

		int count = 0;
		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql)) {
			for (final Integer timesheetId : timesheetIds) {
				int index = 1;
				ps.setBoolean(index++, exported);
				// exporterId will always have a value when exported is true,
				// based on the above Validate.isTrue
				if (exported && exporterId != null)
					ps.setInt(index++, exporterId);
				else
					ps.setNull(index++, Types.INTEGER);
				ps.setInt(index++, timesheetId);
				ps.setInt(index++, companyId);
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
	public int delete(final Integer companyId, final Integer... timesheetIds)
			throws DatabaseException {
		return this.delete(companyId,
				timesheetIds == null ? null : Arrays.asList(timesheetIds));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int delete(final Integer companyId,
			final Collection<Integer> timesheetIds) throws DatabaseException {
		if (timesheetIds == null || timesheetIds.isEmpty())
			return 0;
		notNull(companyId, "Invalid null company id");

		final String sql = "DELETE FROM timesheets WHERE company_id = ? "
				+ " AND id = ?";

		int count = 0;
		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql)) {
			for (final Integer timesheetId : timesheetIds) {
				ps.setInt(1, companyId);
				ps.setInt(2, timesheetId);
				count += ps.executeUpdate();
			}
		} catch (final SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
		return count;
	}

	protected void enrich(final Connection conn, final Integer companyId,
			final Set<Timesheet> timesheets, final Set<Enrichment> enrichments)
			throws DatabaseException, HolidayConfigurationException {
		if (timesheets == null || timesheets.isEmpty())
			return;
		if (enrichments == null || enrichments.isEmpty())
			return;

		notNull(conn, "Invalid null connection");

		for (final Enrichment enrichment : enrichments) {
			if (enrichment == Enrichment.USERS)
				enrichWithUsers(conn, companyId, timesheets);
			else if (enrichment == Enrichment.PAY_PERIODS)
				enrichWithPayPeriods(conn, companyId, timesheets);
			else if (enrichment == Enrichment.HOLIDAYS)
				enrichWithHolidays(conn, companyId, timesheets);
			else if (enrichment == Enrichment.AUDIT_LOGS)
				enrichWithAuditLogs(conn, companyId, timesheets);
			else if (enrichment == Enrichment.TASKS)
				enrichWithTasks(conn, companyId, timesheets);
			else if (enrichment == Enrichment.BILLS)
				enrichWithBills(conn, companyId, timesheets);
			else
				throw new DatabaseException("Invalid enrichment specified: "
						+ enrichment);
		}
	}

	protected void enrichWithUsers(final Connection conn,
			final Integer companyId, final Set<Timesheet> timesheets)
			throws DatabaseException {
		final Set<Integer> userIds = getUserIds(timesheets);

		final Map<Integer, User> userMap = new JdbcUserDao(
				this.connectionManager).get(userIds);

		for (final Timesheet timesheet : timesheets) {
			User user = null;
			User approver = null;
			User verifier = null;
			User exporter = null;

			if (timesheet.getUserId() != null)
				user = userMap.get(timesheet.getUserId());
			if (timesheet.getApproverId() != null)
				approver = userMap.get(timesheet.getApproverId());
			if (timesheet.getVerifierId() != null)
				verifier = userMap.get(timesheet.getVerifierId());
			if (timesheet.getExporterId() != null)
				exporter = userMap.get(timesheet.getExporterId());

			if (user != null)
				timesheet.setUser(user);
			if (approver != null)
				timesheet.setApprover(approver);
			if (verifier != null)
				timesheet.setVerifier(verifier);
			if (exporter != null)
				timesheet.setExporter(exporter);
		}

		userMap.clear();
	}

	protected void enrichWithPayPeriods(final Connection conn,
			final Integer companyId, final Set<Timesheet> timesheets)
			throws DatabaseException {
		final Set<Integer> ids = getTimesheetIds(timesheets);
		if (ids.isEmpty())
			return;

		final Map<Integer, Timesheet> timesheetMap = getTimesheetMap(timesheets);
		final Map<Integer, PayPeriod> payPeriodMap = new JdbcPayPeriodDao(
				this.connectionManager).getForTimesheets(conn, companyId, ids);

		for (final Entry<Integer, PayPeriod> entry : payPeriodMap.entrySet()) {
			final Timesheet timesheet = timesheetMap.get(entry.getKey());
			if (timesheet != null)
				timesheet.setPayPeriod(entry.getValue());
		}

		ids.clear();
		timesheetMap.clear();
		payPeriodMap.clear();
	}

	protected void enrichWithHolidays(final Connection conn,
			final Integer companyId, final Set<Timesheet> timesheets)
			throws DatabaseException, HolidayConfigurationException {
		final Set<Holiday> holidays = new JdbcHolidayDao(this.connectionManager)
				.getAll(companyId);

		for (final Timesheet timesheet : timesheets) {
			final PayPeriod pp = timesheet.getPayPeriod();
			if (pp == null)
				continue;

			for (final Holiday holiday : holidays)
				if (pp.contains(holiday))
					timesheet.addHolidays(holiday);
		}

		holidays.clear();
	}

	protected void enrichWithAuditLogs(final Connection conn,
			final Integer companyId, final Set<Timesheet> timesheets)
			throws DatabaseException {
		final Set<Integer> ids = getTimesheetIds(timesheets);
		if (ids.isEmpty())
			return;

		final Map<Integer, Timesheet> timesheetMap = getTimesheetMap(timesheets);
		final Map<Integer, Set<AuditLog>> auditLogMap = new JdbcAuditLogDao(
				this.connectionManager).getForTimesheets(conn, ids);

		for (final Entry<Integer, Set<AuditLog>> entry : auditLogMap.entrySet()) {
			final Timesheet timesheet = timesheetMap.get(entry.getKey());
			if (timesheet != null)
				timesheet.setAuditLogs(entry.getValue());
		}

		ids.clear();
		timesheetMap.clear();
		auditLogMap.clear();
	}

	protected void enrichWithTasks(final Connection conn,
			final Integer companyId, final Set<Timesheet> timesheets)
			throws DatabaseException {
		if (timesheets.isEmpty())
			return;

		final Map<PayPeriod, Map<Integer, Timesheet>> userGroups = new HashMap<>();
		for (final Timesheet timesheet : timesheets) {
			final PayPeriod payPeriod = timesheet.getPayPeriod();
			if (payPeriod == null)
				continue;

			Map<Integer, Timesheet> userIds = userGroups.get(payPeriod);
			if (userIds == null) {
				userIds = new TreeMap<>();
				userGroups.put(payPeriod, userIds);
			}
			userIds.put(timesheet.getUserId(), timesheet);
		}

		final JdbcTaskDao taskDao = new JdbcTaskDao(this.connectionManager);
		for (final Entry<PayPeriod, Map<Integer, Timesheet>> entry : userGroups
				.entrySet()) {
			final Map<Integer, Set<Task>> userTasks = taskDao.getForPayPeriod(
					conn, entry.getKey(), entry.getValue().keySet());

			for (final Entry<Integer, Set<Task>> taskEntry : userTasks
					.entrySet()) {
				if (taskEntry.getKey() == 0) {
					for (final Timesheet timesheet : timesheets)
						timesheet.addTasks(taskEntry.getValue());
				} else {
					final Timesheet timesheet = entry.getValue().get(
							taskEntry.getKey());
					timesheet.addTasks(taskEntry.getValue());
				}
			}
		}

		userGroups.clear();
	}

	protected void enrichWithBills(final Connection conn,
			final Integer companyId, final Set<Timesheet> timesheets)
			throws DatabaseException {
		final Set<Integer> ids = getTimesheetIds(timesheets);
		if (ids.isEmpty())
			return;

		final Map<Integer, Timesheet> timesheetMap = getTimesheetMap(timesheets);
		final Map<Integer, Set<Bill>> billMap = new JdbcBillDao(
				this.connectionManager).getForTimesheets(conn, ids);

		for (final Entry<Integer, Set<Bill>> billEntry : billMap.entrySet()) {
			final Timesheet timesheet = timesheetMap.get(billEntry.getKey());

			for (final Bill bill : billEntry.getValue()) {
				final Task task = timesheet.getTask(bill.getTaskId());
				if (task != null) {
					if (bill.getAssignmentId() != null) {
						final Assignment assignment = task.getAssignment(bill
								.getAssignmentId());
						if (assignment != null
								&& assignment.contains(bill.getDay()))
							assignment.addBills(bill);
					} else
						task.addBills(bill);
				}
			}
		}

		ids.clear();
		timesheetMap.clear();
		billMap.clear();
	}

	protected SortedSet<Integer> getTimesheetIds(final Set<Timesheet> timesheets) {
		final SortedSet<Integer> ids = new TreeSet<>();
		for (final Timesheet timesheet : timesheets)
			if (timesheet.getId() != null)
				ids.add(timesheet.getId());
		return ids;
	}

	protected Map<Integer, Timesheet> getTimesheetMap(
			final Set<Timesheet> timesheets) {
		final Map<Integer, Timesheet> map = new HashMap<>();
		for (final Timesheet timesheet : timesheets)
			if (timesheet.getId() != null)
				map.put(timesheet.getId(), timesheet);
		return map;
	}

	protected SortedSet<Integer> getUserIds(final Set<Timesheet> timesheets) {
		final SortedSet<Integer> ids = new TreeSet<>();
		for (final Timesheet timesheet : timesheets)
			ids.addAll(timesheet.getUserIds());
		return ids;
	}
}
