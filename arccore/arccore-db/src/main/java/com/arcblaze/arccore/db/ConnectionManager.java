package com.arcblaze.arccore.db;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;

import com.arcblaze.arccore.common.config.Config;

/**
 * Used to manage database connections.
 */
public class ConnectionManager {
	/** Used to pool connections to the database. */
	private final BasicDataSource dataSource;

	/**
	 * @param config
	 *            contains the system configuration properties used to configure
	 *            connections
	 */
	public ConnectionManager(final Config config) {
		this.dataSource = new BasicDataSource();
		this.dataSource.setDriverClassName(config
				.getString(DatabaseProperty.DB_DRIVER));
		this.dataSource.setUrl(config.getString(DatabaseProperty.DB_URL));
		this.dataSource.setUsername(config
				.getString(DatabaseProperty.DB_USERNAME));
		this.dataSource.setPassword(config
				.getString(DatabaseProperty.DB_PASSWORD));
		this.dataSource.setDefaultAutoCommit(true);
	}

	/**
	 * @return a {@link Connection} to the database
	 * 
	 * @throws SQLException
	 *             if there is a database connection problem
	 */
	public Connection getConnection() throws SQLException {
		return this.dataSource.getConnection();
	}

	/**
	 * Close all of the cached connections in the connection pool.
	 * 
	 * @throws SQLException
	 *             if there is a problem closing the data source
	 */
	public void close() throws SQLException {
		this.dataSource.close();
	}
}
