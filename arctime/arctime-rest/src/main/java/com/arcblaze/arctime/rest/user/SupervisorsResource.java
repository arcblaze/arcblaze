package com.arcblaze.arctime.rest.user;

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
import com.arcblaze.arctime.common.model.Supervisor;
import com.arcblaze.arctime.db.ArcTimeDaoFactory;
import com.codahale.metrics.Timer;

/**
 * The REST end-point for retrieving the supervisors for the current user.
 */
@Path("/user/supervisors")
public class SupervisorsResource extends BaseResource {
    private final static Logger log = LoggerFactory.getLogger(SupervisorsResource.class);

    @XmlRootElement
    static class Supervisors {
        @XmlElement
        public Set<Supervisor> supervisors;
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
     * @return the user supervisors response
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    public Supervisors get(@Context final SecurityContext security, @Context final Config config,
            @Context final ArcTimeDaoFactory daoFactory, @Context final Timer timer) {
        log.debug("User supervisor request");
        final User currentUser = (User) security.getUserPrincipal();
        try (final Timer.Context timerContext = timer.time()) {
            final Set<Supervisor> supervisors = daoFactory.getSupervisorDao().getSupervisors(
                    currentUser.getCompanyId(), currentUser.getId());
            log.debug("Found supervisors: {}", supervisors.size());

            final Supervisors response = new Supervisors();
            response.supervisors = supervisors;
            return response;
        } catch (DatabaseException dbException) {
            throw dbError(config, currentUser, dbException);
        }
    }
}
