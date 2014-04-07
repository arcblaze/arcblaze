package com.arcblaze.arccore.server.tasks;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arcblaze.arccore.common.config.Config;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;

/**
 * Responsible for periodically logging system memory usage.
 */
public class MemoryUsageLoggingTask extends BackgroundTask {
	private final static Logger log = LoggerFactory
			.getLogger(MemoryUsageLoggingTask.class);

	/**
	 * @param config
	 *            the system configuration information
	 * @param metricRegistry
	 *            the registry of metrics used to track system performance
	 *            information
	 * @param healthCheckRegistry
	 *            the registry of system health and status information
	 */
	public MemoryUsageLoggingTask(final Config config,
			final MetricRegistry metricRegistry,
			final HealthCheckRegistry healthCheckRegistry) {
		super(config, metricRegistry, healthCheckRegistry);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void schedule(final ScheduledExecutorService executor) {
		executor.scheduleAtFixedRate(this, 0, 3, TimeUnit.MINUTES);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void process() throws BackgroundTaskException {
		final MemoryUsage heap = ManagementFactory.getMemoryMXBean()
				.getHeapMemoryUsage();
		final MemoryUsage nonHeap = ManagementFactory.getMemoryMXBean()
				.getNonHeapMemoryUsage();

		final double usedHeapMegs = heap.getUsed() / 1024d / 1024d;
		final double maxHeapMegs = heap.getMax() / 1024d / 1024d;
		final double heapPctUsed = usedHeapMegs / maxHeapMegs * 100d;

		final double usedNonHeapMegs = nonHeap.getUsed() / 1024d / 1024d;
		final double maxNonHeapMegs = nonHeap.getMax() / 1024d / 1024d;
		final double nonHeapPctUsed = usedNonHeapMegs / maxNonHeapMegs * 100d;

		log.info(String.format("Memory Usage: Heap %.2f%% of %.0fM, "
				+ "Non Heap %.2f%% of %.0fM", heapPctUsed, maxHeapMegs,
				nonHeapPctUsed, maxNonHeapMegs));
	}
}
