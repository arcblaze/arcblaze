package com.arcblaze.arctime.rest.manager;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import com.arcblaze.arccore.common.config.Config;
import com.arcblaze.arccore.common.model.Company;
import com.arcblaze.arccore.common.model.User;
import com.arcblaze.arccore.db.DaoFactory;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arccore.rest.BaseResource;
import com.codahale.metrics.Timer;

/**
 * The REST end-point for performing management actions on companies.
 */
@Path("/manager/company")
public class CompanyResource extends BaseResource {
    /**
     * @param security
     *            the security information associated with the request
     * @param config
     *            the system configuration information
     * @param daoFactory
     *            used to communicate with the back-end database
     * @param timer
     *            tracks performance metrics of this REST end-point
     * 
     * @return the company in which the current user account resides
     * 
     * @throws DatabaseException
     *             if there is an error communicating with the back-end
     */
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Company mine(@Context final SecurityContext security, @Context final Config config,
            @Context final DaoFactory daoFactory, @Context final Timer timer) throws DatabaseException {
        final User currentUser = (User) security.getUserPrincipal();
        try (final Timer.Context timerContext = timer.time()) {
            return daoFactory.getCompanyDao().get(currentUser.getCompanyId());
        } catch (final DatabaseException dbException) {
            throw dbError(config, currentUser, dbException);
        }
    }
}
