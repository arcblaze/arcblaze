package com.arcblaze.arctime.rest.factory;

import java.util.Set;
import java.util.TreeSet;

import javax.ws.rs.FormParam;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.glassfish.hk2.utilities.binding.AbstractBinder;

import com.arcblaze.arccore.common.model.Password;
import com.arcblaze.arccore.common.model.Role;
import com.arcblaze.arccore.common.model.User;
import com.arcblaze.arccore.rest.factory.BaseFactory;

/**
 * Provides access to {@link User} objects within the REST resource classes,
 * based on parameters from the web request.
 */
public class UserFactory extends BaseFactory<User> {
	private final Integer id;
	private final Integer companyId;
	private final String login;
	private final String password;
	private final String confirm;
	private final String email;
	private final String firstName;
	private final String lastName;
	private final Boolean active;
	private final Set<Role> roles = new TreeSet<>();

	/**
	 * @param id
	 *            the unique id of the user
	 * @param companyId
	 *            the unique id of the company associated with the user
	 * @param login
	 *            the unique login of the user
	 * @param password
	 *            the new user's password
	 * @param confirm
	 *            a confirmation of the user's password
	 * @param email
	 *            the user's email address
	 * @param firstName
	 *            the first name of the user
	 * @param lastName
	 *            the last name of the user
	 * @param active
	 *            whether the user is active or not
	 * @param roles
	 *            the roles to include in the user
	 */
	public UserFactory(@FormParam("id") final Integer id,
			@FormParam("companyId") final Integer companyId,
			@FormParam("login") final String login,
			@FormParam("password") final String password,
			@FormParam("confirm") final String confirm,
			@FormParam("email") final String email,
			@FormParam("firstName") final String firstName,
			@FormParam("lastName") final String lastName,
			@FormParam("active") final Boolean active,
			@FormParam("roles") final Set<String> roles) {
		this.id = id;
		this.companyId = companyId;
		this.login = StringEscapeUtils.escapeHtml(login);
		this.password = StringEscapeUtils.escapeHtml(password);
		this.confirm = StringEscapeUtils.escapeHtml(confirm);
		this.email = StringEscapeUtils.escapeHtml(email);
		this.firstName = StringEscapeUtils.escapeHtml(firstName);
		this.lastName = StringEscapeUtils.escapeHtml(lastName);
		this.active = active;

		if (roles != null) {
			for (final String role : roles)
				this.roles.add(new Role(role));
		}

		if (!StringUtils.equals(this.password, this.confirm))
			throw new IllegalArgumentException(
					"Provided passwords do not match.");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public User provide() {
		final User user = new User();
		if (this.id != null)
			user.setId(this.id);
		if (this.companyId != null)
			user.setCompanyId(this.companyId);
		if (StringUtils.isNotBlank(this.login))
			user.setLogin(this.login);
		if (StringUtils.isNotBlank(this.password)) {
			final Password passwd = new Password();
			final String salt = passwd.random(8);
			final String hashedPass = passwd.hash(this.password, salt);
			user.setHashedPass(hashedPass);
			user.setSalt(salt);
		}
		if (StringUtils.isNotBlank(this.email))
			user.setEmail(this.email);
		if (StringUtils.isNotBlank(this.firstName))
			user.setFirstName(this.firstName);
		if (StringUtils.isNotBlank(this.lastName))
			user.setLastName(this.lastName);
		if (this.active != null)
			user.setActive(this.active);
		user.addRoles(this.roles);
		return user;
	}

	/**
	 * @return a binder that can register this factory
	 */
	public static AbstractBinder getBinder() {
		return new AbstractBinder() {
			@Override
			protected void configure() {
				bindFactory(UserFactory.class).to(User.class);
			}
		};
	}
}
