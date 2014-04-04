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
import com.arcblaze.arctime.common.model.Holiday;
import com.arcblaze.arctime.common.model.util.HolidayConfigurationException;
import com.arcblaze.arctime.db.ArcTimeDaoFactory;
import com.arcblaze.arctime.db.dao.HolidayDao;
import com.codahale.metrics.Timer;

/**
 * The REST end-point for managing company holidays.
 */
@Path("/manager/holiday")
public class HolidayResource extends BaseResource {
	@Context
	private ServletContext servletContext;

	/**
	 * @param security
	 *            the security information associated with the request
	 * @param daoFactory
	 *            used to communicate with the back-end database
	 * @param timer
	 *            tracks performance metrics of this REST end-point
	 * @param holidayId
	 *            the unique id of the holiday to retrieve
	 * 
	 * @return the requested holiday (if in the same company as the current
	 *         user)
	 * 
	 * @throws DatabaseException
	 *             if there is an error communicating with the back-end
	 * @throws HolidayConfigurationException
	 *             if there is a problem parsing the holiday configuration
	 *             information
	 */
	@GET
	@Path("{holidayId}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Holiday one(@Context final SecurityContext security,
			@Context final ArcTimeDaoFactory daoFactory,
			@Context final Timer timer,
			@PathParam("holidayId") final Integer holidayId)
			throws DatabaseException, HolidayConfigurationException {
		try (final Timer.Context timerContext = timer.time()) {
			final User currentUser = (User) security.getUserPrincipal();
			final HolidayDao dao = daoFactory.getHolidayDao();
			return dao.get(currentUser.getCompanyId(), holidayId);
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
	 * @return all of the available holidays in the same company as the current
	 *         user
	 * 
	 * @throws DatabaseException
	 *             if there is an error communicating with the back-end
	 * @throws HolidayConfigurationException
	 *             if there is a problem parsing the holiday configuration
	 *             information
	 */
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Set<Holiday> all(@Context final SecurityContext security,
			@Context final ArcTimeDaoFactory daoFactory,
			@Context final Timer timer) throws DatabaseException,
			HolidayConfigurationException {
		try (final Timer.Context timerContext = timer.time()) {
			final User currentUser = (User) security.getUserPrincipal();
			final HolidayDao dao = daoFactory.getHolidayDao();
			return dao.getAll(currentUser.getCompanyId());
		}
	}
}
