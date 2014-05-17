package com.arcblaze.arctime.rest.factory;

import javax.ws.rs.FormParam;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import com.arcblaze.arccore.rest.factory.BaseFactory;
import com.arcblaze.arctime.common.model.Holiday;
import com.arcblaze.arctime.common.model.util.HolidayConfigurationException;

/**
 * Provides access to {@link Holiday} objects within the REST resource classes, based on parameters from the web
 * request.
 */
public class HolidayFactory extends BaseFactory<Holiday> {
    private final Integer id;
    private final Integer companyId;
    private final String description;
    private final String config;

    /**
     * @param id
     *            the unique id of the holiday
     * @param companyId
     *            the id of the company that owns the holiday
     * @param description
     *            a description of the holiday
     * @param config
     *            the holiday configuration value
     */
    public HolidayFactory(@FormParam("id") final Integer id, @FormParam("companyId") final Integer companyId,
            @FormParam("description") final String description, @FormParam("config") final String config) {
        this.id = id;
        this.companyId = companyId;
        this.description = StringEscapeUtils.escapeHtml(description);
        this.config = StringEscapeUtils.escapeHtml(config);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Holiday provide() {
        try {
            final Holiday holiday = new Holiday();
            if (this.id != null)
                holiday.setId(this.id);
            if (this.companyId != null)
                holiday.setCompanyId(this.companyId);
            if (StringUtils.isNotBlank(this.description))
                holiday.setDescription(this.description);
            if (StringUtils.isNotBlank(this.config))
                holiday.setConfig(this.config);
            return holiday;
        } catch (final HolidayConfigurationException badConfig) {
            throw new IllegalArgumentException("Invalid holiday configuration: " + this.config);
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
