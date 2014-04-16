package com.arcblaze.arctime.rest.manager;

import java.text.ParseException;
import java.util.Date;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.arcblaze.arccore.common.config.Config;
import com.arcblaze.arccore.common.model.User;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arccore.rest.BaseResource;
import com.arcblaze.arctime.common.model.Assignment;
import com.arcblaze.arctime.db.ArcTimeDaoFactory;
import com.codahale.metrics.Timer;

/**
 * The REST end-point for managing users.
 */
@Path("/manager/assignment")
public class AssignmentResource extends BaseResource {
	private final static String[] FMT = { "yyyy-MM-dd" };

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
	 *            the unique id of the task for which assignments should be
	 *            retrieved
	 * @param daystr
	 *            the day for which assignments should be retrieved, possibly
	 *            {@code null}
	 * 
	 * @return the requested assignments
	 */
	@GET
	@Path("/task/{taskId:\\d+}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Set<Assignment> getForTask(@Context final SecurityContext security,
			@Context final Config config,
			@Context final ArcTimeDaoFactory daoFactory,
			@Context final Timer timer,
			@PathParam("taskId") final Integer taskId,
			@QueryParam("day") final String daystr) {
		final User currentUser = (User) security.getUserPrincipal();
		try (final Timer.Context timerContext = timer.time()) {
			if (taskId == null)
				throw badRequest("A task id must be provided.");
			Date day = null;
			if (StringUtils.isNotBlank(daystr))
				day = DateUtils.parseDate(daystr, FMT);
			return daoFactory.getAssignmentDao().getForTask(
					currentUser.getCompanyId(), taskId, day);
		} catch (final DatabaseException dbException) {
			throw dbError(config, currentUser, dbException);
		} catch (final ParseException badDate) {
			throw badRequest("Invalid date format for day parameter");
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
	 *            the unique id of the user for which assignments should be
	 *            retrieve
	 * @param daystr
	 *            the day for which assignments should be retrieved, possibly
	 *            {@code null}
	 * 
	 * @return the requested assignments
	 */
	@GET
	@Path("/user/{userId:\\d+}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Set<Assignment> getForUser(@Context final SecurityContext security,
			@Context final Config config,
			@Context final ArcTimeDaoFactory daoFactory,
			@Context final Timer timer,
			@PathParam("userId") final Integer userId,
			@QueryParam("day") final String daystr) {
		final User currentUser = (User) security.getUserPrincipal();
		try (final Timer.Context timerContext = timer.time()) {
			if (userId == null)
				throw badRequest("A user id must be provided.");
			Date day = null;
			if (StringUtils.isNotBlank(daystr))
				day = DateUtils.parseDate(daystr, FMT);
			return daoFactory.getAssignmentDao().getForUser(
					currentUser.getCompanyId(), userId, day);
		} catch (final DatabaseException dbException) {
			throw dbError(config, currentUser, dbException);
		} catch (final ParseException badDate) {
			throw badRequest("Invalid date format for day parameter");
		}
	}
}
