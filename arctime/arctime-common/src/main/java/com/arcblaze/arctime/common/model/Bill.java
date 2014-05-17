package com.arcblaze.arctime.common.model;

import static org.apache.commons.lang.Validate.isTrue;
import static org.apache.commons.lang.Validate.notEmpty;
import static org.apache.commons.lang.Validate.notNull;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;

/**
 * Represents a bill of hours by a user to a task assignment.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Bill implements Comparable<Bill> {
    /**
     * The unique id of this bill.
     */
    private Integer id;

    /**
     * The unique id of the task assignment for which this bill applies.
     */
    private Integer assignmentId;

    /**
     * The unique id of the task to which the user is billing hours.
     */
    private Integer taskId;

    /**
     * The unique id of the user assigned to the task.
     */
    private Integer userId;

    /**
     * The day in which the hours are to be applied.
     */
    private Date day;

    /**
     * The number of hours being applied to the task assignment.
     */
    private BigDecimal hours;

    /**
     * The time stamp when this bill was created.
     */
    private Date timestamp;

    /**
     * The reason for changing hours from one value to the other.
     */
    private String reason;

    /**
     * Default constructor.
     */
    public Bill() {
        // Nothing to do.
    }

    /**
     * @param other
     *            the bill to duplicate
     * 
     * @throws IllegalArgumentException
     *             if the provided bill is invalid
     */
    public Bill(final Bill other) {
        notNull(other, "Invalid null bill");
        if (other.getId() != null)
            setId(other.getId());
        if (other.hasAssignmentId())
            setAssignmentId(other.getAssignmentId());
        if (other.getTaskId() != null)
            setTaskId(other.getTaskId());
        if (other.getUserId() != null)
            setUserId(other.getUserId());
        if (other.getDay() != null)
            setDay(other.getDay());
        if (other.getHours() != null)
            setHours(other.getHours());
        if (other.getTimestamp() != null)
            setTimestamp(other.getTimestamp());
        if (other.hasReason())
            setReason(other.getReason());
    }

    /**
     * @return the unique id of this assignment
     */
    @XmlElement
    public Integer getId() {
        return this.id;
    }

    /**
     * @param id
     *            the new unique id of this assignment
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided id value is invalid
     */
    public Bill setId(final Integer id) {
        notNull(id, "Invalid null id");
        isTrue(id >= 0, "Invalid negative id");

        this.id = id;
        return this;
    }

    /**
     * @return whether this bill includes an assignment id
     */
    public boolean hasAssignmentId() {
        return this.assignmentId != null;
    }

    /**
     * @return the unique id of the task assignment for which this bill applies
     */
    @XmlElement
    public Integer getAssignmentId() {
        return this.assignmentId;
    }

    /**
     * @param assignmentId
     *            the new unique id of the task assignment for which this bill applies
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided id value is invalid
     */
    public Bill setAssignmentId(final Integer assignmentId) {
        notNull(assignmentId, "Invalid null assignment id");
        isTrue(assignmentId >= 0, "Invalid negative assignment id");

        this.assignmentId = assignmentId;
        return this;
    }

    /**
     * @return the unique id of the task for which this bill applies
     */
    @XmlElement
    public Integer getTaskId() {
        return this.taskId;
    }

    /**
     * @param taskId
     *            the new unique id of the task for which this bill applies
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided id value is invalid
     */
    public Bill setTaskId(final Integer taskId) {
        notNull(taskId, "Invalid null task id");
        isTrue(taskId >= 0, "Invalid negative task id");

        this.taskId = taskId;
        return this;
    }

    /**
     * @return a unique id capable of distinguishing this bill in a timesheet
     */
    @XmlTransient
    public String getUniqueId() {
        final StringBuilder uid = new StringBuilder();
        uid.append(getTaskId());
        uid.append(":");
        uid.append(getAssignmentId());
        uid.append(":");
        uid.append(DateFormatUtils.format(getDay(), "yyyyMMdd"));
        return uid.toString();
    }

    /**
     * @return the unique id of the user billing the hours
     */
    @XmlElement
    public Integer getUserId() {
        return this.userId;
    }

    /**
     * @param userId
     *            the new unique id of the user billing the hours
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided id value is invalid
     */
    public Bill setUserId(final Integer userId) {
        notNull(userId, "Invalid null user id");
        isTrue(userId >= 0, "Invalid negative user id");

        this.userId = userId;
        return this;
    }

    /**
     * @return the day in which the hours are being billed
     */
    @XmlElement
    public Date getDay() {
        return this.day;
    }

    /**
     * @param day
     *            the new day when the hours were billed
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided day value is invalid
     */
    public Bill setDay(final Date day) {
        if (day == null)
            throw new IllegalArgumentException("Invalid null day");

        this.day = DateUtils.truncate(day, Calendar.DATE);
        return this;
    }

    /**
     * @return the hours being billed to the task assignment
     */
    @XmlElement
    public BigDecimal getHours() {
        return this.hours;
    }

    /**
     * @param hours
     *            the new value specifying the hours being billed to the task assignment
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided hours value is invalid
     */
    public Bill setHours(final Float hours) {
        notNull(hours, "Invalid null hours");

        this.hours = new BigDecimal(hours).setScale(2);
        return this;
    }

    /**
     * @param hours
     *            the new value specifying the hours being billed to the task assignment
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided hours value is invalid
     */
    public Bill setHours(final String hours) {
        notNull(hours, "Invalid null hours");

        try {
            this.hours = new BigDecimal(hours).setScale(2);
        } catch (final NumberFormatException badNumber) {
            throw new IllegalArgumentException("Invalid number: " + hours, badNumber);
        }
        return this;
    }

    /**
     * @param hours
     *            the new value specifying the hours being billed to the task assignment
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided hours value is invalid
     */
    public Bill setHours(final BigDecimal hours) {
        notNull(hours, "Invalid null hours");

        this.hours = new BigDecimal(hours.toPlainString()).setScale(2);
        return this;
    }

    /**
     * @return the time stamp indicating when this bill was created
     */
    @XmlElement
    public Date getTimestamp() {
        return this.timestamp;
    }

    /**
     * @param timestamp
     *            the new value indicating when this bill was created
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided time stamp value is invalid
     */
    public Bill setTimestamp(final Date timestamp) {
        notNull(timestamp, "Invalid null timestamp value");

        this.timestamp = new Date(timestamp.getTime());
        return this;
    }

    /**
     * @return whether this bill contains a reason why the hours were modified.
     */
    public boolean hasReason() {
        return this.reason != null;
    }

    /**
     * @return the reason indicating why hours were changed from an old value to a new value
     */
    @XmlElement
    public String getReason() {
        return this.reason;
    }

    /**
     * @param reason
     *            the new reason value indicating why hours were changed from an old value to a new value
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided reason value is invalid
     */
    public Bill setReason(final String reason) {
        notEmpty(reason, "Invalid blank reason value");

        this.reason = reason;
        return this;
    }

    /**
     * @return the encoded bill data as used when saving bills into a timesheet
     */
    public String toTimesheetData() {
        final StringBuilder str = new StringBuilder();
        str.append(getTaskId());
        str.append("_");
        if (hasAssignmentId())
            str.append(getAssignmentId());
        str.append(":");
        str.append(DateFormatUtils.format(getDay(), "yyyyMMdd"));
        str.append(":");
        str.append(getHours().toPlainString());
        if (hasReason()) {
            str.append(":");
            str.append(getReason());
        }
        return str.toString();
    }

    /**
     * @param bills
     *            the bills to be encoded
     * 
     * @return the encoded bill data as used when saving bills into a timesheet
     */
    public static String toTimesheetData(final Collection<Bill> bills) {
        if (bills == null || bills.isEmpty())
            return "";
        final List<String> parts = new ArrayList<>(bills.size());
        for (final Bill bill : bills)
            parts.add(bill.toTimesheetData());
        return StringUtils.join(parts, ";");
    }

    /**
     * @param data
     *            the raw encoded data sent from the time sheet user interface to be parsed into the individual bills
     * 
     * @return the parsed data in the form of bills
     * 
     * @throws IllegalArgumentException
     *             if the provided data value could not be parsed successfully
     */
    public static Set<Bill> fromTimesheetData(final String data) {
        final Set<Bill> bills = new TreeSet<>();

        if (StringUtils.isBlank(data))
            return bills;

        // data looks like: "8_57:20100602:5.00:reason;8_56:20100605:8.00"
        final String[] dataParts = data.split(";");
        for (final String dataPart : dataParts) {
            if (StringUtils.isBlank(dataPart))
                continue;

            final String[] pieces = dataPart.split(":", 4);

            try {
                final Bill bill = new Bill();

                final String[] taskAssignmentId = pieces[0].split("_", 2);
                bill.setTaskId(Integer.parseInt(taskAssignmentId[0]));

                if (taskAssignmentId.length == 2 && StringUtils.isNotBlank(taskAssignmentId[1]))
                    bill.setAssignmentId(Integer.parseInt(taskAssignmentId[1]));

                bill.setDay(DateUtils.parseDate(pieces[1], new String[] { "yyyyMMdd" }));
                bill.setHours(new BigDecimal(pieces[2]));

                if (pieces.length == 4 && StringUtils.isNotBlank(pieces[3]))
                    bill.setReason(StringUtils.trim(StringEscapeUtils.escapeHtml(pieces[3])));

                bills.add(bill);
            } catch (final NumberFormatException badNumber) {
                throw new IllegalArgumentException("Invalid numeric value.", badNumber);
            } catch (final ParseException badDate) {
                throw new IllegalArgumentException("Invalid date value.", badDate);
            }
        }

        return bills;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        builder.append("id", getId());
        builder.append("assignmentId", getAssignmentId());
        builder.append("taskId", getTaskId());
        builder.append("userId", getUserId());
        builder.append("day", getDay());
        builder.append("hours", getHours());
        builder.append("reason", getReason());
        builder.append("timestamp", getTimestamp());
        return builder.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        return (obj instanceof Bill) ? compareTo((Bill) obj) == 0 : false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(getId());
        builder.append(getAssignmentId());
        builder.append(getTaskId());
        builder.append(getUserId());
        builder.append(getDay());
        builder.append(getHours());
        builder.append(getTimestamp());
        builder.append(getReason());
        return builder.toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(final Bill other) {
        final CompareToBuilder builder = new CompareToBuilder();
        builder.append(other.getAssignmentId(), getAssignmentId());
        builder.append(getUserId(), other.getUserId());
        builder.append(getTaskId(), other.getTaskId());
        builder.append(getDay(), other.getDay());
        builder.append(other.getTimestamp(), getTimestamp());
        builder.append(getHours(), other.getHours());
        builder.append(getReason(), other.getReason());
        return builder.toComparison();
    }
}
