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

import org.apache.commons.lang.StringEscapeUtils;
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
    private final static Logger log = LoggerFactory.getLogger(LoginResource.class);

    /**
     * @param request
     *            the web request from the client
     * @param uriInfo
     *            the URI information contained in the request
     * @param timer
     *            tracks performance metrics on this REST end-point
     * @param login
     *            the user login name to use when logging in
     * @param password
     *            the password to use when logging in
     * @param redirectUri
     *            the location to which we should redirect after a successful login
     * 
     * @return a redirection to the user timesheet page
     * 
     * @throws URISyntaxException
     *             if there is a problem with the created URI
     */
    @POST
    public Response login(@Context final HttpServletRequest request, @Context final UriInfo uriInfo,
            @Context final Timer timer, @FormParam("login") final String login,
            @FormParam("password") final String password, @FormParam("redirectUri") final String redirectUri)
            throws URISyntaxException {
        log.debug("User login request: {}", login);
        try (final Timer.Context timerContext = timer.time()) {
            final String escapedLogin = StringEscapeUtils.escapeHtml(login);
            if (StringUtils.isBlank(escapedLogin))
                throw badRequest("Invalid blank user login");
            if (StringUtils.isBlank(password))
                throw badRequest("Invalid blank user password");

            final String remoteUser = request.getRemoteUser();
            if (StringUtils.isNotBlank(remoteUser)) {
                if (!remoteUser.equals(escapedLogin)) {
                    request.logout();
                    request.login(escapedLogin, password);
                } else
                    log.debug("Already logged in.");
            } else
                request.login(escapedLogin, password);

            final String baseUri = StringUtils.substringBefore(uriInfo.getBaseUri().toString(), "/rest");
            final URI uri = redirectUri == null ? new URI(baseUri + "/") : new URI(baseUri + redirectUri);
            return Response.seeOther(uri).build();
        } catch (final ServletException loginFailed) {
            log.error("User login failed: {}", loginFailed.getMessage());
            throw new NotAuthorizedException("Login failed.", loginFailed);
        }
    }
}
