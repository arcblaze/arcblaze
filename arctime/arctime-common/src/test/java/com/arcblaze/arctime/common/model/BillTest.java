package com.arcblaze.arctime.common.model;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;

/**
 * Perform testing of the bill class.
 */
public class BillTest {
	private final static String[] FMT = { "yyyyMMdd" };

	/**
	 * @throws ParseException
	 *             if there is a date parsing issue
	 */
	@Test
	public void testEquals() throws ParseException {
		final Bill a = new Bill().setTaskId(1).setAssignmentId(2)
				.setDay(DateUtils.parseDate("20140101", FMT)).setHours("8");
		final Bill b = new Bill().setTaskId(1).setAssignmentId(2)
				.setDay(DateUtils.parseDate("20140101", FMT)).setHours("8");
		final Bill c = new Bill().setTaskId(1).setAssignmentId(2)
				.setDay(DateUtils.parseDate("20140101", FMT)).setHours("8.0");
		final Bill d = new Bill().setTaskId(1).setAssignmentId(2)
				.setDay(DateUtils.parseDate("20140101", FMT)).setHours(8.0f);

		assertEquals(a, b);
		assertEquals(a, c);
		assertEquals(a, d);
		assertEquals(b, c);
		assertEquals(b, d);
		assertEquals(c, d);
	}
}
