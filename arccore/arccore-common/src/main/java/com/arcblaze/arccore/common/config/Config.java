package com.arcblaze.arccore.common.config;

import static org.apache.commons.lang.Validate.notEmpty;
import static org.apache.commons.lang.Validate.notNull;

import java.io.File;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConversionException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides a mechanism for loading configuration properties.
 */
public class Config {
	/** This will be used to log messages. */
	private final static Logger log = LoggerFactory.getLogger(Config.class);

	/** Holds all of the loaded configuration properties. */
	private final PropertiesConfiguration config;

	/**
	 * @param configurationFile
	 *            the path to the configuration file from which configuration
	 *            properties will be loaded
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided configuration file is invalid
	 * @throws ConfigurationException
	 *             if there is a problem loading the configuration information
	 *             from the specified file
	 */
	private Config(final String configurationFile)
			throws ConfigurationException {
		notEmpty(configurationFile);

		final File configFile = new File(configurationFile);
		if (configFile.exists()) {
			log.info("Loading configuration from "
					+ configFile.getAbsolutePath());
			try {
				this.config = new PropertiesConfiguration(configFile);
				this.config.setDelimiterParsingDisabled(true);
				this.config
						.setReloadingStrategy(new FileChangedReloadingStrategy());
			} catch (final ConfigurationException badConfig) {
				throw new ConfigurationException(
						"Failed to load system configuration from "
								+ configFile.getAbsolutePath(), badConfig);
			}
		} else
			throw new ConfigurationException(
					"Failed to load configuration from non-existent file: "
							+ configFile.getAbsolutePath());

	}

	/**
	 * Update the internal property value for the specified property. The
	 * updated value is not persisted to the configuration file.
	 * 
	 * @param property
	 *            the {@link Property} for which the value will be updated
	 * @param value
	 *            the new value to use for this configuration property
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided property object is {@code null}
	 */
	public void set(final Property property, final String value) {
		notNull(property, "Invalid null property");

		if (value == null)
			this.config.clearProperty(property.getKey());
		else
			this.config.setProperty(property.getKey(), value);
	}

	/**
	 * @param property
	 *            the {@link Property} object specifying how the configuration
	 *            information should be retrieved, and the default value
	 * 
	 * @return the value for this property as a string, potentially the default
	 *         value if no specific value was configured
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided property object is {@code null}
	 */
	public String getString(final Property property) {
		notNull(property, "Invalid null property");

		return this.config.getString(property.getKey(),
				property.getDefaultValue());
	}

	/**
	 * @param property
	 *            the {@link Property} object specifying how the configuration
	 *            information should be retrieved, and the default value
	 * 
	 * @return the value for this property as an int, potentially the default
	 *         value if no specific value was configured
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided property object is {@code null}
	 * @throws ConversionException
	 *             if the configured value or the default value for the property
	 *             is not an integer
	 */
	public int getInt(final Property property) {
		notNull(property, "Invalid null property");

		try {
			return this.config.getInt(property.getKey(),
					Integer.parseInt(property.getDefaultValue()));
		} catch (final NumberFormatException badNumber) {
			throw new ConversionException("Not an integer value.", badNumber);
		}
	}

	/**
	 * @param property
	 *            the {@link Property} object specifying how the configuration
	 *            information should be retrieved, and the default value
	 * 
	 * @return the value for this property as a long, potentially the default
	 *         value if no specific value was configured
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided property object is {@code null}
	 * @throws ConversionException
	 *             if the configured value or the default value for the property
	 *             is not a long
	 */
	public long getLong(final Property property) {
		notNull(property, "Invalid null property");

		try {
			return this.config.getLong(property.getKey(),
					Long.parseLong(property.getDefaultValue()));
		} catch (final NumberFormatException badNumber) {
			throw new ConversionException("Not a long value.", badNumber);
		}
	}

	/**
	 * @param property
	 *            the {@link Property} object specifying how the configuration
	 *            information should be retrieved, and the default value
	 * 
	 * @return the value for this property as a boolean, potentially the default
	 *         value if no specific value was configured
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided property object is {@code null}
	 * @throws ConversionException
	 *             if the configured value or the default value for the property
	 *             is not a boolean
	 */
	public boolean getBoolean(final Property property) {
		notNull(property, "Invalid null property");

		return this.config.getBoolean(property.getKey(),
				Boolean.parseBoolean(property.getDefaultValue()));
	}
}
