package com.arcblaze.arctime.db.dao.jdbc;

import static org.apache.commons.lang.Validate.notNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import com.arcblaze.arccore.db.ConnectionManager;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arctime.db.dao.HolidayDao;
import com.arcblaze.arctime.model.Holiday;
import com.arcblaze.arctime.model.PayPeriod;
import com.arcblaze.arctime.model.util.HolidayConfigurationException;

/**
 * Manages holidays within the back-end database.
 */
public class JdbcHolidayDao implements HolidayDao {
	/** Used to retrieve database connection objects. */
	private final ConnectionManager connectionManager;

	/**
	 * @param connectionManager
	 *            used to retrieve database connection objects
	 */
	public JdbcHolidayDao(final ConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
	}

	protected Holiday fromResultSet(final ResultSet rs) throws SQLException,
			HolidayConfigurationException {
		final Holiday holiday = new Holiday();
		holiday.setId(rs.getInt("id"));
		holiday.setCompanyId(rs.getInt("company_id"));
		holiday.setDescription(rs.getString("description"));
		holiday.setConfig(rs.getString("config"));
		return holiday;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Holiday get(final Integer id) throws DatabaseException,
			HolidayConfigurationException {
		notNull(id, "Invalid null id");

		final String sql = "SELECT * FROM holidays WHERE id = ?";

		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, id);
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
	public Set<Holiday> getAll(final Integer companyId)
			throws DatabaseException, HolidayConfigurationException {
		if (companyId == null)
			throw new IllegalArgumentException("Invalid null company id");

		final String sql = "SELECT * FROM holidays WHERE company_id = ?";

		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, companyId);
			final Set<Holiday> holidays = new TreeSet<>();
			try (final ResultSet rs = ps.executeQuery()) {
				while (rs.next())
					holidays.add(fromResultSet(rs));
			}
			return holidays;
		} catch (final SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<Holiday> getForPayPeriod(final Integer companyId,
			final PayPeriod payPeriod) throws DatabaseException,
			HolidayConfigurationException {
		notNull(companyId, "Invalid null company id");
		notNull(payPeriod, "Invalid null pay period");

		final Set<Holiday> all = getAll(companyId);
		Set<Holiday> inPayPeriod = new TreeSet<>();
		for (final Holiday holiday : all) {
			if (payPeriod.contains(holiday))
				inPayPeriod.add(holiday);
		}
		return inPayPeriod;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final Holiday... holidays) throws DatabaseException {
		this.add(holidays == null ? null : Arrays.asList(holidays));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final Collection<Holiday> holidays)
			throws DatabaseException {
		if (holidays == null || holidays.isEmpty())
			return;

		final String sql = "INSERT INTO holidays (company_id, description, "
				+ "config) VALUES (?, ?, ?)";

		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql,
						Statement.RETURN_GENERATED_KEYS)) {
			for (final Holiday holiday : holidays) {
				int index = 1;
				ps.setInt(index++, holiday.getCompanyId());
				ps.setString(index++, holiday.getDescription());
				ps.setString(index++, holiday.getConfig());
				ps.executeUpdate();

				try (final ResultSet rs = ps.getGeneratedKeys()) {
					if (rs.next())
						holiday.setId(rs.getInt(1));
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
	public void update(final Holiday... holidays) throws DatabaseException {
		this.update(holidays == null ? null : Arrays.asList(holidays));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(final Collection<Holiday> holidays)
			throws DatabaseException {
		if (holidays == null || holidays.isEmpty())
			return;

		final String sql = "UPDATE holidays SET company_id = ?, "
				+ "description = ?, config = ? WHERE id = ?";

		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql)) {
			for (final Holiday holiday : holidays) {
				int index = 1;
				ps.setInt(index++, holiday.getCompanyId());
				ps.setString(index++, holiday.getDescription());
				ps.setString(index++, holiday.getConfig());
				ps.setInt(index++, holiday.getId());
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

		final String sql = "DELETE FROM holidays WHERE id = ?";

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