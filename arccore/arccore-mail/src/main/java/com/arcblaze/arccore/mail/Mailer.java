package com.arcblaze.arccore.mail;

import static org.apache.commons.lang.Validate.notNull;

import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

import com.arcblaze.arccore.common.config.Config;

/**
 * Responsible for sending emails. Package private since it is only meant to be used inside {@link MailSender} objects.
 */
class Mailer {
    /** Holds the configuration information for making mail connections. */
    private final Config config;

    /**
     * @param config
     *            the object from which configuration information will be retrieved
     * 
     * @throws IllegalArgumentException
     *             if the provided configuration is {@code null}
     */
    public Mailer(final Config config) {
        notNull(config, "Invalid null config");

        this.config = config;
    }

    private Properties getMailConfiguration() {
        final Properties props = System.getProperties();
        props.setProperty("mail.smtp.host", this.config.getString(MailProperty.SMTP_MAIL_SERVER));
        props.setProperty("mail.smtp.port", String.valueOf(this.config.getInt(MailProperty.SMTP_MAIL_SERVER_PORT)));

        if (this.config.getBoolean(MailProperty.SMTP_MAIL_USE_SSL)) {
            props.setProperty("mail.smtp.ssl.enable", "true");
            props.setProperty("mail.smtp.starttls.enable", "true");
            props.setProperty("mail.smtp.ssl.protocols", "SSLv3 TLSv1");
        }

        if (this.config.getBoolean(MailProperty.SMTP_MAIL_AUTHENTICATE))
            props.setProperty("mail.smtp.auth", "true");

        if (this.config.getBoolean(MailProperty.SMTP_MAIL_DEBUG))
            props.setProperty("mail.debug", "true");

        return props;
    }

    /**
     * @return a session configured with the current mail configuration settings
     */
    Session getSession() {
        return Session.getInstance(getMailConfiguration(), null);
    }

    /**
     * @param session
     *            the configured session to use when sending the email
     * @param message
     *            the message to be sent
     * 
     * @throws MessagingException
     *             if there is a problem sending the message
     */
    void send(final Session session, final MimeMessage message) throws MessagingException {
        if (this.config.getBoolean(MailProperty.SMTP_MAIL_AUTHENTICATE)) {
            final String server = this.config.getString(MailProperty.SMTP_MAIL_SERVER);
            final int port = this.config.getInt(MailProperty.SMTP_MAIL_SERVER_PORT);
            final String user = this.config.getString(MailProperty.SMTP_MAIL_AUTHENTICATE_USER);
            final String password = this.config.getString(MailProperty.SMTP_MAIL_AUTHENTICATE_PASSWORD);
            final String protocol = this.config.getBoolean(MailProperty.SMTP_MAIL_USE_SSL) ? "smtps" : "smtp";
            final Transport transport = session.getTransport(protocol);
            transport.connect(server, port, user, password);
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        } else
            Transport.send(message);
    }
}
