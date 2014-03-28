package com.arcblaze.arccore.db;

import com.arcblaze.arccore.common.config.Property;

/**
 * Defines email-related configuration properties.
 */
public enum DatabaseProperty implements Property {
	/** The type of back-end database being used. */
	DB_TYPE("jdbc"),
	/** The driver class name to use when creating connections. */
	DB_DRIVER("com.mysql.jdbc.Driver"),
	/** The JDBC connection URL to use when accessing the database. */
	DB_URL("jdbc:mysql://localhost/db"),
	/** The name of the user to use when authenticating with the database. */
	DB_USERNAME(""),
	/** The password to use when authenticating with the database. */
	DB_PASSWORD(""),

	;

	private final String defaultValue;

	private DatabaseProperty(final String defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getKey() {
		return this.name().toLowerCase().replaceAll("_", ".");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDefaultValue() {
		return this.defaultValue;
	}
}
