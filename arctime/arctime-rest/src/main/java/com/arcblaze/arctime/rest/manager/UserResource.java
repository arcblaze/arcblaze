package com.arcblaze.arctime.rest.manager;

import java.util.Set;
import java.util.TreeSet;

import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;

import com.arcblaze.arccore.common.config.Config;
import com.arcblaze.arccore.common.model.IdSet;
import com.arcblaze.arccore.common.model.Role;
import com.arcblaze.arccore.common.model.User;
import com.arcblaze.arccore.db.DaoFactory;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arccore.rest.BaseResource;
import com.arcblaze.arctime.db.ArcTimeDaoFactory;
import com.codahale.metrics.Timer;

/**
 * The REST end-point for managing users.
 */
@Path("/manager/user")
public class UserResource extends BaseResource {
	@XmlRootElement
	static class DeleteResponse {
		@XmlElement
		public final boolean success = true;

		@XmlElement
		public final String title = "User Deleted";

		@XmlElement
		public final String msg = "The specified users have been deleted "
				+ "successfully.";
	}

	@XmlRootElement
	static class AddResponse {
		@XmlElement
		public final boolean success = true;

		@XmlElement
		public final String msg = "The user was added successfully.";

		@XmlElement
		public User user;
	}

	@XmlRootElement
	static class ActivateResponse {
		@XmlElement
		public final boolean success = true;

		@XmlElement
		public final String msg = "The specified users were activated successfully.";
	}

	@XmlRootElement
	static class DeactivateResponse {
		@XmlElement
		public final boolean success = true;

		@XmlElement
		public final String msg = "The specified users were deactivated successfully.";
	}

	@XmlRootElement
	static class UpdateResponse {
		@XmlElement
		public final boolean success = true;

		@XmlElement
		public final String msg = "The user was modified successfully.";

		@XmlElement
		public User user;
	}

	/**
	 * @param security
	 *            the security information associated with the request
	 * @param config
	 *            the system configuration properties
	 * @param daoFactory
	 *            used to communicate with the back-end database
	 * @param timer
	 *            tracks performance metrics of this REST end-point
	 * @param userId
	 *            the unique id of the user to retrieve
	 * 
	 * @return the requested user (if in the same company as the current user)
	 */
	@GET
	@Path("{userId:\\d+}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public User get(@Context final SecurityContext security,
			@Context final Config config,
			@Context final ArcTimeDaoFactory daoFactory,
			@Context final Timer timer,
			@PathParam("userId") final Integer userId) {
		final User currentUser = (User) security.getUserPrincipal();
		try (final Timer.Context timerContext = timer.time()) {
			if (userId == null)
				throw badRequest("A user id must be provided.");
			return daoFactory.getUserDao().get(currentUser.getCompanyId(),
					userId);
		} catch (final DatabaseException dbException) {
			throw dbError(config, currentUser, dbException);
		}
	}

	/**
	 * @param security
	 *            the security information associated with the request
	 * @param config
	 *            the system configuration properties
	 * @param daoFactory
	 *            used to communicate with the back-end database
	 * @param timer
	 *            tracks performance metrics of this REST end-point
	 * @param includeInactive
	 *            whether inactive user accounts should be included in the
	 *            response
	 * @param filterMe
	 *            whether the current user should be excluded from the returned
	 *            results
	 * 
	 * @return all of the available users in the same company as the current
	 *         user
	 */
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Set<User> all(
			@Context final SecurityContext security,
			@Context final Config config,
			@Context final ArcTimeDaoFactory daoFactory,
			@Context final Timer timer,
			@QueryParam("includeInactive") @DefaultValue("true") final Boolean includeInactive,
			@QueryParam("filterMe") @DefaultValue("false") final Boolean filterMe) {
		final User currentUser = (User) security.getUserPrincipal();
		try (final Timer.Context timerContext = timer.time()) {
			final Set<User> users = daoFactory.getUserDao().getAll(
					currentUser.getCompanyId(), includeInactive);
			if (filterMe)
				users.remove(currentUser);
			daoFactory.getRoleDao().populateUsers(users);
			return users;
		} catch (final DatabaseException dbException) {
			throw dbError(config, currentUser, dbException);
		}
	}

	/**
	 * @param security
	 *            the security information associated with the request
	 * @param config
	 *            the system configuration properties
	 * @param daoFactory
	 *            used to communicate with the back-end database
	 * @param timer
	 *            tracks performance metrics of this REST end-point
	 * @param user
	 *            the user to add to the back-end database
	 * 
	 * @return the new user that was added
	 */
	@POST
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public AddResponse add(@Context final SecurityContext security,
			@Context final Config config, @Context final DaoFactory daoFactory,
			@Context final Timer timer, @Context final User user) {
		final User currentUser = (User) security.getUserPrincipal();
		try (final Timer.Context timerContext = timer.time()) {
			if (user == null)
				throw badRequest("A user must be provided.");
			user.setCompanyId(currentUser.getCompanyId());
			daoFactory.getUserDao().add(user);

			final Set<Role> roles = new TreeSet<>(user.getRoles());
			roles.retainAll(currentUser.getRoles());
			user.setRoles(roles);
			daoFactory.getRoleDao().add(user.getId(), user.getRoles());

			final AddResponse response = new AddResponse();
			response.user = user;
			return response;
		} catch (final DatabaseException dbException) {
			throw dbError(config, currentUser, dbException);
		}
	}

	/**
	 * @param security
	 *            the security information associated with the request
	 * @param config
	 *            the system configuration properties
	 * @param daoFactory
	 *            used to communicate with the back-end database
	 * @param timer
	 *            tracks performance metrics of this REST end-point
	 * @param userIds
	 *            the unique ids of the users to make active
	 * 
	 * @return an activate response
	 */
	@PUT
	@Path("/activate")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public ActivateResponse activate(@Context final SecurityContext security,
			@Context final Config config, @Context final DaoFactory daoFactory,
			@Context final Timer timer, @HeaderParam("ids") final IdSet userIds) {
		final User currentUser = (User) security.getUserPrincipal();
		try (final Timer.Context timerContext = timer.time()) {
			if (userIds == null || userIds.isEmpty())
				throw badRequest("No user ids provided");
			daoFactory.getUserDao().activate(currentUser.getCompanyId(),
					userIds);
			return new ActivateResponse();
		} catch (final DatabaseException dbException) {
			throw dbError(config, currentUser, dbException);
		}
	}

	/**
	 * @param security
	 *            the security information associated with the request
	 * @param config
	 *            the system configuration properties
	 * @param daoFactory
	 *            used to communicate with the back-end database
	 * @param timer
	 *            tracks performance metrics of this REST end-point
	 * @param userIds
	 *            the unique ids of the users to make inactive
	 * 
	 * @return a deactivate response
	 */
	@PUT
	@Path("/deactivate")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public DeactivateResponse deactivate(
			@Context final SecurityContext security,
			@Context final Config config, @Context final DaoFactory daoFactory,
			@Context final Timer timer, @HeaderParam("ids") final IdSet userIds) {
		final User currentUser = (User) security.getUserPrincipal();
		try (final Timer.Context timerContext = timer.time()) {
			if (userIds == null || userIds.isEmpty())
				throw badRequest("No user ids provided");
			daoFactory.getUserDao().deactivate(currentUser.getCompanyId(),
					userIds);
			return new DeactivateResponse();
		} catch (final DatabaseException dbException) {
			throw dbError(config, currentUser, dbException);
		}
	}

	/**
	 * @param security
	 *            the security information associated with the request
	 * @param config
	 *            the system configuration properties
	 * @param daoFactory
	 *            used to communicate with the back-end database
	 * @param timer
	 *            tracks performance metrics of this REST end-point
	 * @param user
	 *            the user to update in the back-end database
	 * 
	 * @return the new user that was updated
	 */
	@PUT
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public UpdateResponse update(@Context final SecurityContext security,
			@Context final Config config, @Context final DaoFactory daoFactory,
			@Context final Timer timer, @Context final User user) {
		final User currentUser = (User) security.getUserPrincipal();
		try (final Timer.Context timerContext = timer.time()) {
			if (user == null || user.getId() == null)
				throw badRequest("A user with id must be provided.");
			user.setCompanyId(currentUser.getCompanyId());
			daoFactory.getUserDao().update(user);
			if (StringUtils.isNotBlank(user.getHashedPass()))
				daoFactory.getUserDao().setPassword(user.getId(),
						user.getHashedPass(), user.getSalt());

			final Set<Role> rolesToAdd = new TreeSet<>(user.getRoles());
			rolesToAdd.retainAll(currentUser.getRoles());
			user.setRoles(rolesToAdd);
			daoFactory.getRoleDao()
					.delete(user.getId(), currentUser.getRoles());
			daoFactory.getRoleDao().add(user.getId(), user.getRoles());

			final UpdateResponse response = new UpdateResponse();
			response.user = user;
			return response;
		} catch (final DatabaseException dbException) {
			throw dbError(config, currentUser, dbException);
		}
	}

	/**
	 * @param security
	 *            the security information associated with the request
	 * @param config
	 *            the system configuration properties
	 * @param daoFactory
	 *            used to communicate with the back-end database
	 * @param timer
	 *            tracks performance metrics of this REST end-point
	 * @param userIds
	 *            the unique ids of the users to delete
	 * 
	 * @return a delete response
	 */
	@DELETE
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public DeleteResponse delete(@Context final SecurityContext security,
			@Context final Config config, @Context final DaoFactory daoFactory,
			@Context final Timer timer, @HeaderParam("ids") final IdSet userIds) {
		final User currentUser = (User) security.getUserPrincipal();
		try (final Timer.Context timerContext = timer.time()) {
			if (userIds == null || userIds.isEmpty())
				throw badRequest("No user ids provided");
			daoFactory.getUserDao().delete(currentUser.getCompanyId(), userIds);
			return new DeleteResponse();
		} catch (final DatabaseException dbException) {
			throw dbError(config, currentUser, dbException);
		}
	}
}
