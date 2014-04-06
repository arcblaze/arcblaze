package com.arcblaze.arccore.mail.sender;

import java.io.UnsupportedEncodingException;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.arcblaze.arccore.common.config.Config;
import com.arcblaze.arccore.mail.MailSender;

/**
 * Responsible for sending emails to the system administrator as part of the
 * contact-us page on the web site.
 */
public class ContactUsMailSender extends MailSender {
	private final String system;
	private final String name;
	private final String email;
	private final String type;
	private final String message;

	/**
	 * @param config
	 *            the system configuration information
	 * @param system
	 *            the name of the system that generated the request
	 * @param name
	 *            the name of the user that sent the message
	 * @param email
	 *            the email of the user that sent the message
	 * @param type
	 *            the type of request being sent
	 * @param message
	 *            the content in the message
	 */
	public ContactUsMailSender(final Config config, final String system,
			final String name, final String email, final String type,
			final String message) {
		super(config);

		this.system = system;
		this.name = name;
		this.email = email;
		this.type = type;
		this.message = message;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void populate(final MimeMessage message) throws MessagingException,
			UnsupportedEncodingException {
		message.setText("\nNotice:\n\nThe " + this.system
				+ " web site received a message from the contact-us page. "
				+ "The contents of the message are shown below:\n\n"
				+ "    Name:  " + this.name + "\n    Email: " + this.email
				+ "\n    Type:  " + this.type + "\n    Message:\n\n"
				+ this.message + "\n\n", "UTF-8", "html");
		message.setSubject(this.system + " Message: " + this.type, "UTF-8");

		message.setFrom(getSenderAddress());
		message.addRecipient(Message.RecipientType.TO, getAdminAddress());
	}
}
