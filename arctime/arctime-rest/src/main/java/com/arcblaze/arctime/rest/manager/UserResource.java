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
	static class AllResponse {
		@XmlElement
		public final boolean success = true;

		@XmlElement
		public final String msg = "The users were retrieved successfully.";

		@XmlElement
		public Set<User> users;

		@XmlElement
		public Integer offset;

		@XmlElement
		public Integer limit;

		@XmlElement
		public Integer total;
	}

	@XmlRootElement
	static class AddResponse {
		@XmlElement
		public boolean success = true;

		@XmlElement
		public String msg = "The user was added successfully.";

		@XmlElement
		public User user;
	}

	@XmlRootElement
	static class ActivateResponse {
		@XmlElement
		public boolean success = true;

		@XmlElement
		public String msg = "The specified users were activated successfully.";
	}

	@XmlRootElement
	static class DeactivateResponse {
		@XmlElement
		public boolean success = true;

		@XmlElement
		public String msg = "The specified users were deactivated successfully.";
	}

	@XmlRootElement
	static class UpdateResponse {
		@XmlElement
		public boolean success = true;

		@XmlElement
		public String msg = "The user was saved successfully.";

		@XmlElement
		public User user;
	}

	@XmlRootElement
	static class DeleteResponse {
		@XmlElement
		public boolean success = true;

		@XmlElement
		public String title = "User Deleted";

		@XmlElement
		public String msg = "The specified users have been deleted "
				+ "successfully.";
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
	 * @param filter
	 *            the search filter to use to restrict results
	 * @param includeInactive
	 *            whether inactive user accounts should be included in the
	 *            response
	 * @param limit
	 *            the maximum number of items to be retrieved
	 * @param offset
	 *            the offset into the items to be retrieved
	 * 
	 * @return all of the available users in the same company as the current
	 *         user
	 */
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public AllResponse all(
			@Context final SecurityContext security,
			@Context final Config config,
			@Context final ArcTimeDaoFactory daoFactory,
			@Context final Timer timer,
			@QueryParam("filter") final String filter,
			@QueryParam("includeInactive") @DefaultValue("true") final Boolean includeInactive,
			@QueryParam("limit") @DefaultValue("100") final Integer limit,
			@QueryParam("start") @DefaultValue("0") final Integer offset) {
		final User currentUser = (User) security.getUserPrincipal();
		try (final Timer.Context timerContext = timer.time()) {
			final AllResponse response = new AllResponse();
			response.users = daoFactory.getUserDao().getAll(
					currentUser.getCompanyId(), includeInactive, limit, offset);
			daoFactory.getRoleDao().populateUsers(response.users);
			response.total = daoFactory.getUserDao().count(filter,
					includeInactive);
			response.limit = limit;
			response.offset = offset;
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
			int added = daoFactory.getUserDao().add(user);

			final Set<Role> roles = new TreeSet<>(user.getRoles());
			roles.retainAll(currentUser.getRoles());
			user.setRoles(roles);
			added += daoFactory.getRoleDao().add(user.getId(), user.getRoles());

			final AddResponse response = new AddResponse();
			if (added == 0) {
				response.success = false;
				response.msg = "Failed to add new user.";
			} else
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
			final int updated = daoFactory.getUserDao().activate(
					currentUser.getCompanyId(), userIds);
			final ActivateResponse response = new ActivateResponse();
			if (updated == 0) {
				response.success = false;
				response.msg = "Failed to deactivate specified users.";
			}
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
			final int updated = daoFactory.getUserDao().deactivate(
					currentUser.getCompanyId(), userIds);
			final DeactivateResponse response = new DeactivateResponse();
			if (updated == 0) {
				response.success = false;
				response.msg = "Failed to deactivate specified users.";
			}
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
			int updated = daoFactory.getUserDao().update(user);
			if (StringUtils.isNotBlank(user.getHashedPass()))
				updated += daoFactory.getUserDao().setPassword(user.getId(),
						user.getHashedPass(), user.getSalt());

			final Set<Role> rolesToAdd = new TreeSet<>(user.getRoles());
			rolesToAdd.retainAll(currentUser.getRoles());
			user.setRoles(rolesToAdd);
			updated += daoFactory.getRoleDao().delete(user.getId(),
					currentUser.getRoles());
			updated += daoFactory.getRoleDao().add(user.getId(),
					user.getRoles());

			final UpdateResponse response = new UpdateResponse();
			if (updated == 0) {
				response.success = false;
				response.msg = "Failed to update the specified user.";
			} else
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
			final int deleted = daoFactory.getUserDao().delete(
					currentUser.getCompanyId(), userIds);
			final DeleteResponse response = new DeleteResponse();
			if (deleted == 0) {
				response.success = false;
				response.msg = "Failed to delete specified users.";
			}
			return response;
		} catch (final DatabaseException dbException) {
			throw dbError(config, currentUser, dbException);
		}
	}
}
