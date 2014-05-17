package com.arcblaze.arccore.mail.sender;

import java.io.UnsupportedEncodingException;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.arcblaze.arccore.common.config.Config;
import com.arcblaze.arccore.common.model.User;
import com.arcblaze.arccore.mail.MailProperty;
import com.arcblaze.arccore.mail.MailSender;

/**
 * Responsible for sending emails to user accounts when they request a password reset.
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
    public ResetPasswordMailSender(final Config config, final User user, final String newPassword) {
        super(config);

        this.user = user;
        this.newPassword = newPassword;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void populate(final MimeMessage message) throws MessagingException, UnsupportedEncodingException {
        final String system = getConfig().getString(MailProperty.SYSTEM_NAME);

        final StringBuilder msg = new StringBuilder();
        msg.append("<b>Notice:</b><br/><br/>");
        msg.append("<p>The <i>").append(system).append("</i> web site ");
        msg.append("received a password change request from your account. ");
        msg.append("If you have not requested a password change from the ");
        msg.append(system).append(" web site by indicating that you ");
        msg.append("forgot your password, please inform your security point ");
        msg.append("of contact about the possible security breach attempt.");
        msg.append("<br/><br/>");
        msg.append("Otherwise, here is your new password:<br/><br/>");
        msg.append("<span style=\"font-family:monospace;padding-left:20px;\">");
        msg.append(this.newPassword).append("</span><br/><br/>");
        msg.append("If you have any questions or problems, contact your ");
        msg.append("company representative or submit a support request.");
        msg.append("<br/><br/>").append(system).append(" Support");
        message.setText(msg.toString(), "UTF-8", "html");
        message.setSubject(system + " Password Change", "UTF-8");

        message.setFrom(getSenderAddress());
        message.addRecipient(Message.RecipientType.TO, getAddress(this.user));
    }
}
