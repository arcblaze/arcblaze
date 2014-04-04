package com.arcblaze.arctime.rest.factory;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import com.arcblaze.arccore.rest.factory.BaseServletContextFactory;
import com.arcblaze.arctime.db.ArcTimeDaoFactory;

/**
 * Provides access to {@link ArcTimeDaoFactory} objects within the REST resource
 * classes.
 */
public class DaoFactoryFactory extends
		BaseServletContextFactory<ArcTimeDaoFactory> {
	/**
	 * @param servletContext
	 *            the servlet context from which the metric registry will be
	 *            retrieved
	 */
	public DaoFactoryFactory(final @Context ServletContext servletContext) {
		super(
				servletContext,
				com.arcblaze.arccore.rest.factory.DaoFactoryFactory.DAO_FACTORY_CONFIG);
	}

	/**
	 * @return a binder that can register this factory
	 */
	public static AbstractBinder getBinder() {
		return new AbstractBinder() {
			@Override
			protected void configure() {
				bindFactory(DaoFactoryFactory.class)
						.to(ArcTimeDaoFactory.class);
			}
		};
	}
}
