package com.arcblaze.arccore.mail.sender;

import java.io.UnsupportedEncodingException;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.StringEscapeUtils;

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

		this.system = StringEscapeUtils.escapeHtml(system);
		this.name = StringEscapeUtils.escapeHtml(name);
		this.email = StringEscapeUtils.escapeHtml(email);
		this.type = StringEscapeUtils.escapeHtml(type);
		this.message = StringEscapeUtils.escapeHtml(message);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void populate(final MimeMessage message) throws MessagingException,
			UnsupportedEncodingException {
		final String escaped = StringEscapeUtils.escapeHtml(this.message);

		final StringBuilder msg = new StringBuilder();
		msg.append("<b>Notice:</b><br/>");
		msg.append("<p>The <i>").append(this.system).append("</i> system ");
		msg.append("received a message from the contact-us page:</p><br/>");
		msg.append("<table style=\"border-width:0px;\">");
		msg.append("  <tr>");
		msg.append("    <td style=\"padding-right:20px;\">Name</td>");
		msg.append("    <td>").append(this.name).append("</td>");
		msg.append("  </tr>");
		msg.append("  <tr>");
		msg.append("    <td style=\"padding-right:20px;\">Email</td>");
		msg.append("    <td>").append(this.email).append("</td>");
		msg.append("  </tr>");
		msg.append("  <tr>");
		msg.append("    <td style=\"padding-right:20px;\">Type</td>");
		msg.append("    <td>").append(this.type).append("</td>");
		msg.append("  </tr>");
		msg.append("  <tr>");
		msg.append("    <td colspan=\"2\">").append(escaped).append("</td>");
		msg.append("  </tr>");
		msg.append("</table><br/><br/>");
		message.setText(msg.toString(), "UTF-8", "html");
		message.setSubject(this.system + " Message: " + this.type, "UTF-8");

		message.setFrom(getSenderAddress());
		message.addRecipient(Message.RecipientType.TO, getAdminAddress());
	}
}
