package com.arcblaze.arccore.server;

import com.arcblaze.arccore.common.config.Property;

/**
 * Defines the application server configuration properties.
 */
public enum ServerProperty implements Property {
	/** The location of the system configuration properties file. */
	SERVER_CONFIG_FILE("conf/system.properties"),
	/** Whether the server should run in development mode. */
	SERVER_DEVELOPMENT_MODE("false"),
	/** Whether the server should run in insecure mode (HTTP) or not. */
	SERVER_INSECURE_MODE("false"),
	/** The insecure (http) port on which the web server will listen. */
	SERVER_PORT_INSECURE("80"),
	/** The secure (https) port on which the web server will listen. */
	SERVER_PORT_SECURE("443"),
	/** The server host name published from the web server. */
	SERVER_HOSTNAME(""),
	/** The name of the certificate alias in the key store. */
	SERVER_CERTIFICATE_KEY_ALIAS(""),
	/** The key store file containing the server certificate. */
	SERVER_KEYSTORE_FILE("conf/server.jks"),
	/** The password to use when accessing the key store (no default). */
	SERVER_KEYSTORE_PASS(""),

	;

	private final String defaultValue;

	private ServerProperty(final String defaultValue) {
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
