package com.arcblaze.arctime.rest.factory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.apache.commons.lang.StringUtils;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import com.arcblaze.arccore.rest.factory.BaseFactory;
import com.arcblaze.arctime.common.model.Holiday;
import com.arcblaze.arctime.common.model.util.HolidayConfigurationException;

/**
 * Provides access to {@link Holiday} objects within the REST resource classes,
 * based on parameters from the web request.
 */
public class HolidayFactory extends BaseFactory<Holiday> {
	private final HttpServletRequest request;

	/**
	 * @param request
	 *            the web request from the client
	 */
	public HolidayFactory(@Context final HttpServletRequest request) {
		this.request = request;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Holiday provide() {
		final String id = this.request.getParameter("id");
		final String companyId = this.request.getParameter("companyId");
		final String description = this.request.getParameter("description");
		final String config = this.request.getParameter("config");

		try {
			final Holiday holiday = new Holiday();
			if (StringUtils.isNotBlank(id) && StringUtils.isNumeric(id))
				holiday.setId(Integer.parseInt(id));
			if (StringUtils.isNotBlank(companyId)
					&& StringUtils.isNumeric(companyId))
				holiday.setCompanyId(Integer.parseInt(companyId));
			if (StringUtils.isNotBlank(description))
				holiday.setDescription(description);
			if (StringUtils.isNotBlank(config))
				holiday.setConfig(config);
			return holiday;
		} catch (final HolidayConfigurationException badConfig) {
			throw new IllegalArgumentException(
					"Invalid holiday configuration: " + config);
		}
	}

	/**
	 * @return a binder that can register this factory
	 */
	public static AbstractBinder getBinder() {
		return new AbstractBinder() {
			@Override
			protected void configure() {
				bindFactory(HolidayFactory.class).to(Holiday.class);
			}
		};
	}
}
