package com.arcblaze.arccore.common.model;

import static org.apache.commons.lang.Validate.isTrue;
import static org.apache.commons.lang.Validate.notEmpty;
import static org.apache.commons.lang.Validate.notNull;

import java.security.Principal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Represents a user of this system.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.PROPERTY)
public class User implements Comparable<User>, Principal {
    /**
     * The unique id of the user.
     */
    private Integer id;

    /**
     * The unique id of the company for which this user account was created.
     */
    private Integer companyId;

    /**
     * The login name for the user.
     */
    private String login;

    /**
     * The hashed password value for the user.
     */
    private String hashedPass;

    /**
     * The salt value used when hashing the user's password.
     */
    private String salt;

    /**
     * The user's email address.
     */
    private String email;

    /**
     * The user's first name.
     */
    private String firstName;

    /**
     * The user's last name.
     */
    private String lastName;

    /**
     * Whether this user is an active account or not.
     */
    private Boolean active = true;

    /**
     * The roles assigned to the account.
     */
    private final Set<Role> roles = new TreeSet<>();

    /**
     * Default constructor.
     */
    public User() {
        // Nothing to do.
    }

    /**
     * @param other
     *            the user to copy
     * 
     * @throws IllegalArgumentException
     *             if the provided name value is invalid
     */
    public User(final User other) {
        notNull(other, "Invalid null user");
        if (other.getId() != null)
            setId(other.getId());
        if (other.getCompanyId() != null)
            setCompanyId(other.getCompanyId());
        if (other.getLogin() != null)
            setLogin(other.getLogin());
        if (other.getHashedPass() != null)
            setHashedPass(other.getHashedPass());
        if (other.getSalt() != null)
            setSalt(other.getSalt());
        if (other.getEmail() != null)
            setEmail(other.getEmail());
        if (other.getFirstName() != null)
            setFirstName(other.getFirstName());
        if (other.getLastName() != null)
            setLastName(other.getLastName());
        if (other.isActive() != null)
            setActive(other.isActive());
        setRoles(other.getRoles());
    }

    /**
     * @return the unique id of the user
     */
    @XmlElement
    public Integer getId() {
        return this.id;
    }

    /**
     * @param id
     *            the new unique user id value
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided id value is invalid
     */
    public User setId(final Integer id) {
        notNull(id, "Invalid null id");
        isTrue(id >= 0, "Invalid negative id");

        this.id = id;
        return this;
    }

    /**
     * @return the unique id of the company for which this user was created
     */
    @XmlElement
    public Integer getCompanyId() {
        return this.companyId;
    }

    /**
     * @param companyId
     *            the new unique id of the company for which this user was created
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided id value is invalid
     */
    public User setCompanyId(final Integer companyId) {
        notNull(companyId, "Invalid null company id");
        isTrue(companyId >= 0, "Invalid negative company id");

        this.companyId = companyId;
        return this;
    }

    /**
     * @return the user login value
     */
    @Override
    @XmlTransient
    public String getName() {
        return getLogin();
    }

    /**
     * @return the user login value
     */
    @XmlElement
    public String getLogin() {
        return this.login;
    }

    /**
     * @param login
     *            the new user login value
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided login value is invalid
     */
    public User setLogin(final String login) {
        notEmpty(login, "Invalid blank login");

        this.login = StringUtils.trim(login);
        return this;
    }

    /**
     * @return the hashed value of the user's password
     */
    @XmlElement
    public String getHashedPass() {
        return this.hashedPass;
    }

    /**
     * @param hashedPass
     *            the new hashed value of the user's password
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided hashed password value is invalid
     */
    public User setHashedPass(final String hashedPass) {
        notEmpty(hashedPass, "Invalid blank hashed password");

        this.hashedPass = StringUtils.trim(hashedPass);
        return this;
    }

    /**
     * @return the password salt value used when hashing the user's password
     */
    @XmlElement
    public String getSalt() {
        return this.salt;
    }

    /**
     * @param salt
     *            the new password salt value used when hashing the user's password
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided password salt value is invalid
     */
    public User setSalt(final String salt) {
        notEmpty(salt, "Invalid blank salt");

        this.salt = StringUtils.trim(salt);
        return this;
    }

    /**
     * @return the user's email address
     */
    @XmlElement
    public String getEmail() {
        return this.email;
    }

    /**
     * @param email
     *            the new email address for the user
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided email value is invalid
     */
    public User setEmail(final String email) {
        notEmpty(email, "Invalid blank email");

        this.email = StringUtils.trim(email);
        return this;
    }

    /**
     * @return the user's first name
     */
    @XmlElement
    public String getFirstName() {
        return this.firstName;
    }

    /**
     * @param firstName
     *            the new first name value for the user
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided first name value is invalid
     */
    public User setFirstName(final String firstName) {
        notEmpty(firstName, "Invalid blank first name");

        this.firstName = StringUtils.trim(firstName);
        return this;
    }

    /**
     * @return the user's last name
     */
    @XmlElement
    public String getLastName() {
        return this.lastName;
    }

    /**
     * @param lastName
     *            the new last name value for the user
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided last name value is invalid
     */
    public User setLastName(final String lastName) {
        notEmpty(lastName, "Invalid blank last name");

        this.lastName = StringUtils.trim(lastName);
        return this;
    }

    /**
     * @return the full name of the user
     */
    @XmlElement
    public String getFullName() {
        final StringBuilder name = new StringBuilder();
        name.append(getFirstName());
        name.append(" ");
        name.append(getLastName());
        return name.toString();
    }

    /**
     * @return whether this user represents an active account in the system
     */
    @XmlElement
    public Boolean isActive() {
        return this.active;
    }

    /**
     * @param active
     *            the new value indicating whether this is an active account in the system
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided active value is invalid
     */
    public User setActive(final Boolean active) {
        notNull(active, "Invalid null active value");

        this.active = active;
        return this;
    }

    /**
     * @return the privileges available to the user, based on the roles
     */
    @XmlElement
    public String getPrivileges() {
        final Set<Character> privs = new TreeSet<>();
        for (final Role role : getRoles())
            privs.add(role.getName().charAt(0));
        return StringUtils.join(privs, " ");
    }

    /**
     * @return all of the roles authorized for this account
     */
    @XmlElementWrapper
    @XmlElement(name = "role")
    public Set<Role> getRoles() {
        return Collections.unmodifiableSet(this.roles);
    }

    /**
     * @param newRoles
     *            the new role values to be assigned to this account
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided roles value is invalid
     */
    public User setRoles(final Role... newRoles) {
        notNull(newRoles, "Invalid null roles");

        return this.setRoles(Arrays.asList(newRoles));
    }

    /**
     * @param newRoles
     *            the new role values to be assigned to this account
     * 
     * @return {@code this}
     */
    public User setRoles(final Collection<Role> newRoles) {
        // Prevent clearing the provided collection.
        if (this.roles != newRoles)
            this.roles.clear();
        if (newRoles != null) {
            for (final Role role : newRoles)
                if (role != null)
                    this.roles.add(new Role(role)); // Defensive copy
        }
        return this;
    }

    /**
     * @param newRoles
     *            the new role values to be assigned to this account
     * 
     * @return {@code this}
     * 
     * @throws IllegalArgumentException
     *             if the provided roles value is invalid
     */
    public User addRoles(final Role... newRoles) {
        notNull(newRoles, "Invalid null roles");

        return this.addRoles(Arrays.asList(newRoles));
    }

    /**
     * @param newRoles
     *            the new role values to be assigned to this account
     * 
     * @return {@code this}
     */
    public User addRoles(final Collection<Role> newRoles) {
        if (newRoles != null) {
            for (final Role role : newRoles)
                if (role != null)
                    this.roles.add(new Role(role)); // Defensive copy
        }
        return this;
    }

    /**
     * @return {@code this}
     */
    public User clearRoles() {
        this.roles.clear();
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
        builder.append("login", getLogin());
        builder.append("hashedPass", getHashedPass());
        builder.append("salt", getSalt());
        builder.append("email", getEmail());
        builder.append("firstName", getFirstName());
        builder.append("lastName", getLastName());
        builder.append("active", isActive());
        builder.append("roles", StringUtils.join(getRoles(), ","));
        return builder.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object obj) {
        return (obj instanceof User) ? compareTo((User) obj) == 0 : false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(getId());
        builder.append(getCompanyId());
        builder.append(getLogin());
        // HashedPass and Salt specifically left out.
        builder.append(getEmail());
        builder.append(getFirstName());
        builder.append(getLastName());
        builder.append(isActive());
        return builder.toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(final User other) {
        final CompareToBuilder builder = new CompareToBuilder();
        builder.append(getCompanyId(), other.getCompanyId());
        builder.append(other.isActive(), isActive());
        builder.append(getLastName(), other.getLastName());
        builder.append(getFirstName(), other.getFirstName());
        builder.append(getLogin(), other.getLogin());
        builder.append(getId(), other.getId());
        builder.append(getEmail(), other.getEmail());
        // HashedPass and Salt specifically left out.
        return builder.toComparison();
    }
}
