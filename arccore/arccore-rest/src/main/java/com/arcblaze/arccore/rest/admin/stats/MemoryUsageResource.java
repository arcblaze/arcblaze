package com.arcblaze.arccore.rest.admin.stats;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.math.BigDecimal;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.arcblaze.arccore.rest.BaseResource;
import com.codahale.metrics.Timer;

/**
 * The REST end-point for retrieving memory usage statistics.
 */
@Path("/admin/stats/memory")
public class MemoryUsageResource extends BaseResource {
    @XmlRootElement
    static class Memory {
        @XmlElement
        public Integer usage;
    }

    @XmlRootElement
    static class MemoryInfo {
        @XmlElement
        public BigDecimal usedHeap;
        @XmlElement
        public BigDecimal maxHeap;
        @XmlElement
        public BigDecimal heapPctUsed;
        @XmlElement
        public BigDecimal usedNonHeap;
        @XmlElement
        public BigDecimal maxNonHeap;
        @XmlElement
        public BigDecimal nonHeapPctUsed;
    }

    private final MemoryMXBean memoryMXBean;

    /**
     * Default constructor.
     */
    public MemoryUsageResource() {
        this.memoryMXBean = ManagementFactory.getMemoryMXBean();
    }

    /**
     * @param memoryMXBean
     *            the bean used to retrieve memory usage information
     */
    public MemoryUsageResource(final MemoryMXBean memoryMXBean) {
        this.memoryMXBean = memoryMXBean;
    }

    /**
     * @param timer
     *            tracks performance metrics of this REST end-point
     * 
     * @return the heap memory usage for this system
     */
    @GET
    @Path("heap")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Memory heap(@Context final Timer timer) {
        try (final Timer.Context timerContext = timer.time()) {
            final MemoryUsage heap = this.memoryMXBean.getHeapMemoryUsage();

            final double usedHeapMB = heap.getUsed() / 1024d / 1024d;
            final double maxHeapMB = heap.getMax() / 1024d / 1024d;
            final double heapPctUsed = usedHeapMB / maxHeapMB * 100d;

            final Memory memory = new Memory();
            memory.usage = (int) heapPctUsed;
            return memory;
        }
    }

    /**
     * @param timer
     *            tracks performance metrics of this REST end-point
     * 
     * @return the non-heap memory usage for this system
     */
    @GET
    @Path("nonheap")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public Memory nonheap(@Context final Timer timer) {
        try (final Timer.Context timerContext = timer.time()) {
            final MemoryUsage nonHeap = this.memoryMXBean.getNonHeapMemoryUsage();

            final double usedNonHeapMB = nonHeap.getUsed() / 1024d / 1024d;
            final double maxNonHeapMB = nonHeap.getMax() / 1024d / 1024d;
            final double nonHeapPctUsed = usedNonHeapMB / maxNonHeapMB * 100d;

            final Memory memory = new Memory();
            memory.usage = (int) nonHeapPctUsed;
            return memory;
        }
    }

    /**
     * @param timer
     *            tracks performance metrics of this REST end-point
     * 
     * @return an overview of the current system memory statistics
     */
    @GET
    @Path("info")
    @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
    public MemoryInfo info(@Context final Timer timer) {
        try (final Timer.Context timerContext = timer.time()) {
            final MemoryUsage heap = this.memoryMXBean.getHeapMemoryUsage();
            final MemoryUsage nonHeap = this.memoryMXBean.getNonHeapMemoryUsage();

            final MemoryInfo info = new MemoryInfo();

            final double usedHeap = heap.getUsed() / 1024d / 1024d;
            final double maxHeap = heap.getMax() / 1024d / 1024d;
            final double heapPctUsed = usedHeap / maxHeap * 100d;

            final double usedNonHeap = nonHeap.getUsed() / 1024d / 1024d;
            final double maxNonHeap = nonHeap.getMax() / 1024d / 1024d;
            final double nonHeapPctUsed = usedNonHeap / maxNonHeap * 100d;

            info.usedHeap = new BigDecimal(usedHeap).setScale(2, BigDecimal.ROUND_HALF_UP);
            info.maxHeap = new BigDecimal(maxHeap).setScale(2, BigDecimal.ROUND_HALF_UP);
            info.heapPctUsed = new BigDecimal(heapPctUsed).setScale(2, BigDecimal.ROUND_HALF_UP);

            info.usedNonHeap = new BigDecimal(usedNonHeap).setScale(2, BigDecimal.ROUND_HALF_UP);
            info.maxNonHeap = new BigDecimal(maxNonHeap).setScale(2, BigDecimal.ROUND_HALF_UP);
            info.nonHeapPctUsed = new BigDecimal(nonHeapPctUsed).setScale(2, BigDecimal.ROUND_HALF_UP);

            return info;
        }
    }
}
