package com.arcblaze.arctime.rest.user;

import static com.arcblaze.arctime.common.model.Enrichment.AUDIT_LOGS;
import static com.arcblaze.arctime.common.model.Enrichment.BILLS;
import static com.arcblaze.arctime.common.model.Enrichment.HOLIDAYS;
import static com.arcblaze.arctime.common.model.Enrichment.PAY_PERIODS;
import static com.arcblaze.arctime.common.model.Enrichment.TASKS;
import static com.arcblaze.arctime.common.model.Enrichment.USERS;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arcblaze.arccore.common.model.User;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arccore.rest.BaseResource;
import com.arcblaze.arctime.common.model.Enrichment;
import com.arcblaze.arctime.common.model.PayPeriod;
import com.arcblaze.arctime.common.model.Timesheet;
import com.arcblaze.arctime.common.model.util.HolidayConfigurationException;
import com.arcblaze.arctime.db.ArcTimeDaoFactory;
import com.arcblaze.arctime.db.dao.PayPeriodDao;
import com.arcblaze.arctime.db.dao.TimesheetDao;
import com.codahale.metrics.Timer;

/**
 * The REST end-point for retrieving the timesheet for a custom-specified pay
 * period.
 */
@Path("/user/timesheet/custom/{date}")
public class TimesheetCustomResource extends BaseResource {
	private final static Logger log = LoggerFactory
			.getLogger(TimesheetCustomResource.class);

	private final static String[] FMT = { "yyyyMMdd" };

	@Context
	private ServletContext servletContext;

	@XmlRootElement
	static class TimesheetResponse {
		@XmlElement
		public Timesheet timesheet = null;
	}

	/**
	 * @param security
	 *            the security information associated with the request
	 * @param daoFactory
	 *            used to communicate with the back-end database
	 * @param timer
	 *            tracks performance metrics of this REST end-point
	 * @param date
	 *            a date within the pay period of interest (in yyyyMMdd format)
	 * 
	 * @return the custom timesheet response
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public TimesheetResponse previous(@Context final SecurityContext security,
			@Context final ArcTimeDaoFactory daoFactory,
			@Context final Timer timer, @PathParam("date") final String date) {
		log.debug("Custom timesheet request");
		try (final Timer.Context timerContext = timer.time()) {
			final Set<Enrichment> timesheetEnrichments = new LinkedHashSet<>(
					Arrays.asList(PAY_PERIODS, AUDIT_LOGS, HOLIDAYS, USERS,
							TASKS, BILLS));

			final User currentUser = (User) security.getUserPrincipal();

			log.debug("Getting requested pay period");
			final Date day = DateUtils.parseDate(date, FMT);
			final PayPeriodDao ppdao = daoFactory.getPayPeriodDao();
			final PayPeriod payPeriod = ppdao.getContaining(
					currentUser.getCompanyId(), day);
			if (payPeriod == null)
				throw notFound("The specified pay period was not found: "
						+ date);

			log.debug("Getting timesheet");
			final TimesheetDao dao = daoFactory.getTimesheetDao();
			final Timesheet timesheet = dao.getForUser(currentUser.getId(),
					payPeriod, timesheetEnrichments);
			log.debug("Found timesheet: {}", timesheet);

			if (timesheet == null)
				throw notFound("The requested timesheet was not found");

			final TimesheetResponse response = new TimesheetResponse();
			response.timesheet = timesheet;
			return response;
		} catch (final DatabaseException dbException) {
			throw dbError(dbException);
		} catch (final HolidayConfigurationException badHoliday) {
			throw serverError(badHoliday);
		} catch (final ParseException badDate) {
			throw badRequest("The specified date is invalid: "
					+ badDate.getMessage());
		}
	}
}
