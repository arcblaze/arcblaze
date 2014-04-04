package com.arcblaze.arccore.server.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

/**
 * Performs formatting of log messages from the embedded tomcat.
 */
public class TomcatLogFormatter extends Formatter {
	private final Date date = new Date();
	private final SimpleDateFormat fmt = new SimpleDateFormat("HH:mm:ss");
	private final String lineEnding = System.getProperty("line.separator");

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String format(final LogRecord record) {
		this.date.setTime(record.getMillis());

		final StringBuilder log = new StringBuilder();
		log.append(this.fmt.format(this.date));
		log.append(" ");
		log.append(StringUtils.rightPad(record.getLevel().getName(), 7));
		log.append(" ");
		log.append(StringUtils.rightPad(StringUtils.substringAfterLast(
				record.getSourceClassName(), "."), 30));
		log.append(" -    "); // line number not available.
		log.append(record.getMessage());
		log.append(this.lineEnding);
		final Throwable throwable = record.getThrown();
		if (throwable != null) {
			log.append(ExceptionUtils.getFullStackTrace(throwable));
			log.append(this.lineEnding);
		}
		return log.toString();
	}
}
