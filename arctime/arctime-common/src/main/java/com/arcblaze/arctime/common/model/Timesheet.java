package com.arcblaze.arctime.common.model;

import static org.apache.commons.lang.Validate.isTrue;
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
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang.time.DateUtils;

import com.arcblaze.arccore.common.model.User;

/**
 * Represents a timesheet for a user during a pay period.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Timesheet implements Comparable<Timesheet> {
    /**
     * The unique id of this timesheet.
     */
    private Integer id;

    /**
     * The unique id of the company in which this timesheet resides.
     */
    private Integer companyId;

    /**
     * The unique id of the user that owns this timesheet.
     */
    private Integer userId;

    /**
     * The user that owns this timesheet.
     */
    private User user;

    /**
     * The first day in the pay period associated with this timesheet.
     */
    private Date begin;

    /**
     * The pay period for which this timesheet contains hours.
     */
    private PayPeriod payPeriod;

    /**
     * Whether this timesheet has been completed by the user.
     */
    private Boolean completed = false;

    /**
     * Whether this timesheet has been approved by the user's supervisor.
     */
    private Boolean approved = false;

    /**
     * Whether this timesheet has been verified by payroll.
     */
    private Boolean verified = false;

    /**
     * Whether this timesheet has been exported by payroll.
     */
    private Boolean exported = false;

    /**
     * The id of the supervisor that approved this timesheet.
     */
    private Integer approverId;

    /**
     * The user that approved this timesheet (if it has been approved).
     */
    private User approver;

    /**
     * The id of the payroll person that verified this timesheet.
     */
    private Integer verifierId;

    /**
     * The payroll user that verified this timesheet (if it has been verified).
     */
    private User verifier;

    /**
     * The id of the payroll person that exported this timesheet.
     */
    private Integer exporterId;

    /**
     * The payroll user that exported this timesheet (if it has been exported).
     */
    private User exporter;

    /**
     * The tasks to which the user can charge hours during this pay period.
     */
    private final Set<Task> tasks = new TreeSet<>();

    /**
     * The audit logs associated with this timesheet.
     */
    private final Set<AuditLog> auditLogs = new TreeSet<>();

    /**
     * The company holidays used to flag specific days in the pay period as being holidays or not.
     */
    private final Set<Holiday> holidays = new TreeSet<>();

    /**
     * Default constructor.
     */
    public Timesheet() {
        // Nothing to do.
    }

    /**
     * @param other
     *            the timesheet to duplicate
     * 
     * @throws IllegalArgumentException
     *             if the provided timesheet is invalid
     */
    public Timesheet(final Timesheet other) {
        notNull(other, "Invalid null timesheet");
        if (other.getId() != null)
            setId(other.getId());
        if (other.getCompanyId() != null)
            setCompanyId(other.getCompanyId());
        if (other.getUserId() != null)
            setUserId(other.getUserId());
        if (other.getUser() != null)
            setUser(other.getUser());
        if (other.getBegin() != null)
            setBegin(other.getBegin());
        if (other.getPayPeriod() != null)
            setPayPeriod(other.getPayPeriod());
        if (other.isCompleted() != null)
            other.setCompleted(other.isCompleted());
        if (other.isApproved() != null)
            other.setApproved(other.isApproved());
        if (other.isVerified() != null)
            other.setVerified(other.isVerified());
        if (other.isExported() != null)
            other.setExported(other.isExported());
        if (other.getApproverId() != null)
            setApproverId(other.getApproverId());
        if (other.getApprover() != null)
            setApprover(other.getApprover());
        if (other.getVerifierId() != null)
            setVerifierId(other.getVerifierId());
        if (other.getVerifier() != null)
            setVerifier(other.getVerifier());
        if (other.getExporterId() != null)
            setExporterId(other.getExporterId());
        if (other.getExporter() != null)
            setExporter(other.getExporter());
        setTasks(other.getTasks());
        setAuditLogs(other.getAuditLogs());
        setHolidays(other.getHolidays());
    }

    /**
     * @return the unique id of this timesheet
     */
    @XmlElement
    public Integer getId() {
        return this.id;
    }

    /**
     * @param id
     *            the new unique id of this timesheet
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided id value is invalid
     */
    public Timesheet setId(final Integer id) {
        notNull(id, "Invalid null id");
        isTrue(id >= 0, "Invalid negative id");

        this.id = id;
        return this;
    }

    /**
     * @return the unique id of the company that owns this timesheet
     */
    @XmlElement
    public Integer getCompanyId() {
        return this.companyId;
    }

    /**
     * @param companyId
     *            the new unique id of the company that owns this timesheet
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided id value is invalid
     */
    public Timesheet setCompanyId(final Integer companyId) {
        notNull(companyId, "Invalid null company id");
        isTrue(companyId >= 0, "Invalid negative company id");

        this.companyId = companyId;
        return this;
    }

    /**
     * @return the unique id of the user that owns this timesheet
     */
    @XmlElement
    public Integer getUserId() {
        return this.userId;
    }

    /**
     * @param userId
     *            the new unique id of the user that owns this timesheet
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided id value is invalid
     */
    public Timesheet setUserId(final Integer userId) {
        notNull(userId, "Invalid null user id");
        isTrue(userId >= 0, "Invalid negative user id");

        this.userId = userId;
        return this;
    }

    /**
     * @return the user that owns this timesheet
     */
    @XmlElement
    public User getUser() {
        return this.user;
    }

    /**
     * @param user
     *            the new user that owns this timesheet
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided value is invalid
     */
    public Timesheet setUser(final User user) {
        notNull(user, "Invalid null user");

        this.user = new User(user); // Defensive copy
        return this;
    }

    /**
     * @return the first day in the pay period associated with this timesheet
     */
    @XmlElement
    public Date getBegin() {
        return this.begin;
    }

    /**
     * @param begin
     *            the new value indicating the first day in the pay period associated with this timesheet
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided begin value is invalid
     */
    public Timesheet setBegin(final Date begin) {
        notNull(begin, "Invalid null begin value");

        // Note that this does a defensive copy.
        this.begin = DateUtils.truncate(begin, Calendar.DATE);
        return this;
    }

    /**
     * @return the pay period associated with this timesheet
     */
    @XmlElement
    public PayPeriod getPayPeriod() {
        return this.payPeriod;
    }

    /**
     * @param payPeriod
     *            the new pay period associated with this timesheet
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided pay period value is invalid
     */
    public Timesheet setPayPeriod(final PayPeriod payPeriod) {
        notNull(payPeriod, "Invalid null pay period value");

        this.payPeriod = new PayPeriod(payPeriod); // Defensive copy
        return this;
    }

    /**
     * @return whether the timesheet has been completed by the user
     */
    @XmlElement
    public Boolean isCompleted() {
        return this.completed;
    }

    /**
     * @param completed
     *            the new value indicating whether this timesheet has been completed by the user
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided completed value is invalid
     */
    public Timesheet setCompleted(final Boolean completed) {
        notNull(completed, "Invalid null completed value");

        this.completed = completed;
        return this;
    }

    /**
     * @return whether the timesheet has been approved by the user's supervisor
     */
    @XmlElement
    public Boolean isApproved() {
        return this.approved;
    }

    /**
     * @param approved
     *            the new value indicating whether this timesheet has been approved by the user's supervisor
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided approved value is invalid
     */
    public Timesheet setApproved(final Boolean approved) {
        notNull(approved, "Invalid null approved value");

        this.approved = approved;
        return this;
    }

    /**
     * @return whether the timesheet has been verified by payroll
     */
    @XmlElement
    public Boolean isVerified() {
        return this.verified;
    }

    /**
     * @param verified
     *            the new value indicating whether this timesheet has been verified by payroll
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided verified value is invalid
     */
    public Timesheet setVerified(final Boolean verified) {
        notNull(verified, "Invalid null verified value");

        this.verified = verified;
        return this;
    }

    /**
     * @return whether the timesheet has been exported by payroll
     */
    @XmlElement
    public Boolean isExported() {
        return this.exported;
    }

    /**
     * @param exported
     *            the new value indicating whether this timesheet has been exported by payroll
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided exported value is invalid
     */
    public Timesheet setExported(final Boolean exported) {
        notNull(exported, "Invalid null exported value");

        this.exported = exported;
        return this;
    }

    /**
     * @return the unique id of the supervisor that approved this timesheet
     */
    @XmlElement
    public Integer getApproverId() {
        return this.approverId;
    }

    /**
     * @param approverId
     *            the new unique id of the supervisor that approved this timesheet
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided id value is invalid
     */
    public Timesheet setApproverId(final Integer approverId) {
        notNull(approverId, "Invalid null approver id");
        isTrue(approverId >= 0, "Invalid negative approver id");

        this.approverId = approverId;
        return this;
    }

    /**
     * @return the person that approved this timesheet
     */
    @XmlElement
    public User getApprover() {
        return this.approver;
    }

    /**
     * @param approver
     *            the new value for the person that approved this timesheet
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided value is invalid
     */
    public Timesheet setApprover(final User approver) {
        notNull(approver, "Invalid null approver");

        this.approver = new User(approver); // Defensive copy
        return this;
    }

    /**
     * @return the unique id of the payroll person that verified this timesheet
     */
    @XmlElement
    public Integer getVerifierId() {
        return this.verifierId;
    }

    /**
     * @param verifierId
     *            the new unique id of the payroll person that verified this timesheet
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided id value is invalid
     */
    public Timesheet setVerifierId(final Integer verifierId) {
        notNull(verifierId, "Invalid null verifier id");
        isTrue(verifierId >= 0, "Invalid negative verifier id");

        this.verifierId = verifierId;
        return this;
    }

    /**
     * @return the payroll person that verified this timesheet
     */
    @XmlElement
    public User getVerifier() {
        return this.verifier;
    }

    /**
     * @param verifier
     *            the new value for the payroll person that verified this timesheet
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided value is invalid
     */
    public Timesheet setVerifier(final User verifier) {
        notNull(verifier, "Invalid null verifier");

        this.verifier = new User(verifier); // Defensive copy
        return this;
    }

    /**
     * @return the unique id of the payroll person that exported this timesheet
     */
    @XmlElement
    public Integer getExporterId() {
        return this.exporterId;
    }

    /**
     * @param exporterId
     *            the new unique id of the payroll person that exported this timesheet
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided id value is invalid
     */
    public Timesheet setExporterId(final Integer exporterId) {
        notNull(exporterId, "Invalid null exporter id");
        isTrue(exporterId >= 0, "Invalid negative exporter id");

        this.exporterId = exporterId;
        return this;
    }

    /**
     * @return the payroll person that exported this timesheet
     */
    @XmlElement
    public User getExporter() {
        return this.exporter;
    }

    /**
     * @param exporter
     *            the new value for the payroll person that exported this timesheet
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided value is invalid
     */
    public Timesheet setExporter(final User exporter) {
        notNull(exporter, "Invalid null exporter");

        this.exporter = new User(exporter); // Defensive copy
        return this;
    }

    /**
     * @return all of the unique ids of the users (owner, approver, verifier, exporter) associated with this timesheet,
     *         if known
     */
    @XmlTransient
    public Set<Integer> getUserIds() {
        final Set<Integer> ids = new TreeSet<>();
        if (getUserId() != null)
            ids.add(getUserId());
        if (getApproverId() != null)
            ids.add(getApproverId());
        if (getVerifierId() != null)
            ids.add(getVerifierId());
        if (getExporterId() != null)
            ids.add(getExporterId());
        return ids;
    }

    /**
     * @return all of the tasks the user can bill hours to
     */
    @XmlElementWrapper
    @XmlElement(name = "task")
    public Set<Task> getTasks() {
        return Collections.unmodifiableSet(this.tasks);
    }

    /**
     * @param taskId
     *            the unique id of the task to search for in this timesheet
     * 
     * @return the requested task if it is available in this timesheet, {@code null} otherwise
     */
    public Task getTask(final Integer taskId) {
        if (taskId == null)
            return null;

        for (final Task task : getTasks())
            if (task.getId() == taskId)
                return task;

        return null;
    }

    /**
     * @param newTasks
     *            the new tasks to which the user can bill hours
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided tasks value is invalid
     */
    public Timesheet setTasks(final Task... newTasks) {
        notNull(newTasks, "Invalid null tasks");

        return this.setTasks(Arrays.asList(newTasks));
    }

    /**
     * @param newTasks
     *            the new tasks to which the user can bill hours
     * 
     * @return {@code this}
     */
    public Timesheet setTasks(final Collection<Task> newTasks) {
        // Prevent accidental clearing when they are the same
        if (newTasks != this.tasks)
            this.tasks.clear();
        if (newTasks != null) {
            for (final Task task : newTasks)
                if (task != null)
                    this.tasks.add(new Task(task)); // Defensive copy
        }
        return this;
    }

    /**
     * @param newTasks
     *            the new tasks to which the user can bill hours
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided tasks value is invalid
     */
    public Timesheet addTasks(final Task... newTasks) {
        notNull(newTasks, "Invalid null tasks");

        return this.addTasks(Arrays.asList(newTasks));
    }

    /**
     * @param newTasks
     *            the new tasks to which the user can bill hours
     * 
     * @return {@code this}
     */
    public Timesheet addTasks(final Collection<Task> newTasks) {
        if (newTasks != null) {
            for (final Task task : newTasks)
                if (task != null)
                    this.tasks.add(new Task(task)); // Defensive copy
        }
        return this;
    }

    /**
     * @return {@code this}
     */
    public Timesheet clearTasks() {
        this.tasks.clear();
        return this;
    }

    /**
     * @return all of the auditLogs the user can bill hours to
     */
    @XmlElementWrapper
    @XmlElement(name = "auditLog")
    public Set<AuditLog> getAuditLogs() {
        return Collections.unmodifiableSet(this.auditLogs);
    }

    /**
     * @param newAuditLogs
     *            the new auditLogs to which the user can bill hours
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided auditLogs value is invalid
     */
    public Timesheet setAuditLogs(final AuditLog... newAuditLogs) {
        notNull(newAuditLogs, "Invalid null auditLogs");

        return this.setAuditLogs(Arrays.asList(newAuditLogs));
    }

    /**
     * @param newAuditLogs
     *            the new auditLogs to which the user can bill hours
     * 
     * @return {@code this}
     */
    public Timesheet setAuditLogs(final Collection<AuditLog> newAuditLogs) {
        // Prevent accidental clearing when they are the same
        if (newAuditLogs != this.auditLogs)
            this.auditLogs.clear();
        if (newAuditLogs != null) {
            for (AuditLog log : newAuditLogs)
                if (log != null)
                    this.auditLogs.add(new AuditLog(log)); // Defensive copy
        }
        return this;
    }

    /**
     * @param newAuditLogs
     *            the new auditLogs to which the user can bill hours
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided auditLogs value is invalid
     */
    public Timesheet addAuditLogs(final AuditLog... newAuditLogs) {
        notNull(newAuditLogs, "Invalid null auditLogs");

        return this.addAuditLogs(Arrays.asList(newAuditLogs));
    }

    /**
     * @param newAuditLogs
     *            the new auditLogs to which the user can bill hours
     * 
     * @return {@code this}
     */
    public Timesheet addAuditLogs(final Collection<AuditLog> newAuditLogs) {
        if (newAuditLogs != null) {
            for (AuditLog log : newAuditLogs)
                if (log != null)
                    this.auditLogs.add(new AuditLog(log)); // Defensive copy
        }
        return this;
    }

    /**
     * @return {@code this}
     */
    public Timesheet clearAuditLogs() {
        this.auditLogs.clear();
        return this;
    }

    /**
     * @return all of the holidays the user can bill hours to
     */
    @XmlElementWrapper
    @XmlElement(name = "holiday")
    public Set<Holiday> getHolidays() {
        return Collections.unmodifiableSet(this.holidays);
    }

    /**
     * @param newHolidays
     *            the new holidays to which the user can bill hours
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided holidays value is invalid
     */
    public Timesheet setHolidays(final Holiday... newHolidays) {
        notNull(newHolidays, "Invalid null holidays");

        return this.setHolidays(Arrays.asList(newHolidays));
    }

    /**
     * @param newHolidays
     *            the new holidays to which the user can bill hours
     * 
     * @return {@code this}
     */
    public Timesheet setHolidays(final Collection<Holiday> newHolidays) {
        // Prevent accidental clearing when they are the same
        if (newHolidays != this.holidays)
            this.holidays.clear();
        if (newHolidays != null) {
            for (final Holiday holiday : newHolidays)
                if (holiday != null)
                    this.holidays.add(new Holiday(holiday)); // Defensive copy
        }
        return this;
    }

    /**
     * @param newHolidays
     *            the new holidays to which the user can bill hours
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided holidays value is invalid
     */
    public Timesheet addHolidays(final Holiday... newHolidays) {
        notNull(newHolidays, "Invalid null holidays");

        return this.addHolidays(Arrays.asList(newHolidays));
    }

    /**
     * @param newHolidays
     *            the new holidays to which the user can bill hours
     * 
     * @return {@code this}
     */
    public Timesheet addHolidays(final Collection<Holiday> newHolidays) {
        if (newHolidays != null) {
            for (final Holiday holiday : newHolidays)
                if (holiday != null)
                    this.holidays.add(new Holiday(holiday)); // Defensive copy
        }
        return this;
    }

    /**
     * @return {@code this}
     */
    public Timesheet clearHolidays() {
        this.holidays.clear();
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
        builder.append("userId", getUserId());
        builder.append("begin", getBegin());
        builder.append("completed", isCompleted());
        builder.append("approved", isApproved());
        builder.append("verified", isVerified());
        builder.append("exported", isExported());
        builder.append("approverId", getApproverId());
        builder.append("verifierId", getVerifierId());
        builder.append("exporterId", getExporterId());
        return builder.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Timesheet) {
            final Timesheet other = (Timesheet) obj;
            final EqualsBuilder builder = new EqualsBuilder();
            builder.append(getId(), other.getId());
            builder.append(getCompanyId(), other.getCompanyId());
            builder.append(getUserId(), other.getUserId());
            builder.append(getBegin(), other.getBegin());
            builder.append(isCompleted(), other.isCompleted());
            builder.append(isApproved(), other.isApproved());
            builder.append(isVerified(), other.isVerified());
            builder.append(isExported(), other.isExported());
            builder.append(getApproverId(), other.getApproverId());
            builder.append(getVerifierId(), other.getVerifierId());
            builder.append(getExporterId(), other.getExporterId());
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
        builder.append(getUserId());
        builder.append(getBegin());
        builder.append(isCompleted());
        builder.append(isApproved());
        builder.append(isVerified());
        builder.append(isExported());
        builder.append(getApproverId());
        builder.append(getVerifierId());
        builder.append(getExporterId());
        return builder.toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(final Timesheet other) {
        final CompareToBuilder builder = new CompareToBuilder();
        builder.append(getCompanyId(), getCompanyId());
        builder.append(other.getBegin(), getBegin());
        builder.append(getUserId(), other.getUserId());
        builder.append(getId(), other.getId());
        builder.append(isCompleted(), other.isCompleted());
        builder.append(isApproved(), other.isApproved());
        builder.append(isVerified(), other.isVerified());
        builder.append(isExported(), other.isExported());
        builder.append(getApproverId(), other.getApproverId());
        builder.append(getVerifierId(), other.getVerifierId());
        builder.append(getExporterId(), other.getExporterId());
        return builder.toComparison();
    }
}
