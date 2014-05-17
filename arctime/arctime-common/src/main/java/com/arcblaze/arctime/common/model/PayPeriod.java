package com.arcblaze.arctime.common.model;

import static org.apache.commons.lang.Validate.isTrue;
import static org.apache.commons.lang.Validate.notNull;

import java.util.Calendar;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;

import com.arcblaze.arctime.common.model.util.HolidayConfigurationException;

/**
 * Represents a pay period.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class PayPeriod implements Comparable<PayPeriod> {
    /**
     * The unique id of the company for which this pay period applies.
     */
    private Integer companyId;

    /**
     * The type of pay period configured.
     */
    private PayPeriodType type;

    /**
     * The first day in the pay period.
     */
    private Date begin;

    /**
     * The last day in the pay period.
     */
    private Date end;

    /**
     * Default constructor.
     */
    public PayPeriod() {
        // Nothing to do.
    }

    /**
     * @param other
     *            the pay period to duplicate
     * 
     * @throws IllegalArgumentException
     *             if the provided parameter is invalid
     */
    public PayPeriod(final PayPeriod other) {
        notNull(other, "Invalid null pay period");
        if (other.getCompanyId() != null)
            setCompanyId(other.getCompanyId());
        if (other.getType() != null)
            setType(other.getType());
        if (other.getBegin() != null)
            setBegin(other.getBegin());
        if (other.getEnd() != null)
            setEnd(other.getEnd());
    }

    /**
     * @param time
     *            the {@link Date} to check to determine if it falls within this pay period
     * 
     * @return whether the provided date falls into this pay period
     */
    public boolean contains(final Date time) {
        if (time == null)
            return false;

        final Date b = getBegin();
        final Date e = getEnd();

        if (b == null || e == null)
            return false;

        final Date day = DateUtils.truncate(time, Calendar.DATE);

        return day.getTime() >= b.getTime() && day.getTime() <= e.getTime();
    }

    /**
     * @param holiday
     *            the {@link Holiday} to check to determine if it falls within this pay period
     * 
     * @return whether the provided date falls into this pay period
     * 
     * @throws HolidayConfigurationException
     *             if there is a problem parsing the holiday configuration information
     */
    public boolean contains(final Holiday holiday) throws HolidayConfigurationException {
        if (holiday == null)
            return false;

        final Date b = getBegin();
        final Date e = getEnd();

        if (b == null || e == null)
            return false;

        final int yb = Integer.parseInt(DateFormatUtils.format(b, "yyyy"));
        final int ye = Integer.parseInt(DateFormatUtils.format(e, "yyyy"));

        return yb == ye ? contains(holiday.getDateForYear(yb)) : contains(holiday.getDateForYear(yb))
                || contains(holiday.getDateForYear(ye));
    }

    /**
     * @param date
     *            the date to check
     * 
     * @return true if the end date in this pay period (truncated to the day) comes before the provided date (also
     *         truncated to the day)
     * 
     * @throws IllegalArgumentException
     *             if the provided parameter is {@code null}
     */
    public boolean isBefore(final Date date) {
        notNull(date, "Invalid null date");

        final Date truncated = DateUtils.truncate(date, Calendar.DATE);
        final Date endTruncated = DateUtils.truncate(getEnd(), Calendar.DATE);

        return endTruncated.before(truncated);
    }

    /**
     * @param date
     *            the date to check
     * 
     * @return true if the begin date in this pay period (truncated to the day) comes after the provided date (also
     *         truncated to the day)
     * 
     * @throws IllegalArgumentException
     *             if the provided parameter is {@code null}
     */
    public boolean isAfter(final Date date) {
        notNull(date, "Invalid null date");

        final Date truncated = DateUtils.truncate(date, Calendar.DATE);
        final Date beginTruncated = DateUtils.truncate(getBegin(), Calendar.DATE);

        return beginTruncated.after(truncated);
    }

    /**
     * @return a calculation of what the previous pay period is based on the configuration of this pay period
     */
    @XmlTransient
    public PayPeriod getPrevious() {
        final PayPeriod payPeriod = new PayPeriod();
        payPeriod.setCompanyId(getCompanyId());
        payPeriod.setType(getType());

        Date b = getBegin();
        Date e = getEnd();

        switch (payPeriod.getType()) {
        case WEEKLY:
            // Subtract 7 days from the start and end.
            b = DateUtils.addDays(b, -7);
            e = DateUtils.addDays(e, -7);
            break;
        case BI_WEEKLY:
            // Subtract 14 days from the start and end.
            b = DateUtils.addDays(b, -14);
            e = DateUtils.addDays(e, -14);
            break;
        case MONTHLY:
            // The end of the previous pay period is the day before the
            // beginning of this pay period.
            e = DateUtils.addDays(b, -1);
            b = DateUtils.addMonths(b, -1);
            break;
        case SEMI_MONTHLY:
            // The end of the previous pay period is the day before the
            // beginning of this pay period.
            e = DateUtils.addDays(b, -1);

            // Use the original end date to calculate the previous begin date.
            b = DateUtils.addMonths(DateUtils.addDays(getEnd(), 1), -1);
            break;
        case CUSTOM:
            // No way to determine the previous pay period.
            return null;
        }

        payPeriod.setBegin(b);
        payPeriod.setEnd(e);
        return payPeriod;
    }

    /**
     * @return a calculation of what the next pay period is based on the configuration of this pay period
     */
    @XmlTransient
    public PayPeriod getNext() {
        final PayPeriod payPeriod = new PayPeriod();
        payPeriod.setCompanyId(getCompanyId());
        payPeriod.setType(getType());

        Date b = getBegin();
        Date e = getEnd();

        switch (payPeriod.getType()) {
        case WEEKLY:
            // Add 7 days to the start and end.
            b = DateUtils.addDays(b, 7);
            e = DateUtils.addDays(e, 7);
            break;
        case BI_WEEKLY:
            // Add 14 days to the start and end.
            b = DateUtils.addDays(b, 14);
            e = DateUtils.addDays(e, 14);
            break;
        case MONTHLY:
            // The beginning of the next pay period is the day after the
            // end of this pay period.
            b = DateUtils.addDays(e, 1);

            // Is the begin date the first day of the month?
            if ("1".equals(DateFormatUtils.format(b, "d"))) {
                // Use the last day of the month.
                Date d = DateUtils.addDays(b, 27);
                while (!"1".equals(DateFormatUtils.format(d, "d")))
                    d = DateUtils.addDays(d, 1);
                e = DateUtils.addDays(d, -1);
            } else
                e = DateUtils.addMonths(getEnd(), 1);
            break;
        case SEMI_MONTHLY:
            // The beginning of the next pay period is the day after the
            // end of this pay period.
            b = DateUtils.addDays(e, 1);

            // Use the original begin date to calculate the end end date.
            final Date prevEnd = DateUtils.addDays(getBegin(), -1);
            // Was the previous end date the last day of the month?
            if ("1".equals(DateFormatUtils.format(DateUtils.addDays(prevEnd, 1), "d"))) {
                // Use the last day of the month.
                Date d = DateUtils.addDays(b, 12);
                while (!"1".equals(DateFormatUtils.format(d, "d")))
                    d = DateUtils.addDays(d, 1);
                e = DateUtils.addDays(d, -1);
            } else
                e = DateUtils.addMonths(prevEnd, 1);
            break;
        case CUSTOM:
            // No way to determine the next pay period.
            return null;
        }

        payPeriod.setBegin(b);
        payPeriod.setEnd(e);
        return payPeriod;
    }

    /**
     * @return the unique id of the company for which this pay period applies
     */
    @XmlElement
    public Integer getCompanyId() {
        return this.companyId;
    }

    /**
     * @param companyId
     *            the new unique id of the company for which this pay period applies
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided id value is invalid
     */
    public PayPeriod setCompanyId(final Integer companyId) {
        notNull(companyId, "Invalid null company id");
        isTrue(companyId >= 0, "Invalid negative company id");

        this.companyId = companyId;
        return this;
    }

    /**
     * @return the type of this pay period
     */
    @XmlElement
    public PayPeriodType getType() {
        return this.type;
    }

    /**
     * @param type
     *            the new type of pay period
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided type value is invalid
     */
    public PayPeriod setType(final PayPeriodType type) {
        notNull(type, "Invalid null type");

        this.type = type;
        return this;
    }

    /**
     * @return the first day in this pay period
     */
    @XmlElement
    public Date getBegin() {
        return this.begin;
    }

    /**
     * @param begin
     *            the new value indicating the first day of this pay period
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided begin value is invalid
     */
    public PayPeriod setBegin(final Date begin) {
        notNull(begin, "Invalid null begin value");

        // Note that this does a copy of the provided Date.
        this.begin = DateUtils.truncate(begin, Calendar.DATE);
        return this;
    }

    /**
     * @return the last day in this pay period
     */
    @XmlElement
    public Date getEnd() {
        return this.end;
    }

    /**
     * @param end
     *            the new value indicating the last day of this pay period
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided end value is invalid
     */
    public PayPeriod setEnd(final Date end) {
        notNull(end, "Invalid null end value");

        // Note that this does a copy of the provided Date.
        this.end = DateUtils.truncate(end, Calendar.DATE);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        builder.append("companyId", getCompanyId());
        builder.append("type", getType());
        builder.append("begin", getBegin());
        builder.append("end", getEnd());
        return builder.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof PayPeriod) {
            final PayPeriod other = (PayPeriod) obj;
            final EqualsBuilder builder = new EqualsBuilder();
            builder.append(getCompanyId(), other.getCompanyId());
            builder.append(getType(), other.getType());
            builder.append(getBegin(), other.getBegin());
            builder.append(getEnd(), other.getEnd());
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
        builder.append(getType());
        builder.append(getBegin());
        builder.append(getEnd());
        return builder.toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(final PayPeriod other) {
        final CompareToBuilder builder = new CompareToBuilder();
        builder.append(other.getCompanyId(), getCompanyId());
        builder.append(getType(), other.getType());
        builder.append(other.getBegin(), getBegin());
        builder.append(other.getEnd(), getEnd());
        return builder.toComparison();
    }
}
