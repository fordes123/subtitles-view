package org.fordes.subview.utils.submerge.parser;

import org.apache.commons.lang3.StringUtils;
import org.fordes.subview.utils.submerge.parser.exception.InvalidSRTSubException;
import org.fordes.subview.utils.submerge.parser.exception.InvalidSubException;
import org.fordes.subview.utils.submerge.subtitle.srt.SRTLine;
import org.fordes.subview.utils.submerge.subtitle.srt.SRTSub;
import org.fordes.subview.utils.submerge.subtitle.srt.SRTTime;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;



/**
 * Parse SRT subtitles
 */
public final class SRTParser extends BaseParser<SRTSub> {

	@Override
	protected void parse(BufferedReader br, SRTSub sub) throws IOException, InvalidSubException {

		boolean found = true;
		while (found) {
			SRTLine line = firstIn(br);
			if (found = (line != null)) {
				sub.add(line);
			}
		}
	}

	/**
	 * Extract the firt SRTLine found in a buffered reader. <br/>
	 * 
	 * Example of SRT line:
	 * 
	 * <pre>
	 * 1
	 * 00:02:46,813 --> 00:02:50,063
	 * A text line
	 * </pre>
	 * 
	 * @param br
	 * @return SRTLine the line extracted, null if no SRTLine found
	 * @throws IOException
	 * @throws InvalidSRTSubException
	 */
	private static SRTLine firstIn(BufferedReader br) throws IOException, InvalidSRTSubException {

		String idLine = readFirstTextLine(br);
		String timeLine = br.readLine();

		if (idLine == null || timeLine == null) {
			return null;
		}

		int id = parseId(idLine);
		SRTTime time = parseTime(timeLine);

		List<String> textLines = new ArrayList<>();
		String testLine;
		while ((testLine = br.readLine()) != null) {
			if (StringUtils.isEmpty(testLine.trim())) {
				break;
			}
			textLines.add(testLine);
		}

		return new SRTLine(id, time, textLines);
	}

	/**
	 * Extract a subtitle id from string
	 * 
	 * @param textLine ex 1
	 * @return the id extracted
	 * @throws InvalidSRTSubException
	 */
	private static int parseId(String textLine) throws InvalidSRTSubException {

		int idSRTLine;
		try {
			idSRTLine = Integer.parseInt(textLine.trim());
		} catch (NumberFormatException e) {
			throw new InvalidSRTSubException("Expected id not found -> " + textLine);
		}

		return idSRTLine;
	}

	/**
	 * Extract a subtitle time from string
	 * 
	 * @param timeLine: ex 00:02:08,822 --> 00:02:11,574
	 * @return the SRTTime object
	 * @throws InvalidSRTSubException
	 */
	public static SRTTime parseTime(String timeLine) throws InvalidSRTSubException {

		SRTTime time = null;
		String times[] = timeLine.split(SRTTime.DELIMITER.trim());

		if (times.length != 2) {
			throw new InvalidSRTSubException("Subtitle " + timeLine + " - invalid times : " + timeLine);
		}

		try {
			LocalTime start = SRTTime.fromString(times[0]);
			LocalTime end = SRTTime.fromString(times[1]);
			time = new SRTTime(start, end);
		} catch (DateTimeParseException e) {
			throw new InvalidSRTSubException("Invalid time string : " + timeLine, e);
		}

		return time;
	}

}
