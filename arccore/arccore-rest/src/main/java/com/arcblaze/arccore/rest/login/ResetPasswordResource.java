package com.arcblaze.arccore.rest.login;

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
import com.arcblaze.arccore.common.model.Password;
import com.arcblaze.arccore.common.model.User;
import com.arcblaze.arccore.db.DaoFactory;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arccore.db.dao.UserDao;
import com.arcblaze.arccore.mail.MailProperty;
import com.arcblaze.arccore.mail.MailSender;
import com.arcblaze.arccore.mail.sender.ResetPasswordMailSender;
import com.arcblaze.arccore.rest.BaseResource;
import com.codahale.metrics.Timer;

/**
 * The REST end-point for performing password resets for a user.
 */
@Path("/login/reset")
public class ResetPasswordResource extends BaseResource {
	private final static Logger log = LoggerFactory
			.getLogger(ResetPasswordResource.class);

	@XmlRootElement
	private static class PasswordReset {
		@XmlElement
		public final boolean success = true;
		@XmlElement
		public final String title = "Password Reset";
		@XmlElement
		public final String msg;

		PasswordReset(final Config config) {
			final String adminEmail = config
					.getString(MailProperty.ADMIN_EMAIL);
			this.msg = "An email with a new random password was "
					+ "sent to the email address associated with your account. "
					+ "Please check your email for your updated login info. "
					+ "If you have any problems, please contact the web site "
					+ "administrator (" + adminEmail + ")";
		}
	}

	/** Used to send the response email to the user. */
	private MailSender mailSender = null;

	/**
	 * Default constructor.
	 */
	public ResetPasswordResource() {
	}

	/**
	 * @param mailSender
	 *            the object responsible for sending emails
	 */
	public ResetPasswordResource(final ResetPasswordMailSender mailSender) {
		notNull(mailSender, "Invalid null mail sender");

		this.mailSender = mailSender;
	}

	/**
	 * @param config
	 *            the system configuration information
	 * @param daoFactory
	 *            used to communicate with the system database
	 * @param password
	 *            used to generate new passwords
	 * @param timer
	 *            tracks timing information for this REST end-point
	 * @param login
	 *            the user login to use when resetting the password
	 * 
	 * @return the password reset response
	 */
	@POST
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public PasswordReset reset(@Context final Config config,
			@Context final DaoFactory daoFactory,
			@Context final Password password, @Context final Timer timer,
			@FormParam("j_username") final String login) {
		log.debug("Password reset request");
		try (final Timer.Context timerContext = timer.time()) {
			if (StringUtils.isBlank(login))
				throw badRequest("The j_username parameter must be specified.");

			final UserDao userDao = daoFactory.getUserDao();
			final User user = userDao.getLogin(login);
			log.debug("  Found user: {}", user);

			if (user == null)
				throw notFound("A user with the specified login was not found.");

			final String salt = password.random(10);
			final String newPassword = password.random();
			final String hashedPass = password.hash(newPassword, salt);
			log.debug("  New password will be: {}", newPassword);
			log.debug("  Hashed password will be: {}", hashedPass);

			userDao.setPassword(user.getId(), hashedPass, salt);
			log.debug("  Password updated successfully");

			try {
				if (this.mailSender == null)
					this.mailSender = new ResetPasswordMailSender(config, user,
							newPassword);
				this.mailSender.send();
			} catch (final MessagingException mailException) {
				log.debug("  Failed to send email, setting password back");
				userDao.setPassword(user.getId(), user.getHashedPass(),
						user.getSalt());
				throw mailError(mailException);
			}

			return new PasswordReset(config);
		} catch (final DatabaseException dbException) {
			throw dbError(dbException);
		}
	}
}
