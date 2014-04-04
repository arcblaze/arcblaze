package com.arcblaze.arccore.db.dao.jdbc;

import static org.apache.commons.lang.Validate.notNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang.time.DateUtils;

import com.arcblaze.arccore.db.ConnectionManager;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arccore.db.dao.UserActivityDao;

/**
 * Manages user activity counts within the back-end database.
 */
public class JdbcUserActivityDao implements UserActivityDao {
	/** Used to retrieve database connection objects. */
	private final ConnectionManager connectionManager;

	/**
	 * @param connectionManager
	 *            used to retrieve database connection objects
	 */
	public JdbcUserActivityDao(final ConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SortedMap<Date, Integer> getActiveByMonth(final Integer companyId,
			final Date begin, final Date end) throws DatabaseException {
		notNull(companyId, "Invalid null company id");
		notNull(begin, "Invalid null begin");
		notNull(end, "Invalid null end");

		final String sql = "SELECT YEAR(day) AS year, MONTH(day) AS month, "
				+ "MAX(active) AS active FROM active_user_counts "
				+ "WHERE company_id = ? AND day >= ? AND day < ? "
				+ "GROUP BY year, month";

		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, companyId);
			ps.setDate(2, new java.sql.Date(begin.getTime()));
			ps.setDate(3, new java.sql.Date(end.getTime()));
			final SortedMap<Date, Integer> map = new TreeMap<>();
			try (final ResultSet rs = ps.executeQuery()) {
				final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
				while (rs.next()) {
					final int year = rs.getInt("year");
					final int month = rs.getInt("month");
					final Integer active = rs.getInt("active");

					final Date date = fmt.parse(String.format("%d-%d-01", year,
							month));
					final Integer num = map.get(date);
					map.put(date, num == null ? active : Math.max(num, active));
				}
			}

			// Add any missing months.
			Date date = DateUtils.truncate(begin, Calendar.MONTH);
			while (!date.after(end)) {
				if (!map.containsKey(date))
					map.put(date, 0);
				date = DateUtils.addMonths(date, 1);
			}

			return map;
		} catch (final SQLException sqlException) {
			throw new DatabaseException(sqlException);
		} catch (final ParseException badDate) {
			throw new DatabaseException("Unexpected date parse problem.",
					badDate);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SortedMap<Date, Integer> getActiveByMonth(final Date begin,
			final Date end) throws DatabaseException {
		notNull(begin, "Invalid null begin");
		notNull(end, "Invalid null end");

		final String sql = "SELECT company_id, YEAR(day) AS year, "
				+ "MONTH(day) AS month, MAX(active) AS active "
				+ "FROM active_user_counts WHERE day >= ? AND day < ? "
				+ "GROUP BY company_id, year, month";

		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setDate(1, new java.sql.Date(begin.getTime()));
			ps.setDate(2, new java.sql.Date(end.getTime()));
			final SortedMap<Date, Integer> map = new TreeMap<>();
			try (final ResultSet rs = ps.executeQuery()) {
				final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
				while (rs.next()) {
					final int year = rs.getInt("year");
					int month = rs.getInt("month");
					final Integer active = rs.getInt("active");

					final Date date = fmt.parse(String.format("%d-%d-01", year,
							month));
					final Integer num = map.get(date);
					map.put(date, num == null ? active : num + active);
				}
			}

			// Add any missing months.
			Date date = DateUtils.truncate(begin, Calendar.MONTH);
			while (!date.after(end)) {
				if (!map.containsKey(date))
					map.put(date, 0);
				date = DateUtils.addMonths(date, 1);
			}

			return map;
		} catch (final SQLException sqlException) {
			throw new DatabaseException(sqlException);
		} catch (final ParseException badDate) {
			throw new DatabaseException("Unexpected date parse problem.",
					badDate);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setActiveUsers(final Date day,
			final Map<Integer, Integer> counts) throws DatabaseException {
		if (counts == null || counts.isEmpty())
			return;
		notNull(day, "Invalid null day");

		final String sql = "INSERT INTO active_user_counts "
				+ "(company_id, day, active) VALUES (?, ?, ?)";

		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql)) {
			final java.sql.Date sqlDay = new java.sql.Date(day.getTime());
			for (final Entry<Integer, Integer> entry : counts.entrySet()) {
				int index = 1;
				ps.setInt(index++, entry.getKey());
				ps.setDate(index++, sqlDay);
				ps.setInt(index++, entry.getValue());
				ps.executeUpdate();
			}
		} catch (final SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}
}
