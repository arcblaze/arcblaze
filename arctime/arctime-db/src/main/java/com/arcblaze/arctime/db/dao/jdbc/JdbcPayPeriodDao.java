package com.arcblaze.arctime.db.dao.jdbc;

import static org.apache.commons.lang.Validate.notNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang.time.DateUtils;

import com.arcblaze.arccore.db.ConnectionManager;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arctime.db.dao.PayPeriodDao;
import com.arcblaze.arctime.model.PayPeriod;
import com.arcblaze.arctime.model.PayPeriodType;

/**
 * Performs operations on pay periods in the system.
 */
public class JdbcPayPeriodDao implements PayPeriodDao {
	/** Used to retrieve database connection objects. */
	private final ConnectionManager connectionManager;

	/**
	 * @param connectionManager
	 *            used to retrieve database connection objects
	 */
	public JdbcPayPeriodDao(final ConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
	}

	protected PayPeriod fromResultSet(final ResultSet rs) throws SQLException {
		final PayPeriod payPeriod = new PayPeriod();
		payPeriod.setCompanyId(rs.getInt("company_id"));
		payPeriod.setBegin(rs.getDate("begin"));
		payPeriod.setEnd(rs.getDate("end"));
		payPeriod.setType(PayPeriodType.parse(rs.getString("type")));
		return payPeriod;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean exists(final Integer companyId, final Date begin)
			throws DatabaseException {
		notNull(companyId, "Invalid null company id");
		notNull(begin, "Invalid null begin date");

		final String sql = "SELECT COUNT(*) FROM pay_periods WHERE "
				+ "company_id = ? AND begin = ?";

		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, companyId);
			ps.setDate(2, new java.sql.Date(begin.getTime()));
			try (final ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return rs.getInt(1) > 0;
				return false;
			}
		} catch (final SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PayPeriod get(final Integer companyId, final Date begin)
			throws DatabaseException {
		notNull(companyId, "Invalid null company id");
		notNull(begin, "Invalid null begin date");

		final String sql = "SELECT * FROM pay_periods WHERE company_id = ? AND "
				+ "begin = ?";

		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, companyId);
			ps.setDate(2, new java.sql.Date(begin.getTime()));
			try (final ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return fromResultSet(rs);
				return null;
			}
		} catch (final SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PayPeriod getContaining(final Integer companyId, final Date day)
			throws DatabaseException {
		notNull(companyId, "Invalid null company id");
		notNull(day, "Invalid null day");

		final Date truncated = DateUtils.truncate(day, Calendar.DATE);

		final String sql = "SELECT * FROM pay_periods WHERE company_id = ? AND "
				+ "begin <= ? AND end >= ?";

		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, companyId);
			ps.setDate(2, new java.sql.Date(truncated.getTime()));
			ps.setDate(3, new java.sql.Date(truncated.getTime()));
			try (final ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return fromResultSet(rs);
			}
			// A pay period for the requested day does not exist. Create the
			// necessary pay periods and return the result.
			return addThrough(companyId, day);
		} catch (final SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * Make sure all of the pay periods exist in the database through the
	 * specified day (either forwards or backwards).
	 * 
	 * @param companyId
	 *            the company for which timesheets should be created
	 * @param day
	 *            the {@link Date} for which pay periods should be created
	 *            through
	 * 
	 * @return the created pay period that contains the specified day, or
	 *         {@code null} if there are no existing pay periods from which to
	 *         derive the pay period that contains the specified day
	 * 
	 * @throws DatabaseException
	 *             if there is a problem communicating with the database
	 */
	protected PayPeriod addThrough(final Integer companyId, final Date day)
			throws DatabaseException {
		final PayPeriod latest = getLatest(companyId);
		final PayPeriod earliest = getEarliest(companyId);

		// Make sure at least one pay period exists in the database for this
		// company.
		if (latest == null || earliest == null)
			return null;

		PayPeriod payPeriod = null;
		if (earliest.isAfter(day))
			payPeriod = earliest;
		else if (latest.isBefore(day))
			payPeriod = latest;
		else {
			// An unexpected situation where a pay period containing the
			// specified date does not exist, though there are pay periods
			// both before and after?
			throw new DatabaseException("Unexpected missing pay period "
					+ "that should contain: " + day);
		}

		if (PayPeriodType.CUSTOM.equals(payPeriod.getType())) {
			throw new DatabaseException("Unable to create additional pay "
					+ "periods based on custom type: " + payPeriod);
		}

		final List<PayPeriod> toAdd = new LinkedList<>();
		while (!payPeriod.contains(day)) {
			if (payPeriod.isAfter(day))
				payPeriod = payPeriod.getPrevious();
			else
				payPeriod = payPeriod.getNext();
			toAdd.add(payPeriod);
		}

		add(toAdd);

		return payPeriod;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PayPeriod getCurrent(final Integer companyId)
			throws DatabaseException {
		return getContaining(companyId, new Date());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PayPeriod getEarliest(final Integer companyId)
			throws DatabaseException {
		notNull(companyId, "Invalid null company id");

		final String sql = "SELECT * FROM pay_periods WHERE company_id = ? "
				+ "ORDER BY begin LIMIT 1";

		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, companyId);
			try (final ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return fromResultSet(rs);
				return null;
			}
		} catch (final SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PayPeriod getLatest(final Integer companyId)
			throws DatabaseException {
		notNull(companyId, "Invalid null company id");

		final String sql = "SELECT * FROM pay_periods WHERE company_id = ? "
				+ "ORDER BY begin DESC LIMIT 1";

		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, companyId);
			try (final ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return fromResultSet(rs);
				return null;
			}
		} catch (final SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * Used to enrich timesheet data.
	 */
	protected Map<Integer, PayPeriod> getForTimesheets(final Connection conn,
			final Integer companyId, final Set<Integer> timesheetIds)
			throws DatabaseException {
		if (timesheetIds == null || timesheetIds.isEmpty())
			return Collections.emptyMap();
		notNull(conn, "Invalid null connection");

		final String sql = "SELECT p.* FROM timesheets t "
				+ "JOIN pay_periods p ON (p.begin = t.pp_begin AND "
				+ "p.company_id = t.company_id) WHERE p.company_id = ?";

		try (final PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, companyId);
			final Map<Integer, PayPeriod> timesheetMap = new TreeMap<>();
			for (final Integer timesheetId : timesheetIds) {
				try (final ResultSet rs = ps.executeQuery()) {
					if (rs.next())
						timesheetMap.put(timesheetId, fromResultSet(rs));
				}
			}
			return timesheetMap;
		} catch (final SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final PayPeriod... payPeriods) throws DatabaseException {
		this.add(payPeriods == null ? null : Arrays.asList(payPeriods));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final Collection<PayPeriod> payPeriods)
			throws DatabaseException {
		if (payPeriods == null || payPeriods.isEmpty())
			return;

		final String sql = "INSERT INTO pay_periods (company_id, begin, end, "
				+ "type) VALUES (?, ?, ?, ?)";

		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql)) {
			for (final PayPeriod payPeriod : payPeriods) {
				int index = 1;
				ps.setInt(index++, payPeriod.getCompanyId());
				ps.setDate(index++, new java.sql.Date(payPeriod.getBegin()
						.getTime()));
				ps.setDate(index++, new java.sql.Date(payPeriod.getEnd()
						.getTime()));
				ps.setString(index++, payPeriod.getType().name());
				ps.executeUpdate();
			}
		} catch (final SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}
}
