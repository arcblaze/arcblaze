package com.arcblaze.arccore.rest.admin;

import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.arcblaze.arccore.common.model.Company;
import com.arcblaze.arccore.db.DaoFactory;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arccore.rest.BaseResource;
import com.codahale.metrics.Timer;

/**
 * The REST end-point for performing admin actions on companies.
 */
@Path("/admin/company")
public class CompanyResource extends BaseResource {
	/**
	 * @param daoFactory
	 *            used to communicate with the back-end database
	 * @param timer
	 *            tracks performance metrics on this REST end-point
	 * @param companyId
	 *            the unique id of the company to retrieve
	 * 
	 * @return the requested company
	 * 
	 * @throws DatabaseException
	 *             if there is an error communicating with the back-end
	 */
	@GET
	@Path("{companyId}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Company one(@Context final DaoFactory daoFactory,
			@Context final Timer timer,
			@PathParam("companyId") final Integer companyId)
			throws DatabaseException {
		try (Timer.Context timerContext = timer.time()) {
			return daoFactory.getCompanyDao().get(companyId);
		}
	}

	/**
	 * @param daoFactory
	 *            used to communicate with the back-end database
	 * @param timer
	 *            tracks performance metrics on this REST end-point
	 * 
	 * @return all of the available companies
	 * 
	 * @throws DatabaseException
	 *             if there is an error communicating with the back-end
	 */
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Set<Company> all(@Context final DaoFactory daoFactory,
			@Context final Timer timer) throws DatabaseException {
		try (Timer.Context timerContext = timer.time()) {
			return daoFactory.getCompanyDao().getAll();
		}
	}
}
