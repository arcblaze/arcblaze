package com.arcblaze.arctime.rest.user;

import static org.apache.commons.lang.Validate.notEmpty;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
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
import com.arcblaze.arccore.db.DatabaseUniqueConstraintException;
import com.arcblaze.arccore.db.dao.UserDao;
import com.arcblaze.arccore.rest.BaseResource;
import com.codahale.metrics.Timer;

/**
 * The REST end-point for saving profile changes on behalf of a user.
 */
@Path("/user/profile")
public class ProfileUpdateResource extends BaseResource {
	private final static Logger log = LoggerFactory
			.getLogger(ProfileUpdateResource.class);

	@XmlRootElement
	static class ProfileUpdate {
		@XmlElement
		public final boolean success = true;

		@XmlElement
		public final String title = "Profile Updated";

		@XmlElement
		public final String msg = "Your profile information has been updated "
				+ "successfully.";
	}

	/**
	 * @param security
	 *            the security information associated with the request
	 * @param firstName
	 *            the updated first name for the user
	 * @param lastName
	 *            the updated last name for the user
	 * @param login
	 *            the updated user login value
	 * @param email
	 *            the updated email address for the user
	 * @param password
	 *            the updated password for the user
	 */
	protected void validateParams(final String firstName,
			final String lastName, final String login, final String email,
			final String password) {
		try {
			notEmpty(firstName, "The firstName parameter must be specified.");
			notEmpty(lastName, "The lastName parameter must be specified.");
			notEmpty(login, "The login parameter must be specified.");

			// NOTE: No attempt at validating the email address is intentional.
			notEmpty(email, "The email parameter must be specified.");
		} catch (final IllegalArgumentException badParam) {
			throw badRequest(badParam.getMessage());
		}
	}

	/**
	 * @param security
	 *            the security information associated with the request
	 * @param config
	 *            the system configuration properties
	 * @param daoFactory
	 *            used to communicate with the back-end database
	 * @param password
	 *            used to perform password hashing
	 * @param timer
	 *            tracks performance metrics of this REST end-point
	 * @param firstName
	 *            the updated first name for the user
	 * @param lastName
	 *            the updated last name for the user
	 * @param login
	 *            the updated user login value
	 * @param email
	 *            the updated email address for the user
	 * @param pass
	 *            the updated password for the user, possibly null if the
	 *            password should not be updated
	 * 
	 * @return the profile update response
	 */
	@POST
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public ProfileUpdate update(@Context final SecurityContext security,
			@Context final Config config, @Context final DaoFactory daoFactory,
			@Context final Password password, @Context final Timer timer,
			@FormParam("firstName") final String firstName,
			@FormParam("lastName") final String lastName,
			@FormParam("login") final String login,
			@FormParam("email") final String email,
			@FormParam("password") final String pass) {
		log.debug("Profile update request");
		final User currentUser = (User) security.getUserPrincipal();
		try (final Timer.Context timerContext = timer.time()) {
			validateParams(firstName, lastName, login, email, pass);

			final UserDao dao = daoFactory.getUserDao();
			final User user = dao.get(currentUser.getId());
			log.debug("Found user: {}", user);

			if (user == null)
				throw notFound("The current user was not found.");

			user.setFirstName(firstName);
			user.setLastName(lastName);
			user.setLogin(login);
			user.setEmail(email);
			user.setActive(currentUser.isActive());
			log.debug("Modified user: {}", user);

			dao.update(user);

			if (StringUtils.isNotBlank(pass)) {
				log.debug("Updating user password");
				final String salt = password.random(10);
				final String hashedPass = password.hash(pass, salt);
				dao.setPassword(currentUser.getId(), hashedPass, salt);
				log.debug("Password updated successfully");
			}

			return new ProfileUpdate();
		} catch (final DatabaseUniqueConstraintException alreadyExists) {
			throw badRequest("The specified login or email already exists.");
		} catch (final DatabaseException dbException) {
			throw dbError(config, currentUser, dbException);
		}
	}
}
