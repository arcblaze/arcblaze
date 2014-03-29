package com.arcblaze.arctime.rest;

import com.arcblaze.arccore.rest.BaseApplication;

/**
 * Configures the REST end-points for this system.
 */
public class ArcTimeApplication extends BaseApplication {
	/**
	 * Default constructor.
	 */
	public ArcTimeApplication() {
		super();

		packages(this.getClass().getPackage().getName());
	}
}
