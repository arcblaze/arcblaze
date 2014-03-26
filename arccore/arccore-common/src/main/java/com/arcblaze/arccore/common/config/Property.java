package com.arcblaze.arccore.common.config;

/**
 * Defines the interface required for property configuration elements.
 */
public interface Property {
	/**
	 * @return the configuration property key name used when accessing this
	 *         property from a configuration
	 */
	public abstract String getKey();

	/**
	 * @return the default value for this configuration property
	 */
	public abstract String getDefaultValue();
}
