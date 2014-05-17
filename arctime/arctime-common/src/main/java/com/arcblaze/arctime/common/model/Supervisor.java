package com.arcblaze.arctime.common.model;

import static org.apache.commons.lang.Validate.notNull;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.arcblaze.arccore.common.model.User;

/**
 * Represents a supervisor in this system.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Supervisor extends User {
    /**
     * Whether the supervisor is a primary supervisor.
     */
    private Boolean primary;

    /**
     * Default constructor.
     */
    public Supervisor() {
        // Nothing to do.
    }

    /**
     * @param other
     *            the object to duplicate
     */
    public Supervisor(final Supervisor other) {
        super(other);
        if (other.isPrimary() != null)
            setPrimary(other.isPrimary());
    }

    /**
     * @param other
     *            the object to duplicate
     */
    public Supervisor(final User other) {
        super(other);
        setPrimary(false);
    }

    /**
     * @return whether this supervisor is a primary supervisor
     */
    @XmlElement
    public Boolean isPrimary() {
        return this.primary;
    }

    /**
     * @param primary
     *            the new value indicating whether this supervisor is the primary supervisor
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided primary value is invalid
     */
    public Supervisor setPrimary(final Boolean primary) {
        notNull(primary, "Invalid null primary");

        this.primary = primary;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        builder.append("primary", isPrimary());
        builder.append("user", super.toString());
        return builder.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Supervisor) {
            final Supervisor other = (Supervisor) obj;
            final EqualsBuilder builder = new EqualsBuilder();
            builder.append(isPrimary(), other.isPrimary());
            if (builder.isEquals())
                return super.equals(obj);
            return false;
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(isPrimary());
        builder.append(super.hashCode());
        return builder.toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(final User other) {
        if (other instanceof Supervisor) {
            final CompareToBuilder builder = new CompareToBuilder();
            builder.append(((Supervisor) other).isPrimary(), isPrimary());
            final int cmp = builder.toComparison();
            if (cmp != 0)
                return cmp;
            return super.compareTo(other);
        }

        return -1;
    }
}
