package com.arcblaze.arctime.rest.manager;

import java.util.Set;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import com.arcblaze.arccore.common.model.User;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arccore.rest.BaseResource;
import com.arcblaze.arctime.common.model.Task;
import com.arcblaze.arctime.db.ArcTimeDaoFactory;
import com.arcblaze.arctime.db.dao.TaskDao;
import com.codahale.metrics.Timer;

/**
 * The REST end-point for managing tasks.
 */
@Path("/manager/task")
public class TaskResource extends BaseResource {
	@Context
	private ServletContext servletContext;

	/**
	 * @param security
	 *            the security information associated with the request
	 * @param daoFactory
	 *            used to communicate with the back-end database
	 * @param timer
	 *            tracks performance metrics of this REST end-point
	 * @param taskId
	 *            the unique id of the task to retrieve
	 * 
	 * @return the requested task (if in the same company as the current user)
	 * 
	 * @throws DatabaseException
	 *             if there is an error communicating with the back-end
	 */
	@GET
	@Path("{taskId}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Task one(@Context final SecurityContext security,
			@Context final ArcTimeDaoFactory daoFactory,
			@Context final Timer timer,
			@PathParam("taskId") final Integer taskId) throws DatabaseException {
		try (final Timer.Context timerContext = timer.time()) {
			final User currentUser = (User) security.getUserPrincipal();
			final TaskDao dao = daoFactory.getTaskDao();
			return dao.get(currentUser.getCompanyId(), taskId);
		}
	}

	/**
	 * @param security
	 *            the security information associated with the request
	 * @param daoFactory
	 *            used to communicate with the back-end database
	 * @param timer
	 *            tracks performance metrics of this REST end-point
	 * 
	 * @return all of the available tasks in the same company as the current
	 *         user
	 * 
	 * @throws DatabaseException
	 *             if there is an error communicating with the back-end
	 */
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Set<Task> all(@Context final SecurityContext security,
			@Context final ArcTimeDaoFactory daoFactory,
			@Context final Timer timer) throws DatabaseException {
		try (final Timer.Context timerContext = timer.time()) {
			final User currentUser = (User) security.getUserPrincipal();
			final TaskDao dao = daoFactory.getTaskDao();
			return dao.getAll(currentUser.getCompanyId());
		}
	}
}
