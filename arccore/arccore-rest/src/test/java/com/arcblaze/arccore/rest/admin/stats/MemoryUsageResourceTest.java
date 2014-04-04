package com.arcblaze.arccore.rest.admin.stats;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

import org.junit.Test;
import org.mockito.Mockito;

import com.arcblaze.arccore.rest.admin.stats.MemoryUsageResource.Memory;
import com.arcblaze.arccore.rest.admin.stats.MemoryUsageResource.MemoryInfo;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

/**
 * Perform testing of the system statistics.
 */
public class MemoryUsageResourceTest {
	/**
	 * Test how the resource responds to the heap request.
	 */
	@Test
	public void testHeap() {
		final MemoryUsage memoryUsage = Mockito.mock(MemoryUsage.class);
		Mockito.when(memoryUsage.getUsed()).thenReturn(100000L);
		Mockito.when(memoryUsage.getMax()).thenReturn(1000000L);
		final MemoryMXBean memoryBean = Mockito.mock(MemoryMXBean.class);
		Mockito.when(memoryBean.getHeapMemoryUsage()).thenReturn(memoryUsage);

		final MetricRegistry metricRegistry = new MetricRegistry();
		final Timer timer = metricRegistry.timer("test");

		final MemoryUsageResource resource = new MemoryUsageResource(memoryBean);
		final Memory memory = resource.heap(timer);

		assertNotNull(memory);
		assertEquals(new Integer(10), memory.usage);
	}

	/**
	 * Test how the resource responds to the non-heap request.
	 */
	@Test
	public void testNonHeap() {
		final MemoryUsage memoryUsage = Mockito.mock(MemoryUsage.class);
		Mockito.when(memoryUsage.getUsed()).thenReturn(100000L);
		Mockito.when(memoryUsage.getMax()).thenReturn(1000000L);
		final MemoryMXBean memoryBean = Mockito.mock(MemoryMXBean.class);
		Mockito.when(memoryBean.getNonHeapMemoryUsage())
				.thenReturn(memoryUsage);

		final MetricRegistry metricRegistry = new MetricRegistry();
		final Timer timer = metricRegistry.timer("test");

		final MemoryUsageResource resource = new MemoryUsageResource(memoryBean);
		final Memory memory = resource.nonheap(timer);

		assertNotNull(memory);
		assertEquals(new Integer(10), memory.usage);
	}

	/**
	 * Test how the resource responds to the info request.
	 */
	@Test
	public void testInfo() {
		final MemoryUsage heapUsage = Mockito.mock(MemoryUsage.class);
		Mockito.when(heapUsage.getUsed()).thenReturn(10000000L);
		Mockito.when(heapUsage.getMax()).thenReturn(100000000L);
		final MemoryUsage nonHeapUsage = Mockito.mock(MemoryUsage.class);
		Mockito.when(nonHeapUsage.getUsed()).thenReturn(25000000L);
		Mockito.when(nonHeapUsage.getMax()).thenReturn(100000000L);
		final MemoryMXBean memoryBean = Mockito.mock(MemoryMXBean.class);
		Mockito.when(memoryBean.getHeapMemoryUsage()).thenReturn(heapUsage);
		Mockito.when(memoryBean.getNonHeapMemoryUsage()).thenReturn(
				nonHeapUsage);

		final MetricRegistry metricRegistry = new MetricRegistry();
		final Timer timer = metricRegistry.timer("test");

		final MemoryUsageResource resource = new MemoryUsageResource(memoryBean);
		final MemoryInfo info = resource.info(timer);

		assertNotNull(info);
		assertEquals("9.54", info.usedHeap.toPlainString());
		assertEquals("95.37", info.maxHeap.toPlainString());
		assertEquals("10.00", info.heapPctUsed.toPlainString());
		assertEquals("23.84", info.usedNonHeap.toPlainString());
		assertEquals("95.37", info.maxNonHeap.toPlainString());
		assertEquals("25.00", info.nonHeapPctUsed.toPlainString());
	}
}
