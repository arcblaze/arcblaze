package com.arcblaze.arccore.db;

import java.sql.SQLException;

import com.arcblaze.arccore.common.config.Config;
import com.arcblaze.arccore.db.dao.RoleDao;
import com.arcblaze.arccore.db.dao.UserDao;
import com.arcblaze.arccore.db.dao.jdbc.JdbcRoleDao;
import com.arcblaze.arccore.db.dao.jdbc.JdbcUserDao;

/**
 * Used to retrieve DAO instances to work with the configured back-end database.
 */
public class DaoFactory {
	/** Holds the type of back-end data store used in the cached DAOs. */
	private final DatabaseType databaseType;

	/** Used to generate connections for the JDBC database type. */
	private final ConnectionManager connectionManager;

	private UserDao cachedUserDao = null;
	private RoleDao cachedRoleDao = null;

	/**
	 * @param config
	 *            the system configuration information
	 */
	public DaoFactory(final Config config) {
		this.databaseType = DatabaseType.parse(config
				.getString(DatabaseProperty.DB_TYPE));

		if (DatabaseType.JDBC.equals(this.databaseType))
			this.connectionManager = new ConnectionManager(config);
		else
			this.connectionManager = null;
	}

	/**
	 * @return the internal connection manager used to access the database
	 */
	public ConnectionManager getConnectionManager() {
		return this.connectionManager;
	}

	/**
	 * @return an {@link UserDao} based on the currently configured database
	 */
	public UserDao getUserDao() {
		if (this.cachedUserDao == null) {
			if (DatabaseType.JDBC.equals(this.databaseType))
				this.cachedUserDao = new JdbcUserDao(this.connectionManager);
			else
				throw new RuntimeException("Invalid database type: "
						+ this.databaseType);
		}

		return this.cachedUserDao;
	}

	/**
	 * @return an {@link RoleDao} based on the currently configured database
	 */
	public RoleDao getRoleDao() {
		if (this.cachedRoleDao == null) {
			if (DatabaseType.JDBC.equals(this.databaseType))
				this.cachedRoleDao = new JdbcRoleDao(this.connectionManager);
			else
				throw new RuntimeException("Invalid database type: "
						+ this.databaseType);
		}

		return this.cachedRoleDao;
	}

	/**
	 * Close any resources associated with the internal DAOs.
	 * 
	 * @throws DatabaseException
	 *             if there is a problem closing resources
	 */
	public void close() throws DatabaseException {
		if (this.connectionManager != null) {
			try {
				this.connectionManager.close();
			} catch (final SQLException sqlException) {
				throw new DatabaseException(sqlException);
			}
		}
	}
}
