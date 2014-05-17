package com.arcblaze.arccore.rest.factory;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.servlets.HealthCheckServlet;

/**
 * Provides access to {@link HealthCheckRegistry} objects within the REST resource classes.
 */
public class HealthCheckRegistryFactory extends BaseServletContextFactory<HealthCheckRegistry> {
    /**
     * @param servletContext
     *            the servlet context from which the health check registry will be retrieved
     */
    public HealthCheckRegistryFactory(final @Context ServletContext servletContext) {
        super(servletContext, HealthCheckServlet.HEALTH_CHECK_REGISTRY);
    }

    /**
     * @return a binder that can register this factory
     */
    public static AbstractBinder getBinder() {
        return new AbstractBinder() {
            @Override
            protected void configure() {
                bindFactory(HealthCheckRegistryFactory.class).to(HealthCheckRegistry.class);
            }
        };
    }
}
