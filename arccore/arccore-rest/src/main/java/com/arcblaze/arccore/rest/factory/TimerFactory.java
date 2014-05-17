package com.arcblaze.arccore.rest.factory;

import static org.apache.commons.lang.Validate.notNull;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

import org.apache.commons.lang.StringUtils;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;

/**
 * Provides access to {@link Timer} objects within the REST resource classes, pre-configured with the REST end-point
 * associated with the request.
 */
public class TimerFactory extends BaseFactory<Timer> {
    private final static Logger log = LoggerFactory.getLogger(TimerFactory.class);

    private final HttpServletRequest request;
    private final MetricRegistry metricRegistry;

    /**
     * @param request
     *            the web request from the client
     * @param metricRegistry
     *            the metric registry from which timer objects will be retrieved
     */
    public TimerFactory(final @Context HttpServletRequest request, final @Context MetricRegistry metricRegistry) {
        notNull(request, "Invalid null servlet request");
        notNull(metricRegistry, "Invalid null metric registry");

        this.request = request;
        this.metricRegistry = metricRegistry;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timer provide() {
        log.info(getRequestLog(this.request));
        return this.metricRegistry.timer(getTimerName(this.request));
    }

    /**
     * @param request
     *            the servlet request associated with the client web request
     * 
     * @return a log message describing this web request
     */
    protected String getRequestLog(final HttpServletRequest request) {
        final String userLogin = StringUtils.defaultIfEmpty(request.getRemoteUser(), "-");
        final StringBuilder msg = new StringBuilder();
        msg.append(StringUtils.rightPad(userLogin, 12));
        msg.append(" ");
        msg.append(StringUtils.rightPad(request.getMethod(), 6));
        msg.append(" ");
        msg.append(request.getRequestURI());
        return msg.toString();
    }

    /**
     * @param request
     *            the servlet request associated with the client web request
     * 
     * @return a representative name to use in the timer
     */
    protected String getTimerName(final HttpServletRequest request) {
        return request.getRequestURI() + " " + request.getMethod();
    }

    /**
     * @return a binder that can register this factory
     */
    public static AbstractBinder getBinder() {
        return new AbstractBinder() {
            @Override
            protected void configure() {
                bindFactory(TimerFactory.class).to(Timer.class);
            }
        };
    }
}
