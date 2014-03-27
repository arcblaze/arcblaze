package com.arcblaze.arccore.mail;

import static org.apache.commons.lang.Validate.notEmpty;
import static org.apache.commons.lang.Validate.notNull;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.arcblaze.arccore.common.config.Config;
import com.arcblaze.arccore.common.model.User;

/**
 * Responsible for sending emails.
 */
public class MailSender {
	/** Holds the configuration information for making mail connections. */
	private final Config config;

	/**
	 * @param config
	 *            the object from which configuration information will be
	 *            retrieved
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided configuration is {@code null}
	 */
	public MailSender(final Config config) {
		notNull(config, "Invalid null config");

		this.config = config;
	}

	Properties getMailConfiguration() {
		final Properties props = System.getProperties();
		props.setProperty("mail.smtp.host",
				this.config.getString(MailProperty.SMTP_MAIL_SERVER));
		props.setProperty("mail.smtp.port", String.valueOf(this.config
				.getInt(MailProperty.SMTP_MAIL_SERVER_PORT)));

		if (this.config.getBoolean(MailProperty.SMTP_MAIL_USE_SSL)) {
			props.setProperty("mail.smtp.ssl.enable", "true");
			props.setProperty("mail.smtp.starttls.enable", "true");
			props.setProperty("mail.smtp.ssl.protocols", "SSLv3 TLSv1");
		}

		if (this.config.getBoolean(MailProperty.SMTP_MAIL_AUTHENTICATE))
			props.setProperty("mail.smtp.auth", "true");

		return props;
	}

	/**
	 * @param msg
	 *            the content within the body of the email
	 * @param subject
	 *            the subject to include in the email
	 * @param users
	 *            the users to which the message will be sent
	 * 
	 * @throws MessagingException
	 *             if there is a problem sending the messages
	 * @throws IllegalArgumentException
	 *             if the parameters are invalid
	 */
	public void send(final String msg, final String subject,
			final User... users) throws MessagingException {
		notEmpty(users, "Invalid empty users");
		send(msg, subject, new HashSet<>(Arrays.asList(users)));
	}

	/**
	 * @param msg
	 *            the content within the body of the email
	 * @param subject
	 *            the subject to include in the email
	 * @param users
	 *            the users to which the message will be sent
	 * 
	 * @throws MessagingException
	 *             if there is a problem sending the messages
	 * @throws IllegalArgumentException
	 *             if the parameters are invalid
	 */
	public void send(final String msg, final String subject,
			final Set<User> users) throws MessagingException {
		notEmpty(msg, "Invalid null message");
		notEmpty(subject, "Invalid null subject");
		notEmpty(users, "Invalid empty users");

		final Properties props = getMailConfiguration();

		final Session session = Session.getDefaultInstance(props, null);
		final MimeMessage message = new MimeMessage(session);
		try {
			message.setFrom(new InternetAddress(this.config
					.getString(MailProperty.SENDER_EMAIL), this.config
					.getString(MailProperty.SENDER_NAME)));
			for (final User user : users)
				message.addRecipient(
						Message.RecipientType.TO,
						new InternetAddress(user.getEmail(), user.getFullName()));
			message.setSubject(subject);
			message.setText(msg);
			message.saveChanges();
		} catch (final UnsupportedEncodingException badEncoding) {
			throw new MessagingException(
					"Invalid encoding when sending message.", badEncoding);
		}

		if (this.config.getBoolean(MailProperty.SMTP_MAIL_AUTHENTICATE)) {
			final String server = this.config
					.getString(MailProperty.SMTP_MAIL_SERVER);
			final String user = this.config
					.getString(MailProperty.SMTP_MAIL_AUTHENTICATE_USER);
			final String password = this.config
					.getString(MailProperty.SMTP_MAIL_AUTHENTICATE_PASSWORD);
			final Transport transport = session.getTransport("smtp");
			transport.connect(server, user, password);
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
		} else
			Transport.send(message);
	}
}
