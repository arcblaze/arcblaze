package com.arcblaze.arccore.common.model;

import static org.apache.commons.lang.Validate.isTrue;
import static org.apache.commons.lang.Validate.notEmpty;
import static org.apache.commons.lang.Validate.notNull;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Represents a company.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Company implements Comparable<Company> {
    /**
     * The unique id of the company.
     */
    private Integer id;

    /**
     * The name for the company.
     */
    private String name;

    /**
     * Whether this company is active or not.
     */
    private Boolean active = true;

    /**
     * Default constructor.
     */
    public Company() {
        // Nothing to do.
    }

    /**
     * @param other
     *            the company object to duplicate
     * 
     * @throws IllegalArgumentException
     *             if the provided parameter is invalid
     */
    public Company(final Company other) {
        notNull(other, "Invalid null company");
        if (other.getId() != null)
            setId(other.getId());
        if (StringUtils.isNotBlank(other.getName()))
            setName(other.getName());
        if (other.isActive() != null)
            setActive(other.isActive());
    }

    /**
     * @return the unique id of the company
     */
    @XmlElement
    public Integer getId() {
        return this.id;
    }

    /**
     * @param id
     *            the new unique company id value
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided id value is invalid
     */
    public Company setId(final Integer id) {
        notNull(id, "Invalid null id");
        isTrue(id >= 0, "Invalid negative id");

        this.id = id;
        return this;
    }

    /**
     * @return the company name
     */
    @XmlElement
    public String getName() {
        return this.name;
    }

    /**
     * @param name
     *            the new company name
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided name value is invalid
     */
    public Company setName(final String name) {
        notEmpty(name, "Invalid blank name");

        this.name = StringUtils.trim(name);
        return this;
    }

    /**
     * @return whether this company has an active account in the system
     */
    @XmlElement
    public Boolean isActive() {
        return this.active;
    }

    /**
     * @param active
     *            the new value indicating whether this is an active company in the system
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided active value is invalid
     */
    public Company setActive(final Boolean active) {
        notNull(active, "Invalid null active value");

        this.active = active;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        builder.append("id", getId());
        builder.append("name", getName());
        builder.append("active", isActive());
        return builder.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        return (obj instanceof Company) ? compareTo((Company) obj) == 0 : false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(getId());
        builder.append(getName());
        builder.append(isActive());
        return builder.toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(Company other) {
        final CompareToBuilder builder = new CompareToBuilder();
        builder.append(other.isActive(), isActive());
        builder.append(getName(), other.getName());
        builder.append(getId(), other.getId());
        return builder.toComparison();
    }
}
