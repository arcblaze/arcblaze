package com.arcblaze.arctime.common.model.util;

import static org.junit.Assert.assertEquals;

import org.apache.commons.lang.time.DateFormatUtils;
import org.junit.Test;

/**
 * Perform testing of the {@link HolidayCalculator}
 */
public class HolidayCalculatorTest {
    /**
     * @throws HolidayConfigurationException
     *             if there is a problem parsing holiday configurations
     */
    @Test(expected = HolidayConfigurationException.class)
    public void testNullConfig() throws HolidayConfigurationException {
        HolidayCalculator.getDay(null, 2013);
    }

    /**
     * @throws HolidayConfigurationException
     *             if there is a problem parsing holiday configurations
     */
    @Test(expected = HolidayConfigurationException.class)
    public void testBlankConfig() throws HolidayConfigurationException {
        HolidayCalculator.getDay("", 2013);
    }

    /**
     * @throws HolidayConfigurationException
     *             if there is a problem parsing holiday configurations
     */
    @Test(expected = HolidayConfigurationException.class)
    public void testInvalidConfig() throws HolidayConfigurationException {
        HolidayCalculator.getDay("invalid", 2013);
    }

    /**
     * @throws HolidayConfigurationException
     *             if there is a problem parsing holiday configurations
     */
    @Test(expected = HolidayConfigurationException.class)
    public void testInvalidYear() throws HolidayConfigurationException {
        HolidayCalculator.getDay("July 4th", -1);
    }

    /**
     * @throws HolidayConfigurationException
     *             if there is a problem parsing holiday configurations
     */
    @Test
    public void testSimpleDate() throws HolidayConfigurationException {
        assertEquals("2013-07-04 Thu",
                DateFormatUtils.format(HolidayCalculator.getDay("July 4th", 2013), "yyyy-MM-dd EEE"));
        assertEquals("2014-07-04 Fri",
                DateFormatUtils.format(HolidayCalculator.getDay("July 4th", 2014), "yyyy-MM-dd EEE"));
        assertEquals("2015-07-04 Sat",
                DateFormatUtils.format(HolidayCalculator.getDay("July 4th", 2015), "yyyy-MM-dd EEE"));
        assertEquals("2016-07-04 Mon",
                DateFormatUtils.format(HolidayCalculator.getDay("Jul 4th", 2016), "yyyy-MM-dd EEE"));
        assertEquals("2017-07-04 Tue",
                DateFormatUtils.format(HolidayCalculator.getDay("July 4", 2017), "yyyy-MM-dd EEE"));
        assertEquals("2018-07-04 Wed",
                DateFormatUtils.format(HolidayCalculator.getDay("Jul 4", 2018), "yyyy-MM-dd EEE"));
    }

    /**
     * @throws HolidayConfigurationException
     *             if there is a problem parsing holiday configurations
     */
    @Test
    public void testSimpleDateWithObservance() throws HolidayConfigurationException {
        assertEquals("2019-07-04 Thu",
                DateFormatUtils.format(HolidayCalculator.getDay("July 4th Observance", 2019), "yyyy-MM-dd EEE"));
        assertEquals("2020-07-03 Fri",
                DateFormatUtils.format(HolidayCalculator.getDay("July 4th Observance", 2020), "yyyy-MM-dd EEE"));
        assertEquals("2021-07-05 Mon",
                DateFormatUtils.format(HolidayCalculator.getDay("July 4th Observance", 2021), "yyyy-MM-dd EEE"));
        assertEquals("2019-07-04 Thu",
                DateFormatUtils.format(HolidayCalculator.getDay("Jul 4 Observance", 2019), "yyyy-MM-dd EEE"));
        assertEquals("2020-07-03 Fri",
                DateFormatUtils.format(HolidayCalculator.getDay("Jul 4th Observance", 2020), "yyyy-MM-dd EEE"));
        assertEquals("2021-07-05 Mon",
                DateFormatUtils.format(HolidayCalculator.getDay("July 4 Observance", 2021), "yyyy-MM-dd EEE"));
    }

    /**
     * @throws HolidayConfigurationException
     *             if there is a problem parsing holiday configurations
     */
    @Test
    public void testSimpleDateWhiteSpaceAndCaseInsensitivity() throws HolidayConfigurationException {
        assertEquals("2013-07-04 Thu",
                DateFormatUtils.format(HolidayCalculator.getDay("July 4th", 2013), "yyyy-MM-dd EEE"));
        assertEquals("2013-07-04 Thu",
                DateFormatUtils.format(HolidayCalculator.getDay("  Jul 4th", 2013), "yyyy-MM-dd EEE"));
        assertEquals("2013-07-04 Thu",
                DateFormatUtils.format(HolidayCalculator.getDay("july 4  ", 2013), "yyyy-MM-dd EEE"));
        assertEquals("2013-07-04 Thu",
                DateFormatUtils.format(HolidayCalculator.getDay("july   4th", 2013), "yyyy-MM-dd EEE"));
        assertEquals("2013-07-04 Thu",
                DateFormatUtils.format(HolidayCalculator.getDay("  JUL   4TH  ", 2013), "yyyy-MM-dd EEE"));
        assertEquals("2013-07-04 Thu",
                DateFormatUtils.format(HolidayCalculator.getDay("July	4th", 2013), "yyyy-MM-dd EEE"));
        assertEquals("2019-07-04 Thu",
                DateFormatUtils.format(HolidayCalculator.getDay("  July   4th  Observance  ", 2019), "yyyy-MM-dd EEE"));
        assertEquals("2020-07-03 Fri",
                DateFormatUtils.format(HolidayCalculator.getDay("  July   4  Observance  ", 2020), "yyyy-MM-dd EEE"));
        assertEquals("2021-07-05 Mon",
                DateFormatUtils.format(HolidayCalculator.getDay("  july   4th observance  ", 2021), "yyyy-MM-dd EEE"));
    }

    /**
     * @throws HolidayConfigurationException
     *             if there is a problem parsing holiday configurations
     */
    @Test
    public void testSpecificDayInMonth() throws HolidayConfigurationException {
        assertEquals("2013-02-18 Mon",
                DateFormatUtils.format(HolidayCalculator.getDay("3rd Monday in February", 2013), "yyyy-MM-dd EEE"));
        assertEquals("2013-05-27 Mon",
                DateFormatUtils.format(HolidayCalculator.getDay("Last Monday in May", 2013), "yyyy-MM-dd EEE"));
        assertEquals("2013-09-02 Mon",
                DateFormatUtils.format(HolidayCalculator.getDay("1st Monday in September", 2013), "yyyy-MM-dd EEE"));
        assertEquals("2013-10-14 Mon",
                DateFormatUtils.format(HolidayCalculator.getDay("2 Mon in October", 2013), "yyyy-MM-dd EEE"));
        assertEquals("2013-11-28 Thu",
                DateFormatUtils.format(HolidayCalculator.getDay("4th Thu in Nov", 2013), "yyyy-MM-dd EEE"));
        assertEquals("2013-11-22 Fri",
                DateFormatUtils.format(HolidayCalculator.getDay("4 Fri in Nov", 2013), "yyyy-MM-dd EEE"));
    }

    /**
     * @throws HolidayConfigurationException
     *             if there is a problem parsing holiday configurations
     */
    @Test
    public void testSpecificDayInMonthWhiteSpaceAndCaseInsensitive() throws HolidayConfigurationException {
        assertEquals("2013-02-18 Mon", DateFormatUtils.format(
                HolidayCalculator.getDay("  3rd   Monday   in   February  ", 2013), "yyyy-MM-dd EEE"));
        assertEquals("2013-05-27 Mon",
                DateFormatUtils.format(HolidayCalculator.getDay(" LAST MONDAY   IN  MAY ", 2013), "yyyy-MM-dd EEE"));
        assertEquals("2013-09-02 Mon",
                DateFormatUtils.format(HolidayCalculator.getDay("1st   monday in   september", 2013), "yyyy-MM-dd EEE"));
        assertEquals("2013-10-14 Mon",
                DateFormatUtils.format(HolidayCalculator.getDay("  2 MON   in OCT", 2013), "yyyy-MM-dd EEE"));
        assertEquals("2013-11-28 Thu",
                DateFormatUtils.format(HolidayCalculator.getDay("4th thursday in november", 2013), "yyyy-MM-dd EEE"));
        assertEquals("2013-11-22 Fri",
                DateFormatUtils.format(HolidayCalculator.getDay("	4TH Friday in NOVEMBER", 2013), "yyyy-MM-dd EEE"));
    }

    /**
     * @throws HolidayConfigurationException
     *             if there is a problem parsing holiday configurations
     */
    @Test
    public void testSpecificDayInMonthWithModifier() throws HolidayConfigurationException {
        assertEquals("2013-02-17 Sun",
                DateFormatUtils.format(HolidayCalculator.getDay("3rd Monday in February - 1", 2013), "yyyy-MM-dd EEE"));
        assertEquals("2013-05-29 Wed",
                DateFormatUtils.format(HolidayCalculator.getDay("Last Monday in May + 2", 2013), "yyyy-MM-dd EEE"));
        assertEquals("2013-09-01 Sun",
                DateFormatUtils.format(HolidayCalculator.getDay("1st Monday in September - 1", 2013), "yyyy-MM-dd EEE"));
        assertEquals("2013-10-16 Wed",
                DateFormatUtils.format(HolidayCalculator.getDay("2 Mon in October +2", 2013), "yyyy-MM-dd EEE"));
        assertEquals("2013-11-25 Mon",
                DateFormatUtils.format(HolidayCalculator.getDay("4th Thu in Nov -3", 2013), "yyyy-MM-dd EEE"));
        assertEquals("2013-11-25 Mon",
                DateFormatUtils.format(HolidayCalculator.getDay("4 Fri in Nov +3", 2013), "yyyy-MM-dd EEE"));
    }
}
