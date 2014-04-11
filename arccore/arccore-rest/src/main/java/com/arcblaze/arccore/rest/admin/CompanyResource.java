package com.arcblaze.arccore.rest.admin;

import java.util.Set;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.arcblaze.arccore.common.config.Config;
import com.arcblaze.arccore.common.model.Company;
import com.arcblaze.arccore.common.model.IdSet;
import com.arcblaze.arccore.common.model.User;
import com.arcblaze.arccore.db.DaoFactory;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arccore.rest.BaseResource;
import com.codahale.metrics.Timer;

/**
 * The REST end-point for performing admin actions on companies.
 */
@Path("/admin/company")
public class CompanyResource extends BaseResource {
	@XmlRootElement
	static class DeleteResponse {
		@XmlElement
		public final boolean success = true;

		@XmlElement
		public final String title = "Company Deleted";

		@XmlElement
		public final String msg = "The specified companies have been deleted "
				+ "successfully.";
	}

	@XmlRootElement
	static class AddResponse {
		@XmlElement
		public final boolean success = true;

		@XmlElement
		public final String msg = "The company was added successfully.";

		@XmlElement
		public Company company;
	}

	@XmlRootElement
	static class ActivateResponse {
		@XmlElement
		public final boolean success = true;

		@XmlElement
		public final String msg = "The specified companies were activated successfully.";
	}

	@XmlRootElement
	static class DeactivateResponse {
		@XmlElement
		public final boolean success = true;

		@XmlElement
		public final String msg = "The specified companies were deactivated successfully.";
	}

	@XmlRootElement
	static class UpdateResponse {
		@XmlElement
		public final boolean success = true;

		@XmlElement
		public final String msg = "The company was modified successfully.";

		@XmlElement
		public Company company;
	}

	/**
	 * @param security
	 *            the security information associated with the request
	 * @param config
	 *            the system configuration properties
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
	@Path("{companyId:\\d+}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Company get(@Context final SecurityContext security,
			@Context final Config config, @Context final DaoFactory daoFactory,
			@Context final Timer timer,
			@PathParam("companyId") final Integer companyId)
			throws DatabaseException {
		final User currentUser = (User) security.getUserPrincipal();
		try (Timer.Context timerContext = timer.time()) {
			if (companyId == null)
				throw badRequest("A company id must be provided.");
			return daoFactory.getCompanyDao().get(companyId);
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
	 *            tracks performance metrics on this REST end-point
	 * 
	 * @return all of the available companies
	 * 
	 * @throws DatabaseException
	 *             if there is an error communicating with the back-end
	 */
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Set<Company> all(@Context final SecurityContext security,
			@Context final Config config, @Context final DaoFactory daoFactory,
			@Context final Timer timer) throws DatabaseException {
		final User currentUser = (User) security.getUserPrincipal();
		try (Timer.Context timerContext = timer.time()) {
			return daoFactory.getCompanyDao().getAll();
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
	 * @param company
	 *            the company to add to the back-end database
	 * 
	 * @return the new company that was added
	 */
	@POST
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public AddResponse add(@Context final SecurityContext security,
			@Context final Config config, @Context final DaoFactory daoFactory,
			@Context final Timer timer, @Context final Company company) {
		final User currentUser = (User) security.getUserPrincipal();
		try (final Timer.Context timerContext = timer.time()) {
			daoFactory.getCompanyDao().add(company);

			final AddResponse response = new AddResponse();
			response.company = company;
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
	 * @param companyIds
	 *            the unique ids of the companies to make active
	 * 
	 * @return an activate response
	 */
	@PUT
	@Path("/activate")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public ActivateResponse activate(@Context final SecurityContext security,
			@Context final Config config, @Context final DaoFactory daoFactory,
			@Context final Timer timer,
			@HeaderParam("ids") final IdSet companyIds) {
		final User currentUser = (User) security.getUserPrincipal();
		try (final Timer.Context timerContext = timer.time()) {
			if (companyIds == null || companyIds.isEmpty())
				throw badRequest("No company ids provided");
			daoFactory.getCompanyDao().activate(companyIds);
			return new ActivateResponse();
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
	 * @param companyIds
	 *            the unique ids of the companies to make inactive
	 * 
	 * @return a deactivate response
	 */
	@PUT
	@Path("/deactivate")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public DeactivateResponse deactivate(
			@Context final SecurityContext security,
			@Context final Config config, @Context final DaoFactory daoFactory,
			@Context final Timer timer,
			@HeaderParam("ids") final IdSet companyIds) {
		final User currentUser = (User) security.getUserPrincipal();
		try (final Timer.Context timerContext = timer.time()) {
			if (companyIds == null || companyIds.isEmpty())
				throw badRequest("No company ids provided");
			daoFactory.getCompanyDao().deactivate(companyIds);
			return new DeactivateResponse();
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
	 * @param company
	 *            the company to add to the back-end database
	 * 
	 * @return the new company that was added
	 */
	@PUT
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public UpdateResponse update(@Context final SecurityContext security,
			@Context final Config config, @Context final DaoFactory daoFactory,
			@Context final Timer timer, @Context final Company company) {
		final User currentUser = (User) security.getUserPrincipal();
		try (final Timer.Context timerContext = timer.time()) {
			if (company == null || company.getId() == null)
				throw badRequest("A company with id must be provided.");

			daoFactory.getCompanyDao().update(company);

			final UpdateResponse response = new UpdateResponse();
			response.company = company;
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
	 * @param companyIds
	 *            the unique ids of the companies to delete
	 * 
	 * @return a delete response
	 */
	@DELETE
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public DeleteResponse delete(@Context final SecurityContext security,
			@Context final Config config, @Context final DaoFactory daoFactory,
			@Context final Timer timer,
			@HeaderParam("ids") final IdSet companyIds) {
		final User currentUser = (User) security.getUserPrincipal();
		try (final Timer.Context timerContext = timer.time()) {
			if (companyIds == null || companyIds.isEmpty())
				throw badRequest("No company ids provided");
			daoFactory.getCompanyDao().delete(companyIds);
			return new DeleteResponse();
		} catch (final DatabaseException dbException) {
			throw dbError(config, currentUser, dbException);
		}
	}
}
