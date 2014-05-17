package com.arcblaze.arccore.rest.factory;

import javax.ws.rs.FormParam;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import com.arcblaze.arccore.common.model.Company;

/**
 * Provides access to {@link Company} objects within the REST resource classes, based on parameters from the web
 * request.
 */
public class CompanyFactory extends BaseFactory<Company> {
    private final Integer id;
    private final String name;
    private final Boolean active;

    /**
     * @param id
     *            the unique id of the company
     * @param name
     *            the name of the company
     * @param active
     *            whether the company is active or not
     */
    public CompanyFactory(@FormParam("id") final Integer id, @FormParam("name") final String name,
            @FormParam("active") final Boolean active) {
        this.id = id;
        this.name = StringEscapeUtils.escapeHtml(name);
        this.active = active;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Company provide() {
        final Company company = new Company();
        if (this.id != null)
            company.setId(this.id);
        if (StringUtils.isNotBlank(this.name))
            company.setName(this.name);
        if (this.active != null)
            company.setActive(this.active);
        return company;
    }

    /**
     * @return a binder that can register this factory
     */
    public static AbstractBinder getBinder() {
        return new AbstractBinder() {
            @Override
            protected void configure() {
                bindFactory(CompanyFactory.class).to(Company.class);
            }
        };
    }
}
