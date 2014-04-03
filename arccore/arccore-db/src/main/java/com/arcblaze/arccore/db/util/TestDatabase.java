package com.arcblaze.arccore.db.util;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.hsqldb.jdbc.JDBCDriver;

import com.arcblaze.arccore.common.config.Config;
import com.arcblaze.arccore.db.ConnectionManager;
import com.arcblaze.arccore.db.DaoFactory;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arccore.db.DatabaseProperty;

/**
 * Used to initialize a simple in-memory database for testing purposes.
 */
public class TestDatabase implements Closeable {
	/** Used to retrieve database connections. */
	private final ConnectionManager connectionManager;

	/**
	 * Default constructor.
	 */
	public TestDatabase() {
		final Config config = new Config();
		config.set(DatabaseProperty.DB_DRIVER, JDBCDriver.class.getName());
		config.set(DatabaseProperty.DB_URL, "jdbc:hsqldb:mem:testdb");
		config.set(DatabaseProperty.DB_USERNAME, "SA");
		config.set(DatabaseProperty.DB_PASSWORD, "");

		this.connectionManager = new ConnectionManager(config);
	}

	/**
	 * @return a {@link DaoFactory} connected to this test database
	 */
	public DaoFactory getDaoFactory() {
		return new DaoFactory(this.connectionManager);
	}

	/**
	 * @return the internal connection manager used to access the test database
	 */
	public ConnectionManager getConnectionManager() {
		return this.connectionManager;
	}

	/**
	 * Close any open database connections.
	 */
	@Override
	public void close() {
		try {
			this.connectionManager.close();
		} catch (final SQLException sqlException) {
			// Ignored.
		}
	}

	/**
	 * @param fileOrResource
	 *            the file path or class path resource of the SQL schema to load
	 * 
	 * @throws DatabaseException
	 *             if there is a problem loading the schema into the database
	 */
	public void load(final String fileOrResource) throws DatabaseException {
		StringBuilder sql = null;
		final File schemaFile = new File(fileOrResource);
		if (!schemaFile.exists()) {
			final URL schemaUrl = getClass().getClassLoader().getResource(
					fileOrResource);
			if (schemaUrl != null) {
				try {
					sql = getSqlFromURL(schemaUrl);
				} catch (final IOException loadFailed) {
					throw new DatabaseException(loadFailed);
				}
			} else
				throw new DatabaseException("Failed to find database schema: "
						+ fileOrResource);
		} else {
			try {
				sql = getSqlFromFile(schemaFile);
			} catch (final IOException loadFailed) {
				throw new DatabaseException(loadFailed);
			}
		}

		try (final Connection conn = this.connectionManager.getConnection();
				final Statement stmt = conn.createStatement()) {
			stmt.execute(sql.toString());
		} catch (final SQLException sqlException) {
			throw new DatabaseException(sqlException);
		}
	}

	protected static StringBuilder getSqlFromFile(final File schema)
			throws IOException {
		final StringBuilder sql = new StringBuilder();
		try (final FileReader fr = new FileReader(schema);
				final BufferedReader br = new BufferedReader(fr)) {
			String line = null;
			while ((line = br.readLine()) != null) {
				sql.append(line);
				sql.append("\n");
			}
		}
		return sql;
	}

	protected static StringBuilder getSqlFromURL(URL schema) throws IOException {
		final StringBuilder sql = new StringBuilder();
		try (final InputStream is = schema.openStream();
				final InputStreamReader isr = new InputStreamReader(is);
				final BufferedReader br = new BufferedReader(isr)) {
			String line = null;
			while ((line = br.readLine()) != null) {
				sql.append(line);
				sql.append("\n");
			}
		}
		return sql;
	}
}
