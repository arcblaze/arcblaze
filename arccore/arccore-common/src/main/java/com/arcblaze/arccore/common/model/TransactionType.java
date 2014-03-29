package com.arcblaze.arccore.common.model;

import org.apache.commons.lang.StringUtils;

/**
 * Describes the types of financial transactions that happen in this system.
 */
public enum TransactionType {
	/** Represents a payment received from a customer. */
	PAYMENT,
	/** Represents money being refunded back to a customer. */
	REFUND,
	/** A catch-all for other forms of transactions. */
	OTHER,

	;

	/**
	 * Attempt to convert the provided value into a {@link TransactionType} with
	 * more flexibility than what the {@link #valueOf(String)} method provides.
	 * 
	 * @param value
	 *            the value to attempt conversion into a {@link TransactionType}
	 * 
	 * @return the identified {@link TransactionType}, or {@code null} if the
	 *         conversion fails
	 */
	public static TransactionType parse(final String value) {
		for (final TransactionType transactionType : values())
			if (StringUtils.equalsIgnoreCase(transactionType.name(), value))
				return transactionType;

		return null;
	}
}
