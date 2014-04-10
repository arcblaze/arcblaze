package com.arcblaze.arccore.mail;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Send emails in a background process.
 */
public class MailSenderThread extends Thread {
	private final static Logger log = LoggerFactory
			.getLogger(MailSenderThread.class);

	private final MailSender mailSender;

	/**
	 * @param mailSender
	 *            the {@link MailSender} used to send the email
	 */
	public MailSenderThread(final MailSender mailSender) {
		this.mailSender = mailSender;
	}

	@Override
	public void run() {
		try {
			this.mailSender.send();
		} catch (final MessagingException mailFailure) {
			log.error("Failed to send background email.", mailFailure);
		}
	}
}
