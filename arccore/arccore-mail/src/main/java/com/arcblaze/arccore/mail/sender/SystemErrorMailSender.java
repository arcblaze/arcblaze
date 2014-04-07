package com.arcblaze.arccore.mail.sender;

import java.io.UnsupportedEncodingException;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.exception.ExceptionUtils;

import com.arcblaze.arccore.common.config.Config;
import com.arcblaze.arccore.common.model.User;
import com.arcblaze.arccore.mail.MailProperty;
import com.arcblaze.arccore.mail.MailSender;

/**
 * Responsible for sending emails to system administrators in the event an error
 * occurs on the web site.
 */
public class SystemErrorMailSender extends MailSender {
	private final User user;
	private final Throwable problem;

	/**
	 * @param config
	 *            the system configuration information
	 * @param user
	 *            the user that generated or experienced the error
	 * @param problem
	 *            the throwable representing the problem
	 */
	public SystemErrorMailSender(final Config config, final User user,
			final Throwable problem) {
		super(config);

		this.user = user;
		this.problem = problem;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void populate(final MimeMessage message) throws MessagingException,
			UnsupportedEncodingException {
		final String system = getConfig().getString(MailProperty.SYSTEM_NAME);

		final StringBuilder msg = new StringBuilder();
		msg.append("<b>Warning:</b><br/><br/>");
		msg.append("<p>The <i>").append(system).append("</i> web site ");
		msg.append("experienced a server error.</p>");

		if (this.user != null) {
			msg.append("<p>This user was involved:</p>");
			msg.append("<table>");
			msg.append("  <tr>");
			msg.append("    <td>").append(this.user.getFullName());
			msg.append(" (").append(this.user.getLogin()).append(")</td>");
			msg.append("  </tr>");
			msg.append("  <tr>");
			msg.append("    <td>").append(this.user.getEmail()).append("</td>");
			msg.append("  </tr>");
			msg.append("</table>");
		}

		if (this.problem != null) {
			msg.append("<p>The error:</p>");
			msg.append("<pre style=\"color:red;\">");
			msg.append(ExceptionUtils.getFullStackTrace(this.problem));
			msg.append("</pre>");
		}

		message.setText(msg.toString(), "UTF-8", "html");
		message.setSubject(system + " System Error", "UTF-8");

		message.setFrom(getSenderAddress());
		message.addRecipient(Message.RecipientType.TO, getAdminAddress());
	}
}
