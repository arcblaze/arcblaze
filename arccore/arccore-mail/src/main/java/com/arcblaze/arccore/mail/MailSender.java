package com.arcblaze.arccore.mail;

import static org.apache.commons.lang.Validate.notNull;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.arcblaze.arccore.common.config.Config;
import com.arcblaze.arccore.common.model.User;

/**
 * The base class for mail-sending implementations.
 */
public abstract class MailSender {
	private final Mailer mailer;
	private final Config config;

	/**
	 * @param config
	 *            the system configuration information
	 */
	public MailSender(final Config config) {
		this.mailer = new Mailer(config);
		this.config = config;
	}

	/**
	 * @return the system configuration information
	 */
	protected Config getConfig() {
		return this.config;
	}

	/**
	 * @return an {@link InternetAddress} representing the administrator email
	 * 
	 * @throws UnsupportedEncodingException
	 *             if there is an encoding issue
	 */
	protected InternetAddress getAdminAddress()
			throws UnsupportedEncodingException {
		final String email = this.config.getString(MailProperty.ADMIN_EMAIL);
		final String name = this.config.getString(MailProperty.ADMIN_NAME);
		return new InternetAddress(email, name);
	}

	/**
	 * @return an {@link InternetAddress} representing the generic system email
	 *         sending account
	 * 
	 * @throws UnsupportedEncodingException
	 *             if there is an encoding issue
	 */
	protected InternetAddress getSenderAddress()
			throws UnsupportedEncodingException {
		final String email = this.config.getString(MailProperty.SENDER_EMAIL);
		final String name = this.config.getString(MailProperty.SENDER_NAME);
		return new InternetAddress(email, name);
	}

	/**
	 * @param user
	 *            the {@link User} for which the corresponding internet address
	 *            will be created
	 * 
	 * @return the {@link InternetAddress} associated with the user account
	 * 
	 * @throws UnsupportedEncodingException
	 *             if there is an encoding issue
	 */
	protected InternetAddress getAddress(final User user)
			throws UnsupportedEncodingException {
		notNull(user.getEmail(), "Invalid null email address");

		// The full name can be null.
		return new InternetAddress(user.getEmail(), user.getFullName());
	}

	/**
	 * Used to fully populate a {@link MimeMessage} in preparation for being
	 * sent.
	 * 
	 * @param message
	 *            the {@link MimeMessage} to be populated with from, to,
	 *            subject, and message information
	 * 
	 * @throws MessagingException
	 *             if there is a problem sending the email
	 * @throws UnsupportedEncodingException
	 *             if there is an encoding issue
	 */
	public abstract void populate(final MimeMessage message)
			throws MessagingException, UnsupportedEncodingException;

	/**
	 * @throws MessagingException
	 *             if there is a problem sending the message
	 */
	public void send() throws MessagingException {
		final Session session = this.mailer.getSession();
		final MimeMessage message = new MimeMessage(session);

		try {
			// Child classes have to populate the message.
			populate(message);
		} catch (final UnsupportedEncodingException badEncoding) {
			throw new MessagingException("Bad encoding.", badEncoding);
		}

		this.mailer.send(session, message);
	}
}
