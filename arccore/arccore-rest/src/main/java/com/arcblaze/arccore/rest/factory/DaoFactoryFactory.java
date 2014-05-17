package com.arcblaze.arccore.rest.factory;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import com.arcblaze.arccore.db.DaoFactory;

/**
 * Provides access to {@link DaoFactory} objects within the REST resource classes.
 */
public class DaoFactoryFactory extends BaseServletContextFactory<DaoFactory> {
    /** The property to use when retrieving the dao factory. */
    public final static String DAO_FACTORY_CONFIG = DaoFactoryFactory.class.getCanonicalName() + ".config";

    /**
     * @param servletContext
     *            the servlet context from which the metric registry will be retrieved
     */
    public DaoFactoryFactory(final @Context ServletContext servletContext) {
        super(servletContext, DAO_FACTORY_CONFIG);
    }

    /**
     * @return a binder that can register this factory
     */
    public static AbstractBinder getBinder() {
        return new AbstractBinder() {
            @Override
            protected void configure() {
                bindFactory(DaoFactoryFactory.class).to(DaoFactory.class);
            }
        };
    }
}
