package com.arcblaze.arctime.rest.user;

import static com.arcblaze.arctime.common.model.Enrichment.BILLS;
import static com.arcblaze.arctime.common.model.Enrichment.PAY_PERIODS;
import static com.arcblaze.arctime.common.model.Enrichment.TASKS;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

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

import org.apache.commons.lang.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arcblaze.arccore.common.config.Config;
import com.arcblaze.arccore.common.model.User;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arccore.rest.BaseResource;
import com.arcblaze.arctime.common.model.Assignment;
import com.arcblaze.arctime.common.model.AuditLog;
import com.arcblaze.arctime.common.model.Bill;
import com.arcblaze.arctime.common.model.Enrichment;
import com.arcblaze.arctime.common.model.Task;
import com.arcblaze.arctime.common.model.Timesheet;
import com.arcblaze.arctime.common.model.util.HolidayConfigurationException;
import com.arcblaze.arctime.db.ArcTimeDaoFactory;
import com.arcblaze.arctime.db.dao.AuditLogDao;
import com.arcblaze.arctime.db.dao.BillDao;
import com.arcblaze.arctime.db.dao.TimesheetDao;
import com.codahale.metrics.Timer;

/**
 * The REST end-point for saving hours into a timesheet.
 */
@Path("/user/timesheet/{id}/save")
public class TimesheetSaveResource extends BaseResource {
	private final static Logger log = LoggerFactory
			.getLogger(TimesheetSaveResource.class);

	@XmlRootElement
	static class SaveResponse {
		@XmlElement
		public final boolean success = true;

		@XmlElement
		public final String msg = "The timesheet information was saved successfully.";
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
	 *            the unique id of the timesheet into which this data will be
	 *            saved
	 * @param data
	 *            the updated timesheet data to save
	 * 
	 * @return the timesheet save response
	 */
	@POST
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public SaveResponse save(@Context final SecurityContext security,
			@Context final Config config,
			@Context final ArcTimeDaoFactory daoFactory,
			@Context final Timer timer, @PathParam("id") final Integer id,
			@FormParam("data") final String data) {
		log.debug("Timesheet save request");
		final User currentUser = (User) security.getUserPrincipal();
		try (final Timer.Context timerContext = timer.time()) {
			if (id == null)
				throw badRequest("Invalid null id");

			final Set<Enrichment> timesheetEnrichments = new LinkedHashSet<>(
					Arrays.asList(PAY_PERIODS, TASKS, BILLS));

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
			saveTimesheet(daoFactory, timesheet, data);

			return new SaveResponse();
		} catch (final DatabaseException dbException) {
			throw dbError(config, currentUser, dbException);
		} catch (final HolidayConfigurationException badHoliday) {
			throw serverError(config, currentUser, badHoliday);
		} catch (final IllegalArgumentException badData) {
			throw badRequest("Invalid timesheet data: " + badData.getMessage());
		}
	}

	protected static void saveTimesheet(final ArcTimeDaoFactory daoFactory,
			final Timesheet timesheet, final String data)
			throws DatabaseException {
		final Date now = new Date();
		final BillDao billDao = daoFactory.getBillDao();
		final AuditLogDao auditLogDao = daoFactory.getAuditLogDao();

		// Keep track of the processed bills sent from the client.
		final Set<String> processed = new TreeSet<>();

		final Set<Bill> bills = Bill.fromTimesheetData(data);
		for (final Bill bill : bills) {
			processed.add(bill.getUniqueId());
			bill.setTimestamp(now);

			final Task task = timesheet.getTask(bill.getTaskId());

			if (task == null)
				continue;

			final Assignment assignment = bill.hasAssignmentId() ? task
					.getAssignment(bill.getAssignmentId()) : null;
			final Bill existing = assignment == null ? task.getBill(bill
					.getDay()) : assignment.getBill(bill.getDay());

			if (existing != null
					&& !existing.getHours().equals(bill.getHours())) {
				bill.setId(existing.getId());
				bill.setUserId(existing.getUserId());
				billDao.update(bill);

				auditLogDao.add(getUpdatedLog(timesheet, task, assignment,
						existing, bill));
			} else if (existing == null) {
				bill.setUserId(timesheet.getUserId());
				billDao.add(bill);

				auditLogDao.add(getAddedLog(timesheet, task, assignment, bill));
			}
		}

		final Set<Integer> toDelete = new TreeSet<>();
		for (final Task task : timesheet.getTasks()) {
			for (final Bill bill : task.getBills()) {
				if (processed.contains(bill.getUniqueId()))
					continue;

				toDelete.add(bill.getId());
				auditLogDao.add(getDeletedLog(timesheet, task, null, bill));
			}
			for (final Assignment assignment : task.getAssignments()) {
				for (final Bill bill : assignment.getBills()) {
					if (processed.contains(bill.getUniqueId()))
						continue;

					toDelete.add(bill.getId());
					auditLogDao.add(getDeletedLog(timesheet, task, assignment,
							bill));
				}
			}
		}

		if (!toDelete.isEmpty())
			billDao.delete(toDelete);
	}

	/**
	 * A utility method for generating the audit log associated with changing
	 * hours associated with a bill.
	 * 
	 * @param timesheet
	 *            the timesheet being modified
	 * @param task
	 *            the task to which the bills apply
	 * @param assignment
	 *            the assignment to which the bills apply
	 * @param existing
	 *            the existing bill
	 * @param updated
	 *            the updated bill
	 * 
	 * @return an appropriate log message describing the changes
	 */
	protected static AuditLog getUpdatedLog(final Timesheet timesheet,
			final Task task, final Assignment assignment, final Bill existing,
			final Bill updated) {
		final StringBuilder log = new StringBuilder();
		log.append("Hours for task ");
		log.append(task.getDescription());
		log.append(" ");
		if (assignment != null) {
			log.append("(LCAT: ");
			log.append(assignment.getLaborCat());
			log.append(") ");
		}
		log.append("on ");
		log.append(DateFormatUtils.format(updated.getDay(), "yyyy-MM-dd"));
		log.append(" changed from ");
		log.append(existing.getHours());
		log.append(" to ");
		log.append(updated.getHours());
		log.append(".");
		if (updated.hasReason()) {
			log.append(" The user-specified reason: ");
			log.append(updated.getReason());
		}

		return new AuditLog().setCompanyId(timesheet.getCompanyId())
				.setLog(log.toString()).setCompanyId(task.getCompanyId())
				.setTimesheetId(timesheet.getId());
	}

	/**
	 * A utility method for generating the audit log associated with adding
	 * hours to a timesheet.
	 * 
	 * @param timesheet
	 *            the timesheet being modified
	 * @param task
	 *            the task to which the bills apply
	 * @param assignment
	 *            the assignment to which the bills apply
	 * @param bill
	 *            the newly added bill
	 * 
	 * @return an appropriate log message describing the changes
	 */
	protected static AuditLog getAddedLog(final Timesheet timesheet,
			final Task task, final Assignment assignment, final Bill bill) {
		final StringBuilder log = new StringBuilder();
		log.append("Added ");
		log.append(bill.getHours());
		log.append(" hours for task ");
		log.append(task.getDescription());
		log.append(" ");
		if (assignment != null) {
			log.append("(LCAT: ");
			log.append(assignment.getLaborCat());
			log.append(") ");
		}
		log.append("on ");
		log.append(DateFormatUtils.format(bill.getDay(), "yyyy-MM-dd"));

		return new AuditLog().setCompanyId(timesheet.getCompanyId())
				.setLog(log.toString()).setCompanyId(task.getCompanyId())
				.setTimesheetId(timesheet.getId());
	}

	/**
	 * A utility method for generating the audit log associated with deleting
	 * hours associated with a bill.
	 * 
	 * @param timesheet
	 *            the timesheet being modified
	 * @param task
	 *            the task to which the bills apply
	 * @param assignment
	 *            the assignment to which the bills apply
	 * @param existing
	 *            the existing bill
	 * 
	 * @return an appropriate log message describing the changes
	 */
	protected static AuditLog getDeletedLog(final Timesheet timesheet,
			final Task task, final Assignment assignment, final Bill existing) {
		final StringBuilder log = new StringBuilder();
		log.append("Hours for task ");
		log.append(task.getDescription());
		log.append(" ");
		if (assignment != null) {
			log.append("(LCAT: ");
			log.append(assignment.getLaborCat());
			log.append(") ");
		}
		log.append("on ");
		log.append(DateFormatUtils.format(existing.getDay(), "yyyy-MM-dd"));
		log.append(" changed from ");
		log.append(existing.getHours());
		log.append(" to 0.00.");

		return new AuditLog().setCompanyId(timesheet.getCompanyId())
				.setLog(log.toString()).setCompanyId(task.getCompanyId())
				.setTimesheetId(timesheet.getId());
	}
}
