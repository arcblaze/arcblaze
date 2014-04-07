package com.arcblaze.arccore.rest.login;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.junit.Test;
import org.mockito.Mockito;

import com.arcblaze.arccore.common.config.Config;
import com.arcblaze.arccore.common.model.User;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

/**
 * Perform testing of the user logout resource.
 */
public class LogoutResourceTest {
	/**
	 * Test how the resource responds when the provided redirect URI is null.
	 * 
	 * @throws URISyntaxException
	 *             if the URI is malformed
	 * @throws ServletException
	 *             never
	 */
	@Test
	public void testLogout() throws URISyntaxException, ServletException {
		final Config config = new Config();
		final HttpSession session = Mockito.mock(HttpSession.class);
		final HttpServletRequest request = Mockito
				.mock(HttpServletRequest.class);
		Mockito.when(request.getSession()).thenReturn(session);
		final UriInfo uriInfo = Mockito.mock(UriInfo.class);
		Mockito.when(uriInfo.getBaseUri()).thenReturn(
				new URI("http://localhost/rest/logout"));
		final MetricRegistry metricRegistry = new MetricRegistry();
		final Timer timer = metricRegistry.timer("test");

		final User user = new User().setId(1).setCompanyId(1).setLogin("user");
		final SecurityContext securityContext = Mockito
				.mock(SecurityContext.class);
		Mockito.when(securityContext.getUserPrincipal()).thenReturn(user);

		final LogoutResource resource = new LogoutResource();
		final Response response = resource.logout(securityContext, request,
				uriInfo, config, timer);

		assertNotNull(response);
		assertEquals(303, response.getStatus());
		assertEquals("/", response.getLocation().getPath());

		Mockito.verify(request, Mockito.times(1)).logout();
	}
}
