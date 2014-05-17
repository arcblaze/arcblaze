package com.arcblaze.arccore.rest.factory;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.servlets.MetricsServlet;

/**
 * Provides access to {@link MetricRegistry} objects within the REST resource classes.
 */
public class MetricRegistryFactory extends BaseServletContextFactory<MetricRegistry> {
    /**
     * @param servletContext
     *            the servlet context from which the metric registry will be retrieved
     */
    public MetricRegistryFactory(final @Context ServletContext servletContext) {
        super(servletContext, MetricsServlet.METRICS_REGISTRY);
    }

    /**
     * @return a binder that can register this factory
     */
    public static AbstractBinder getBinder() {
        return new AbstractBinder() {
            @Override
            protected void configure() {
                bindFactory(MetricRegistryFactory.class).to(MetricRegistry.class);
            }
        };
    }
}
