package com.arcblaze.arccore.db.dao.jdbc;

import static org.apache.commons.lang.Validate.notNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;

import com.arcblaze.arccore.common.model.Role;
import com.arcblaze.arccore.common.model.User;
import com.arcblaze.arccore.db.ConnectionManager;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arccore.db.dao.RoleDao;

/**
 * Manages user roles within the back-end database.
 */
public class JdbcRoleDao implements RoleDao {
	/** Used to retrieve database connection objects. */
	private final ConnectionManager connectionManager;

	/**
	 * @param connectionManager
	 *            used to retrieve database connection objects
	 */
	public JdbcRoleDao(final ConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<Role> get(final Integer userId) throws DatabaseException {
		notNull(userId, "Invalid null user id");

		final String sql = "SELECT * FROM roles WHERE user_id = ?";

		final Set<Role> roles = new TreeSet<>();
		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, userId);

			try (final ResultSet rs = ps.executeQuery()) {
				while (rs.next())
					roles.add(new Role(rs.getString("name")));
			}
		} catch (final SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
		return roles;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void populateUsers(final User... users) throws DatabaseException {
		populateUsers(users == null ? null : Arrays.asList(users));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void populateUsers(final Collection<User> users)
			throws DatabaseException {
		if (users == null || users.isEmpty())
			return;

		final Map<Integer, User> userMap = new HashMap<>();
		for (final User user : users)
			userMap.put(user.getId(), user);

		final String sql = String.format(
				"SELECT * FROM roles WHERE user_id IN (%s)",
				StringUtils.join(userMap.keySet(), ","));

		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql);
				final ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				final User user = userMap.get(rs.getInt("user_id"));
				if (user != null)
					user.addRoles(new Role(rs.getString("name")));
			}
		} catch (final SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final Integer userId, final Role... roles)
			throws DatabaseException {
		this.add(userId, roles == null ? null : Arrays.asList(roles));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(final Integer userId, final Collection<Role> roles)
			throws DatabaseException {
		if (roles == null || roles.isEmpty())
			return;
		notNull(userId, "Invalid null user id");

		final String sql = "INSERT INTO roles (name, user_id) VALUES (?, ?)";

		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql)) {
			for (final Role role : roles) {
				ps.setString(1, role.getName());
				ps.setInt(2, userId);
				ps.executeUpdate();
			}
		} catch (SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete(final Integer userId, final Role... roles)
			throws DatabaseException {
		this.delete(userId, roles == null ? null : Arrays.asList(roles));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete(final Integer userId, final Collection<Role> roles)
			throws DatabaseException {
		if (roles == null || roles.isEmpty())
			return;
		notNull(userId, "Invalid null user id");

		String sql = "DELETE FROM roles WHERE name = ? AND user_id = ?";

		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql)) {
			for (final Role role : roles) {
				ps.setString(1, role.getName());
				ps.setInt(2, userId);
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
	public void delete(final Integer userId) throws DatabaseException {
		notNull(userId, "Invalid null user id");

		String sql = "DELETE FROM roles WHERE user_id = ?";

		try (final Connection conn = this.connectionManager.getConnection();
				final PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, userId);
			ps.executeUpdate();
		} catch (final SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}
}
