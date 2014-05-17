package com.arcblaze.arccore.rest.login;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arcblaze.arccore.common.config.Config;
import com.arcblaze.arccore.common.model.User;
import com.arcblaze.arccore.rest.BaseResource;
import com.codahale.metrics.Timer;

/**
 * The REST end-point for invalidating user sessions (to logout).
 */
@Path("/logout")
public class LogoutResource extends BaseResource {
    private final static Logger log = LoggerFactory.getLogger(LogoutResource.class);

    /**
     * @param security
     *            the security information associated with the request
     * @param request
     *            the request containing the session to invalidate
     * @param uriInfo
     *            the URI information associated with this web request
     * @param config
     *            the system configuration properties
     * @param timer
     *            tracks performance metrics for this REST end-point
     * 
     * @return a redirection back to the home page
     * 
     * @throws URISyntaxException
     *             if there is a problem with the created URI
     */
    @GET
    public Response logout(@Context final SecurityContext security, @Context final HttpServletRequest request,
            @Context final UriInfo uriInfo, @Context final Config config, @Context final Timer timer)
            throws URISyntaxException {
        log.debug("User logout request");
        try (final Timer.Context timerContext = timer.time()) {
            request.getSession().invalidate();
            request.logout();

            final String baseUri = StringUtils.substringBefore(uriInfo.getBaseUri().toString(), "/rest");
            return Response.seeOther(new URI(baseUri + "/")).build();
        } catch (final ServletException servletException) {
            throw serverError(config, (User) security.getUserPrincipal(), servletException);
        }
    }
}
