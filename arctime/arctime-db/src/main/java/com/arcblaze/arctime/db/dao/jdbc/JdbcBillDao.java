package com.arcblaze.arctime.db.dao.jdbc;

import static org.apache.commons.lang.Validate.notNull;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.arcblaze.arccore.db.ConnectionManager;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arctime.common.model.Bill;
import com.arcblaze.arctime.db.dao.BillDao;

/**
 * Manages bills within the back-end database.
 */
public class JdbcBillDao implements BillDao {
    /** Used to retrieve database connection objects. */
    private final ConnectionManager connectionManager;

    /**
     * @param connectionManager
     *            used to retrieve database connection objects
     */
    public JdbcBillDao(final ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    protected Bill fromResultSet(final ResultSet rs) throws SQLException {
        final Bill bill = new Bill();
        bill.setId(rs.getInt("id"));
        final int assignmentId = rs.getInt("assignment_id");
        if (!rs.wasNull())
            bill.setAssignmentId(assignmentId);
        final int taskId = rs.getInt("task_id");
        if (!rs.wasNull())
            bill.setTaskId(taskId);
        bill.setUserId(rs.getInt("user_id"));
        bill.setDay(rs.getDate("day"));
        bill.setHours(new BigDecimal(rs.getString("hours")));
        bill.setTimestamp(rs.getTimestamp("timestamp"));
        return bill;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Bill get(final Integer id) throws DatabaseException {
        notNull("Invalid null id");

        final String sql = "SELECT * FROM bills WHERE id = ?";

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
    public Set<Bill> getForTimesheet(final Integer timesheetId) throws DatabaseException {
        notNull(timesheetId, "Invalid null timesheet id");

        final String sql = "SELECT b.* FROM bills b JOIN pay_periods p ON " + "(b.day >= p.begin AND b.day <= p.end) "
                + "JOIN timesheets t ON (t.pp_begin = p.begin AND t.id = ?)";

        try (final Connection conn = this.connectionManager.getConnection();
                final PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, timesheetId);
            final Set<Bill> bills = new TreeSet<>();
            try (final ResultSet rs = ps.executeQuery()) {
                while (rs.next())
                    bills.add(fromResultSet(rs));
            }
            return bills;
        } catch (final SQLException sqlException) {
            throw new DatabaseException(sqlException);
        }
    }

    protected Map<Integer, Set<Bill>> getForTimesheets(final Connection conn, final Set<Integer> timesheetIds)
            throws DatabaseException {
        if (timesheetIds == null || timesheetIds.isEmpty())
            return Collections.emptyMap();
        notNull(conn, "Invalid null connection");

        String sql = "SELECT t.id AS timesheet_id, b.* FROM bills b "
                + "JOIN pay_periods p ON (b.day >= p.begin AND b.day <= p.end) "
                + "JOIN timesheets t ON (t.pp_begin = p.begin) " + "WHERE t.id = ?";

        try (final PreparedStatement ps = conn.prepareStatement(sql)) {
            final Map<Integer, Set<Bill>> billMap = new HashMap<>();
            for (final Integer timesheetId : timesheetIds) {
                ps.setInt(1, timesheetId);
                try (final ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        final Bill bill = fromResultSet(rs);
                        Set<Bill> bills = billMap.get(timesheetId);
                        if (bills == null) {
                            bills = new TreeSet<>();
                            billMap.put(timesheetId, bills);
                        }
                        bills.add(bill);
                    }
                }
            }

            return billMap;
        } catch (final SQLException sqlException) {
            throw new DatabaseException(sqlException);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int add(final Bill... bills) throws DatabaseException {
        return this.add(bills == null ? null : Arrays.asList(bills));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int add(final Collection<Bill> bills) throws DatabaseException {
        if (bills == null || bills.isEmpty())
            return 0;

        final String sql = "INSERT INTO bills (assignment_id, task_id, "
                + "user_id, day, hours, timestamp) VALUES (?, ?, ?, ?, ?, ?)";

        int count = 0;
        try (final Connection conn = this.connectionManager.getConnection();
                final PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            for (final Bill bill : bills) {
                int index = 1;
                if (bill.getAssignmentId() != null)
                    ps.setInt(index++, bill.getAssignmentId());
                else
                    ps.setNull(index++, Types.INTEGER);
                ps.setInt(index++, bill.getTaskId());
                ps.setInt(index++, bill.getUserId());
                ps.setDate(index++, new Date(bill.getDay().getTime()));
                ps.setString(index++, bill.getHours().toPlainString());
                ps.setTimestamp(index++, new Timestamp(bill.getTimestamp().getTime()));
                count += ps.executeUpdate();

                try (final ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next())
                        bill.setId(rs.getInt(1));
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
    public int update(final Bill... bills) throws DatabaseException {
        return this.update(bills == null ? null : Arrays.asList(bills));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int update(final Collection<Bill> bills) throws DatabaseException {
        if (bills == null || bills.isEmpty())
            return 0;

        final String sql = "UPDATE bills SET assignment_id = ?, task_id = ?, "
                + "user_id = ?, day = ?, hours = ?, timestamp = ? " + "WHERE id = ?";

        int count = 0;
        try (final Connection conn = this.connectionManager.getConnection();
                final PreparedStatement ps = conn.prepareStatement(sql)) {
            for (final Bill bill : bills) {
                int index = 1;
                if (bill.getAssignmentId() != null)
                    ps.setInt(index++, bill.getAssignmentId());
                else
                    ps.setNull(index++, Types.INTEGER);
                ps.setInt(index++, bill.getTaskId());
                ps.setInt(index++, bill.getUserId());
                ps.setDate(index++, new Date(bill.getDay().getTime()));
                ps.setString(index++, bill.getHours().toPlainString());
                ps.setTimestamp(index++, new Timestamp(bill.getTimestamp().getTime()));
                ps.setInt(index++, bill.getId());
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
    public int delete(final Integer... ids) throws DatabaseException {
        return this.delete(ids == null ? null : Arrays.asList(ids));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int delete(final Collection<Integer> ids) throws DatabaseException {
        if (ids == null || ids.isEmpty())
            return 0;

        final String sql = "DELETE FROM bills WHERE id = ?";

        int count = 0;
        try (final Connection conn = this.connectionManager.getConnection();
                final PreparedStatement ps = conn.prepareStatement(sql)) {
            for (final Integer id : ids) {
                ps.setInt(1, id);
                count += ps.executeUpdate();
            }
        } catch (final SQLException sqlException) {
            throw new DatabaseException(sqlException);
        }
        return count;
    }
}
