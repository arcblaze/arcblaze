package com.arcblaze.arctime.common.model;

import static org.apache.commons.lang.Validate.isTrue;
import static org.apache.commons.lang.Validate.notEmpty;
import static org.apache.commons.lang.Validate.notNull;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Represents an audit log.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class AuditLog implements Comparable<AuditLog> {
    /**
     * The unique id of the company to which this audit log applies.
     */
    private Integer companyId;

    /**
     * The unique id of the timesheet to which this audit log applies.
     */
    private Integer timesheetId;

    /**
     * The log message describing the action that occurred.
     */
    private String log;

    /**
     * The time stamp when the audited activity took place.
     */
    private Date timestamp = new Date();

    /**
     * Default constructor.
     */
    public AuditLog() {
        // Nothing to do.
    }

    /**
     * @param other
     *            the audit log to duplicate
     * 
     * @throws IllegalArgumentException
     *             if the provided parameter is invalid
     */
    public AuditLog(final AuditLog other) {
        notNull(other, "Invalid null audit log");
        if (other.getCompanyId() != null)
            setCompanyId(other.getCompanyId());
        if (other.getTimesheetId() != null)
            setTimesheetId(other.getTimesheetId());
        if (other.getLog() != null)
            setLog(other.getLog());
        if (other.getTimestamp() != null)
            setTimestamp(other.getTimestamp());
    }

    /**
     * @return the unique id of the company for which this audit log applies
     */
    @XmlElement
    public Integer getCompanyId() {
        return this.companyId;
    }

    /**
     * @param companyId
     *            the new unique id of the company for which this audit log applies
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided id value is invalid
     */
    public AuditLog setCompanyId(final Integer companyId) {
        notNull(companyId, "Invalid null company id");
        isTrue(companyId >= 0, "Invalid negative company id");

        this.companyId = companyId;
        return this;
    }

    /**
     * @return the unique id of the timesheet for which this audit log applies
     */
    @XmlElement
    public Integer getTimesheetId() {
        return this.timesheetId;
    }

    /**
     * @param timesheetId
     *            the new unique id of the timesheet for which this audit log applies
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided id value is invalid
     */
    public AuditLog setTimesheetId(final Integer timesheetId) {
        notNull(timesheetId, "Invalid null timesheet id");
        isTrue(timesheetId >= 0, "Invalid negative timesheet id");

        this.timesheetId = timesheetId;
        return this;
    }

    /**
     * @return the log message describing the activity that occurred
     */
    @XmlElement
    public String getLog() {
        return this.log;
    }

    /**
     * @param log
     *            the new log message describing the activity that occurred
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided log value is invalid
     */
    public AuditLog setLog(final String log) {
        notEmpty(log, "Invalid blank log");

        this.log = StringUtils.trim(log);
        return this;
    }

    /**
     * @return the time stamp when the audited activity took place
     */
    @XmlElement
    public Date getTimestamp() {
        return this.timestamp;
    }

    /**
     * @param timestamp
     *            the new value indicating when the audited activity occurred
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided time stamp value is invalid
     */
    public AuditLog setTimestamp(final Date timestamp) {
        notNull(timestamp, "Invalid null timestamp value");

        this.timestamp = new Date(timestamp.getTime()); // Defensive copy
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        builder.append("companyId", getCompanyId());
        builder.append("timesheetId", getTimesheetId());
        builder.append("log", getLog());
        builder.append("timestamp", getTimestamp());
        return builder.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof AuditLog) {
            final AuditLog other = (AuditLog) obj;
            final EqualsBuilder builder = new EqualsBuilder();
            builder.append(getCompanyId(), other.getCompanyId());
            builder.append(getTimesheetId(), other.getTimesheetId());
            builder.append(getLog(), other.getLog());
            builder.append(getTimestamp(), other.getTimestamp());
            return builder.isEquals();
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(getCompanyId());
        builder.append(getTimesheetId());
        builder.append(getLog());
        builder.append(getTimestamp());
        return builder.toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(final AuditLog other) {
        final CompareToBuilder builder = new CompareToBuilder();
        builder.append(other.getCompanyId(), getCompanyId());
        builder.append(other.getTimesheetId(), getTimesheetId());
        builder.append(getTimestamp(), other.getTimestamp());
        builder.append(getLog(), other.getLog());
        return builder.toComparison();
    }
}
