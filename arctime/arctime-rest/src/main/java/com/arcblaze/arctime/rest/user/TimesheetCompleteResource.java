package com.arcblaze.arctime.rest.user;

import static com.arcblaze.arctime.common.model.Enrichment.BILLS;
import static com.arcblaze.arctime.common.model.Enrichment.PAY_PERIODS;
import static com.arcblaze.arctime.common.model.Enrichment.TASKS;
import static com.arcblaze.arctime.common.model.Enrichment.USERS;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
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

import com.arcblaze.arccore.common.config.Config;
import com.arcblaze.arccore.common.model.User;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arccore.rest.BaseResource;
import com.arcblaze.arctime.common.model.AuditLog;
import com.arcblaze.arctime.common.model.Enrichment;
import com.arcblaze.arctime.common.model.PayPeriod;
import com.arcblaze.arctime.common.model.Timesheet;
import com.arcblaze.arctime.common.model.util.HolidayConfigurationException;
import com.arcblaze.arctime.db.ArcTimeDaoFactory;
import com.arcblaze.arctime.db.dao.PayPeriodDao;
import com.arcblaze.arctime.db.dao.TimesheetDao;
import com.codahale.metrics.Timer;

/**
 * The REST end-point for completing a timesheet.
 */
@Path("/user/timesheet/{id}/complete")
public class TimesheetCompleteResource extends BaseResource {
	private final static Logger log = LoggerFactory
			.getLogger(TimesheetCompleteResource.class);

	@XmlRootElement
	static class CompleteResponse {
		@XmlElement
		public final boolean success = true;

		@XmlElement
		public final String msg = "The timesheet was completed successfully. "
				+ "Moved to the next pay period.";

		@XmlElement
		public Timesheet next = null;
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
	 * @param id
	 *            the unique id of the timesheet to complete
	 * @param data
	 *            the updated timesheet data to save
	 * 
	 * @return the completed timesheet response
	 */
	@POST
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public CompleteResponse complete(@Context final SecurityContext security,
			@Context final Config config,
			@Context final ArcTimeDaoFactory daoFactory,
			@Context final Timer timer, @PathParam("id") final Integer id,
			@FormParam("data") final String data) {
		log.debug("Timesheet complete request");
		final User currentUser = (User) security.getUserPrincipal();
		try (final Timer.Context timerContext = timer.time()) {
			final Set<Enrichment> timesheetEnrichments = new LinkedHashSet<>(
					Arrays.asList(USERS, PAY_PERIODS, TASKS, BILLS));

			log.debug("Retrieving current timesheet");
			final TimesheetDao dao = daoFactory.getTimesheetDao();
			final Timesheet timesheet = dao.get(currentUser.getCompanyId(), id,
					timesheetEnrichments);

			if (timesheet == null)
				throw badRequest("The requested timesheet could not be found");
			if (timesheet.getUserId() != currentUser.getId())
				throw forbidden(config, currentUser, "Unable to save "
						+ "timesheet data into a timesheet you do not own.");

			log.debug("Saving timesheet data");
			TimesheetSaveResource.saveTimesheet(daoFactory, timesheet, data);

			log.debug("Marking timesheet as complete");
			dao.complete(timesheet.getCompanyId(), true, timesheet.getId());
			daoFactory.getAuditLogDao().add(
					new AuditLog().setCompanyId(currentUser.getCompanyId())
							.setTimesheetId(timesheet.getId())
							.setLog("Timesheet completed"));

			log.debug("Finding next pay period");
			final PayPeriod nextPayPeriod = timesheet.getPayPeriod().getNext();
			final PayPeriodDao ppdao = daoFactory.getPayPeriodDao();
			if (!ppdao.exists(timesheet.getCompanyId(),
					nextPayPeriod.getBegin())) {
				ppdao.add(nextPayPeriod);
			}

			log.debug("Finding next timesheet");
			Timesheet next = dao.getForUser(currentUser.getId(), nextPayPeriod,
					timesheetEnrichments);
			if (next == null) {
				log.debug("Creating next timesheet");
				next = new Timesheet();
				next.setCompanyId(currentUser.getCompanyId());
				next.setUserId(currentUser.getId());
				next.setBegin(nextPayPeriod.getBegin());
				dao.add(next);
				log.debug("Created next timesheet: {}", next);

				// Retrieve an enriched version of the newly created timesheet.
				next = dao.get(currentUser.getCompanyId(), next.getId(),
						timesheetEnrichments);
			}

			final CompleteResponse response = new CompleteResponse();
			response.next = next;
			return response;
		} catch (final DatabaseException dbException) {
			throw dbError(config, currentUser, dbException);
		} catch (final HolidayConfigurationException badHoliday) {
			throw serverError(config, currentUser, badHoliday);
		} catch (final IllegalArgumentException badData) {
			throw badRequest("Invalid timesheet data: " + badData.getMessage());
		}
	}
}
