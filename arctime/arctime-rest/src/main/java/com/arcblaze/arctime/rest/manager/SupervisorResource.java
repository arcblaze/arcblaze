package com.arcblaze.arctime.rest.manager;

import java.util.Set;

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.arcblaze.arccore.common.config.Config;
import com.arcblaze.arccore.common.model.User;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arccore.rest.BaseResource;
import com.arcblaze.arctime.common.model.Supervisor;
import com.arcblaze.arctime.db.ArcTimeDaoFactory;
import com.codahale.metrics.Timer;

/**
 * The REST end-point for managing supervisors.
 */
@Path("/manager/supervisor")
public class SupervisorResource extends BaseResource {
	@XmlRootElement
	static class DeleteResponse {
		@XmlElement
		public final boolean success = true;

		@XmlElement
		public final String title = "Supervisor Deleted";

		@XmlElement
		public final String msg = "The specified supervisors have been deleted "
				+ "successfully.";
	}

	@XmlRootElement
	static class AddResponse {
		@XmlElement
		public final boolean success = true;

		@XmlElement
		public final String msg = "The supervisor was added successfully.";
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
	 *            the unique id of the user for which supervisors will be
	 *            retrieved
	 * 
	 * @return the requested supervisors (if in the same company as the current
	 *         user)
	 */
	@GET
	@Path("{userId:\\d+}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Set<Supervisor> get(@Context final SecurityContext security,
			@Context final Config config,
			@Context final ArcTimeDaoFactory daoFactory,
			@Context final Timer timer,
			@PathParam("userId") final Integer userId) {
		final User currentUser = (User) security.getUserPrincipal();
		try (final Timer.Context timerContext = timer.time()) {
			if (userId == null)
				throw badRequest("A user id must be provided.");
			return daoFactory.getSupervisorDao().getSupervisors(
					currentUser.getCompanyId(), userId);
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
	 * @param userId
	 *            the unique id of the user for which a supervisor will be added
	 * @param supervisorId
	 *            the unique id of the user becoming a supervisor
	 * @param primary
	 *            whether the supervisor should be a primary supervisor
	 * 
	 * @return the add response indicating success
	 */
	@POST
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public AddResponse add(@Context final SecurityContext security,
			@Context final Config config,
			@Context final ArcTimeDaoFactory daoFactory,
			@Context final Timer timer,
			@FormParam("userId") final Integer userId,
			@FormParam("supervisorId") final Integer supervisorId,
			@FormParam("primary") final Boolean primary) {
		final User currentUser = (User) security.getUserPrincipal();
		try (final Timer.Context timerContext = timer.time()) {
			if (userId == null)
				throw badRequest("A user id must be provided.");
			if (supervisorId == null)
				throw badRequest("A supervisor id must be provided.");
			if (primary == null)
				throw badRequest("The primary value must be provided.");
			daoFactory.getSupervisorDao().add(currentUser.getCompanyId(),
					userId, primary, supervisorId);
			return new AddResponse();
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
	 * @param userId
	 *            the unique id of the user for which supervisors will be
	 *            deleted
	 * @param supervisorIds
	 *            the unique ids of the supervisors to remove
	 * 
	 * @return the delete response indicating success
	 */
	@DELETE
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public DeleteResponse delete(@Context final SecurityContext security,
			@Context final Config config,
			@Context final ArcTimeDaoFactory daoFactory,
			@Context final Timer timer,
			@HeaderParam("userId") final Integer userId,
			@HeaderParam("supervisorIds") final Set<Integer> supervisorIds) {
		final User currentUser = (User) security.getUserPrincipal();
		try (final Timer.Context timerContext = timer.time()) {
			if (userId == null)
				throw badRequest("A user id must be provided.");
			if (supervisorIds == null)
				throw badRequest("Supervisor ids must be provided.");
			daoFactory.getSupervisorDao().delete(currentUser.getCompanyId(),
					userId, supervisorIds);
			return new DeleteResponse();
		} catch (final DatabaseException dbException) {
			throw dbError(config, currentUser, dbException);
		}
	}
}
