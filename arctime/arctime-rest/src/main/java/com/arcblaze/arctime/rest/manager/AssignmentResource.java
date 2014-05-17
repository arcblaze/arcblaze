package com.arcblaze.arctime.rest.manager;

import java.text.ParseException;
import java.util.Date;
import java.util.Set;

import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.arcblaze.arccore.common.config.Config;
import com.arcblaze.arccore.common.model.IdSet;
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
    private final static String[] FMT = { "MM/dd/yyyy", "yyyy-MM-dd" };

    @XmlRootElement
    static class AddResponse {
        @XmlElement
        public boolean success = true;

        @XmlElement
        public String msg = "The assignment was added successfully.";

        @XmlElement
        public Assignment assignment;
    }

    @XmlRootElement
    static class UpdateResponse {
        @XmlElement
        public boolean success = true;

        @XmlElement
        public String msg = "The assignment was saved successfully.";

        @XmlElement
        public Assignment assignment;
    }

    @XmlRootElement
    static class DeleteResponse {
        @XmlElement
        public boolean success = true;

        @XmlElement
        public String msg = "The assignments were deleted successfully.";
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
     * @param taskId
     *            the unique id of the task for which assignments should be retrieved
     * @param daystr
     *            the day for which assignments should be retrieved, possibly {@code null}
     * @param includeInactive
     *            whether assignments for inactive users should be included
     * 
     * @return the requested assignments
     */
    @GET
    @Path("/task/{taskId:\\d+}")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Set<Assignment> getForTask(@Context final SecurityContext security, @Context final Config config,
            @Context final ArcTimeDaoFactory daoFactory, @Context final Timer timer,
            @PathParam("taskId") final Integer taskId, @QueryParam("day") final String daystr,
            @QueryParam("includeInactive") @DefaultValue("false") final Boolean includeInactive) {
        final User currentUser = (User) security.getUserPrincipal();
        try (final Timer.Context timerContext = timer.time()) {
            if (taskId == null)
                throw badRequest("A task id must be provided.");
            Date day = null;
            if (StringUtils.isNotBlank(daystr))
                day = DateUtils.parseDate(daystr, FMT);
            return daoFactory.getAssignmentDao().getForTask(currentUser.getCompanyId(), taskId, day, includeInactive);
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
     *            the unique id of the user for which assignments should be retrieve
     * @param daystr
     *            the day for which assignments should be retrieved, possibly {@code null}
     * @param includeInactive
     *            whether assignments for inactive tasks should be included
     * 
     * @return the requested assignments
     */
    @GET
    @Path("/user/{userId:\\d+}")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Set<Assignment> getForUser(@Context final SecurityContext security, @Context final Config config,
            @Context final ArcTimeDaoFactory daoFactory, @Context final Timer timer,
            @PathParam("userId") final Integer userId, @QueryParam("day") final String daystr,
            @QueryParam("includeInactive") @DefaultValue("false") final Boolean includeInactive) {
        final User currentUser = (User) security.getUserPrincipal();
        try (final Timer.Context timerContext = timer.time()) {
            if (userId == null)
                throw badRequest("A user id must be provided.");
            Date day = null;
            if (StringUtils.isNotBlank(daystr))
                day = DateUtils.parseDate(daystr, FMT);
            return daoFactory.getAssignmentDao().getForUser(currentUser.getCompanyId(), userId, day, includeInactive);
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
     * @param assignment
     *            the assignment to be added
     * 
     * @return the assignment add response
     */
    @POST
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public AddResponse add(@Context final SecurityContext security, @Context final Config config,
            @Context final ArcTimeDaoFactory daoFactory, @Context final Timer timer,
            @Context final Assignment assignment) {
        final User currentUser = (User) security.getUserPrincipal();
        try (final Timer.Context timerContext = timer.time()) {
            if (assignment.getTaskId() == null)
                throw badRequest("A task id must be provided.");
            if (assignment.getUserId() == null)
                throw badRequest("A user id must be provided.");
            assignment.setCompanyId(currentUser.getCompanyId());
            final int added = daoFactory.getAssignmentDao().add(assignment);
            final AddResponse response = new AddResponse();
            if (added == 0) {
                response.success = false;
                response.msg = "Failed to add the new assignment.";
            } else
                response.assignment = assignment;
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
     * @param assignment
     *            the assignment to be modified
     * 
     * @return the assignment update response
     */
    @PUT
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public UpdateResponse update(@Context final SecurityContext security, @Context final Config config,
            @Context final ArcTimeDaoFactory daoFactory, @Context final Timer timer,
            @Context final Assignment assignment) {
        final User currentUser = (User) security.getUserPrincipal();
        try (final Timer.Context timerContext = timer.time()) {
            if (assignment.getTaskId() == null)
                throw badRequest("A task id must be provided.");
            if (assignment.getUserId() == null)
                throw badRequest("A user id must be provided.");
            assignment.setCompanyId(currentUser.getCompanyId());
            final int updated = daoFactory.getAssignmentDao().update(assignment);
            final UpdateResponse response = new UpdateResponse();
            if (updated == 0) {
                response.success = false;
                response.msg = "Failed to save the modified assignment.";
            } else
                response.assignment = assignment;
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
     * @param ids
     *            the unique ids of the assignments to be deleted
     * 
     * @return the assignment update response
     */
    @DELETE
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public DeleteResponse delete(@Context final SecurityContext security, @Context final Config config,
            @Context final ArcTimeDaoFactory daoFactory, @Context final Timer timer, @HeaderParam("ids") final IdSet ids) {
        final User currentUser = (User) security.getUserPrincipal();
        try (final Timer.Context timerContext = timer.time()) {
            if (ids == null || ids.isEmpty())
                throw badRequest("An assignment id must be provided.");
            final int deleted = daoFactory.getAssignmentDao().delete(currentUser.getCompanyId(), ids);
            final DeleteResponse response = new DeleteResponse();
            if (deleted == 0) {
                response.success = false;
                response.msg = "Failed to delete the requested assignments.";
            }
            return response;
        } catch (final DatabaseException dbException) {
            throw dbError(config, currentUser, dbException);
        }
    }
}
