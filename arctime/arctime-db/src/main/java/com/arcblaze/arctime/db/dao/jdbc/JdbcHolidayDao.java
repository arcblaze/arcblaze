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
import com.arcblaze.arctime.common.model.Holiday;
import com.arcblaze.arctime.common.model.PayPeriod;
import com.arcblaze.arctime.common.model.util.HolidayConfigurationException;
import com.arcblaze.arctime.db.dao.HolidayDao;

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

    protected Holiday fromResultSet(final ResultSet rs, final boolean includeCompanyId) throws SQLException,
            HolidayConfigurationException {
        final Holiday holiday = new Holiday();
        holiday.setId(rs.getInt("id"));
        holiday.setDescription(rs.getString("description"));
        holiday.setConfig(rs.getString("config"));
        // The common_holidays table does not include a company id, so
        // false is passed in as a parameter when retrieving holidays from
        // it. This is easier and faster than using the ResultSetMetaData
        // to check and see if the column exists.
        if (includeCompanyId)
            holiday.setCompanyId(rs.getInt("company_id"));
        return holiday;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Holiday get(final Integer companyId, final Integer id) throws DatabaseException,
            HolidayConfigurationException {
        notNull(companyId, "Invalid null company id");
        notNull(id, "Invalid null id");

        final String sql = "SELECT * FROM holidays " + "WHERE company_id = ? AND id = ?";

        try (final Connection conn = this.connectionManager.getConnection();
                final PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, companyId);
            ps.setInt(2, id);
            try (final ResultSet rs = ps.executeQuery();) {
                if (rs.next())
                    return fromResultSet(rs, true);
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
    public Set<Holiday> getAll(final Integer companyId) throws DatabaseException, HolidayConfigurationException {
        if (companyId == null)
            throw new IllegalArgumentException("Invalid null company id");

        final String sql = "SELECT * FROM holidays WHERE company_id = ?";

        try (final Connection conn = this.connectionManager.getConnection();
                final PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, companyId);
            final Set<Holiday> holidays = new TreeSet<>();
            try (final ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    holidays.add(fromResultSet(rs, true));
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
    public Set<Holiday> getCommon() throws DatabaseException, HolidayConfigurationException {
        final String sql = "SELECT * FROM common_holidays";

        try (final Connection conn = this.connectionManager.getConnection();
                final PreparedStatement ps = conn.prepareStatement(sql)) {
            final Set<Holiday> holidays = new TreeSet<>();
            try (final ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    holidays.add(fromResultSet(rs, false));
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
    public Set<Holiday> getForPayPeriod(final Integer companyId, final PayPeriod payPeriod) throws DatabaseException,
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
    public int add(final Holiday... holidays) throws DatabaseException {
        return this.add(holidays == null ? null : Arrays.asList(holidays));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int add(final Collection<Holiday> holidays) throws DatabaseException {
        if (holidays == null || holidays.isEmpty())
            return 0;

        final String sql = "INSERT INTO holidays (company_id, description, " + "config) VALUES (?, ?, ?)";

        int count = 0;
        try (final Connection conn = this.connectionManager.getConnection();
                final PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            for (final Holiday holiday : holidays) {
                int index = 1;
                ps.setInt(index++, holiday.getCompanyId());
                ps.setString(index++, holiday.getDescription());
                ps.setString(index++, holiday.getConfig());
                count += ps.executeUpdate();

                try (final ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next())
                        holiday.setId(rs.getInt(1));
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
    public int update(final Holiday... holidays) throws DatabaseException {
        return this.update(holidays == null ? null : Arrays.asList(holidays));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int update(final Collection<Holiday> holidays) throws DatabaseException {
        if (holidays == null || holidays.isEmpty())
            return 0;

        final String sql = "UPDATE holidays SET company_id = ?, " + "description = ?, config = ? WHERE id = ?";

        int count = 0;
        try (final Connection conn = this.connectionManager.getConnection();
                final PreparedStatement ps = conn.prepareStatement(sql)) {
            for (final Holiday holiday : holidays) {
                int index = 1;
                ps.setInt(index++, holiday.getCompanyId());
                ps.setString(index++, holiday.getDescription());
                ps.setString(index++, holiday.getConfig());
                ps.setInt(index++, holiday.getId());
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
    public int delete(final Integer companyId, final Integer... ids) throws DatabaseException {
        return this.delete(companyId, ids == null ? null : Arrays.asList(ids));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int delete(final Integer companyId, final Collection<Integer> ids) throws DatabaseException {
        if (ids == null || ids.isEmpty())
            return 0;
        notNull(companyId, "Invalid null company id");

        final String sql = "DELETE FROM holidays " + "WHERE company_id = ? AND id = ?";

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
