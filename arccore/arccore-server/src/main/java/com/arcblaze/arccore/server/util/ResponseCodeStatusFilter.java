package com.arcblaze.arccore.server.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.ws.rs.core.Response;

import com.codahale.metrics.servlet.AbstractInstrumentedFilter;
import com.codahale.metrics.servlet.InstrumentedFilter;

/**
 * A status filter that adds support for more/different status codes compared to the default {@link InstrumentedFilter}
 * from codahale.
 */
public class ResponseCodeStatusFilter extends AbstractInstrumentedFilter {
    /** The name prefix to include in the metric response info. */
    private static final String NAME_PREFIX = "responseCodes.";

    /**
     * Creates a new instance of the filter.
     */
    public ResponseCodeStatusFilter() {
        super(InstrumentedFilter.REGISTRY_ATTRIBUTE, createMeterNamesByStatusCode(), NAME_PREFIX + "Other");
    }

    private static Map<Integer, String> createMeterNamesByStatusCode() {
        final Map<Integer, String> meterNamesByStatusCode = new HashMap<>();
        for (final Response.Status status : getSupportedStatusCodes())
            meterNamesByStatusCode.put(status.getStatusCode(), NAME_PREFIX + getStatusCodeName(status));
        return meterNamesByStatusCode;
    }

    private static Set<Response.Status> getSupportedStatusCodes() {
        return new TreeSet<>(Arrays.asList(Response.Status.OK, Response.Status.BAD_REQUEST, Response.Status.FORBIDDEN,
                Response.Status.NOT_FOUND, Response.Status.INTERNAL_SERVER_ERROR, Response.Status.UNAUTHORIZED,
                Response.Status.METHOD_NOT_ALLOWED));
    }

    private static String getStatusCodeName(final Response.Status status) {
        final StringBuilder str = new StringBuilder();
        boolean underscore = true;
        for (int i = 0; i < status.name().length(); i++) {
            final char c = status.name().charAt(i);
            if (c == '_') {
                str.append(" ");
                underscore = true;
            } else {
                if (underscore) {
                    str.append(Character.toUpperCase(c));
                    underscore = false;
                } else
                    str.append(Character.toLowerCase(c));
            }
        }
        return str.toString();
    }
}
