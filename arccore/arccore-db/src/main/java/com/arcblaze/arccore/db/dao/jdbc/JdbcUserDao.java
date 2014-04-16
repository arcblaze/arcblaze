package com.arcblaze.arccore.db.dao.jdbc;

import static org.apache.commons.lang.Validate.notEmpty;
import static org.apache.commons.lang.Validate.notNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.arcblaze.arccore.common.model.User;
import com.arcblaze.arccore.db.ConnectionManager;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arccore.db.DatabaseUniqueConstraintException;
import com.arcblaze.arccore.db.dao.UserDao;

/**
 * Manages users within the back-end database.
 */
public class JdbcUserDao implements UserDao {
	/** Used to retrieve database connection objects. */
	private final ConnectionManager connectionManager;

	/**
	 * @param connectionManager
	 *            used to retrieve database connection objects
	 */
	public JdbcUserDao(final ConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
	}

	protected User fromResultSet(final ResultSet rs, final boolean includePass)
			throws SQLException {
		final User user = new User();
		user.setId(rs.getInt("id"));
		user.setCompanyId(rs.getInt("company_id"));
		user.setLogin(rs.getString("login"));
		if (includePass) {
			user.setHashedPass(rs.getString("hashed_pass"));
			user.setSalt(rs.getString("salt"));
		}
		user.setEmail(rs.getString("email"));
		user.setFirstName(rs.getString("first_name"));
		user.setLastName(rs.getString("last_name"));
		user.setActive(rs.getBoolean("active"));
		return user;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int count(final boolean includeInactive) throws DatabaseException {
		final String sql = "SELECT COUNT(*) FROM users"
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
	public Map<Integer, Integer> countPerCompany(final boolean includeInactive)
			throws DatabaseException {
		final String sql = "SELECT company_id, COUNT(*) AS count FROM users"
				+ (includeInactive ? "" : " WHERE active = TRUE")
				+ " GROUP BY company_id";

		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql);
				final ResultSet rs = ps.executeQuery()) {
			final Map<Integer, Integer> counts = new HashMap<>();
			while (rs.next())
				counts.put(rs.getInt(1), rs.getInt(2));
			return counts;
		} catch (final SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public User getLogin(final String login) throws DatabaseException {
		notEmpty(login, "Invalid blank login");

		final String sql = "SELECT * FROM users WHERE active = true AND "
				+ "(login = ? OR LOWER(email) = LOWER(?))";

		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, login);
			ps.setString(2, login);
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
	public User get(final Integer userId) throws DatabaseException {
		notNull(userId, "Invalid null user id");

		final String sql = "SELECT * FROM users WHERE id = ?";

		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, userId);
			try (final ResultSet rs = ps.executeQuery();) {
				if (rs.next())
					return fromResultSet(rs, false);
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
	public User get(final Integer companyId, final Integer userId)
			throws DatabaseException {
		notNull(companyId, "Invalid null company id");
		notNull(userId, "Invalid null user id");

		final String sql = "SELECT * FROM users "
				+ "WHERE company_id = ? AND id = ?";

		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, companyId);
			ps.setInt(2, userId);
			try (final ResultSet rs = ps.executeQuery()) {
				if (rs.next())
					return fromResultSet(rs, false);
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
	public Map<Integer, User> get(final Set<Integer> ids)
			throws DatabaseException {
		if (ids == null || ids.isEmpty())
			return Collections.emptyMap();

		final String sql = "SELECT * FROM users WHERE id = ?";

		final Map<Integer, User> userMap = new TreeMap<>();
		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql)) {
			for (final Integer id : ids) {
				ps.setInt(1, id);
				try (final ResultSet rs = ps.executeQuery()) {
					while (rs.next())
						userMap.put(id, fromResultSet(rs, false));
				}
			}
		} catch (final SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}

		return userMap;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<User> getAll(final Integer companyId,
			final boolean includeInactive) throws DatabaseException {
		notNull(companyId, "Invalid null company id");

		final StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM users WHERE company_id = ?");
		if (!includeInactive)
			sql.append(" AND active = true");

		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql
						.toString())) {
			ps.setInt(1, companyId);
			final Set<User> users = new TreeSet<>();
			try (final ResultSet rs = ps.executeQuery()) {
				while (rs.next())
					users.add(fromResultSet(rs, false));
				return users;
			}
		} catch (final SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<User> getAll() throws DatabaseException {
		final String sql = "SELECT * FROM users";

		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql);
				final ResultSet rs = ps.executeQuery()) {
			final Set<User> users = new TreeSet<>();
			while (rs.next())
				users.add(fromResultSet(rs, false));
			return users;
		} catch (final SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final User... users)
			throws DatabaseUniqueConstraintException, DatabaseException {
		this.add(users == null ? null : Arrays.asList(users));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final Collection<User> users)
			throws DatabaseUniqueConstraintException, DatabaseException {
		if (users == null || users.isEmpty())
			return;

		final String sql = "INSERT INTO users (company_id, login, hashed_pass, "
				+ "salt, email, first_name, last_name, active) VALUES "
				+ "(?, ?, ?, ?, ?, ?, ?, ?)";

		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql,
						Statement.RETURN_GENERATED_KEYS)) {
			for (final User user : users) {
				int index = 1;
				ps.setInt(index++, user.getCompanyId());
				ps.setString(index++, user.getLogin());
				ps.setString(index++, user.getHashedPass());
				ps.setString(index++, user.getSalt());
				ps.setString(index++, user.getEmail());
				ps.setString(index++, user.getFirstName());
				ps.setString(index++, user.getLastName());
				ps.setBoolean(index++, user.isActive());
				ps.executeUpdate();

				try (final ResultSet rs = ps.getGeneratedKeys()) {
					if (rs.next())
						user.setId(rs.getInt(1));
				}
			}
		} catch (final SQLIntegrityConstraintViolationException notUnique) {
			throw new DatabaseUniqueConstraintException(notUnique);
		} catch (final SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void activate(final Integer companyId, final Integer... ids)
			throws DatabaseException {
		this.activate(companyId, ids == null ? null : Arrays.asList(ids));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void activate(final Integer companyId, final Collection<Integer> ids)
			throws DatabaseException {
		if (ids == null || ids.isEmpty())
			return;
		notNull(companyId, "Invalid null company id");

		final String sql = "UPDATE users SET active = true "
				+ "WHERE company_id = ? AND id = ?";

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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deactivate(final Integer companyId, final Integer... ids)
			throws DatabaseException {
		this.deactivate(companyId, ids == null ? null : Arrays.asList(ids));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deactivate(final Integer companyId,
			final Collection<Integer> ids) throws DatabaseException {
		if (ids == null || ids.isEmpty())
			return;
		notNull(companyId, "Invalid null company id");

		final String sql = "UPDATE users SET active = false "
				+ "WHERE company_id = ? AND id = ?";

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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(final User... users)
			throws DatabaseUniqueConstraintException, DatabaseException {
		this.update(users == null ? null : Arrays.asList(users));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(final Collection<User> users)
			throws DatabaseUniqueConstraintException, DatabaseException {
		if (users == null || users.isEmpty())
			return;

		// NOTE: the hashed_pass and salt values are not updated.

		final String sql = "UPDATE users SET login = ?, email = ?, "
				+ "first_name = ?, last_name = ?, active = ? "
				+ "WHERE company_id = ? AND id = ?";

		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql)) {
			for (final User user : users) {
				int index = 1;
				ps.setString(index++, user.getLogin());
				ps.setString(index++, user.getEmail());
				ps.setString(index++, user.getFirstName());
				ps.setString(index++, user.getLastName());
				ps.setBoolean(index++, user.isActive());
				ps.setInt(index++, user.getCompanyId());
				ps.setInt(index++, user.getId());
				ps.executeUpdate();
			}
		} catch (final SQLIntegrityConstraintViolationException notUnique) {
			throw new DatabaseUniqueConstraintException(notUnique);
		} catch (final SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPassword(final Integer userId, final String hashedPass,
			final String salt) throws DatabaseException {
		notNull(userId, "Invalid null user id");
		notEmpty(hashedPass, "Invalid blank password");
		notEmpty(salt, "Invalid blank salt");

		final String sql = "UPDATE users SET hashed_pass = ?, salt = ? "
				+ "WHERE id = ?";

		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, hashedPass);
			ps.setString(2, salt);
			ps.setInt(3, userId);
			ps.executeUpdate();
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

		final String sql = "DELETE FROM users WHERE id = ?";

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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete(final Integer companyId, final Collection<Integer> ids)
			throws DatabaseException {
		if (ids == null || ids.isEmpty())
			return;

		final String sql = "DELETE FROM users WHERE company_id = ? AND id = ?";

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
