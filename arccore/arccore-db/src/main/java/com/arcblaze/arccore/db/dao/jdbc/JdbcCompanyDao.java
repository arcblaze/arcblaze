package com.arcblaze.arccore.db.dao.jdbc;

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

import com.arcblaze.arccore.common.model.Company;
import com.arcblaze.arccore.db.ConnectionManager;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arccore.db.dao.CompanyDao;

/**
 * Manages companies within the back-end database.
 */
public class JdbcCompanyDao implements CompanyDao {
	/** Used to retrieve database connection objects. */
	private final ConnectionManager connectionManager;

	/**
	 * @param connectionManager
	 *            used to retrieve database connection objects
	 */
	public JdbcCompanyDao(final ConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
	}

	protected Company fromResultSet(final ResultSet rs) throws SQLException {
		final Company company = new Company();
		company.setId(rs.getInt("id"));
		company.setName(rs.getString("name"));
		company.setActive(rs.getBoolean("active"));
		return company;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int count(final boolean includeInactive) throws DatabaseException {
		final String sql = "SELECT COUNT(*) FROM companies"
				+ (includeInactive ? "" : " WHERE active = TRUE");

		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql);
				final ResultSet rs = ps.executeQuery()) {
			if (rs.next())
				return rs.getInt(1);
			return 0;
		} catch (final SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Company get(final Integer id) throws DatabaseException {
		notNull(id, "Invalid null id");

		final String sql = "SELECT * FROM companies WHERE id = ?";

		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, id);

			try (final ResultSet rs = ps.executeQuery()) {
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
	public Company getForUser(final Integer userId) throws DatabaseException {
		notNull(userId, "Invalid null user id");

		final String sql = "SELECT * FROM companies WHERE id IN ("
				+ "SELECT company_id FROM users WHERE id = ?)";

		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, userId);

			try (final ResultSet rs = ps.executeQuery()) {
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
	public Set<Company> getAll(final Integer limit, final Integer offset)
			throws DatabaseException {
		final StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM companies ORDER BY name");
		if (limit != null)
			sql.append(" LIMIT ?");
		if (offset != null)
			sql.append(" OFFSET ?");

		final Set<Company> companies = new TreeSet<>();
		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql
						.toString())) {
			int index = 1;
			if (limit != null)
				ps.setInt(index++, limit);
			if (offset != null)
				ps.setInt(index++, offset);
			try (final ResultSet rs = ps.executeQuery()) {
				while (rs.next())
					companies.add(fromResultSet(rs));
			}
			return companies;
		} catch (final SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int add(final Company... companies) throws DatabaseException {
		return this.add(companies == null ? null : Arrays.asList(companies));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int add(final Collection<Company> companies)
			throws DatabaseException {
		if (companies == null || companies.isEmpty())
			return 0;

		final String sql = "INSERT INTO companies (name, active) VALUES (?, ?)";

		int count = 0;
		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql,
						Statement.RETURN_GENERATED_KEYS)) {
			for (final Company company : companies) {
				int index = 1;
				ps.setString(index++, company.getName());
				ps.setBoolean(index++, company.isActive());
				count += ps.executeUpdate();

				try (final ResultSet rs = ps.getGeneratedKeys()) {
					if (rs.next())
						company.setId(rs.getInt(1));
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
	public int update(final Company... companies) throws DatabaseException {
		return this.update(companies == null ? null : Arrays.asList(companies));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int update(final Collection<Company> companies)
			throws DatabaseException {
		if (companies == null || companies.isEmpty())
			return 0;

		final String sql = "UPDATE companies SET name = ?, active = ? "
				+ "WHERE id = ?";

		int count = 0;
		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql)) {
			for (final Company company : companies) {
				int index = 1;
				ps.setString(index++, company.getName());
				ps.setBoolean(index++, company.isActive());
				ps.setInt(index++, company.getId());
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
	public int activate(final Integer... ids) throws DatabaseException {
		return this.activate(ids == null ? null : Arrays.asList(ids));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int activate(final Collection<Integer> ids) throws DatabaseException {
		if (ids == null || ids.isEmpty())
			return 0;

		final String sql = "UPDATE companies SET active = true WHERE id = ?";

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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int deactivate(final Integer... ids) throws DatabaseException {
		return this.deactivate(ids == null ? null : Arrays.asList(ids));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int deactivate(final Collection<Integer> ids)
			throws DatabaseException {
		if (ids == null || ids.isEmpty())
			return 0;

		final String sql = "UPDATE companies SET active = false WHERE id = ?";

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

		final String sql = "DELETE FROM companies WHERE id = ?";

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
