package com.arcblaze.arccore.db;

import org.apache.commons.lang.StringUtils;

/**
 * Describes the supported types of databases that can be used as a back-end in
 * this system.
 */
public enum DatabaseType {
	/**
	 * Any database with JDBC-based access.
	 */
	JDBC,

	;

	/**
	 * Attempt to convert the provided value back into a personnel type with
	 * more flexibility than what the {@link #valueOf(String)} method provides.
	 * 
	 * @param value
	 *            the value to attempt conversion into a {@link DatabaseType}
	 * 
	 * @return the identified {@link DatabaseType}, or {@code null} if the
	 *         conversion fails
	 */
	public static DatabaseType parse(final String value) {
		for (final DatabaseType databaseType : values())
			if (StringUtils.equalsIgnoreCase(databaseType.name(), value))
				return databaseType;

		try {
			return DatabaseType.valueOf(value);
		} catch (final IllegalArgumentException badValue) {
			return null;
		}
	}
}
