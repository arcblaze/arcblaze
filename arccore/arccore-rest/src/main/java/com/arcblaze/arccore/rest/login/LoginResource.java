package com.arcblaze.arccore.rest.login;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arcblaze.arccore.rest.BaseResource;
import com.codahale.metrics.Timer;

/**
 * The REST end-point for creating user sessions (to login).
 */
@Path("/login")
public class LoginResource extends BaseResource {
	private final static Logger log = LoggerFactory
			.getLogger(LoginResource.class);

	@Context
	private Timer timer;
	@Context
	private HttpServletRequest request;
	@Context
	private UriInfo uriInfo;

	/**
	 * @param login
	 *            the user login name to use when logging in
	 * @param password
	 *            the password to use when logging in
	 * @param redirectUri
	 *            the location to which we should redirect after a successful
	 *            login
	 * 
	 * @return a redirection to the user timesheet page
	 * 
	 * @throws URISyntaxException
	 *             if there is a problem with the created URI
	 */
	@POST
	public Response login(final @FormParam("login") String login,
			final @FormParam("password") String password,
			final @FormParam("redirectUri") String redirectUri)
			throws URISyntaxException {
		log.debug("User login request: {}", login);
		try (Timer.Context timerContext = this.timer.time()) {
			final String remoteUser = this.request.getRemoteUser();
			if (StringUtils.isNotBlank(remoteUser)) {
				if (!remoteUser.equals(login)) {
					this.request.logout();
					this.request.login(login, password);
				} else
					log.debug("Already logged in.");
			} else
				this.request.login(login, password);

			String baseUri = this.uriInfo.getBaseUri().toString();
			baseUri = baseUri.substring(0, baseUri.indexOf("/rest"));
			return Response.seeOther(new URI(baseUri + redirectUri)).build();
		} catch (final ServletException loginFailed) {
			log.error("User login failed.", loginFailed);
			throw new NotAuthorizedException("Login failed.", loginFailed);
		}
	}
}
