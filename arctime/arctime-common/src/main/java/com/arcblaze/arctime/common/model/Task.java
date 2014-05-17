package com.arcblaze.arctime.common.model;

import static org.apache.commons.lang.Validate.isTrue;
import static org.apache.commons.lang.Validate.notEmpty;
import static org.apache.commons.lang.Validate.notNull;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang.time.DateUtils;

/**
 * Represents a task to which a user can be assigned.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Task implements Comparable<Task> {
    /**
     * The unique id of the task.
     */
    private Integer id;

    /**
     * The unique id of the company in which this task resides.
     */
    private Integer companyId;

    /**
     * The description of the task as displayed to users in the system on their timesheets.
     */
    private String description;

    /**
     * The task job code used by payroll personnel to track the task in finance systems.
     */
    private String jobCode;

    /**
     * Whether this task is administrative and available to all users in the company.
     */
    private Boolean administrative = false;

    /**
     * Whether this task is active and available for users to charge against.
     */
    private Boolean active = true;

    /**
     * Holds the assignments associated with this task.
     */
    private final Set<Assignment> assignments = new TreeSet<>();

    /**
     * The bills applied directly to this task during a pay period.
     */
    private final Set<Bill> bills = new TreeSet<>();

    /**
     * Default constructor.
     */
    public Task() {
        // Nothing to do.
    }

    /**
     * @param other
     *            the task to duplicate
     * 
     * @throws IllegalArgumentException
     *             if the provided task is invalid
     */
    public Task(final Task other) {
        notNull(other, "Invalid null task");
        if (other.getId() != null)
            setId(other.getId());
        if (other.getCompanyId() != null)
            setCompanyId(other.getCompanyId());
        if (other.getDescription() != null)
            setDescription(other.getDescription());
        if (other.getJobCode() != null)
            setJobCode(other.getJobCode());
        if (other.isAdministrative() != null)
            setAdministrative(other.isAdministrative());
        if (other.isActive() != null)
            setActive(other.isActive());
        setAssignments(other.getAssignments());
        setBills(other.getBills());
    }

    /**
     * @return the unique id of the task
     */
    @XmlElement
    public Integer getId() {
        return this.id;
    }

    /**
     * @param id
     *            the new unique task id value
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided id value is invalid
     */
    public Task setId(final Integer id) {
        notNull(id, "Invalid null id");
        isTrue(id >= 0, "Invalid negative id");

        this.id = id;
        return this;
    }

    /**
     * @return the unique id of the company in which this task resides
     */
    @XmlElement
    public Integer getCompanyId() {
        return this.companyId;
    }

    /**
     * @param companyId
     *            the new unique company id value
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided companyId value is invalid
     */
    public Task setCompanyId(final Integer companyId) {
        notNull(companyId, "Invalid null company id");
        isTrue(companyId >= 0, "Invalid negative company id");

        this.companyId = companyId;
        return this;
    }

    /**
     * @return the task description
     */
    @XmlElement
    public String getDescription() {
        return this.description;
    }

    /**
     * @param description
     *            the new task description value
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided description value is invalid
     */
    public Task setDescription(final String description) {
        notEmpty(description, "Invalid blank login");

        this.description = StringUtils.trim(description);
        return this;
    }

    /**
     * @return the job code associated with this task
     */
    @XmlElement
    public String getJobCode() {
        return this.jobCode;
    }

    /**
     * @param jobCode
     *            the new job code for the task
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided email value is invalid
     */
    public Task setJobCode(final String jobCode) {
        notEmpty(jobCode, "Invalid blank job code");

        this.jobCode = StringUtils.trim(jobCode);
        return this;
    }

    /**
     * @return whether this task is administrative and available to all users
     */
    @XmlElement
    public Boolean isAdministrative() {
        return this.administrative;
    }

    /**
     * @param administrative
     *            the new value indicating whether this is an administrative task
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided administrative value is invalid
     */
    public Task setAdministrative(final Boolean administrative) {
        notNull(administrative, "Invalid null administrative value");

        this.administrative = administrative;
        return this;
    }

    /**
     * @return whether this task is active and available for hours to be charged against it
     */
    @XmlElement
    public Boolean isActive() {
        return this.active;
    }

    /**
     * @param active
     *            the new value indicating whether this is an active task
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided active value is invalid
     */
    public Task setActive(final Boolean active) {
        notNull(active, "Invalid null active value");

        this.active = active;
        return this;
    }

    /**
     * @return all of the assignments being assignments by this account
     */
    @XmlElementWrapper
    @XmlElement(name = "assignment")
    public Set<Assignment> getAssignments() {
        return Collections.unmodifiableSet(this.assignments);
    }

    /**
     * @param assignmentId
     *            the unique id of the assignment to search for in this task
     * 
     * @return the requested assignment if it exists in this task, {@code null} otherwise
     */
    public Assignment getAssignment(final Integer assignmentId) {
        if (assignmentId == null)
            return null;

        for (final Assignment assignment : getAssignments())
            if (assignment.getId() == assignmentId)
                return assignment;

        return null;
    }

    /**
     * @param newAssignments
     *            the new assignments to be assigned to this account
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided assignments assignments value is invalid
     */
    public Task setAssignments(final Assignment... newAssignments) {
        notNull(newAssignments, "Invalid null assignments");

        return this.setAssignments(Arrays.asList(newAssignments));
    }

    /**
     * @param newAssignments
     *            the new assignments to be assigned to this account
     * 
     * @return {@code this}
     */
    public Task setAssignments(final Collection<Assignment> newAssignments) {
        // Prevent an accidental clear if they are the same
        if (newAssignments != this.assignments)
            this.assignments.clear();
        if (newAssignments != null) {
            for (final Assignment assignment : newAssignments)
                if (assignment != null)
                    // Defensive copy
                    this.assignments.add(new Assignment(assignment));
        }
        return this;
    }

    /**
     * @param newAssignments
     *            the new assignments to be assigned to this account
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided assignments assignments value is invalid
     */
    public Task addAssignments(final Assignment... newAssignments) {
        notNull(newAssignments, "Invalid null assignments");

        return this.addAssignments(Arrays.asList(newAssignments));
    }

    /**
     * @param newAssignments
     *            the new assignments to be assigned to this account
     * 
     * @return {@code this}
     */
    public Task addAssignments(final Collection<Assignment> newAssignments) {
        if (newAssignments != null) {
            for (final Assignment assignment : newAssignments)
                if (assignment != null)
                    // Defensive copy
                    this.assignments.add(new Assignment(assignment));
        }
        return this;
    }

    /**
     * @return {@code this}
     */
    public Task clearAssignments() {
        this.assignments.clear();
        return this;
    }

    /**
     * @return all of the bills assigned to this task
     */
    @XmlElementWrapper
    @XmlElement(name = "bill")
    public Set<Bill> getBills() {
        return Collections.unmodifiableSet(this.bills);
    }

    /**
     * @param day
     *            the day for which to search for a bill in this task
     * 
     * @return the requested bill if available, {@code null} otherwise
     */
    public Bill getBill(final Date day) {
        if (day == null)
            return null;

        final Date d = DateUtils.truncate(day, Calendar.DATE);

        for (final Bill bill : getBills())
            if (d.equals(bill.getDay()))
                return bill;
        return null;
    }

    /**
     * @param newBills
     *            the new bill values to be assigned to this account
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided bills value is invalid
     */
    public Task setBills(final Bill... newBills) {
        notNull(newBills, "Invalid null bills");

        return this.setBills(Arrays.asList(newBills));
    }

    /**
     * @param newBills
     *            the new bill values to be assigned to this account
     * 
     * @return {@code this}
     */
    public Task setBills(final Collection<Bill> newBills) {
        // Prevent accidental clearing when they are the same
        if (newBills != this.bills)
            this.bills.clear();
        if (newBills != null) {
            for (final Bill bill : newBills)
                if (bill != null)
                    this.bills.add(new Bill(bill)); // Defensive copy
        }
        return this;
    }

    /**
     * @param newBills
     *            the new bill values to be assigned to this account
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided bills value is invalid
     */
    public Task addBills(final Bill... newBills) {
        notNull(newBills, "Invalid null bills");

        return this.addBills(Arrays.asList(newBills));
    }

    /**
     * @param newBills
     *            the new bill values to be assigned to this account
     * 
     * @return {@code this}
     */
    public Task addBills(final Collection<Bill> newBills) {
        if (newBills != null) {
            for (final Bill bill : newBills)
                if (bill != null)
                    this.bills.add(new Bill(bill)); // Defensive copy
        }
        return this;
    }

    /**
     * @return {@code this}
     */
    public Task clearBills() {
        this.bills.clear();
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        builder.append("id", getId());
        builder.append("companyId", getCompanyId());
        builder.append("description", getDescription());
        builder.append("jobCode", getJobCode());
        builder.append("administrative", isAdministrative());
        builder.append("active", isActive());
        return builder.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Task) {
            final Task other = (Task) obj;
            final EqualsBuilder builder = new EqualsBuilder();
            builder.append(getId(), other.getId());
            builder.append(getCompanyId(), other.getCompanyId());
            builder.append(getDescription(), other.getDescription());
            builder.append(getJobCode(), other.getJobCode());
            builder.append(isAdministrative(), other.isAdministrative());
            builder.append(isActive(), other.isActive());
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
        builder.append(getJobCode());
        builder.append(isAdministrative());
        builder.append(isActive());
        return builder.toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(final Task other) {
        final CompareToBuilder builder = new CompareToBuilder();
        builder.append(getCompanyId(), other.getCompanyId());
        builder.append(other.isActive(), isActive());
        builder.append(isAdministrative(), other.isAdministrative());
        builder.append(getDescription(), other.getDescription());
        builder.append(getJobCode(), other.getJobCode());
        builder.append(getId(), other.getId());
        return builder.toComparison();
    }
}
