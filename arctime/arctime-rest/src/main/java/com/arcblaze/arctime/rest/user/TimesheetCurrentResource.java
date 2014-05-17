package com.arcblaze.arctime.rest.user;

import static com.arcblaze.arctime.common.model.Enrichment.AUDIT_LOGS;
import static com.arcblaze.arctime.common.model.Enrichment.BILLS;
import static com.arcblaze.arctime.common.model.Enrichment.HOLIDAYS;
import static com.arcblaze.arctime.common.model.Enrichment.PAY_PERIODS;
import static com.arcblaze.arctime.common.model.Enrichment.TASKS;
import static com.arcblaze.arctime.common.model.Enrichment.USERS;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
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
import com.arcblaze.arctime.common.model.Enrichment;
import com.arcblaze.arctime.common.model.PayPeriod;
import com.arcblaze.arctime.common.model.Timesheet;
import com.arcblaze.arctime.common.model.util.HolidayConfigurationException;
import com.arcblaze.arctime.db.ArcTimeDaoFactory;
import com.arcblaze.arctime.db.dao.TimesheetDao;
import com.codahale.metrics.Timer;

/**
 * The REST end-point for retrieving the user's current timesheet.
 */
@Path("/user/timesheet/current")
public class TimesheetCurrentResource extends BaseResource {
    private final static Logger log = LoggerFactory.getLogger(TimesheetCurrentResource.class);

    @XmlRootElement
    static class CurrentResponse {
        @XmlElement
        public Timesheet timesheet = null;
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
     * @return the current timesheet response
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public CurrentResponse current(@Context final SecurityContext security, @Context final Config config,
            @Context final ArcTimeDaoFactory daoFactory, @Context final Timer timer) {
        log.debug("Current timesheet request");
        final User currentUser = (User) security.getUserPrincipal();
        try (final Timer.Context timerContext = timer.time()) {
            final Set<Enrichment> timesheetEnrichments = new LinkedHashSet<>(Arrays.asList(PAY_PERIODS, AUDIT_LOGS,
                    HOLIDAYS, USERS, TASKS, BILLS));

            final TimesheetDao dao = daoFactory.getTimesheetDao();
            Timesheet timesheet = dao.getLatestForUser(currentUser.getId(), timesheetEnrichments);
            log.debug("Found timesheet: {}", timesheet);

            if (timesheet == null) {
                log.debug("Timesheet not found, creating it...");
                final PayPeriod payPeriod = daoFactory.getPayPeriodDao().getCurrent(currentUser.getCompanyId());
                if (payPeriod == null)
                    throw notFound("A pay period could not be found.");

                timesheet = new Timesheet();
                timesheet.setCompanyId(currentUser.getCompanyId());
                timesheet.setUserId(currentUser.getId());
                timesheet.setBegin(payPeriod.getBegin());
                dao.add(timesheet);
                log.debug("Created timesheet: {}", timesheet);

                // Retrieve an enriched version of the newly created timesheet.
                timesheet = dao.get(currentUser.getCompanyId(), timesheet.getId(), timesheetEnrichments);
            }

            final CurrentResponse response = new CurrentResponse();
            response.timesheet = timesheet;
            return response;
        } catch (final DatabaseException dbException) {
            throw dbError(config, currentUser, dbException);
        } catch (final HolidayConfigurationException badHoliday) {
            throw serverError(config, currentUser, badHoliday);
        }
    }
}
