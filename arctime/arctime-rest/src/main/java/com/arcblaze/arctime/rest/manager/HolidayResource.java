package com.arcblaze.arctime.rest.manager;

import java.util.Date;
import java.util.Set;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.time.DateFormatUtils;

import com.arcblaze.arccore.common.config.Config;
import com.arcblaze.arccore.common.model.IdSet;
import com.arcblaze.arccore.common.model.User;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arccore.rest.BaseResource;
import com.arcblaze.arctime.common.model.Holiday;
import com.arcblaze.arctime.common.model.util.HolidayCalculator;
import com.arcblaze.arctime.common.model.util.HolidayConfigurationException;
import com.arcblaze.arctime.db.ArcTimeDaoFactory;
import com.codahale.metrics.Timer;

/**
 * The REST end-point for managing company holidays.
 */
@Path("/manager/holiday")
public class HolidayResource extends BaseResource {
	@XmlRootElement
	static class DeleteResponse {
		@XmlElement
		public final boolean success = true;

		@XmlElement
		public final String title = "Holiday Deleted";

		@XmlElement
		public final String msg = "The specified holidays have been deleted "
				+ "successfully.";
	}

	@XmlRootElement
	static class ValidateResponse {
		@XmlElement
		public boolean success;

		@XmlElement
		public String msg;

		@XmlElement
		public boolean valid;

		@XmlElement
		public Date day;
	}

	@XmlRootElement
	static class AddResponse {
		@XmlElement
		public final boolean success = true;

		@XmlElement
		public final String msg = "The holiday was added successfully.";

		@XmlElement
		public Holiday holiday;
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
	 * @param holidayId
	 *            the unique id of the holiday to retrieve
	 * 
	 * @return the requested holiday (if in the same company as the current
	 *         user)
	 * 
	 * @throws HolidayConfigurationException
	 *             if there is a problem parsing the holiday configuration
	 *             information
	 */
	@GET
	@Path("{holidayId:\\d+}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Holiday get(@Context final SecurityContext security,
			@Context final Config config,
			@Context final ArcTimeDaoFactory daoFactory,
			@Context final Timer timer,
			@PathParam("holidayId") final Integer holidayId)
			throws HolidayConfigurationException {
		final User currentUser = (User) security.getUserPrincipal();
		try (final Timer.Context timerContext = timer.time()) {
			if (holidayId == null)
				throw badRequest("Invalid null holiday id");

			return daoFactory.getHolidayDao().get(currentUser.getCompanyId(),
					holidayId);
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
	 * @return all of the available holidays in the same company as the current
	 *         user
	 * 
	 * @throws HolidayConfigurationException
	 *             if there is a problem parsing the holiday configuration
	 *             information
	 */
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Set<Holiday> all(@Context final SecurityContext security,
			@Context final Config config,
			@Context final ArcTimeDaoFactory daoFactory,
			@Context final Timer timer) throws HolidayConfigurationException {
		final User currentUser = (User) security.getUserPrincipal();
		try (final Timer.Context timerContext = timer.time()) {
			return daoFactory.getHolidayDao()
					.getAll(currentUser.getCompanyId());
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
	 * @return all of the common holidays available in the system
	 * 
	 * @throws HolidayConfigurationException
	 *             if there is a problem parsing the holiday configuration
	 *             information
	 */
	@GET
	@Path("/common")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Set<Holiday> common(@Context final SecurityContext security,
			@Context final Config config,
			@Context final ArcTimeDaoFactory daoFactory,
			@Context final Timer timer) throws HolidayConfigurationException {
		final User currentUser = (User) security.getUserPrincipal();
		try (final Timer.Context timerContext = timer.time()) {
			return daoFactory.getHolidayDao().getCommon();
		} catch (final DatabaseException dbException) {
			throw dbError(config, currentUser, dbException);
		}
	}

	/**
	 * @param timer
	 *            tracks performance metrics of this REST end-point
	 * @param config
	 *            the holiday configuration to validate
	 * 
	 * @return whether the provided holiday configuration is valid
	 */
	@GET
	@Path("/validate")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public ValidateResponse validate(@Context final Timer timer,
			@QueryParam("config") final String config) {
		try (final Timer.Context timerContext = timer.time()) {
			final ValidateResponse response = new ValidateResponse();
			response.success = true;
			try {
				response.day = HolidayCalculator.getDay(config, Integer
						.parseInt(DateFormatUtils.format(new Date(), "yyyy")));
				response.valid = true;
				response.msg = "Successfully processed holiday configuration.";
			} catch (final HolidayConfigurationException badConfig) {
				response.valid = false;
				response.msg = "Invalid holiday configuration: "
						+ badConfig.getMessage();
			}
			return response;
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
	 * @param holiday
	 *            the holiday to add to the back-end database
	 * 
	 * @return the new holiday that was added
	 */
	@POST
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public AddResponse add(@Context final SecurityContext security,
			@Context final Config config,
			@Context final ArcTimeDaoFactory daoFactory,
			@Context final Timer timer, @Context final Holiday holiday) {
		final User currentUser = (User) security.getUserPrincipal();
		try (final Timer.Context timerContext = timer.time()) {
			holiday.setCompanyId(currentUser.getCompanyId());
			daoFactory.getHolidayDao().add(holiday);

			final AddResponse response = new AddResponse();
			response.holiday = holiday;
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
	 * @param holidayIds
	 *            the unique ids of the holidays to delete
	 * 
	 * @return a delete response
	 * 
	 * @throws HolidayConfigurationException
	 *             if there is a problem parsing the holiday configuration
	 *             information
	 */
	@DELETE
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public DeleteResponse delete(@Context final SecurityContext security,
			@Context final Config config,
			@Context final ArcTimeDaoFactory daoFactory,
			@Context final Timer timer,
			@HeaderParam("ids") final IdSet holidayIds)
			throws HolidayConfigurationException {
		final User currentUser = (User) security.getUserPrincipal();
		try (final Timer.Context timerContext = timer.time()) {
			if (holidayIds == null || holidayIds.isEmpty())
				throw badRequest("No holiday ids provided");
			daoFactory.getHolidayDao().delete(currentUser.getCompanyId(),
					holidayIds);
			return new DeleteResponse();
		} catch (final DatabaseException dbException) {
			throw dbError(config, currentUser, dbException);
		}
	}
}
