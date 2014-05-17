package com.arcblaze.arccore.rest.login;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.junit.Test;
import org.mockito.Mockito;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

/**
 * Perform testing of the user login resource.
 */
public class LoginResourceTest {
    /**
     * Test how the resource responds when the provided login and password values are null.
     * 
     * @throws URISyntaxException
     *             if the URI is malformed
     */
    @Test(expected = BadRequestException.class)
    public void testNullParams() throws URISyntaxException {
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        final UriInfo uriInfo = Mockito.mock(UriInfo.class);
        final MetricRegistry metricRegistry = new MetricRegistry();
        final Timer timer = metricRegistry.timer("test");

        final LoginResource resource = new LoginResource();
        resource.login(request, uriInfo, timer, null, null, null);
    }

    /**
     * Test how the resource responds when the provided login is null.
     * 
     * @throws URISyntaxException
     *             if the URI is malformed
     */
    @Test(expected = BadRequestException.class)
    public void testNullLogin() throws URISyntaxException {
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        final UriInfo uriInfo = Mockito.mock(UriInfo.class);
        final MetricRegistry metricRegistry = new MetricRegistry();
        final Timer timer = metricRegistry.timer("test");

        final LoginResource resource = new LoginResource();
        resource.login(request, uriInfo, timer, null, "password", "/uri");
    }

    /**
     * Test how the resource responds when the provided password is null.
     * 
     * @throws URISyntaxException
     *             if the URI is malformed
     */
    @Test(expected = BadRequestException.class)
    public void testNullPassword() throws URISyntaxException {
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        final UriInfo uriInfo = Mockito.mock(UriInfo.class);
        final MetricRegistry metricRegistry = new MetricRegistry();
        final Timer timer = metricRegistry.timer("test");

        final LoginResource resource = new LoginResource();
        resource.login(request, uriInfo, timer, "user", null, "/uri");
    }

    /**
     * Test how the resource responds when the provided redirect URI is null.
     * 
     * @throws URISyntaxException
     *             if the URI is malformed
     * @throws ServletException
     *             never
     */
    @Test
    public void testNullRedirectUri() throws URISyntaxException, ServletException {
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        final UriInfo uriInfo = Mockito.mock(UriInfo.class);
        Mockito.when(uriInfo.getBaseUri()).thenReturn(new URI("http://localhost/rest/login"));
        final MetricRegistry metricRegistry = new MetricRegistry();
        final Timer timer = metricRegistry.timer("test");

        final LoginResource resource = new LoginResource();
        final Response response = resource.login(request, uriInfo, timer, "user", "password", null);

        assertNotNull(response);
        assertEquals(303, response.getStatus());
        assertEquals("/", response.getLocation().getPath());

        Mockito.verify(request, Mockito.times(1)).login("user", "password");
    }

    /**
     * Test how the resource responds when all parameters are valid.
     * 
     * @throws URISyntaxException
     *             if the URI is malformed
     * @throws ServletException
     *             never
     */
    @Test
    public void testFullLoginNoException() throws URISyntaxException, ServletException {
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        final UriInfo uriInfo = Mockito.mock(UriInfo.class);
        Mockito.when(uriInfo.getBaseUri()).thenReturn(new URI("http://localhost/rest/login"));
        final MetricRegistry metricRegistry = new MetricRegistry();
        final Timer timer = metricRegistry.timer("test");

        final LoginResource resource = new LoginResource();
        final Response response = resource.login(request, uriInfo, timer, "user", "password", "/some/page.jsp");

        assertNotNull(response);
        assertEquals(303, response.getStatus());
        assertEquals("/some/page.jsp", response.getLocation().getPath());

        Mockito.verify(request, Mockito.times(1)).login("user", "password");
    }

    /**
     * Test how the resource responds when all parameters are valid.
     * 
     * @throws URISyntaxException
     *             if the URI is malformed
     * @throws ServletException
     *             never
     */
    @Test
    public void testFullLoginAlreadyLoggedIn() throws URISyntaxException, ServletException {
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getRemoteUser()).thenReturn("user");
        final UriInfo uriInfo = Mockito.mock(UriInfo.class);
        Mockito.when(uriInfo.getBaseUri()).thenReturn(new URI("http://localhost/rest/login"));
        final MetricRegistry metricRegistry = new MetricRegistry();
        final Timer timer = metricRegistry.timer("test");

        final LoginResource resource = new LoginResource();
        final Response response = resource.login(request, uriInfo, timer, "user", "password", "/some/page.jsp");

        assertNotNull(response);
        assertEquals(303, response.getStatus());
        assertEquals("/some/page.jsp", response.getLocation().getPath());

        Mockito.verify(request, Mockito.times(0)).logout();
        Mockito.verify(request, Mockito.times(0)).login("user", "password");
    }

    /**
     * Test how the resource responds when all parameters are valid.
     * 
     * @throws URISyntaxException
     *             if the URI is malformed
     * @throws ServletException
     *             never
     */
    @Test
    public void testFullLoginSomeoneElseAlreadyLoggedIn() throws URISyntaxException, ServletException {
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getRemoteUser()).thenReturn("different-user");
        final UriInfo uriInfo = Mockito.mock(UriInfo.class);
        Mockito.when(uriInfo.getBaseUri()).thenReturn(new URI("http://localhost/rest/login"));
        final MetricRegistry metricRegistry = new MetricRegistry();
        final Timer timer = metricRegistry.timer("test");

        final LoginResource resource = new LoginResource();
        final Response response = resource.login(request, uriInfo, timer, "user", "password", "/some/page.jsp");

        assertNotNull(response);
        assertEquals(303, response.getStatus());
        assertEquals("/some/page.jsp", response.getLocation().getPath());

        Mockito.verify(request, Mockito.times(1)).logout();
        Mockito.verify(request, Mockito.times(1)).login("user", "password");
    }

    /**
     * Test how the resource responds when all parameters are valid.
     * 
     * @throws URISyntaxException
     *             if the URI is malformed
     * @throws ServletException
     *             during login
     */
    @Test(expected = NotAuthorizedException.class)
    public void testFullLoginWithException() throws URISyntaxException, ServletException {
        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.doThrow(new ServletException("Login failed")).when(request).login("user", "password");
        final UriInfo uriInfo = Mockito.mock(UriInfo.class);
        Mockito.when(uriInfo.getBaseUri()).thenReturn(new URI("http://localhost/rest/login"));
        final MetricRegistry metricRegistry = new MetricRegistry();
        final Timer timer = metricRegistry.timer("test");

        final LoginResource resource = new LoginResource();
        resource.login(request, uriInfo, timer, "user", "password", "/some/page.jsp");
    }
}
