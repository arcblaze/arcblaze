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
import org.apache.commons.lang.time.DateFormatUtils;

import com.arcblaze.arctime.common.model.util.HolidayCalculator;
import com.arcblaze.arctime.common.model.util.HolidayConfigurationException;

/**
 * Represents a holiday.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Holiday implements Comparable<Holiday> {
	/**
	 * The unique id of the holiday.
	 */
	private Integer id;

	/**
	 * The unique id of the company that owns this holiday.
	 */
	private Integer companyId;

	/**
	 * The description for the holiday.
	 */
	private String description;

	/**
	 * The configuration of this holiday.
	 */
	private String config;

	/**
	 * The day on which this holiday applies during the current year.
	 */
	private Date day;

	/**
	 * Default constructor.
	 */
	public Holiday() {
		// Nothing to do.
	}

	/**
	 * @param other
	 *            the holiday to duplicate
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided parameter is invalid
	 */
	public Holiday(final Holiday other) {
		notNull(other, "Invalid null holiday");
		if (other.getId() != null)
			setId(other.getId());
		if (other.getCompanyId() != null)
			setCompanyId(other.getCompanyId());
		if (other.getDescription() != null)
			setDescription(other.getDescription());
		if (other.getConfig() != null) {
			try {
				setConfig(other.getConfig());
			} catch (final HolidayConfigurationException badConfig) {
				// Ignored. Since the config was in the other holiday, it
				// should be valid.
			}
		}
	}

	/**
	 * @param year
	 *            the year for which the holiday date will be calculated, e.g.,
	 *            2013
	 * 
	 * @return the calculated day on which this holiday will land
	 * 
	 * @throws HolidayConfigurationException
	 *             if there is a problem parsing the configuration of this
	 *             holiday
	 */
	public Date getDateForYear(final int year)
			throws HolidayConfigurationException {
		return HolidayCalculator.getDay(getConfig(), year);
	}

	/**
	 * @return the unique id of the holiday
	 */
	@XmlElement
	public Integer getId() {
		return this.id;
	}

	/**
	 * @param id
	 *            the new unique holiday id value
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided id value is invalid
	 */
	public Holiday setId(final Integer id) {
		notNull(id, "Invalid null id");
		isTrue(id >= 0, "Invalid negative id");

		this.id = id;
		return this;
	}

	/**
	 * @return the unique id of the company that owns this holiday
	 */
	@XmlElement
	public Integer getCompanyId() {
		return this.companyId;
	}

	/**
	 * @param companyId
	 *            the new unique id of the company that owns this holiday
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided company id value is invalid
	 */
	public Holiday setCompanyId(final Integer companyId) {
		notNull(companyId, "Invalid null company id");
		isTrue(companyId >= 0, "Invalid negative company id");

		this.companyId = companyId;
		return this;
	}

	/**
	 * @return the holiday description
	 */
	@XmlElement
	public String getDescription() {
		return this.description;
	}

	/**
	 * @param description
	 *            the new holiday description
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided description value is invalid
	 */
	public Holiday setDescription(final String description) {
		notEmpty(description, "Invalid blank description");

		this.description = StringUtils.trim(description);
		return this;
	}

	/**
	 * @return the configuration of this holiday
	 */
	@XmlElement
	public String getConfig() {
		return this.config;
	}

	/**
	 * @param config
	 *            the new value determining when this holiday occurs
	 * 
	 * @return {@code this}
	 * 
	 * @throws IllegalArgumentException
	 *             if the provided config value is empty
	 * @throws HolidayConfigurationException
	 *             if the provided configuration is not valid
	 */
	public Holiday setConfig(final String config)
			throws HolidayConfigurationException {
		notEmpty(config, "Invalid empty config value");

		this.day = HolidayCalculator.getDay(config,
				Integer.parseInt(DateFormatUtils.format(new Date(), "yyyy")));
		this.config = config;

		return this;
	}

	/**
	 * @return the {@link Date} during the current year on which this holiday
	 *         falls
	 */
	@XmlElement
	public Date getDay() {
		return this.day;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final ToStringBuilder builder = new ToStringBuilder(this,
				ToStringStyle.SHORT_PREFIX_STYLE);
		builder.append("id", getId());
		builder.append("companyId", getCompanyId());
		builder.append("description", getDescription());
		builder.append("config", getConfig());
		builder.append("day", getDay());
		return builder.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof Holiday) {
			final Holiday other = (Holiday) obj;
			final EqualsBuilder builder = new EqualsBuilder();
			builder.append(getId(), other.getId());
			builder.append(getCompanyId(), other.getCompanyId());
			builder.append(getDescription(), other.getDescription());
			builder.append(getConfig(), other.getConfig());
			builder.append(getDay(), other.getDay());
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
		builder.append(getId());
		builder.append(getCompanyId());
		builder.append(getDescription());
		builder.append(getConfig());
		builder.append(getDay());
		return builder.toHashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(final Holiday other) {
		final CompareToBuilder builder = new CompareToBuilder();
		builder.append(getCompanyId(), other.getCompanyId());
		builder.append(getDay(), other.getDay());
		builder.append(other.getConfig(), getConfig());
		builder.append(getDescription(), other.getDescription());
		builder.append(getId(), other.getId());
		return builder.toComparison();
	}
}
