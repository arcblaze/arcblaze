package com.arcblaze.arccore.rest.guest;

import static org.apache.commons.lang.Validate.notNull;

import javax.mail.MessagingException;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arcblaze.arccore.common.config.Config;
import com.arcblaze.arccore.mail.MailSender;
import com.arcblaze.arccore.mail.sender.ContactUsMailSender;
import com.arcblaze.arccore.rest.BaseResource;
import com.codahale.metrics.Timer;

/**
 * The REST end-point for sending messages to the site system administrator.
 */
@Path("/contact/send")
public class ContactUsResource extends BaseResource {
	private final static Logger log = LoggerFactory
			.getLogger(ContactUsResource.class);

	@XmlRootElement
	static class MessageSent {
		@XmlElement
		public final boolean success = true;
		@XmlElement
		public final String title = "Message Sent";
		@XmlElement
		public final String msg = "Your message has been sent to the "
				+ "system personnel for processing. If appropriate, they will "
				+ "respond to your message via email soon.";
	}

	/** Used to send the message email to the system administrator. */
	private MailSender mailSender = null;

	/**
	 * Default constructor.
	 */
	public ContactUsResource() {
	}

	/**
	 * @param mailSender
	 *            the object responsible for sending emails
	 */
	public ContactUsResource(final ContactUsMailSender mailSender) {
		notNull(mailSender, "Invalid null mail sender");

		this.mailSender = mailSender;
	}

	/**
	 * @param config
	 *            the system configuration information
	 * @param timer
	 *            tracks timing information for this REST end-point
	 * @param name
	 *            the name of the user that sent the message
	 * @param email
	 *            the email of the user that sent the message
	 * @param type
	 *            the type of request being sent
	 * @param message
	 *            the content in the message
	 * 
	 * @return the password reset response
	 */
	@POST
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public MessageSent send(@Context final Config config,
			@Context final Timer timer, @FormParam("name") final String name,
			@FormParam("email") final String email,
			@FormParam("type") final String type,
			@FormParam("message") final String message) {
		log.debug("Contact Us message request");
		try (final Timer.Context timerContext = timer.time()) {
			if (StringUtils.isBlank(name))
				throw badRequest("The name parameter must be specified.");
			if (StringUtils.isBlank(email))
				throw badRequest("The email parameter must be specified.");
			if (StringUtils.isBlank(type))
				throw badRequest("The type parameter must be specified.");
			if (StringUtils.isBlank(message))
				throw badRequest("The message parameter must be specified.");

			try {
				// The parameters are escaped in ContactUsMailSender.
				if (this.mailSender == null)
					this.mailSender = new ContactUsMailSender(config,
							"ArcTime", name, email, type, message);
				this.mailSender.send();
			} catch (final MessagingException mailException) {
				throw mailError(config, null, mailException);
			}

			return new MessageSent();
		}
	}
}
