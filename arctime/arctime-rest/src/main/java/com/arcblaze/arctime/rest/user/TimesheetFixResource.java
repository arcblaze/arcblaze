package com.arcblaze.arctime.rest.user;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arcblaze.arccore.common.model.User;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arccore.rest.BaseResource;
import com.arcblaze.arctime.common.model.AuditLog;
import com.arcblaze.arctime.common.model.Timesheet;
import com.arcblaze.arctime.common.model.util.HolidayConfigurationException;
import com.arcblaze.arctime.db.ArcTimeDaoFactory;
import com.arcblaze.arctime.db.dao.TimesheetDao;
import com.codahale.metrics.Timer;

/**
 * The REST end-point for fixing a timesheet.
 */
@Path("/user/timesheet/{id}/fix")
public class TimesheetFixResource extends BaseResource {
	private final static Logger log = LoggerFactory
			.getLogger(TimesheetFixResource.class);

	@XmlRootElement
	static class FixResponse {
		@XmlElement
		public final boolean success = true;

		@XmlElement
		public final String msg = "The timesheet was reopened successfully.";
	}

	/**
	 * @param security
	 *            the security information associated with the request
	 * @param daoFactory
	 *            used to communicate with the back-end database
	 * @param timer
	 *            tracks performance metrics of this REST end-point
	 * @param id
	 *            the unique id of the timesheet to reopen
	 * 
	 * @return the fixed timesheet response
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public FixResponse fix(@Context final SecurityContext security,
			@Context final ArcTimeDaoFactory daoFactory,
			@Context final Timer timer, @PathParam("id") final Integer id) {
		if (id == null)
			throw badRequest("Missing id parameter");

		log.debug("Timesheet fix request");
		try (final Timer.Context timerContext = timer.time()) {
			final User currentUser = (User) security.getUserPrincipal();

			log.debug("Getting current timesheet");
			final TimesheetDao dao = daoFactory.getTimesheetDao();
			final Timesheet timesheet = dao.get(currentUser.getCompanyId(), id);

			if (timesheet == null)
				throw notFound("The requested timesheet could not be found");
			if (timesheet.getUserId() != currentUser.getId())
				throw forbidden(currentUser, "Unable to fix timesheet that "
						+ "you do not own.");

			log.debug("Reopening timesheet");
			dao.complete(timesheet.getCompanyId(), false, timesheet.getId());
			daoFactory.getAuditLogDao().add(
					new AuditLog().setCompanyId(currentUser.getCompanyId())
							.setTimesheetId(timesheet.getId())
							.setLog("Timesheet reopened"));

			return new FixResponse();
		} catch (final DatabaseException dbException) {
			throw dbError(dbException);
		} catch (final HolidayConfigurationException badHoliday) {
			throw serverError(badHoliday);
		}
	}
}
