package com.arcblaze.arccore.mail;

import com.arcblaze.arccore.common.config.Property;

/**
 * Defines email-related configuration properties.
 */
public enum MailProperty implements Property {
	/** The name of the system from which the emails originate. */
	SYSTEM_NAME("ArcBlaze"),
	/** The server to use when sending emails over SMTP. */
	SMTP_MAIL_SERVER("localhost"),
	/** The port to use when sending emails over SMTP. */
	SMTP_MAIL_SERVER_PORT("587"),
	/** Whether SSL should be used when sending emails over SMTP. */
	SMTP_MAIL_USE_SSL("true"),
	/** Whether to authenticate with the SMTP server. */
	SMTP_MAIL_AUTHENTICATE("true"),
	/** The user name to use when authenticating SMTP emails. */
	SMTP_MAIL_AUTHENTICATE_USER(""),
	/** The password to use when authenticating SMTP emails. */
	SMTP_MAIL_AUTHENTICATE_PASSWORD(""),
	/** The password to use when authenticating SMTP emails. */
	SMTP_MAIL_DEBUG("false"),
	/** The email address to use as the sender when sending SMTP emails. */
	SENDER_EMAIL(""),
	/** The display name of the user to use when sending SMTP emails. */
	SENDER_NAME("admin"),
	/** The email address of the system administrator. */
	ADMIN_EMAIL(""),
	/** The display name of the system administrator. */
	ADMIN_NAME("admin"),

	;

	private final String defaultValue;

	private MailProperty(final String defaultValue) {
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
