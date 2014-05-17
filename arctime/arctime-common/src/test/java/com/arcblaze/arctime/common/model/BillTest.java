package com.arcblaze.arctime.common.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

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
        final Bill a = new Bill().setTaskId(1).setAssignmentId(2).setDay(DateUtils.parseDate("20140101", FMT))
                .setHours("8");
        final Bill b = new Bill().setTaskId(1).setAssignmentId(2).setDay(DateUtils.parseDate("20140101", FMT))
                .setHours("8");
        final Bill c = new Bill().setTaskId(1).setAssignmentId(2).setDay(DateUtils.parseDate("20140101", FMT))
                .setHours("8.0");
        final Bill d = new Bill().setTaskId(1).setAssignmentId(2).setDay(DateUtils.parseDate("20140101", FMT))
                .setHours(8.0f);

        assertEquals(a, b);
        assertEquals(a, c);
        assertEquals(a, d);
        assertEquals(b, c);
        assertEquals(b, d);
        assertEquals(c, d);
    }

    /**
     * @throws ParseException
     *             if there is a date parsing issue
     */
    @Test
    public void testToTimesheetDataFullyPopulated() throws ParseException {
        final Bill bill = new Bill().setTaskId(1).setAssignmentId(2).setDay(DateUtils.parseDate("20140101", FMT))
                .setHours("8.25").setReason("Reason");

        assertEquals("1_2:20140101:8.25:Reason", bill.toTimesheetData());

        final Set<Bill> bills = Bill.fromTimesheetData(bill.toTimesheetData());
        assertNotNull(bills);
        assertEquals(1, bills.size());
        assertEquals(bill, bills.iterator().next());
    }

    /**
     * @throws ParseException
     *             if there is a date parsing issue
     */
    @Test
    public void testToTimesheetDataReasonWithColon() throws ParseException {
        final Bill bill = new Bill().setTaskId(1).setAssignmentId(2).setDay(DateUtils.parseDate("20140101", FMT))
                .setHours("8.25").setReason("Reason:has:colons");

        assertEquals("1_2:20140101:8.25:Reason:has:colons", bill.toTimesheetData());

        final Set<Bill> bills = Bill.fromTimesheetData(bill.toTimesheetData());
        assertNotNull(bills);
        assertEquals(1, bills.size());
        assertEquals(bill, bills.iterator().next());
    }

    /**
     * @throws ParseException
     *             if there is a date parsing issue
     */
    @Test
    public void testToTimesheetDataNoReason() throws ParseException {
        final Bill bill = new Bill().setTaskId(1).setAssignmentId(2).setDay(DateUtils.parseDate("20140101", FMT))
                .setHours("8.25");

        assertEquals("1_2:20140101:8.25", bill.toTimesheetData());

        final Set<Bill> bills = Bill.fromTimesheetData(bill.toTimesheetData());
        assertNotNull(bills);
        assertEquals(1, bills.size());
        assertEquals(bill, bills.iterator().next());
    }

    /**
     * @throws ParseException
     *             if there is a date parsing issue
     */
    @Test
    public void testToTimesheetDataNoAssignmentId() throws ParseException {
        final Bill bill = new Bill().setTaskId(1).setDay(DateUtils.parseDate("20140101", FMT)).setHours("8.25")
                .setReason("Reason");

        assertEquals("1_:20140101:8.25:Reason", bill.toTimesheetData());

        final Set<Bill> bills = Bill.fromTimesheetData(bill.toTimesheetData());
        assertNotNull(bills);
        assertEquals(1, bills.size());
        assertEquals(bill, bills.iterator().next());
    }

    /**
     * @throws ParseException
     *             if there is a date parsing issue
     */
    @Test
    public void testToTimesheetDataNoAssignmentIdOrReason() throws ParseException {
        final Bill bill = new Bill().setTaskId(1).setDay(DateUtils.parseDate("20140101", FMT)).setHours("8.25");

        assertEquals("1_:20140101:8.25", bill.toTimesheetData());

        final Set<Bill> bills = Bill.fromTimesheetData(bill.toTimesheetData());
        assertNotNull(bills);
        assertEquals(1, bills.size());
        assertEquals(bill, bills.iterator().next());
    }

    /**
     * @throws ParseException
     *             if there is a date parsing issue
     */
    @Test
    public void testToTimesheetDataMultiple() throws ParseException {
        final Bill a = new Bill().setTaskId(1).setAssignmentId(2).setDay(DateUtils.parseDate("20140101", FMT))
                .setHours("8.25").setReason("Reason");
        final Bill b = new Bill().setTaskId(1).setAssignmentId(2).setDay(DateUtils.parseDate("20140101", FMT))
                .setHours("8.25").setReason("Reason:has:colons");
        final Bill c = new Bill().setTaskId(1).setAssignmentId(2).setDay(DateUtils.parseDate("20140101", FMT))
                .setHours("8.25");
        final Bill d = new Bill().setTaskId(1).setDay(DateUtils.parseDate("20140101", FMT)).setHours("8.25")
                .setReason("Reason");
        final Bill e = new Bill().setTaskId(1).setDay(DateUtils.parseDate("20140101", FMT)).setHours("8.25");

        final List<Bill> bills = Arrays.asList(a, b, c, d, e);
        final String encoded = Bill.toTimesheetData(bills);

        assertEquals("1_2:20140101:8.25:Reason;1_2:20140101:8.25:" + "Reason:has:colons;1_2:20140101:8.25;1_:20140101:"
                + "8.25:Reason;1_:20140101:8.25", encoded);

        final Set<Bill> decoded = Bill.fromTimesheetData(encoded);
        assertNotNull(bills);
        assertEquals(bills.size(), decoded.size());
        for (final Bill bill : bills)
            assertTrue(decoded.contains(bill));
    }
}
