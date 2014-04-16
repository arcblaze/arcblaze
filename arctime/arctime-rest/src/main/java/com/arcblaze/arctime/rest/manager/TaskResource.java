package com.arcblaze.arctime.rest.manager;

import java.util.Set;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.arcblaze.arccore.common.config.Config;
import com.arcblaze.arccore.common.model.IdSet;
import com.arcblaze.arccore.common.model.User;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arccore.rest.BaseResource;
import com.arcblaze.arctime.common.model.Task;
import com.arcblaze.arctime.db.ArcTimeDaoFactory;
import com.codahale.metrics.Timer;

/**
 * The REST end-point for managing tasks.
 */
@Path("/manager/task")
public class TaskResource extends BaseResource {
	@XmlRootElement
	static class DeleteResponse {
		@XmlElement
		public final boolean success = true;

		@XmlElement
		public final String title = "User Deleted";

		@XmlElement
		public final String msg = "The specified tasks have been deleted "
				+ "successfully.";
	}

	@XmlRootElement
	static class AddResponse {
		@XmlElement
		public final boolean success = true;

		@XmlElement
		public final String msg = "The task was added successfully.";

		@XmlElement
		public Task task;
	}

	@XmlRootElement
	static class ActivateResponse {
		@XmlElement
		public final boolean success = true;

		@XmlElement
		public final String msg = "The specified tasks were activated successfully.";
	}

	@XmlRootElement
	static class DeactivateResponse {
		@XmlElement
		public final boolean success = true;

		@XmlElement
		public final String msg = "The specified tasks were deactivated successfully.";
	}

	@XmlRootElement
	static class UpdateResponse {
		@XmlElement
		public final boolean success = true;

		@XmlElement
		public final String msg = "The task was modified successfully.";

		@XmlElement
		public Task task;
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
	 * @param taskId
	 *            the unique id of the task to retrieve
	 * 
	 * @return the requested task
	 */
	@GET
	@Path("{taskId:\\d+}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Task get(@Context final SecurityContext security,
			@Context final Config config,
			@Context final ArcTimeDaoFactory daoFactory,
			@Context final Timer timer,
			@PathParam("taskId") final Integer taskId) {
		final User currentUser = (User) security.getUserPrincipal();
		try (final Timer.Context timerContext = timer.time()) {
			if (taskId == null)
				throw badRequest("A task id must be provided.");
			return daoFactory.getTaskDao().get(currentUser.getCompanyId(),
					taskId);
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
	 * 
	 * @return all of the available tasks in the same company as the current
	 *         user
	 */
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Set<Task> all(@Context final SecurityContext security,
			@Context final Config config,
			@Context final ArcTimeDaoFactory daoFactory,
			@Context final Timer timer) {
		final User currentUser = (User) security.getUserPrincipal();
		try (final Timer.Context timerContext = timer.time()) {
			return daoFactory.getTaskDao().getAll(currentUser.getCompanyId());
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
	 * @param task
	 *            the task to add to the back-end database
	 * 
	 * @return the new task that was added
	 */
	@POST
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public AddResponse add(@Context final SecurityContext security,
			@Context final Config config,
			@Context final ArcTimeDaoFactory daoFactory,
			@Context final Timer timer, @Context final Task task) {
		final User currentUser = (User) security.getUserPrincipal();
		try (final Timer.Context timerContext = timer.time()) {
			if (task == null)
				throw badRequest("A task must be provided.");
			task.setCompanyId(currentUser.getCompanyId());
			daoFactory.getTaskDao().add(task);
			final AddResponse response = new AddResponse();
			response.task = task;
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
	 * @param taskIds
	 *            the unique ids of the tasks to make active
	 * 
	 * @return an activate response
	 */
	@PUT
	@Path("/activate")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public ActivateResponse activate(@Context final SecurityContext security,
			@Context final Config config,
			@Context final ArcTimeDaoFactory daoFactory,
			@Context final Timer timer, @HeaderParam("ids") final IdSet taskIds) {
		final User currentUser = (User) security.getUserPrincipal();
		try (final Timer.Context timerContext = timer.time()) {
			if (taskIds == null || taskIds.isEmpty())
				throw badRequest("No user ids provided");
			daoFactory.getTaskDao().activate(currentUser.getCompanyId(),
					taskIds);
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
	 * @param taskIds
	 *            the unique ids of the tasks to make inactive
	 * 
	 * @return a deactivate response
	 */
	@PUT
	@Path("/deactivate")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public DeactivateResponse deactivate(
			@Context final SecurityContext security,
			@Context final Config config,
			@Context final ArcTimeDaoFactory daoFactory,
			@Context final Timer timer, @HeaderParam("ids") final IdSet taskIds) {
		final User currentUser = (User) security.getUserPrincipal();
		try (final Timer.Context timerContext = timer.time()) {
			if (taskIds == null || taskIds.isEmpty())
				throw badRequest("No user ids provided");
			daoFactory.getTaskDao().deactivate(currentUser.getCompanyId(),
					taskIds);
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
	 * @param task
	 *            the task to modify in the back-end database
	 * 
	 * @return the new task that was modified
	 */
	@PUT
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public UpdateResponse update(@Context final SecurityContext security,
			@Context final Config config,
			@Context final ArcTimeDaoFactory daoFactory,
			@Context final Timer timer, @Context final Task task) {
		final User currentUser = (User) security.getUserPrincipal();
		try (final Timer.Context timerContext = timer.time()) {
			if (task == null || task.getId() == null)
				throw badRequest("A user with id must be provided.");
			task.setCompanyId(currentUser.getCompanyId());
			daoFactory.getTaskDao().update(task);
			final UpdateResponse response = new UpdateResponse();
			response.task = task;
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
	 * @param taskIds
	 *            the unique ids of the tasks to delete
	 * 
	 * @return a delete response
	 */
	@DELETE
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public DeleteResponse delete(@Context final SecurityContext security,
			@Context final Config config,
			@Context final ArcTimeDaoFactory daoFactory,
			@Context final Timer timer, @HeaderParam("ids") final IdSet taskIds) {
		final User currentUser = (User) security.getUserPrincipal();
		try (final Timer.Context timerContext = timer.time()) {
			if (taskIds == null || taskIds.isEmpty())
				throw badRequest("No user ids provided");
			daoFactory.getTaskDao().delete(currentUser.getCompanyId(), taskIds);
			return new DeleteResponse();
		} catch (final DatabaseException dbException) {
			throw dbError(config, currentUser, dbException);
		}
	}
}
