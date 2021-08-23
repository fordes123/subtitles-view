package org.fordes.subview.utils.submerge.subtitle.srt;

import org.fordes.subview.utils.submerge.subtitle.common.SubtitleTime;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;


public class SRTTime extends SubtitleTime {

	private static final long serialVersionUID = -5787808223967579723L;

	public static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(SRTTime.PATTERN);
	public static final String PATTERN = "HH:mm:ss,SSS";
	private static final String TS_PATTERN = "%02d:%02d:%02d,%03d";
	public static final String DELIMITER = " --> ";

	public SRTTime() {
		super();
	}

	public SRTTime(LocalTime start, LocalTime end) {

		super(start, end);
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();
		sb.append(format(this.start));
		sb.append(DELIMITER);
		sb.append(format(this.end));
		return sb.toString();
	}

	/**
	 * Convert a <code>LocalTime</code> to string
	 * 
	 * @param time: the time to format
	 * @return the formatted time
	 */
	public static String format(LocalTime time) {

		int hr = time.get(ChronoField.HOUR_OF_DAY);
		int min = time.get(ChronoField.MINUTE_OF_HOUR);
		int sec = time.get(ChronoField.SECOND_OF_MINUTE);
		int ms = time.get(ChronoField.MILLI_OF_SECOND);

		return String.format(TS_PATTERN, hr, min, sec, ms);
	}

	/**
	 * Convert a string pattern to a Local time
	 * 
	 * @param time
	 * @see SRTTime.PATTERN
	 * @return
	 * @throws DateTimeParseException
	 */
	public static LocalTime fromString(String times) {

		return LocalTime.parse(times.replace('.', ',').trim(), FORMATTER);
	}
}
