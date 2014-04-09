package com.arcblaze.arctime.common.model.util;

import java.text.ParseException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;

/**
 * Used to perform calculations to determine when a holiday will occur.
 */
public class HolidayCalculator {
	private final static String OCCURRENCES = "(1st|1|2nd|2|3rd|3|4th|4|"
			+ "5th|5|First|Second|Third|Fourth|Fifth|Last)";
	private final static String DAYS = "(Sunday|Sun|Monday|Mon|Tuesday|Tue|"
			+ "Wednesday|Wed|Thursday|Thu|Friday|Fri|Saturday|Sat)";
	private final static String MONTHS = "(January|Jan|February|Feb|March|Mar|"
			+ "April|Apr|May|June|Jun|July|Jul|August|Aug|September|Sep|"
			+ "October|Oct|November|Nov|December|Dec)";
	private final static String DAY_OCCS = "(1st|1|2nd|2|3rd|3|4th|4|5th|5|"
			+ "6th|6|7th|7|8th|8|9th|9|10th|10|11th|11|12th|12|13th|13|14th|"
			+ "14|15th|15|16th|16|17th|17|18th|18|19th|19|20th|20|21st|21|"
			+ "22nd|22|23rd|23|24th|24|25th|25|26th|26|27th|27|28th|28|29th|"
			+ "29|30th|30|31st|31)";

	/**
	 * Used to parse holiday configurations like:
	 * 
	 * {@code "1st Monday in January"}
	 */
	private final static Pattern WEEK_OCCURRENCE_PATTERN = Pattern.compile(
			"\\s*" + OCCURRENCES + "\\s*" + DAYS + "\\s*in\\s*" + MONTHS
					+ "\\s*(([-+])\\s*([0-9]))?\\s*", Pattern.CASE_INSENSITIVE);

	/**
	 * Used to parse holiday configurations like:
	 * 
	 * {@code "January 1st Observance"}
	 */
	private final static Pattern DATE_PATTERN = Pattern.compile("\\s*" + MONTHS
			+ "\\s*" + DAY_OCCS + "\\s*(Observance)?\\s*",
			Pattern.CASE_INSENSITIVE);

	/**
	 * @param config
	 *            the holiday configuration text
	 * @param year
	 *            the year for which the holiday will be calculated
	 * 
	 * @return the calculated {@link Date} on which the holiday will occur
	 * 
	 * @throws HolidayConfigurationException
	 *             if there is a problem parsing the provided holiday
	 *             configuration
	 */
	public static Date getDay(final String config, final int year)
			throws HolidayConfigurationException {
		if (StringUtils.isBlank(config))
			throw new HolidayConfigurationException("Invalid blank config");
		if (year < 1970 || year > 2200)
			throw new HolidayConfigurationException("Invalid year: " + year);

		Date holiday = null;
		try {
			final Matcher weekOccMatcher = WEEK_OCCURRENCE_PATTERN
					.matcher(config);
			final Matcher dateMatcher = DATE_PATTERN.matcher(config);
			if (weekOccMatcher.matches()) {
				// convert "1st" into 1.
				final int week = convertToInt(weekOccMatcher.group(1));
				// the full day, e.g., Thursday
				final String day = normalizeDay(weekOccMatcher.group(2));
				// the month number, e.g., 1 == January
				final int month = convertMonth(weekOccMatcher.group(3));

				if (week < 0) {
					// Looking for "last"
					holiday = DateUtils.parseDate(
							String.format("%d-%02d-01", year, month + 1),
							new String[] { "yyyy-MM-dd" });
					while (!StringUtils.equalsIgnoreCase(day,
							DateFormatUtils.format(holiday, "EEEE")))
						holiday = DateUtils.addDays(holiday, -1);
				} else {
					holiday = DateUtils.parseDate(
							String.format("%d-%02d-01", year, month),
							new String[] { "yyyy-MM-dd" });
					while (!StringUtils.equalsIgnoreCase(day,
							DateFormatUtils.format(holiday, "EEEE")))
						holiday = DateUtils.addDays(holiday, 1);
					if (week > 1)
						holiday = DateUtils.addWeeks(holiday, week - 1);
				}

				final String modifier = weekOccMatcher.group(4);
				if (StringUtils.isNotBlank(modifier)) {
					int count = Integer.parseInt(weekOccMatcher.group(6));
					if (StringUtils.equals("-", weekOccMatcher.group(5)))
						count *= -1;
					holiday = DateUtils.addDays(holiday, count);
				}
			} else if (dateMatcher.matches()) {
				// the month number, e.g., 1 == January
				final int month = convertMonth(dateMatcher.group(1));
				// the day number, 1 == "1st"
				final int day = convertToInt(dateMatcher.group(2));

				holiday = DateUtils.parseDate(
						String.format("%d-%02d-%02d", year, month, day),
						new String[] { "yyyy-MM-dd" });

				final String observance = dateMatcher.group(3);
				if (StringUtils.isNotBlank(observance)) {
					if ("Saturday".equals(DateFormatUtils.format(holiday,
							"EEEE")))
						holiday = DateUtils.addDays(holiday, -1); // use Friday
					if ("Sunday"
							.equals(DateFormatUtils.format(holiday, "EEEE")))
						holiday = DateUtils.addDays(holiday, 1); // use Monday
				}
			} else
				throw new HolidayConfigurationException(
						"Unrecognized holiday configuration pattern: " + config);
		} catch (final ParseException badDate) {
			throw new HolidayConfigurationException("Failed to parse date.",
					badDate);
		}

		return holiday;
	}

	/**
	 * @param value
	 *            the numeric value to parse, for example:
	 *            {@code "1st", "2nd", etc.}
	 * 
	 * @return the parsed numeric value
	 */
	protected static int convertToInt(final String value) {
		if (StringUtils.isBlank(value))
			return 0;
		if (StringUtils.equalsIgnoreCase("first", value))
			return 1;
		if (StringUtils.equalsIgnoreCase("second", value))
			return 2;
		if (StringUtils.equalsIgnoreCase("third", value))
			return 3;
		if (StringUtils.equalsIgnoreCase("fourth", value))
			return 4;
		if (StringUtils.equalsIgnoreCase("fifth", value))
			return 5;
		if (StringUtils.equalsIgnoreCase("last", value))
			return -1;

		final StringBuilder num = new StringBuilder();

		if (Character.isDigit(value.charAt(0)))
			num.append(value.charAt(0));
		if (value.length() > 1 && Character.isDigit(value.charAt(1)))
			num.append(value.charAt(1));

		return Integer.parseInt(num.toString());
	}

	/**
	 * @param month
	 *            the month value (e.g., "January") to be converted into its
	 *            numeric value (e.g., "01")
	 * 
	 * @return the corresponding numeric value for the provided month
	 */
	protected static int convertMonth(final String month) {
		if (StringUtils.startsWithIgnoreCase(month, "Jan"))
			return 1;
		if (StringUtils.startsWithIgnoreCase(month, "Feb"))
			return 2;
		if (StringUtils.startsWithIgnoreCase(month, "Mar"))
			return 3;
		if (StringUtils.startsWithIgnoreCase(month, "Apr"))
			return 4;
		if (StringUtils.startsWithIgnoreCase(month, "May"))
			return 5;
		if (StringUtils.startsWithIgnoreCase(month, "Jun"))
			return 6;
		if (StringUtils.startsWithIgnoreCase(month, "Jul"))
			return 7;
		if (StringUtils.startsWithIgnoreCase(month, "Aug"))
			return 8;
		if (StringUtils.startsWithIgnoreCase(month, "Sep"))
			return 9;
		if (StringUtils.startsWithIgnoreCase(month, "Oct"))
			return 10;
		if (StringUtils.startsWithIgnoreCase(month, "Nov"))
			return 11;
		if (StringUtils.startsWithIgnoreCase(month, "Dec"))
			return 12;
		return -1;
	}

	/**
	 * @param day
	 *            the day value (e.g., "MON" or "monday") to be converted into
	 *            its normalized value (e.g., "Monday")
	 * 
	 * @return the corresponding normalized day value for the provided day
	 */
	protected static String normalizeDay(final String day) {
		if (StringUtils.startsWithIgnoreCase(day, "sun"))
			return "Sunday";
		if (StringUtils.startsWithIgnoreCase(day, "mon"))
			return "Monday";
		if (StringUtils.startsWithIgnoreCase(day, "tue"))
			return "Tuesday";
		if (StringUtils.startsWithIgnoreCase(day, "wed"))
			return "Wednesday";
		if (StringUtils.startsWithIgnoreCase(day, "thu"))
			return "Thursday";
		if (StringUtils.startsWithIgnoreCase(day, "fri"))
			return "Friday";
		if (StringUtils.startsWithIgnoreCase(day, "sat"))
			return "Saturday";
		return null;
	}
}
