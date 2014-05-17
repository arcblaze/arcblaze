package com.arcblaze.arccore.rest.factory;

import static org.apache.commons.lang.Validate.notNull;

import javax.servlet.ServletContext;

/**
 * The default implementation of a factory used to inject objects into our REST resources, retrieving the object from
 * the servlet context.
 * 
 * @param <T>
 *            the type of objects to be injected into the REST resources
 */
public abstract class BaseServletContextFactory<T> extends BaseFactory<T> {
    private final ServletContext servletContext;
    private final String attributeName;

    /**
     * @param servletContext
     *            the servlet context associated with the request
     * @param attributeName
     *            the name of the request attribute from which this factory will retrieve objects
     */
    public BaseServletContextFactory(final ServletContext servletContext, final String attributeName) {
        notNull(servletContext, "Invalid null servlet context");
        notNull(attributeName, "Invalid null attribute name");

        this.servletContext = servletContext;
        this.attributeName = attributeName;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public T provide() {
        return (T) this.servletContext.getAttribute(this.attributeName);
    }
}
