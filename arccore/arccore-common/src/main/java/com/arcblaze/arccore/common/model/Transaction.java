package com.arcblaze.arccore.common.model;

import static org.apache.commons.lang.Validate.isTrue;
import static org.apache.commons.lang.Validate.notEmpty;
import static org.apache.commons.lang.Validate.notNull;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Represents a financial transaction that took place in this system.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class Transaction implements Comparable<Transaction> {
    /**
     * The unique id of this transaction.
     */
    private Integer id;

    /**
     * The unique id of the company for which this transaction applies.
     */
    private Integer companyId;

    /**
     * The unique id of the user that invoked this transaction.
     */
    private Integer userId;

    /**
     * The user that invoked this transaction.
     */
    private User user;

    /**
     * The time stamp when this transaction took place.
     */
    private Date timestamp = new Date();

    /**
     * The type of transaction that occurred.
     */
    private TransactionType transactionType;

    /**
     * A brief description of the transaction that took place.
     */
    private String description;

    /**
     * The amount associated with this transaction.
     */
    private BigDecimal amount;

    /**
     * Any additional notes associated with the transaction.
     */
    private String notes;

    /**
     * Default constructor.
     */
    public Transaction() {
        // Nothing to do.
    }

    /**
     * @param other
     *            the transaction to duplicate
     * 
     * @throws IllegalArgumentException
     *             if the provided parameter is invalid
     */
    public Transaction(final Transaction other) {
        notNull(other, "Invalid null transaction");
        if (other.getId() != null)
            setId(other.getId());
        if (other.getCompanyId() != null)
            setCompanyId(other.getCompanyId());
        if (other.getUserId() != null)
            setUserId(other.getUserId());
        if (other.getUser() != null)
            setUser(other.getUser());
        if (other.getTimestamp() != null)
            setTimestamp(other.getTimestamp());
        if (other.getTransactionType() != null)
            setTransactionType(other.getTransactionType());
        if (other.getDescription() != null)
            setDescription(other.getDescription());
        if (other.getAmount() != null)
            setAmount(other.getAmount());
        if (other.hasNotes())
            setNotes(other.getNotes());
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
    public Transaction setId(final Integer id) {
        notNull(id, "Invalid null id");
        isTrue(id >= 0, "Invalid negative id");

        this.id = id;
        return this;
    }

    /**
     * @return the unique id of the company involved in this transaction
     */
    @XmlElement
    public Integer getCompanyId() {
        return this.companyId;
    }

    /**
     * @param companyId
     *            the new unique id of the company involved in this transaction
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided id value is invalid
     */
    public Transaction setCompanyId(final Integer companyId) {
        notNull(companyId, "Invalid null company id");
        isTrue(companyId >= 0, "Invalid negative company id");

        this.companyId = companyId;
        return this;
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
    public Transaction setUserId(final Integer userId) {
        notNull(userId, "Invalid null user id");
        isTrue(userId >= 0, "Invalid negative user id");

        this.userId = userId;
        return this;
    }

    /**
     * @return whether this transaction has an embedded user
     */
    public boolean hasUser() {
        return this.user != null;
    }

    /**
     * @return the user that invoked this transaction, possibly {@code null}
     */
    public User getUser() {
        return this.user;
    }

    /**
     * @param user
     *            the new user that invoked this transaction
     */
    public void setUser(final User user) {
        this.user = user == null ? null : new User(user);
    }

    /**
     * @return the time stamp indicating when this transaction took place
     */
    @XmlElement
    public Date getTimestamp() {
        return this.timestamp;
    }

    /**
     * @param timestamp
     *            the new value indicating when this transaction took place
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided time stamp value is invalid
     */
    public Transaction setTimestamp(final Date timestamp) {
        notNull(timestamp, "Invalid null timestamp value");

        this.timestamp = new Date(timestamp.getTime());
        return this;
    }

    /**
     * @return the type of transaction this object represents
     */
    @XmlElement
    public TransactionType getTransactionType() {
        return this.transactionType;
    }

    /**
     * @param transactionType
     *            the new value describing the type of this transaction
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided transaction type value is invalid
     */
    public Transaction setTransactionType(final TransactionType transactionType) {
        notNull(transactionType, "Invalid null transaction type value");

        this.transactionType = transactionType;
        return this;
    }

    /**
     * @return a description of the transaction
     */
    @XmlElement
    public String getDescription() {
        return this.description;
    }

    /**
     * @param description
     *            the new value describing this transaction
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided description value is invalid
     */
    public Transaction setDescription(final String description) {
        notEmpty(description, "Invalid blank description value");

        this.description = description;
        return this;
    }

    /**
     * @return the amount associated with this transaction
     */
    @XmlElement
    public BigDecimal getAmount() {
        return this.amount;
    }

    /**
     * @param amount
     *            the new amount associated with this transaction
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided amount value is invalid
     */
    public Transaction setAmount(final Float amount) {
        notNull(amount, "Invalid null amount");

        this.amount = new BigDecimal(amount).setScale(2);
        return this;
    }

    /**
     * @param amount
     *            the new amount associated with this transaction
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided amount value is invalid
     */
    public Transaction setAmount(final String amount) {
        notEmpty(amount, "Invalid empty amount");

        try {
            this.amount = new BigDecimal(amount).setScale(2);
        } catch (final NumberFormatException badNumber) {
            throw new IllegalArgumentException("Invalid number: " + amount, badNumber);
        }
        return this;
    }

    /**
     * @param amount
     *            the new amount associated with this transaction
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided amount value is invalid
     */
    public Transaction setAmount(final BigDecimal amount) {
        notNull(amount, "Invalid null amount");

        this.amount = new BigDecimal(amount.toPlainString()).setScale(2);
        return this;
    }

    /**
     * @return whether this transaction contains additional notes describing what took place
     */
    public boolean hasNotes() {
        return this.notes != null;
    }

    /**
     * @return the notes providing additional information that describes this transaction
     */
    @XmlElement
    public String getNotes() {
        return this.notes;
    }

    /**
     * @param notes
     *            the new notes value associated with this transaction
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided notes value is invalid
     */
    public Transaction setNotes(final String notes) {
        notEmpty(notes, "Invalid blank notes value");

        this.notes = notes;
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
        builder.append("timestamp", getTimestamp());
        builder.append("transactionType", getTransactionType());
        builder.append("description", getDescription());
        builder.append("amount", getAmount());
        builder.append("notes", getNotes());
        return builder.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        return (obj instanceof Transaction) ? compareTo((Transaction) obj) == 0 : false;
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
        builder.append(getTimestamp());
        builder.append(getTransactionType());
        builder.append(getDescription());
        builder.append(getAmount());
        builder.append(getNotes());
        return builder.toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(final Transaction other) {
        final CompareToBuilder builder = new CompareToBuilder();
        builder.append(other.getCompanyId(), getCompanyId());
        builder.append(getUserId(), other.getUserId());
        builder.append(other.getTimestamp(), getTimestamp());
        builder.append(getTransactionType(), other.getTransactionType());
        builder.append(getDescription(), other.getDescription());
        builder.append(getAmount(), other.getAmount());
        builder.append(getNotes(), other.getNotes());
        return builder.toComparison();
    }
}
