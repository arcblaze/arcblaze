package com.arcblaze.arccore.rest.admin.stats;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.BadRequestException;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;

import com.arcblaze.arccore.common.model.Company;
import com.arcblaze.arccore.db.DaoFactory;
import com.arcblaze.arccore.db.DatabaseException;
import com.arcblaze.arccore.db.dao.UserActivityDao;
import com.arcblaze.arccore.db.util.TestDatabase;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

/**
 * Perform testing of the system statistics.
 */
public class ActiveUsersResourceTest {
    /**
     * Test how the resource responds when the provided date parameters are invalid.
     * 
     * @throws DatabaseException
     *             if there is a database issue
     */
    @Test(expected = BadRequestException.class)
    public void testInvalidDates() throws DatabaseException {
        try (final TestDatabase testDatabase = new TestDatabase()) {
            final DaoFactory daoFactory = testDatabase.getDaoFactory();
            final MetricRegistry metricRegistry = new MetricRegistry();
            final Timer timer = metricRegistry.timer("test");

            final ActiveUsersResource resource = new ActiveUsersResource();
            resource.activeUsers(daoFactory, timer, "abcd", "abcd");
        }
    }

    /**
     * Test how the resource responds when the provided date parameters are invalid.
     * 
     * @throws DatabaseException
     *             if there is a database issue
     */
    @Test(expected = BadRequestException.class)
    public void testInvalidEndDate() throws DatabaseException {
        try (final TestDatabase testDatabase = new TestDatabase()) {
            final DaoFactory daoFactory = testDatabase.getDaoFactory();
            final MetricRegistry metricRegistry = new MetricRegistry();
            final Timer timer = metricRegistry.timer("test");

            final ActiveUsersResource resource = new ActiveUsersResource();
            resource.activeUsers(daoFactory, timer, "2014-01-01", "abcd");
        }
    }

    /**
     * Test how the resource responds when the provided start date is null.
     * 
     * @throws DatabaseException
     *             if there is a database issue
     */
    @Test
    public void testNullStartDate() throws DatabaseException {
        final StringBuilder correct = new StringBuilder();
        correct.append("index\tcount\n");
        correct.append("1\t0\n");
        correct.append("2\t0\n");
        correct.append("3\t0\n");
        correct.append("4\t0\n");
        correct.append("5\t0\n");
        correct.append("6\t0\n");
        correct.append("7\t0\n");
        correct.append("8\t0\n");
        correct.append("9\t0\n");
        correct.append("10\t0\n");

        try (final TestDatabase testDatabase = new TestDatabase()) {
            testDatabase.load("hsqldb/db.sql");
            final DaoFactory daoFactory = testDatabase.getDaoFactory();
            final MetricRegistry metricRegistry = new MetricRegistry();
            final Timer timer = metricRegistry.timer("test");

            final ActiveUsersResource resource = new ActiveUsersResource();
            final String data = resource.activeUsers(daoFactory, timer, null, "2014-01-01");

            assertNotNull(data);
            assertEquals(correct.toString(), data);
        }
    }

    /**
     * Test how the resource responds when the provided end date is null.
     * 
     * @throws DatabaseException
     *             if there is a database issue
     */
    @Test
    public void testNullEndDate() throws DatabaseException {
        final StringBuilder correct = new StringBuilder();
        correct.append("index\tcount\n");
        correct.append("1\t0\n");
        correct.append("2\t0\n");
        correct.append("3\t0\n");
        correct.append("4\t0\n");

        try (final TestDatabase testDatabase = new TestDatabase()) {
            testDatabase.load("hsqldb/db.sql");
            final DaoFactory daoFactory = testDatabase.getDaoFactory();
            final MetricRegistry metricRegistry = new MetricRegistry();
            final Timer timer = metricRegistry.timer("test");

            final ActiveUsersResource resource = new ActiveUsersResource();
            final String data = resource.activeUsers(daoFactory, timer, "2014-01-01", null);

            assertNotNull(data);
            assertEquals(correct.toString(), data);
        }
    }

    /**
     * Test how the resource responds with no transaction data available.
     * 
     * @throws DatabaseException
     *             if there is a database issue
     */
    @Test
    public void testNoData() throws DatabaseException {
        final StringBuilder correct = new StringBuilder();
        correct.append("index\tcount\n");
        correct.append("1\t0\n");
        correct.append("2\t0\n");
        correct.append("3\t0\n");
        correct.append("4\t0\n");
        correct.append("5\t0\n");
        correct.append("6\t0\n");
        correct.append("7\t0\n");
        correct.append("8\t0\n");
        correct.append("9\t0\n");
        correct.append("10\t0\n");
        correct.append("11\t0\n");
        correct.append("12\t0\n");
        correct.append("13\t0\n");

        try (final TestDatabase testDatabase = new TestDatabase()) {
            testDatabase.load("hsqldb/db.sql");
            final DaoFactory daoFactory = testDatabase.getDaoFactory();
            final MetricRegistry metricRegistry = new MetricRegistry();
            final Timer timer = metricRegistry.timer("test");

            final ActiveUsersResource resource = new ActiveUsersResource();
            final String data = resource.activeUsers(daoFactory, timer, null, null);

            assertNotNull(data);
            assertEquals(correct.toString(), data);
        }
    }

    /**
     * Test how the resource responds with some transactions available.
     * 
     * @throws DatabaseException
     *             if there is a database issue
     */
    @Test
    public void testWithDataAvailable() throws DatabaseException {
        try (final TestDatabase testDatabase = new TestDatabase()) {
            testDatabase.load("hsqldb/db.sql");
            final DaoFactory daoFactory = testDatabase.getDaoFactory();
            final MetricRegistry metricRegistry = new MetricRegistry();
            final Timer timer = metricRegistry.timer("test");

            final Company company1 = new Company().setName("company1").setActive(true);
            final Company company2 = new Company().setName("company2").setActive(true);
            daoFactory.getCompanyDao().add(company1, company2);

            final Map<Integer, Integer> mapA = new HashMap<>();
            mapA.put(company1.getId(), 22);
            mapA.put(company2.getId(), 43);
            final Map<Integer, Integer> mapB = new HashMap<>();
            mapB.put(company1.getId(), 23);
            mapB.put(company2.getId(), 45);
            final Map<Integer, Integer> mapC = new HashMap<>();
            mapC.put(company1.getId(), 22);
            mapC.put(company2.getId(), 46);
            final Map<Integer, Integer> mapD = new HashMap<>();
            mapD.put(company1.getId(), 21);
            mapD.put(company2.getId(), 45);
            final Map<Integer, Integer> mapE = new HashMap<>();
            mapE.put(company1.getId(), 24);
            mapE.put(company2.getId(), 50);

            final UserActivityDao dao = daoFactory.getUserActivityDao();
            final Date now = new Date();
            dao.setActiveUsers(DateUtils.addMonths(now, -5), mapA);
            dao.setActiveUsers(DateUtils.addMonths(now, -4), mapB);
            dao.setActiveUsers(DateUtils.addMonths(now, -3), mapC);
            dao.setActiveUsers(DateUtils.addMonths(now, -2), mapD);
            dao.setActiveUsers(DateUtils.addMonths(now, -1), mapE);
            final StringBuilder correct = new StringBuilder();
            correct.append("index\tcount\n");
            correct.append("1\t0\n");
            correct.append("2\t0\n");
            correct.append("3\t0\n");
            correct.append("4\t0\n");
            correct.append("5\t0\n");
            correct.append("6\t0\n");
            correct.append("7\t0\n");
            correct.append("8\t65\n");
            correct.append("9\t68\n");
            correct.append("10\t68\n");
            correct.append("11\t66\n");
            correct.append("12\t74\n");
            correct.append("13\t0\n");

            final ActiveUsersResource resource = new ActiveUsersResource();
            final String data = resource.activeUsers(daoFactory, timer, null, null);

            assertNotNull(data);
            assertEquals(correct.toString(), data);
        }
    }

    /**
     * Test how the resource responds with some transactions available.
     * 
     * @throws DatabaseException
     *             if there is a database issue
     */
    @Test
    public void testWithDataAvailableAndDates() throws DatabaseException {
        try (final TestDatabase testDatabase = new TestDatabase()) {
            testDatabase.load("hsqldb/db.sql");
            final DaoFactory daoFactory = testDatabase.getDaoFactory();
            final MetricRegistry metricRegistry = new MetricRegistry();
            final Timer timer = metricRegistry.timer("test");

            final Company company1 = new Company().setName("company1").setActive(true);
            final Company company2 = new Company().setName("company2").setActive(true);
            daoFactory.getCompanyDao().add(company1, company2);

            final Map<Integer, Integer> mapA = new HashMap<>();
            mapA.put(company1.getId(), 22);
            mapA.put(company2.getId(), 43);
            final Map<Integer, Integer> mapB = new HashMap<>();
            mapB.put(company1.getId(), 23);
            mapB.put(company2.getId(), 45);
            final Map<Integer, Integer> mapC = new HashMap<>();
            mapC.put(company1.getId(), 22);
            mapC.put(company2.getId(), 46);
            final Map<Integer, Integer> mapD = new HashMap<>();
            mapD.put(company1.getId(), 21);
            mapD.put(company2.getId(), 45);
            final Map<Integer, Integer> mapE = new HashMap<>();
            mapE.put(company1.getId(), 24);
            mapE.put(company2.getId(), 50);

            final UserActivityDao dao = daoFactory.getUserActivityDao();
            final Date now = new Date();
            dao.setActiveUsers(DateUtils.addMonths(now, -5), mapA);
            dao.setActiveUsers(DateUtils.addMonths(now, -4), mapB);
            dao.setActiveUsers(DateUtils.addMonths(now, -3), mapC);
            dao.setActiveUsers(DateUtils.addMonths(now, -2), mapD);
            dao.setActiveUsers(DateUtils.addMonths(now, -1), mapE);

            final StringBuilder correct = new StringBuilder();
            correct.append("index\tcount\n");
            correct.append("1\t0\n");
            correct.append("2\t0\n");
            correct.append("3\t0\n");
            correct.append("4\t0\n");
            correct.append("5\t0\n");
            correct.append("6\t0\n");
            correct.append("7\t0\n");
            correct.append("8\t65\n");
            correct.append("9\t68\n");
            correct.append("10\t68\n");
            correct.append("11\t66\n");
            correct.append("12\t74\n");
            correct.append("13\t0\n");

            final String begin = DateFormatUtils.format(DateUtils.addMonths(now, -12), "yyyy-MM-dd");
            final String end = DateFormatUtils.format(now, "yyyy-MM-dd");

            final ActiveUsersResource resource = new ActiveUsersResource();
            final String data = resource.activeUsers(daoFactory, timer, begin, end);

            assertNotNull(data);
            assertEquals(correct.toString(), data);
        }
    }
}
