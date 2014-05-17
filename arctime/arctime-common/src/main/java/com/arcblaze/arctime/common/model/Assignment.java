package com.arcblaze.arctime.common.model;

import static org.apache.commons.lang.Validate.isTrue;
import static org.apache.commons.lang.Validate.notEmpty;
import static org.apache.commons.lang.Validate.notNull;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
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

import com.arcblaze.arccore.common.model.Role;
import com.arcblaze.arccore.common.model.User;

/**
 * Represents an assignment of a user to a task.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Assignment implements Comparable<Assignment> {
    /**
     * The unique id of this assignment.
     */
    private Integer id;

    /**
     * The unique id of the company for which this assignment applies.
     */
    private Integer companyId;

    /**
     * The unique id of the task to which the user is assigned.
     */
    private Integer taskId;

    /**
     * The task associated with this assignment.
     */
    private Task task;

    /**
     * The unique id of the user assigned to the task.
     */
    private Integer userId;

    /**
     * The user associated with this assignment.
     */
    private User user;

    /**
     * The labor category being used by the user on the task.
     */
    private String laborCat;

    /**
     * The item name of this assignment used to match financial data.
     */
    private String itemName;

    /**
     * The first day in the assignment.
     */
    private Date begin;

    /**
     * The last day in the assignment.
     */
    private Date end;

    /**
     * The bills applied to this assignment during a pay period.
     */
    private final Set<Bill> bills = new TreeSet<>();

    /**
     * Default constructor.
     */
    public Assignment() {
        // Nothing to do.
    }

    /**
     * @param other
     *            the assignment to duplicate
     * 
     * @throws IllegalArgumentException
     *             if the provided assignment is invalid
     */
    public Assignment(final Assignment other) {
        notNull(other, "Invalid null assignment");
        if (other.getId() != null)
            setId(other.getId());
        if (other.getCompanyId() != null)
            setCompanyId(other.getCompanyId());
        if (other.getTaskId() != null)
            setTaskId(other.getTaskId());
        if (other.getUserId() != null)
            setUserId(other.getUserId());
        if (other.getLaborCat() != null)
            setLaborCat(other.getLaborCat());
        if (other.getItemName() != null)
            setItemName(other.getItemName());
        if (other.getBegin() != null)
            setBegin(other.getBegin());
        if (other.getEnd() != null)
            setEnd(other.getEnd());
        setBills(other.getBills());

        if (other.getUser() != null)
            setUser(other.getUser());
        if (other.getTask() != null)
            setTask(other.getTask());
    }

    /**
     * @param time
     *            the {@link Date} to check to determine if it falls within this assignment
     * 
     * @return whether the provided date falls into this assignment
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
    public Assignment setId(final Integer id) {
        notNull(id, "Invalid null id");
        isTrue(id >= 0, "Invalid negative id");

        this.id = id;
        return this;
    }

    /**
     * @return the unique id of the company for which this assignment applies
     */
    @XmlElement
    public Integer getCompanyId() {
        return this.companyId;
    }

    /**
     * @param companyId
     *            the new unique id of the company for which this assignment applies
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided id value is invalid
     */
    public Assignment setCompanyId(final Integer companyId) {
        notNull(companyId, "Invalid null company id");
        isTrue(companyId >= 0, "Invalid negative company id");

        this.companyId = companyId;
        return this;
    }

    /**
     * @return the unique id of the task for which this assignment applies
     */
    @XmlElement
    public Integer getTaskId() {
        return this.taskId;
    }

    /**
     * @param taskId
     *            the new unique id of the task for which this assignment applies
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided id value is invalid
     */
    public Assignment setTaskId(final Integer taskId) {
        notNull(taskId, "Invalid null task id");
        isTrue(taskId >= 0, "Invalid negative task id");

        this.taskId = taskId;
        return this;
    }

    /**
     * @return whether a task has been set in this assignment
     */
    public boolean hasTask() {
        return this.task != null;
    }

    /**
     * @return the task in the assignment, possibly {@code null}
     */
    public Task getTask() {
        return this.task;
    }

    /**
     * @param task
     *            the new task in the assignment
     */
    public void setTask(final Task task) {
        this.task = task == null ? null : new Task(task);
    }

    /**
     * @return the unique id of the user for which this assignment applies
     */
    @XmlElement
    public Integer getUserId() {
        return this.userId;
    }

    /**
     * @param userId
     *            the new unique id of the user for which this assignment applies
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided id value is invalid
     */
    public Assignment setUserId(final Integer userId) {
        notNull(userId, "Invalid null user id");
        isTrue(userId >= 0, "Invalid negative user id");

        this.userId = userId;
        return this;
    }

    /**
     * @return whether a user has been set in this assignment
     */
    public boolean hasUser() {
        return this.user != null;
    }

    /**
     * @return the user in the assignment, possibly {@code null}
     */
    public User getUser() {
        return this.user;
    }

    /**
     * @param user
     *            the new user in the assignment
     */
    public void setUser(final User user) {
        this.user = user == null ? null : new User(user);
    }

    /**
     * @return the labor category of this assignment
     */
    @XmlElement
    public String getLaborCat() {
        return this.laborCat;
    }

    /**
     * @param laborCat
     *            the new labor category of this assignment
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided labor category value is invalid
     */
    public Assignment setLaborCat(final String laborCat) {
        notEmpty(laborCat, "Invalid blank labor category");

        this.laborCat = StringUtils.trim(laborCat);
        return this;
    }

    /**
     * @return the item name of this assignment
     */
    @XmlElement
    public String getItemName() {
        return this.itemName;
    }

    /**
     * @param itemName
     *            the new item name of this assignment
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided item name value is invalid
     */
    public Assignment setItemName(final String itemName) {
        notEmpty(itemName, "Invalid blank item name");

        this.itemName = StringUtils.trim(itemName);
        return this;
    }

    /**
     * @return the first day in this assignment
     */
    @XmlElement
    public Date getBegin() {
        return this.begin;
    }

    /**
     * @param begin
     *            the new value indicating the first day of this assignment
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided begin value is invalid
     */
    public Assignment setBegin(final Date begin) {
        notNull(begin, "Invalid null begin value");

        // Note that this does a copy of the provided Date.
        this.begin = DateUtils.truncate(begin, Calendar.DATE);
        return this;
    }

    /**
     * @return the last day in this assignment
     */
    @XmlElement
    public Date getEnd() {
        return this.end;
    }

    /**
     * @param end
     *            the new value indicating the last day of this assignment
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided end value is invalid
     */
    public Assignment setEnd(final Date end) {
        notNull(end, "Invalid null end value");

        // Note that this does a copy of the provided Date.
        this.end = DateUtils.truncate(end, Calendar.DATE);
        return this;
    }

    /**
     * @return all of the bills authorized for this account
     */
    @XmlElementWrapper
    @XmlElement(name = "bill")
    public Set<Bill> getBills() {
        return Collections.unmodifiableSet(this.bills);
    }

    /**
     * @param day
     *            the day for which to search for a bill in this assignment
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
    public Assignment setBills(final Bill... newBills) {
        notNull(newBills, "Invalid null bills");

        return this.setBills(Arrays.asList(newBills));
    }

    /**
     * @param newBills
     *            the new bill values to be assigned to this account
     * 
     * @return {@code this}
     */
    public Assignment setBills(final Collection<Bill> newBills) {
        // Prevent an accidental deletion when the sets are the same.
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
    public Assignment addBills(final Bill... newBills) {
        notNull(newBills, "Invalid null bills");

        return this.addBills(Arrays.asList(newBills));
    }

    /**
     * @param newBills
     *            the new bill values to be assigned to this account
     * 
     * @return {@code this}
     */
    public Assignment addBills(final Collection<Bill> newBills) {
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
    public Assignment clearBills() {
        this.bills.clear();
        return this;
    }

    /**
     * @return the task description
     */
    @XmlElement
    public String getDescription() {
        return this.task == null ? null : this.task.getDescription();
    }

    /**
     * @return the job code associated with the task
     */
    @XmlElement
    public String getJobCode() {
        return this.task == null ? null : this.task.getJobCode();
    }

    /**
     * @return whether the task is administrative and available to all users
     */
    @XmlElement
    public Boolean isAdministrative() {
        return this.task == null ? null : this.task.isAdministrative();
    }

    /**
     * @return the user login value
     */
    @XmlElement
    public String getLogin() {
        return this.user == null ? null : this.user.getLogin();
    }

    /**
     * @return the user's email address
     */
    @XmlElement
    public String getEmail() {
        return this.user == null ? null : this.user.getEmail();
    }

    /**
     * @return the user's first name
     */
    @XmlElement
    public String getFirstName() {
        return this.user == null ? null : this.user.getFirstName();
    }

    /**
     * @return the user's last name
     */
    @XmlElement
    public String getLastName() {
        return this.user == null ? null : this.user.getLastName();
    }

    /**
     * @return the full name of the user
     */
    @XmlElement
    public String getFullName() {
        if (this.user == null)
            return null;
        final StringBuilder name = new StringBuilder();
        name.append(getFirstName());
        name.append(" ");
        name.append(getLastName());
        return name.toString();
    }

    /**
     * @return the privileges available to the user, based on the roles
     */
    @XmlElement
    public String getPrivileges() {
        if (this.user == null)
            return null;
        final Set<Character> privs = new TreeSet<>();
        for (final Role role : getRoles())
            privs.add(role.getName().charAt(0));
        return StringUtils.join(privs, " ");
    }

    /**
     * @return all of the roles authorized for the user account
     */
    @XmlElementWrapper
    @XmlElement(name = "role")
    public Set<Role> getRoles() {
        return this.user == null ? new HashSet<Role>() : this.user.getRoles();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        builder.append("id", getId());
        builder.append("companyId", getCompanyId());
        builder.append("taskId", getTaskId());
        builder.append("userId", getUserId());
        builder.append("laborCat", getLaborCat());
        builder.append("itemName", getItemName());
        builder.append("begin", getBegin());
        builder.append("end", getEnd());
        return builder.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Assignment) {
            final Assignment other = (Assignment) obj;
            final EqualsBuilder builder = new EqualsBuilder();
            builder.append(getId(), other.getId());
            builder.append(getCompanyId(), other.getCompanyId());
            builder.append(getTaskId(), other.getTaskId());
            builder.append(getUserId(), other.getUserId());
            builder.append(getLaborCat(), other.getLaborCat());
            builder.append(getItemName(), other.getItemName());
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
        builder.append(getId());
        builder.append(getCompanyId());
        builder.append(getTaskId());
        builder.append(getUserId());
        builder.append(getLaborCat());
        builder.append(getItemName());
        builder.append(getBegin());
        builder.append(getEnd());
        return builder.toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(final Assignment other) {
        final CompareToBuilder builder = new CompareToBuilder();
        builder.append(other.getCompanyId(), getCompanyId());
        builder.append(getUserId(), other.getUserId());
        builder.append(getTaskId(), other.getTaskId());
        builder.append(getLaborCat(), other.getLaborCat());
        builder.append(getItemName(), other.getItemName());
        builder.append(getBegin(), other.getBegin());
        builder.append(getEnd(), other.getEnd());
        return builder.toComparison();
    }
}
