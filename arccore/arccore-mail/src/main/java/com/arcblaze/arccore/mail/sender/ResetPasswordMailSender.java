package com.arcblaze.arccore.mail.sender;

import java.io.UnsupportedEncodingException;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.arcblaze.arccore.common.config.Config;
import com.arcblaze.arccore.common.model.User;
import com.arcblaze.arccore.mail.MailSender;

/**
 * Responsible for sending emails to user accounts when they request a password
 * reset.
 */
public class ResetPasswordMailSender extends MailSender {
	private final User user;
	private final String newPassword;

	/**
	 * @param config
	 *            the system configuration information
	 * @param user
	 *            the user whose password has been reset
	 * @param newPassword
	 *            the new password value
	 */
	public ResetPasswordMailSender(final Config config, final User user,
			final String newPassword) {
		super(config);

		this.user = user;
		this.newPassword = newPassword;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void populate(final MimeMessage message) throws MessagingException,
			UnsupportedEncodingException {
		message.setText("\nNotice:\n\n"
				+ "The ArcTime web site received a password change request "
				+ "for your account.  If you have not requested a password "
				+ "change from the ArcTime web site by indicating that you "
				+ "forgot your password, please inform your security point of "
				+ "contact about the possible security breach attempt.\n\n"
				+ "Otherwise, here is your new password:\n    "
				+ this.newPassword + "\n\n"
				+ "If you have any problems or questions, contact your "
				+ "supervisor or ArcTime support.\n\n", "UTF-8", "html");
		message.setSubject("Password Change Notification", "UTF-8");

		message.setFrom(getAdminAddress());
		message.addRecipient(Message.RecipientType.TO, getAddress(this.user));
	}
}
