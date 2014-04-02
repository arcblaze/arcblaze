package com.arcblaze.arccore.mail.sender;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.arcblaze.arccore.common.config.Config;
import com.arcblaze.arccore.mail.MailSender;

/**
 * An artificial mail sender that doesn't actually send anything.
 */
public class MockMailSender extends MailSender {
	/**
	 * @param config
	 *            the system configuration information
	 */
	public MockMailSender(final Config config) {
		super(config);
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
	 */
	@Override
	public void populate(final MimeMessage message) throws MessagingException {
		// No need to actually do anything with the message.
	}

	/**
	 * @throws MessagingException
	 *             if there is a problem sending the message
	 */
	@Override
	public void send() throws MessagingException {
		// The message is not sent.
	}
}
