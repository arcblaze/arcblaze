package com.arcblaze.arccore.rest.factory;

import org.glassfish.hk2.utilities.binding.AbstractBinder;

import com.arcblaze.arccore.common.model.Password;

/**
 * Provides access to {@link Password} objects within the REST resource classes.
 */
public class PasswordFactory extends BaseFactory<Password> {
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Password provide() {
		return new Password();
	}

	/**
	 * @return a binder that can register this factory
	 */
	public static AbstractBinder getBinder() {
		return new AbstractBinder() {
			@Override
			protected void configure() {
				bindFactory(PasswordFactory.class).to(Password.class);
			}
		};
	}
}
