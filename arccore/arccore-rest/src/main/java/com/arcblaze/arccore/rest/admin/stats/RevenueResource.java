package com.arcblaze.arccore.rest.admin.stats;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map.Entry;
import java.util.SortedMap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.arcblaze.arccore.db.DaoFactory;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arccore.db.dao.TransactionDao;
import com.arcblaze.arccore.rest.BaseResource;
import com.codahale.metrics.Timer;

/**
 * The REST end-point for retrieving system revenue statistics.
 */
@Path("/admin/stats/revenue")
public class RevenueResource extends BaseResource {
	/** The format used to parse dates passed into this resource. */
	private final static String[] FMT = { "yyyy-MM-dd" };

	/**
	 * @param daoFactory
	 *            used to communicate with the back-end database
	 * @param timer
	 *            tracks performance metrics of this REST end-point
	 * @param beginStr
	 *            the beginning date range to use, possibly null
	 * @param endStr
	 *            the ending date range to use, possibly null
	 * 
	 * @return the relevant revenue information
	 * 
	 * @throws DatabaseException
	 *             if there is an error communicating with the back-end
	 */
	@GET
	@Produces({ MediaType.TEXT_PLAIN })
	public String getRevenue(@Context final DaoFactory daoFactory,
			@Context final Timer timer,
			@QueryParam("begin") final String beginStr,
			@QueryParam("end") final String endStr) throws DatabaseException {
		try (final Timer.Context timerContext = timer.time()) {
			final Date begin = StringUtils.isBlank(beginStr) ? DateUtils
					.truncate(DateUtils.addDays(new Date(), -365),
							Calendar.MONTH) : DateUtils
					.parseDate(beginStr, FMT);
			final Date end = StringUtils.isBlank(endStr) ? DateUtils.addDays(
					new Date(), 1) : DateUtils.parseDate(endStr, FMT);

			final TransactionDao dao = daoFactory.getTransactionDao();
			final SortedMap<Date, BigDecimal> data = dao.getSumByMonth(begin,
					end);

			final StringBuilder ret = new StringBuilder();
			ret.append("index\tamount\n");
			int index = 1;
			for (final Entry<Date, BigDecimal> entry : data.entrySet()) {
				ret.append(index++);
				ret.append("\t");
				ret.append(entry.getValue().toPlainString());
				ret.append("\n");
			}
			return ret.toString();
		} catch (final ParseException badDate) {
			throw badRequest("Invalid date: " + badDate.getMessage());
		}
	}
}
