package com.arcblaze.arctime.rest.finance;

import java.util.Set;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.arcblaze.arccore.common.config.Config;
import com.arcblaze.arccore.common.model.Transaction;
import com.arcblaze.arccore.common.model.User;
import com.arcblaze.arccore.db.DaoFactory;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arccore.rest.BaseResource;
import com.codahale.metrics.Timer;

/**
 * The REST end-point for tracking transactions.
 */
@Path("/finance/transaction")
public class TransactionResource extends BaseResource {
	@XmlRootElement
	static class AllResponse {
		@XmlElement
		public final boolean success = true;

		@XmlElement
		public final String msg = "The transactions were retrieved successfully.";

		@XmlElement
		public Set<Transaction> transactions;

		@XmlElement
		public Integer offset;

		@XmlElement
		public Integer limit;

		@XmlElement
		public Integer total;
	}

	/**
	 * @param security
	 *            the security information associated with the request
	 * @param config
	 *            the system configuration information
	 * @param daoFactory
	 *            used to communicate with the back-end database
	 * @param timer
	 *            tracks performance metrics of this REST end-point
	 * @param filter
	 *            the search filter to use to restrict results
	 * @param limit
	 *            the maximum number of items to be retrieved
	 * @param offset
	 *            the offset into the items to be retrieved
	 * 
	 * @return the transactions available for the current user's company
	 * 
	 * @throws DatabaseException
	 *             if there is an error communicating with the back-end
	 */
	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public AllResponse all(@Context final SecurityContext security,
			@Context final Config config, @Context final DaoFactory daoFactory,
			@Context final Timer timer,
			@QueryParam("filter") final String filter,
			@QueryParam("limit") @DefaultValue("100") final Integer limit,
			@QueryParam("start") @DefaultValue("0") final Integer offset)
			throws DatabaseException {
		final User currentUser = (User) security.getUserPrincipal();
		try (final Timer.Context timerContext = timer.time()) {
			final AllResponse response = new AllResponse();
			response.transactions = daoFactory.getTransactionDao()
					.searchForCompany(currentUser.getCompanyId(), filter,
							limit, offset);
			response.total = daoFactory.getTransactionDao().count(filter);
			response.limit = limit;
			response.offset = offset;
			return response;
		} catch (final DatabaseException dbException) {
			throw dbError(config, currentUser, dbException);
		}
	}
}
