package com.arcblaze.arctime.common.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Test;

import com.arcblaze.arctime.common.model.util.HolidayConfigurationException;

/**
 * Perform testing on the pay period class.
 */
public class PayPeriodTest {
	private final static String[] FMT = { "yyyyMMdd HHmmss", "yyyyMMdd" };

	/**
	 * Test the {@link PayPeriod#contains(java.util.Date)} method.
	 * 
	 * @throws ParseException
	 *             if there is a date processing issue
	 */
	@Test
	public void testContainsDate() throws ParseException {
		final PayPeriod p = new PayPeriod().setType(PayPeriodType.WEEKLY)
				.setCompanyId(1).setBegin(DateUtils.parseDate("20140101", FMT))
				.setEnd(DateUtils.parseDate("20140107", FMT));

		assertTrue(p.contains(DateUtils.parseDate("20140101", FMT)));
		assertTrue(p.contains(DateUtils.parseDate("20140101 230000", FMT)));
		assertTrue(p.contains(DateUtils.parseDate("20140107", FMT)));
		assertTrue(p.contains(DateUtils.parseDate("20140107 230000", FMT)));
		assertFalse(p.contains(DateUtils.parseDate("20131231 235959", FMT)));
		assertFalse(p.contains(DateUtils.parseDate("20140108", FMT)));
	}

	/**
	 * Test the {@link PayPeriod#contains(Holiday)} method.
	 * 
	 * @throws ParseException
	 *             if there is a date processing issue
	 * @throws HolidayConfigurationException
	 *             if there is a holiday configuration issue
	 */
	@Test
	public void testContainsHoliday() throws ParseException,
			HolidayConfigurationException {
		final PayPeriod p1 = new PayPeriod().setType(PayPeriodType.WEEKLY)
				.setCompanyId(1).setBegin(DateUtils.parseDate("20140101", FMT))
				.setEnd(DateUtils.parseDate("20140107", FMT));
		final PayPeriod p2 = new PayPeriod().setType(PayPeriodType.MONTHLY)
				.setCompanyId(1).setBegin(DateUtils.parseDate("20140201", FMT))
				.setEnd(DateUtils.parseDate("20140228", FMT));

		final Holiday h1 = new Holiday().setConfig("January 1st Observance");
		final Holiday h2 = new Holiday().setConfig("1st Thursday in January");
		final Holiday h3 = new Holiday().setConfig("2nd Thursday in January");
		final Holiday h4 = new Holiday().setConfig("February 1st");
		final Holiday h5 = new Holiday().setConfig("February 1st Observance");

		assertTrue(p1.contains(h1));
		assertTrue(p1.contains(h2));
		assertFalse(p1.contains(h3));
		assertFalse(p1.contains(h4));
		assertFalse(p1.contains(h5));
		assertFalse(p2.contains(h1));
		assertFalse(p2.contains(h2));
		assertFalse(p2.contains(h3));
		assertTrue(p2.contains(h4));
		assertFalse(p2.contains(h5));
	}

	/**
	 * Test the {@link PayPeriod#isBefore(java.util.Date)} method.
	 * 
	 * @throws ParseException
	 *             if there is a date processing issue
	 */
	@Test
	public void testIsBefore() throws ParseException {
		final PayPeriod p = new PayPeriod().setType(PayPeriodType.WEEKLY)
				.setCompanyId(1).setBegin(DateUtils.parseDate("20140101", FMT))
				.setEnd(DateUtils.parseDate("20140107", FMT));

		assertTrue(p.isBefore(DateUtils.parseDate("20140801", FMT)));
		assertFalse(p.isBefore(DateUtils.parseDate("20140107 230000", FMT)));
		assertFalse(p.isBefore(DateUtils.parseDate("20140104", FMT)));
		assertTrue(p.isBefore(DateUtils.parseDate("20140108 230000", FMT)));
		assertFalse(p.isBefore(DateUtils.parseDate("20131231 235959", FMT)));
		assertTrue(p.isBefore(DateUtils.parseDate("20140201", FMT)));
	}

	/**
	 * Test the {@link PayPeriod#isAfter(java.util.Date)} method.
	 * 
	 * @throws ParseException
	 *             if there is a date processing issue
	 */
	@Test
	public void testIsAfter() throws ParseException {
		final PayPeriod p = new PayPeriod().setType(PayPeriodType.WEEKLY)
				.setCompanyId(1).setBegin(DateUtils.parseDate("20140101", FMT))
				.setEnd(DateUtils.parseDate("20140107", FMT));

		assertFalse(p.isAfter(DateUtils.parseDate("20140108", FMT)));
		assertFalse(p.isAfter(DateUtils.parseDate("20140101 230000", FMT)));
		assertFalse(p.isAfter(DateUtils.parseDate("20140101", FMT)));
		assertFalse(p.isAfter(DateUtils.parseDate("20140108 230000", FMT)));
		assertTrue(p.isAfter(DateUtils.parseDate("20131231 235959", FMT)));
		assertTrue(p.isAfter(DateUtils.parseDate("20131231", FMT)));
	}

	/**
	 * Test the {@link PayPeriod#getPrevious()} method.
	 * 
	 * @throws ParseException
	 *             if there is a date processing issue
	 */
	@Test
	public void testGetPreviousWeekly() throws ParseException {
		final PayPeriod p = new PayPeriod().setType(PayPeriodType.WEEKLY)
				.setCompanyId(1).setBegin(DateUtils.parseDate("20140101", FMT))
				.setEnd(DateUtils.parseDate("20140107", FMT));

		final PayPeriod n1 = p.getPrevious();
		assertEquals(p.getCompanyId(), n1.getCompanyId());
		assertEquals(p.getType(), n1.getType());
		assertEquals("20131225", DateFormatUtils.format(n1.getBegin(), FMT[1]));
		assertEquals("20131231", DateFormatUtils.format(n1.getEnd(), FMT[1]));

		final PayPeriod n2 = n1.getPrevious();
		assertEquals(p.getCompanyId(), n2.getCompanyId());
		assertEquals(p.getType(), n2.getType());
		assertEquals("20131218", DateFormatUtils.format(n2.getBegin(), FMT[1]));
		assertEquals("20131224", DateFormatUtils.format(n2.getEnd(), FMT[1]));

		final PayPeriod n3 = n2.getPrevious();
		assertEquals(p.getCompanyId(), n3.getCompanyId());
		assertEquals(p.getType(), n3.getType());
		assertEquals("20131211", DateFormatUtils.format(n3.getBegin(), FMT[1]));
		assertEquals("20131217", DateFormatUtils.format(n3.getEnd(), FMT[1]));

		final PayPeriod n4 = n3.getPrevious();
		assertEquals(p.getCompanyId(), n4.getCompanyId());
		assertEquals(p.getType(), n4.getType());
		assertEquals("20131204", DateFormatUtils.format(n4.getBegin(), FMT[1]));
		assertEquals("20131210", DateFormatUtils.format(n4.getEnd(), FMT[1]));
	}

	/**
	 * Test the {@link PayPeriod#getPrevious()} method.
	 * 
	 * @throws ParseException
	 *             if there is a date processing issue
	 */
	@Test
	public void testGetPreviousBiWeekly() throws ParseException {
		final PayPeriod p = new PayPeriod().setType(PayPeriodType.BI_WEEKLY)
				.setCompanyId(1).setBegin(DateUtils.parseDate("20140101", FMT))
				.setEnd(DateUtils.parseDate("20140114", FMT));

		final PayPeriod n1 = p.getPrevious();
		assertEquals(p.getCompanyId(), n1.getCompanyId());
		assertEquals(p.getType(), n1.getType());
		assertEquals("20131218", DateFormatUtils.format(n1.getBegin(), FMT[1]));
		assertEquals("20131231", DateFormatUtils.format(n1.getEnd(), FMT[1]));

		final PayPeriod n2 = n1.getPrevious();
		assertEquals(p.getCompanyId(), n2.getCompanyId());
		assertEquals(p.getType(), n2.getType());
		assertEquals("20131204", DateFormatUtils.format(n2.getBegin(), FMT[1]));
		assertEquals("20131217", DateFormatUtils.format(n2.getEnd(), FMT[1]));

		final PayPeriod n3 = n2.getPrevious();
		assertEquals(p.getCompanyId(), n3.getCompanyId());
		assertEquals(p.getType(), n3.getType());
		assertEquals("20131120", DateFormatUtils.format(n3.getBegin(), FMT[1]));
		assertEquals("20131203", DateFormatUtils.format(n3.getEnd(), FMT[1]));

		final PayPeriod n4 = n3.getPrevious();
		assertEquals(p.getCompanyId(), n4.getCompanyId());
		assertEquals(p.getType(), n4.getType());
		assertEquals("20131106", DateFormatUtils.format(n4.getBegin(), FMT[1]));
		assertEquals("20131119", DateFormatUtils.format(n4.getEnd(), FMT[1]));
	}

	/**
	 * Test the {@link PayPeriod#getPrevious()} method.
	 * 
	 * @throws ParseException
	 *             if there is a date processing issue
	 */
	@Test
	public void testGetPreviousSemiMonthly() throws ParseException {
		final PayPeriod p = new PayPeriod().setType(PayPeriodType.SEMI_MONTHLY)
				.setCompanyId(1).setBegin(DateUtils.parseDate("20140101", FMT))
				.setEnd(DateUtils.parseDate("20140115", FMT));

		final PayPeriod n1 = p.getPrevious();
		assertEquals(p.getCompanyId(), n1.getCompanyId());
		assertEquals(p.getType(), n1.getType());
		assertEquals("20131216", DateFormatUtils.format(n1.getBegin(), FMT[1]));
		assertEquals("20131231", DateFormatUtils.format(n1.getEnd(), FMT[1]));

		final PayPeriod n2 = n1.getPrevious();
		assertEquals(p.getCompanyId(), n2.getCompanyId());
		assertEquals(p.getType(), n2.getType());
		assertEquals("20131201", DateFormatUtils.format(n2.getBegin(), FMT[1]));
		assertEquals("20131215", DateFormatUtils.format(n2.getEnd(), FMT[1]));

		final PayPeriod n3 = n2.getPrevious();
		assertEquals(p.getCompanyId(), n3.getCompanyId());
		assertEquals(p.getType(), n3.getType());
		assertEquals("20131116", DateFormatUtils.format(n3.getBegin(), FMT[1]));
		assertEquals("20131130", DateFormatUtils.format(n3.getEnd(), FMT[1]));

		final PayPeriod n4 = n3.getPrevious();
		assertEquals(p.getCompanyId(), n4.getCompanyId());
		assertEquals(p.getType(), n4.getType());
		assertEquals("20131101", DateFormatUtils.format(n4.getBegin(), FMT[1]));
		assertEquals("20131115", DateFormatUtils.format(n4.getEnd(), FMT[1]));
	}

	/**
	 * Test the {@link PayPeriod#getPrevious()} method.
	 * 
	 * @throws ParseException
	 *             if there is a date processing issue
	 */
	@Test
	public void testGetPreviousSemiMonthlyMidMonth() throws ParseException {
		final PayPeriod p = new PayPeriod().setType(PayPeriodType.SEMI_MONTHLY)
				.setCompanyId(1).setBegin(DateUtils.parseDate("20140110", FMT))
				.setEnd(DateUtils.parseDate("20140125", FMT));

		final PayPeriod n1 = p.getPrevious();
		assertEquals(p.getCompanyId(), n1.getCompanyId());
		assertEquals(p.getType(), n1.getType());
		assertEquals("20131226", DateFormatUtils.format(n1.getBegin(), FMT[1]));
		assertEquals("20140109", DateFormatUtils.format(n1.getEnd(), FMT[1]));

		final PayPeriod n2 = n1.getPrevious();
		assertEquals(p.getCompanyId(), n2.getCompanyId());
		assertEquals(p.getType(), n2.getType());
		assertEquals("20131210", DateFormatUtils.format(n2.getBegin(), FMT[1]));
		assertEquals("20131225", DateFormatUtils.format(n2.getEnd(), FMT[1]));

		final PayPeriod n3 = n2.getPrevious();
		assertEquals(p.getCompanyId(), n3.getCompanyId());
		assertEquals(p.getType(), n3.getType());
		assertEquals("20131126", DateFormatUtils.format(n3.getBegin(), FMT[1]));
		assertEquals("20131209", DateFormatUtils.format(n3.getEnd(), FMT[1]));

		final PayPeriod n4 = n3.getPrevious();
		assertEquals(p.getCompanyId(), n4.getCompanyId());
		assertEquals(p.getType(), n4.getType());
		assertEquals("20131110", DateFormatUtils.format(n4.getBegin(), FMT[1]));
		assertEquals("20131125", DateFormatUtils.format(n4.getEnd(), FMT[1]));
	}

	/**
	 * Test the {@link PayPeriod#getPrevious()} method.
	 * 
	 * @throws ParseException
	 *             if there is a date processing issue
	 */
	@Test
	public void testGetPreviousMonthly() throws ParseException {
		final PayPeriod p = new PayPeriod().setType(PayPeriodType.MONTHLY)
				.setCompanyId(1).setBegin(DateUtils.parseDate("20140101", FMT))
				.setEnd(DateUtils.parseDate("20140131", FMT));

		final PayPeriod n1 = p.getPrevious();
		assertEquals(p.getCompanyId(), n1.getCompanyId());
		assertEquals(p.getType(), n1.getType());
		assertEquals("20131201", DateFormatUtils.format(n1.getBegin(), FMT[1]));
		assertEquals("20131231", DateFormatUtils.format(n1.getEnd(), FMT[1]));

		final PayPeriod n2 = n1.getPrevious();
		assertEquals(p.getCompanyId(), n2.getCompanyId());
		assertEquals(p.getType(), n2.getType());
		assertEquals("20131101", DateFormatUtils.format(n2.getBegin(), FMT[1]));
		assertEquals("20131130", DateFormatUtils.format(n2.getEnd(), FMT[1]));

		final PayPeriod n3 = n2.getPrevious();
		assertEquals(p.getCompanyId(), n3.getCompanyId());
		assertEquals(p.getType(), n3.getType());
		assertEquals("20131001", DateFormatUtils.format(n3.getBegin(), FMT[1]));
		assertEquals("20131031", DateFormatUtils.format(n3.getEnd(), FMT[1]));

		final PayPeriod n4 = n3.getPrevious();
		assertEquals(p.getCompanyId(), n4.getCompanyId());
		assertEquals(p.getType(), n4.getType());
		assertEquals("20130901", DateFormatUtils.format(n4.getBegin(), FMT[1]));
		assertEquals("20130930", DateFormatUtils.format(n4.getEnd(), FMT[1]));
	}

	/**
	 * Test the {@link PayPeriod#getPrevious()} method.
	 * 
	 * @throws ParseException
	 *             if there is a date processing issue
	 */
	@Test
	public void testGetPreviousMonthlyMidMonth() throws ParseException {
		final PayPeriod p = new PayPeriod().setType(PayPeriodType.MONTHLY)
				.setCompanyId(1).setBegin(DateUtils.parseDate("20140115", FMT))
				.setEnd(DateUtils.parseDate("20140214", FMT));

		final PayPeriod n1 = p.getPrevious();
		assertEquals(p.getCompanyId(), n1.getCompanyId());
		assertEquals(p.getType(), n1.getType());
		assertEquals("20131215", DateFormatUtils.format(n1.getBegin(), FMT[1]));
		assertEquals("20140114", DateFormatUtils.format(n1.getEnd(), FMT[1]));

		final PayPeriod n2 = n1.getPrevious();
		assertEquals(p.getCompanyId(), n2.getCompanyId());
		assertEquals(p.getType(), n2.getType());
		assertEquals("20131115", DateFormatUtils.format(n2.getBegin(), FMT[1]));
		assertEquals("20131214", DateFormatUtils.format(n2.getEnd(), FMT[1]));

		final PayPeriod n3 = n2.getPrevious();
		assertEquals(p.getCompanyId(), n3.getCompanyId());
		assertEquals(p.getType(), n3.getType());
		assertEquals("20131015", DateFormatUtils.format(n3.getBegin(), FMT[1]));
		assertEquals("20131114", DateFormatUtils.format(n3.getEnd(), FMT[1]));

		final PayPeriod n4 = n3.getPrevious();
		assertEquals(p.getCompanyId(), n4.getCompanyId());
		assertEquals(p.getType(), n4.getType());
		assertEquals("20130915", DateFormatUtils.format(n4.getBegin(), FMT[1]));
		assertEquals("20131014", DateFormatUtils.format(n4.getEnd(), FMT[1]));
	}

	/**
	 * Test the {@link PayPeriod#getNext()} method.
	 * 
	 * @throws ParseException
	 *             if there is a date processing issue
	 */
	@Test
	public void testGetNextWeekly() throws ParseException {
		final PayPeriod p = new PayPeriod().setType(PayPeriodType.WEEKLY)
				.setCompanyId(1).setBegin(DateUtils.parseDate("20140101", FMT))
				.setEnd(DateUtils.parseDate("20140107", FMT));

		final PayPeriod n1 = p.getNext();
		assertEquals(p.getCompanyId(), n1.getCompanyId());
		assertEquals(p.getType(), n1.getType());
		assertEquals("20140108", DateFormatUtils.format(n1.getBegin(), FMT[1]));
		assertEquals("20140114", DateFormatUtils.format(n1.getEnd(), FMT[1]));

		final PayPeriod n2 = n1.getNext();
		assertEquals(p.getCompanyId(), n2.getCompanyId());
		assertEquals(p.getType(), n2.getType());
		assertEquals("20140115", DateFormatUtils.format(n2.getBegin(), FMT[1]));
		assertEquals("20140121", DateFormatUtils.format(n2.getEnd(), FMT[1]));

		final PayPeriod n3 = n2.getNext();
		assertEquals(p.getCompanyId(), n3.getCompanyId());
		assertEquals(p.getType(), n3.getType());
		assertEquals("20140122", DateFormatUtils.format(n3.getBegin(), FMT[1]));
		assertEquals("20140128", DateFormatUtils.format(n3.getEnd(), FMT[1]));

		final PayPeriod n4 = n3.getNext();
		assertEquals(p.getCompanyId(), n4.getCompanyId());
		assertEquals(p.getType(), n4.getType());
		assertEquals("20140129", DateFormatUtils.format(n4.getBegin(), FMT[1]));
		assertEquals("20140204", DateFormatUtils.format(n4.getEnd(), FMT[1]));
	}

	/**
	 * Test the {@link PayPeriod#getNext()} method.
	 * 
	 * @throws ParseException
	 *             if there is a date processing issue
	 */
	@Test
	public void testGetNextBiWeekly() throws ParseException {
		final PayPeriod p = new PayPeriod().setType(PayPeriodType.BI_WEEKLY)
				.setCompanyId(1).setBegin(DateUtils.parseDate("20140101", FMT))
				.setEnd(DateUtils.parseDate("20140114", FMT));

		final PayPeriod n1 = p.getNext();
		assertEquals(p.getCompanyId(), n1.getCompanyId());
		assertEquals(p.getType(), n1.getType());
		assertEquals("20140115", DateFormatUtils.format(n1.getBegin(), FMT[1]));
		assertEquals("20140128", DateFormatUtils.format(n1.getEnd(), FMT[1]));

		final PayPeriod n2 = n1.getNext();
		assertEquals(p.getCompanyId(), n2.getCompanyId());
		assertEquals(p.getType(), n2.getType());
		assertEquals("20140129", DateFormatUtils.format(n2.getBegin(), FMT[1]));
		assertEquals("20140211", DateFormatUtils.format(n2.getEnd(), FMT[1]));

		final PayPeriod n3 = n2.getNext();
		assertEquals(p.getCompanyId(), n3.getCompanyId());
		assertEquals(p.getType(), n3.getType());
		assertEquals("20140212", DateFormatUtils.format(n3.getBegin(), FMT[1]));
		assertEquals("20140225", DateFormatUtils.format(n3.getEnd(), FMT[1]));

		final PayPeriod n4 = n3.getNext();
		assertEquals(p.getCompanyId(), n4.getCompanyId());
		assertEquals(p.getType(), n4.getType());
		assertEquals("20140226", DateFormatUtils.format(n4.getBegin(), FMT[1]));
		assertEquals("20140311", DateFormatUtils.format(n4.getEnd(), FMT[1]));
	}

	/**
	 * Test the {@link PayPeriod#getNext()} method.
	 * 
	 * @throws ParseException
	 *             if there is a date processing issue
	 */
	@Test
	public void testGetNextSemiMonthly() throws ParseException {
		final PayPeriod p = new PayPeriod().setType(PayPeriodType.SEMI_MONTHLY)
				.setCompanyId(1).setBegin(DateUtils.parseDate("20140101", FMT))
				.setEnd(DateUtils.parseDate("20140115", FMT));

		final PayPeriod n1 = p.getNext();
		assertEquals(p.getCompanyId(), n1.getCompanyId());
		assertEquals(p.getType(), n1.getType());
		assertEquals("20140116", DateFormatUtils.format(n1.getBegin(), FMT[1]));
		assertEquals("20140131", DateFormatUtils.format(n1.getEnd(), FMT[1]));

		final PayPeriod n2 = n1.getNext();
		assertEquals(p.getCompanyId(), n2.getCompanyId());
		assertEquals(p.getType(), n2.getType());
		assertEquals("20140201", DateFormatUtils.format(n2.getBegin(), FMT[1]));
		assertEquals("20140215", DateFormatUtils.format(n2.getEnd(), FMT[1]));

		final PayPeriod n3 = n2.getNext();
		assertEquals(p.getCompanyId(), n3.getCompanyId());
		assertEquals(p.getType(), n3.getType());
		assertEquals("20140216", DateFormatUtils.format(n3.getBegin(), FMT[1]));
		assertEquals("20140228", DateFormatUtils.format(n3.getEnd(), FMT[1]));

		final PayPeriod n4 = n3.getNext();
		assertEquals(p.getCompanyId(), n4.getCompanyId());
		assertEquals(p.getType(), n4.getType());
		assertEquals("20140301", DateFormatUtils.format(n4.getBegin(), FMT[1]));
		assertEquals("20140315", DateFormatUtils.format(n4.getEnd(), FMT[1]));
	}

	/**
	 * Test the {@link PayPeriod#getNext()} method.
	 * 
	 * @throws ParseException
	 *             if there is a date processing issue
	 */
	@Test
	public void testGetNextSemiMonthlyMidMonth() throws ParseException {
		final PayPeriod p = new PayPeriod().setType(PayPeriodType.SEMI_MONTHLY)
				.setCompanyId(1).setBegin(DateUtils.parseDate("20140110", FMT))
				.setEnd(DateUtils.parseDate("20140125", FMT));

		final PayPeriod n1 = p.getNext();
		assertEquals(p.getCompanyId(), n1.getCompanyId());
		assertEquals(p.getType(), n1.getType());
		assertEquals("20140126", DateFormatUtils.format(n1.getBegin(), FMT[1]));
		assertEquals("20140209", DateFormatUtils.format(n1.getEnd(), FMT[1]));

		final PayPeriod n2 = n1.getNext();
		assertEquals(p.getCompanyId(), n2.getCompanyId());
		assertEquals(p.getType(), n2.getType());
		assertEquals("20140210", DateFormatUtils.format(n2.getBegin(), FMT[1]));
		assertEquals("20140225", DateFormatUtils.format(n2.getEnd(), FMT[1]));

		final PayPeriod n3 = n2.getNext();
		assertEquals(p.getCompanyId(), n3.getCompanyId());
		assertEquals(p.getType(), n3.getType());
		assertEquals("20140226", DateFormatUtils.format(n3.getBegin(), FMT[1]));
		assertEquals("20140309", DateFormatUtils.format(n3.getEnd(), FMT[1]));

		final PayPeriod n4 = n3.getNext();
		assertEquals(p.getCompanyId(), n4.getCompanyId());
		assertEquals(p.getType(), n4.getType());
		assertEquals("20140310", DateFormatUtils.format(n4.getBegin(), FMT[1]));
		assertEquals("20140325", DateFormatUtils.format(n4.getEnd(), FMT[1]));
	}

	/**
	 * Test the {@link PayPeriod#getNext()} method.
	 * 
	 * @throws ParseException
	 *             if there is a date processing issue
	 */
	@Test
	public void testGetNextMonthly() throws ParseException {
		final PayPeriod p = new PayPeriod().setType(PayPeriodType.MONTHLY)
				.setCompanyId(1).setBegin(DateUtils.parseDate("20140101", FMT))
				.setEnd(DateUtils.parseDate("20140131", FMT));

		final PayPeriod n1 = p.getNext();
		assertEquals(p.getCompanyId(), n1.getCompanyId());
		assertEquals(p.getType(), n1.getType());
		assertEquals("20140201", DateFormatUtils.format(n1.getBegin(), FMT[1]));
		assertEquals("20140228", DateFormatUtils.format(n1.getEnd(), FMT[1]));

		final PayPeriod n2 = n1.getNext();
		assertEquals(p.getCompanyId(), n2.getCompanyId());
		assertEquals(p.getType(), n2.getType());
		assertEquals("20140301", DateFormatUtils.format(n2.getBegin(), FMT[1]));
		assertEquals("20140331", DateFormatUtils.format(n2.getEnd(), FMT[1]));

		final PayPeriod n3 = n2.getNext();
		assertEquals(p.getCompanyId(), n3.getCompanyId());
		assertEquals(p.getType(), n3.getType());
		assertEquals("20140401", DateFormatUtils.format(n3.getBegin(), FMT[1]));
		assertEquals("20140430", DateFormatUtils.format(n3.getEnd(), FMT[1]));

		final PayPeriod n4 = n3.getNext();
		assertEquals(p.getCompanyId(), n4.getCompanyId());
		assertEquals(p.getType(), n4.getType());
		assertEquals("20140501", DateFormatUtils.format(n4.getBegin(), FMT[1]));
		assertEquals("20140531", DateFormatUtils.format(n4.getEnd(), FMT[1]));
	}

	/**
	 * Test the {@link PayPeriod#getNext()} method.
	 * 
	 * @throws ParseException
	 *             if there is a date processing issue
	 */
	@Test
	public void testGetNextMonthlyMidMonth() throws ParseException {
		final PayPeriod p = new PayPeriod().setType(PayPeriodType.MONTHLY)
				.setCompanyId(1).setBegin(DateUtils.parseDate("20140115", FMT))
				.setEnd(DateUtils.parseDate("20140214", FMT));

		final PayPeriod n1 = p.getNext();
		assertEquals(p.getCompanyId(), n1.getCompanyId());
		assertEquals(p.getType(), n1.getType());
		assertEquals("20140215", DateFormatUtils.format(n1.getBegin(), FMT[1]));
		assertEquals("20140314", DateFormatUtils.format(n1.getEnd(), FMT[1]));

		final PayPeriod n2 = n1.getNext();
		assertEquals(p.getCompanyId(), n2.getCompanyId());
		assertEquals(p.getType(), n2.getType());
		assertEquals("20140315", DateFormatUtils.format(n2.getBegin(), FMT[1]));
		assertEquals("20140414", DateFormatUtils.format(n2.getEnd(), FMT[1]));

		final PayPeriod n3 = n2.getNext();
		assertEquals(p.getCompanyId(), n3.getCompanyId());
		assertEquals(p.getType(), n3.getType());
		assertEquals("20140415", DateFormatUtils.format(n3.getBegin(), FMT[1]));
		assertEquals("20140514", DateFormatUtils.format(n3.getEnd(), FMT[1]));

		final PayPeriod n4 = n3.getNext();
		assertEquals(p.getCompanyId(), n4.getCompanyId());
		assertEquals(p.getType(), n4.getType());
		assertEquals("20140515", DateFormatUtils.format(n4.getBegin(), FMT[1]));
		assertEquals("20140614", DateFormatUtils.format(n4.getEnd(), FMT[1]));
	}
}
