package com.arcblaze.arctime.server;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.configuration.ConfigurationException;

import com.arcblaze.arccore.common.config.Config;
import com.arcblaze.arccore.common.model.Role;
import com.arcblaze.arccore.db.DaoFactory;
import com.arcblaze.arccore.rest.BaseApplication;
import com.arcblaze.arccore.server.BaseServer;
import com.arcblaze.arccore.server.tasks.BackgroundTask;
import com.arcblaze.arctime.db.ArcTimeDaoFactory;
import com.arcblaze.arctime.rest.ArcTimeApplication;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;

/**
 * The entry point into this system.
 */
public class ArcTimeServer extends BaseServer {
	/**
	 * @throws ConfigurationException
	 *             if there is a problem loading the system configuration
	 *             information
	 */
	public ArcTimeServer() throws ConfigurationException {
		// Nothing to do.
	}

	/**
	 * @param args
	 *            the command-line arguments
	 * 
	 * @throws ConfigurationException
	 *             if there is a problem loading the system configuration
	 *             information
	 */
	public static void main(final String... args) throws ConfigurationException {
		new ArcTimeServer().start();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<? extends BaseApplication> getApplicationClass(
			final Config config) {
		return ArcTimeApplication.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DaoFactory getDaoFactory(final Config config) {
		return new ArcTimeDaoFactory(config);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, List<Role>> getEndpointRoleMap(final Config config) {
		final Role admin = new Role("ADMIN");
		final Role manager = new Role("MANAGER");
		final Role payroll = new Role("PAYROLL");
		final Role supervisor = new Role("SUPERVISOR");
		final Role user = new Role("USER");

		final Map<String, List<Role>> map = new HashMap<>();
		map.put("/rest/admin/*", Arrays.asList(admin));
		map.put("/rest/manager/*", Arrays.asList(admin, manager));
		map.put("/rest/payroll/*", Arrays.asList(admin, payroll));
		map.put("/rest/supervisor/*", Arrays.asList(admin, supervisor));
		map.put("/rest/user/*", Arrays.asList(admin, user));

		map.put("/admin/*", Arrays.asList(admin));
		map.put("/manager/*", Arrays.asList(admin, manager));
		map.put("/payroll/*", Arrays.asList(admin, payroll));
		map.put("/supervisor/*", Arrays.asList(admin, supervisor));
		map.put("/user/*", Arrays.asList(admin, user));
		return map;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<Role> getSystemRoles(final Config config) {
		return new TreeSet<>(Arrays.asList(new Role("USER"),
				new Role("MANAGER"), new Role("SUPERVISOR"),
				new Role("PAYROLL"), new Role("ADMIN")));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<BackgroundTask> getBackgroundTasks(final Config config,
			final MetricRegistry metricRegistry,
			final HealthCheckRegistry healthCheckRegistry) {
		return null;
	}
}
