package com.arcblaze.arctime.common.model;

import org.apache.commons.lang.StringUtils;

/**
 * Describes the types of enrichment to support when retrieving model objects in this system.
 */
public enum Enrichment {
    /** Enrich timesheet data with the user that owns each timesheet. */
    USERS,
    /** Enrich timesheet data with the associated pay period. */
    PAY_PERIODS,
    /** Enrich timesheet data with the associated holidays. */
    HOLIDAYS,
    /** Enrich timesheet data with audit log information. */
    AUDIT_LOGS,
    /** Enrich timesheet data with the associated tasks. */
    TASKS,
    /** Enrich timesheet data with the hours billed. */
    BILLS,

    ;

    /**
     * Attempt to convert the provided value into an {@link Enrichment} with more flexibility than what the
     * {@link #valueOf(String)} method provides.
     * 
     * @param value
     *            the value to attempt conversion into a {@link Enrichment}
     * 
     * @return the identified {@link Enrichment}, or {@code null} if the conversion fails
     */
    public static Enrichment parse(final String value) {
        for (final Enrichment enrichment : values())
            if (StringUtils.equalsIgnoreCase(enrichment.name(), value))
                return enrichment;

        return null;
    }
}
