package com.arcblaze.arccore.common.model;

import static org.apache.commons.lang.Validate.notNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;

/**
 * A utility class used to manage a collection of distinct object ids.
 */
public class IdSet extends TreeSet<Integer> {
	private static final long serialVersionUID = 1558528000430338251L;

	/**
	 * Default constructor.
	 */
	public IdSet() {
		// Nothing to do.
	}

	/**
	 * @param ids
	 *            the ids to include in this set
	 */
	public IdSet(final Integer... ids) {
		notNull(ids, "Invalid null ids");

		addAll(Arrays.asList(ids));
	}

	/**
	 * @param ids
	 *            the ids to include in this set
	 */
	public IdSet(final Collection<Integer> ids) {
		if (ids != null)
			addAll(ids);
	}

	/**
	 * @param ids
	 *            a delimited list of the integer ids to be included in this set
	 */
	public IdSet(final String ids) {
		if (StringUtils.isBlank(ids))
			return;

		final String[] idStrs = ids.split("[,;]");
		for (final String idStr : idStrs) {
			try {
				add(Integer.parseInt(StringUtils.trim(idStr)));
			} catch (final NumberFormatException badNumber) {
				throw new IllegalArgumentException("Invalid non-numeric id: "
						+ idStr);
			}
		}
	}
}
