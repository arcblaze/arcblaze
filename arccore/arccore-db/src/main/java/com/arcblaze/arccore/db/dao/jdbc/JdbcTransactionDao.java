package com.arcblaze.arccore.db.dao.jdbc;

import static org.apache.commons.lang.Validate.notNull;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.arcblaze.arccore.common.model.Transaction;
import com.arcblaze.arccore.common.model.TransactionType;
import com.arcblaze.arccore.db.ConnectionManager;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arccore.db.dao.TransactionDao;

/**
 * Manages transactions within the back-end database.
 */
public class JdbcTransactionDao implements TransactionDao {
	/** Used to retrieve database connection objects. */
	private final ConnectionManager connectionManager;

	/**
	 * @param connectionManager
	 *            used to retrieve database connection objects
	 */
	public JdbcTransactionDao(final ConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
	}

	protected Transaction fromResultSet(final ResultSet rs) throws SQLException {
		final Transaction transaction = new Transaction();
		transaction.setId(rs.getInt("id"));
		transaction.setCompanyId(rs.getInt("company_id"));
		transaction.setUserId(rs.getInt("user_id"));
		transaction.setTimestamp(rs.getTimestamp("timestamp"));
		transaction.setTransactionType(TransactionType.parse(rs
				.getString("type")));
		transaction.setDescription(rs.getString("description"));
		transaction.setAmount(rs.getFloat("amount"));

		final String notes = rs.getString("notes");
		if (StringUtils.isNotBlank(notes))
			transaction.setNotes(notes);
		return transaction;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BigDecimal amountBetween(final Integer companyId, final Date begin,
			final Date end) throws DatabaseException {
		notNull(companyId, "Invalid null company id");
		notNull(begin, "Invalid null begin");
		notNull(end, "Invalid null end");

		final String sql = "SELECT SUM(amount) FROM transactions WHERE "
				+ "company_id = ? AND timestamp >= ? AND timestamp < ?";

		BigDecimal sum = new BigDecimal(0).setScale(2);
		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, companyId);
			ps.setTimestamp(2, new Timestamp(begin.getTime()));
			ps.setTimestamp(3, new Timestamp(end.getTime()));
			try (final ResultSet rs = ps.executeQuery()) {
				while (rs.next())
					sum = sum.add(new BigDecimal(rs.getFloat(1)).setScale(2));
			}
			return sum;
		} catch (final SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	@Override
	public BigDecimal amountBetween(final Date begin, final Date end)
			throws DatabaseException {
		notNull(begin, "Invalid null begin");
		notNull(end, "Invalid null end");

		final String sql = "SELECT SUM(amount) FROM transactions WHERE "
				+ "timestamp >= ? AND timestamp < ?";

		BigDecimal sum = new BigDecimal(0).setScale(2);
		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setTimestamp(1, new Timestamp(begin.getTime()));
			ps.setTimestamp(2, new Timestamp(end.getTime()));
			try (final ResultSet rs = ps.executeQuery()) {
				while (rs.next())
					sum = sum.add(new BigDecimal(rs.getFloat(1)).setScale(2));
			}
			return sum;
		} catch (final SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SortedMap<Date, BigDecimal> getSumByMonth(final Date begin,
			final Date end) throws DatabaseException {
		notNull(begin, "Invalid null begin");
		notNull(end, "Invalid null end");

		final String sql = "SELECT YEAR(timestamp) AS year, "
				+ "MONTH(timestamp) AS month, SUM(amount) AS amount "
				+ "FROM transactions WHERE timestamp >= ? AND timestamp < ? "
				+ "GROUP BY year, month";

		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setTimestamp(1, new Timestamp(begin.getTime()));
			ps.setTimestamp(2, new Timestamp(end.getTime()));
			final SortedMap<Date, BigDecimal> map = new TreeMap<>();
			try (final ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					final int year = rs.getInt("year");
					final int month = rs.getInt("month");
					final BigDecimal count = new BigDecimal(
							rs.getFloat("amount")).setScale(2);

					final Date date = DateUtils.parseDate(
							String.format("%d-%d-01", year, month),
							new String[] { "yyyy-MM-dd" });
					final BigDecimal sum = map.get(date);
					map.put(date, sum == null ? count : sum.add(count));
				}
			} catch (final ParseException badDate) {
				throw new DatabaseException("Unexpected date parse problem.",
						badDate);
			}

			// Add any missing months.
			Date date = DateUtils.truncate(begin, Calendar.MONTH);
			while (!date.after(end)) {
				if (!map.containsKey(date))
					map.put(date, BigDecimal.ZERO.setScale(2));
				date = DateUtils.addMonths(date, 1);
			}

			return map;
		} catch (final SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Transaction get(final Integer id) throws DatabaseException {
		notNull(id, "Invalid null id");

		final String sql = "SELECT * FROM transactions WHERE id = ?";

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
	public Set<Transaction> getForCompany(final Integer companyId,
			final Integer limit, final Integer offset) throws DatabaseException {
		notNull(companyId, "Invalid null company id");

		final StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM transactions WHERE company_id = ?");
		if (limit != null)
			sql.append(" LIMIT ?");
		if (offset != null)
			sql.append(" OFFSET ?");

		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql
						.toString())) {
			int index = 1;
			ps.setInt(index++, companyId);
			if (limit != null)
				ps.setInt(index++, limit);
			if (offset != null)
				ps.setInt(index++, offset);
			final Set<Transaction> transactions = new TreeSet<>();
			try (final ResultSet rs = ps.executeQuery()) {
				while (rs.next())
					transactions.add(fromResultSet(rs));
			}
			return transactions;
		} catch (final SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<Transaction> getForCompany(final Integer companyId,
			final Date begin, final Date end, final Integer limit,
			final Integer offset) throws DatabaseException {
		notNull(companyId, "Invalid null company id");
		notNull(begin, "Invalid null begin");
		notNull(end, "Invalid null end");

		final StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM transactions WHERE company_id = ? AND ");
		sql.append("timestamp >= ? AND timestamp <= ? ORDER BY timestamp DESC");
		if (limit != null)
			sql.append(" LIMIT ?");
		if (offset != null)
			sql.append(" OFFSET ?");

		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql
						.toString())) {
			int index = 1;
			ps.setInt(index++, companyId);
			ps.setTimestamp(index++, new Timestamp(begin.getTime()));
			ps.setTimestamp(index++, new Timestamp(end.getTime()));
			if (limit != null)
				ps.setInt(index++, limit);
			if (offset != null)
				ps.setInt(index++, offset);
			final Set<Transaction> transactions = new TreeSet<>();
			try (final ResultSet rs = ps.executeQuery()) {
				while (rs.next())
					transactions.add(fromResultSet(rs));
			}
			return transactions;
		} catch (final SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int add(final Transaction... transactions) throws DatabaseException {
		return this.add(transactions == null ? null : Arrays
				.asList(transactions));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int add(final Collection<Transaction> transactions)
			throws DatabaseException {
		if (transactions == null || transactions.isEmpty())
			return 0;

		final String sql = "INSERT INTO transactions (company_id, user_id, "
				+ "timestamp, type, description, amount, notes) VALUES "
				+ "(?, ?, ?, ?, ?, ?, ?)";

		int count = 0;
		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql,
						Statement.RETURN_GENERATED_KEYS)) {
			for (final Transaction transaction : transactions) {
				int index = 1;
				ps.setInt(index++, transaction.getCompanyId());
				ps.setInt(index++, transaction.getUserId());
				ps.setTimestamp(index++, new Timestamp(transaction
						.getTimestamp().getTime()));
				ps.setString(index++, transaction.getTransactionType().name());
				ps.setString(index++, transaction.getDescription());
				ps.setString(index++, transaction.getAmount().toPlainString());
				ps.setString(index++, transaction.getNotes());
				count += ps.executeUpdate();

				try (final ResultSet rs = ps.getGeneratedKeys()) {
					if (rs.next())
						transaction.setId(rs.getInt(1));
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
	public int update(final Transaction... transactions)
			throws DatabaseException {
		return this.update(transactions == null ? null : Arrays
				.asList(transactions));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int update(final Collection<Transaction> transactions)
			throws DatabaseException {
		if (transactions == null || transactions.isEmpty())
			return 0;

		final String sql = "UPDATE transactions SET company_id = ?, user_id = ?, "
				+ "timestamp = ?, type = ?, description = ?, amount = ?, "
				+ "notes = ? WHERE id = ?";

		int count = 0;
		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql)) {
			for (Transaction transaction : transactions) {
				int index = 1;
				ps.setInt(index++, transaction.getCompanyId());
				ps.setInt(index++, transaction.getUserId());
				ps.setTimestamp(index++, new Timestamp(transaction
						.getTimestamp().getTime()));
				ps.setString(index++, transaction.getTransactionType().name());
				ps.setString(index++, transaction.getDescription());
				ps.setString(index++, transaction.getAmount().toPlainString());
				ps.setString(index++, transaction.getNotes());
				ps.setInt(index++, transaction.getId());
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

		final String sql = "DELETE FROM transactions WHERE id = ?";

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
