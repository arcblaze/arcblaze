package com.arcblaze.arccore.rest.admin.stats;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang.time.DateUtils;

import com.arcblaze.arccore.common.config.Config;
import com.arcblaze.arccore.common.model.User;
import com.arcblaze.arccore.db.DaoFactory;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arccore.db.dao.TransactionDao;
import com.arcblaze.arccore.db.dao.UserDao;
import com.arcblaze.arccore.rest.BaseResource;
import com.codahale.metrics.Timer;

/**
 * The REST end-point for retrieving system statistics.
 */
@Path("/admin/stats/system")
public class SystemStatsResource extends BaseResource {
    @XmlRootElement
    static class SystemStat {
        @XmlElement
        public String name;

        @XmlElement
        public String value;
    }

    @XmlRootElement
    static class Stats {
        @XmlElement(name = "stats")
        public List<SystemStat> statList;
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
     * @return the relevant system statistics
     * 
     * @throws DatabaseException
     *             if there is an error communicating with the back-end
     */
    @GET
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Stats getStats(@Context final SecurityContext security, @Context final Config config,
            @Context final DaoFactory daoFactory, @Context final Timer timer) throws DatabaseException {
        try (final Timer.Context timerContext = timer.time()) {
            final List<FutureTask<SystemStat>> tasks = Arrays.asList(getRevenueYTD(daoFactory),
                    getRevenueYear(daoFactory), getActiveUsers(daoFactory), getActiveCompanies(daoFactory));

            for (final FutureTask<SystemStat> task : tasks)
                task.run();

            final List<SystemStat> statList = new ArrayList<>(tasks.size());
            for (final FutureTask<SystemStat> task : tasks)
                statList.add(task.get());

            final Stats stats = new Stats();
            stats.statList = statList;
            return stats;
        } catch (final InterruptedException | ExecutionException error) {
            throw serverError(config, (User) security.getUserPrincipal(), error);
        }
    }

    protected FutureTask<SystemStat> getRevenueYTD(final DaoFactory daoFactory) {
        return new FutureTask<>(new Callable<SystemStat>() {
            @Override
            public SystemStat call() throws DatabaseException {
                final TransactionDao transactionDao = daoFactory.getTransactionDao();
                final Date tomorrow = DateUtils.addDays(new Date(), 1);
                final Date jan1 = DateUtils.truncate(new Date(), Calendar.YEAR);

                final SystemStat revenueYTD = new SystemStat();
                revenueYTD.name = "Revenue YTD";
                revenueYTD.value = String.format("$%.2f", transactionDao.amountBetween(jan1, tomorrow));
                return revenueYTD;
            }
        });
    }

    protected FutureTask<SystemStat> getRevenueYear(final DaoFactory daoFactory) {
        return new FutureTask<>(new Callable<SystemStat>() {
            @Override
            public SystemStat call() throws DatabaseException {
                final TransactionDao transactionDao = daoFactory.getTransactionDao();
                final Date tomorrow = DateUtils.addDays(new Date(), 1);
                final Date yearAgo = DateUtils.addDays(new Date(), -365);

                final SystemStat revenueYear = new SystemStat();
                revenueYear.name = "Revenue Year";
                revenueYear.value = String.format("$%.2f", transactionDao.amountBetween(yearAgo, tomorrow));
                return revenueYear;
            }
        });
    }

    protected FutureTask<SystemStat> getActiveUsers(final DaoFactory daoFactory) {
        return new FutureTask<>(new Callable<SystemStat>() {
            @Override
            public SystemStat call() throws DatabaseException {
                final UserDao userDao = daoFactory.getUserDao();
                final SystemStat activeUsers = new SystemStat();
                activeUsers.name = "Active Users";
                activeUsers.value = String.valueOf(userDao.count(false));
                return activeUsers;
            }
        });
    }

    protected FutureTask<SystemStat> getActiveCompanies(final DaoFactory daoFactory) {
        return new FutureTask<>(new Callable<SystemStat>() {
            @Override
            public SystemStat call() throws DatabaseException {
                final UserDao companyDao = daoFactory.getUserDao();
                final SystemStat activeCompanies = new SystemStat();
                activeCompanies.name = "Active Companies";
                activeCompanies.value = String.valueOf(companyDao.count(false));
                return activeCompanies;
            }
        });
    }
}
