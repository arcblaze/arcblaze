package com.arcblaze.arccore.rest.login;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arcblaze.arccore.rest.BaseResource;
import com.codahale.metrics.Timer;

/**
 * The REST end-point for invalidating user sessions (to logout).
 */
@Path("/logout")
public class LogoutResource extends BaseResource {
	private final static Logger log = LoggerFactory
			.getLogger(LogoutResource.class);

	@Context
	private Timer timer;
	@Context
	private HttpServletRequest request;

	/**
	 * @param request
	 *            the request containing the session to invalidate
	 * @param uriInfo
	 *            the URI information associated with this web request
	 * 
	 * @return a redirection back to the home page
	 * 
	 * @throws URISyntaxException
	 *             if there is a problem with the created URI
	 */
	@GET
	public Response logout(@Context HttpServletRequest request,
			@Context UriInfo uriInfo) throws URISyntaxException {
		log.debug("User logout request");
		try (Timer.Context timerContext = this.timer.time()) {
			request.getSession().invalidate();

			String baseUri = uriInfo.getBaseUri().toString();
			baseUri = baseUri.substring(0, baseUri.indexOf("/rest"));
			return Response.seeOther(new URI(baseUri)).build();
		}
	}
}
