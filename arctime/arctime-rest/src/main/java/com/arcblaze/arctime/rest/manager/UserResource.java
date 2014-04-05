package com.arcblaze.arctime.rest.manager;

import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import com.arcblaze.arccore.common.model.User;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arccore.db.dao.UserDao;
import com.arcblaze.arccore.rest.BaseResource;
import com.arcblaze.arctime.db.ArcTimeDaoFactory;
import com.codahale.metrics.Timer;

/**
 * The REST end-point for managing users.
 */
@Path("/manager/user")
public class UserResource extends BaseResource {
	/**
	 * @param security
	 *            the security information associated with the request
	 * @param daoFactory
	 *            used to communicate with the back-end database
	 * @param timer
	 *            tracks performance metrics of this REST end-point
	 * @param userId
	 *            the unique id of the user to retrieve
	 * 
	 * @return the requested user (if in the same company as the current user)
	 * 
	 * @throws DatabaseException
	 *             if there is an error communicating with the back-end
	 */
	@GET
	@Path("{userId}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public User one(@Context final SecurityContext security,
			@Context final ArcTimeDaoFactory daoFactory,
			@Context final Timer timer,
			@PathParam("userId") final Integer userId) throws DatabaseException {
		try (final Timer.Context timerContext = timer.time()) {
			final User currentUser = (User) security.getUserPrincipal();
			final UserDao dao = daoFactory.getUserDao();
			return dao.get(currentUser.getCompanyId(), userId);
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
	 * @return all of the available users in the same company as the current
	 *         user
	 * 
	 * @throws DatabaseException
	 *             if there is an error communicating with the back-end
	 */
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Set<User> all(@Context final SecurityContext security,
			@Context final ArcTimeDaoFactory daoFactory,
			@Context final Timer timer) throws DatabaseException {
		try (final Timer.Context timerContext = timer.time()) {
			final User currentUser = (User) security.getUserPrincipal();
			final UserDao dao = daoFactory.getUserDao();
			return dao.getAll(currentUser.getCompanyId());
		}
	}
}
