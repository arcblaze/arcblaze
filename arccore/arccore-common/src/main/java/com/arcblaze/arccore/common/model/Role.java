package com.arcblaze.arccore.common.model;

import static org.apache.commons.lang.Validate.notEmpty;
import static org.apache.commons.lang.Validate.notNull;

import java.security.Principal;

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
 * Represents a role in this system.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Role implements Comparable<Role>, Principal {
    /**
     * The name of this role.
     */
    private String name;

    /**
     * Default constructor.
     */
    public Role() {
        // Nothing to do.
    }

    /**
     * @param name
     *            the name of this role
     * 
     * @throws IllegalArgumentException
     *             if the provided name value is invalid
     */
    public Role(final String name) {
        setName(name);
    }

    /**
     * @param other
     *            the role to copy
     * 
     * @throws IllegalArgumentException
     *             if the provided role is invalid
     */
    public Role(final Role other) {
        notNull(other);
        if (other.getName() != null)
            setName(other.getName());
    }

    /**
     * @return the role name value
     */
    @Override
    @XmlElement
    public String getName() {
        return this.name;
    }

    /**
     * @param name
     *            the new name of this role
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided name value is invalid
     */
    public Role setName(final String name) {
        notEmpty(name, "Invalid blank name");

        this.name = StringUtils.trim(name);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        builder.append("name", getName());
        return builder.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        return (obj instanceof Role) ? compareTo((Role) obj) == 0 : false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(getName());
        return builder.toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(final Role other) {
        final CompareToBuilder builder = new CompareToBuilder();
        builder.append(getName(), other.getName());
        return builder.toComparison();
    }
}
