package com.arcblaze.arctime.common.model;

import org.apache.commons.lang.StringUtils;

/**
 * Describes the supported types of pay periods in this system.
 */
public enum PayPeriodType {
    /** A pay period every month. */
    MONTHLY,
    /** Two pay periods per month. */
    SEMI_MONTHLY,
    /** One pay period every two weeks. */
    BI_WEEKLY,
    /** One pay period every week. */
    WEEKLY,
    /** Some other custom pay period. */
    CUSTOM,

    ;

    /**
     * Attempt to convert the provided value back into a personnel type with more flexibility than what the
     * {@link #valueOf(String)} method provides.
     * 
     * @param value
     *            the value to attempt conversion into a {@link PayPeriodType}
     * 
     * @return the identified {@link PayPeriodType}, or {@code null} if the conversion fails
     */
    public static PayPeriodType parse(final String value) {
        for (final PayPeriodType payPeriodType : values())
            if (StringUtils.equalsIgnoreCase(payPeriodType.name(), value))
                return payPeriodType;

        return null;
    }
}
