package com.arcblaze.arccore.server.tasks;

import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arcblaze.arccore.common.config.Config;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;

/**
 * Responsible for periodically performing health checks on all system
 * components.
 */
public class SystemHealthCheckTask extends BackgroundTask {
	private final static Logger log = LoggerFactory
			.getLogger(SystemHealthCheckTask.class);

	/**
	 * @param config
	 *            the system configuration information
	 * @param metricRegistry
	 *            the registry of metrics used to track system performance
	 *            information
	 * @param healthCheckRegistry
	 *            the registry of system health and status information
	 */
	public SystemHealthCheckTask(final Config config,
			final MetricRegistry metricRegistry,
			final HealthCheckRegistry healthCheckRegistry) {
		super(config, metricRegistry, healthCheckRegistry);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void schedule(final ScheduledExecutorService executor) {
		executor.scheduleAtFixedRate(this, 1, 3, TimeUnit.MINUTES);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void process() throws BackgroundTaskException {
		final SortedMap<String, Result> results = getHealthCheckRegistry()
				.runHealthChecks();

		int healthy = 0, unhealthy = 0;
		for (final Result result : results.values()) {
			healthy += result.isHealthy() ? 1 : 0;
			unhealthy += result.isHealthy() ? 0 : 1;
		}

		if (unhealthy > 0)
			log.warn("Health Checks: {} healthy, {} unhealthy", healthy,
					unhealthy);
		else
			log.info("Health Checks: {} healthy, {} unhealthy", healthy,
					unhealthy);

		if (unhealthy > 0) {
			for (final Entry<String, Result> entry : results.entrySet()) {
				final Result result = entry.getValue();
				if (result.isHealthy())
					continue;

				log.error("{} => {}", entry.getKey(), entry.getValue());
			}
		}
	}
}
