package com.arcblaze.arccore.rest;

import org.glassfish.jersey.server.ResourceConfig;

import com.arcblaze.arccore.rest.factory.HealthCheckRegistryFactory;
import com.arcblaze.arccore.rest.factory.MetricRegistryFactory;
import com.arcblaze.arccore.rest.factory.TimerFactory;

/**
 * The base REST application class.
 */
public abstract class BaseApplication extends ResourceConfig {
	/**
	 * Default constructor.
	 */
	public BaseApplication() {
		packages(this.getClass().getPackage().getName());

		register(MetricRegistryFactory.getBinder());
		register(HealthCheckRegistryFactory.getBinder());
		register(TimerFactory.getBinder());
	}
}
