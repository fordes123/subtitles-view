package org.fordes.subview.utils.submerge;

import org.fordes.subview.utils.submerge.subtitle.ass.ASSSub;
import org.fordes.subview.utils.submerge.subtitle.ass.Events;
import org.fordes.subview.utils.submerge.subtitle.common.TimedLine;
import org.fordes.subview.utils.submerge.subtitle.common.TimedObject;
import org.fordes.subview.utils.submerge.subtitle.common.TimedTextFile;
import org.fordes.subview.utils.submerge.subtitle.config.SimpleSubConfig;
import org.fordes.subview.utils.submerge.subtitle.srt.SRTLine;
import org.fordes.subview.utils.submerge.subtitle.srt.SRTSub;
import org.fordes.subview.utils.submerge.subtitle.srt.SRTTime;
import org.fordes.subview.utils.submerge.utils.ConvertionUtils;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service used to manage subtitles
 */
public class SubmergeAPI {

	/**
	 * Change the framerate of a subtitle
	 * 
	 * @param timedFile the subtitle
	 * @param sourceFramerate le source framerate. Ex: 25.000
	 * @param targetFramerate the target framerate. Ex: 23.976
	 */
	public void convertFramerate(TimedTextFile timedFile, double sourceFramerate, double targetFramerate) {

		double ratio = sourceFramerate / targetFramerate;

		for (TimedLine timedLine : timedFile.getTimedLines()) {

			TimedObject time = timedLine.getTime();
			long s = Math.round(time.getStart().toNanoOfDay() * ratio);
			long e = Math.round(time.getEnd().toNanoOfDay() * ratio);

			time.setStart(LocalTime.ofNanoOfDay(s));
			time.setEnd(LocalTime.ofNanoOfDay(e));
		}
	}

	/**
	 * TimedTextFile to SRT conversion
	 * 
	 * @param timedFile the TimedTextFile
	 * @return the SRTSub object
	 */
	public SRTSub toSRT(TimedTextFile timedFile) {

		SRTSub srt = new SRTSub();

		int i = 0;
		for (TimedLine timedLine : timedFile.getTimedLines()) {

			int id = ++i;
			TimedObject time = timedLine.getTime();
			LocalTime start = time.getStart();
			LocalTime end = time.getEnd();
			SRTTime srtTime = new SRTTime(start, end);

			List<String> textLines = timedLine.getTextLines();
			List<String> newLines = new ArrayList<>();

			for (String textLine : textLines) {
				newLines.add(ConvertionUtils.toSRTString(textLine));
			}

			SRTLine srtLine = new SRTLine(id, srtTime, newLines);

			srt.add(srtLine);
		}

		return srt;
	}

	/**
	 * SubInput to ASS conversion
	 * 
	 * @param config the configuration object
	 * @return the ASSSub object
	 */
	public ASSSub toASS(SimpleSubConfig config) {

		return mergeToAss(config);
	}

	/**
	 * Merge several subtitles into one ASS
	 * 
	 * @param configs : configuration object of the subtitles
	 * @return
	 */
	public ASSSub mergeToAss(SimpleSubConfig... configs) {

		ASSSub ass = new ASSSub();
		Set<Events> ev = ass.getEvents();

		for (SimpleSubConfig config : configs) {
			ass.getStyle().add(ConvertionUtils.createV4Style(config));
			TimedTextFile sub = config.getSub();
			sub.getTimedLines().forEach(line -> ev.add(ConvertionUtils.createEvent(line, config.getStyleName())));
		}

		return ass;
	}

	/**
	 * Transform all multi-lines subtitles to single-line
	 * 
	 * @param timedFile the TimedTextFile
	 */
	public void mergeTextLines(TimedTextFile timedFile) {

		for (TimedLine timedLine : timedFile.getTimedLines()) {
			List<String> textLines = timedLine.getTextLines();
			if (textLines.size() > 1) {
				textLines.set(0, textLines.stream().collect(Collectors.joining(" ")));
				textLines.subList(1, textLines.size()).clear();
			}
		}
	}

	/**
	 * Synchronise the timecodes of a subtitle from another one
	 * 
	 * @param fileToAdjust the subtitle to modify
	 * @param referenceFile the subtitle to take the timecodes from
	 * @param delay the number of milliseconds allowed to differ
	 */
	public void adjustTimecodes(TimedTextFile fileToAdjust, TimedTextFile referenceFile, int delay) {

		TimedLinesAPI linesAPI = new TimedLinesAPI();
		List<? extends TimedLine> timedLines = new ArrayList<>(fileToAdjust.getTimedLines());
		List<? extends TimedLine> referenceLines = new ArrayList<>(referenceFile.getTimedLines());

		for (TimedLine lineToAdjust : timedLines) {

			TimedObject originalTime = lineToAdjust.getTime();
			LocalTime originalStart = originalTime.getStart();

			TimedLine referenceLine = linesAPI.closestByStart(referenceLines, originalStart, delay);

			if (referenceLine != null) {
				LocalTime targetStart = referenceLine.getTime().getStart();
				LocalTime targetEnd = referenceLine.getTime().getEnd();

				TimedLine fullIntersect = linesAPI.intersected(timedLines, targetStart, targetEnd);

				if (fullIntersect != null && !lineToAdjust.equals(fullIntersect)) {
					continue;
				}

				TimedLine startIntersect = linesAPI.intersected(timedLines, targetStart);
				TimedLine endIntersect = linesAPI.intersected(timedLines, targetEnd);

				if (startIntersect == null || originalTime.equals(startIntersect.getTime())) {
					originalTime.setStart(targetStart);
				} else {
					originalTime.setStart(startIntersect.getTime().getEnd());
				}

				if (endIntersect == null || originalTime.getStart().equals(endIntersect.getTime().getStart())) {
					originalTime.setEnd(targetEnd);
				} else {
					originalTime.setEnd(endIntersect.getTime().getStart());
				}
			}
		}

		expandLongLines(timedLines, referenceLines, 1500);
	}

	/**
	 * Expand lines in the adjusted file that should be displayed during 2 lines of the
	 * reference file
	 * 
	 * @param adjustedLines the adjusted lines (ascending sort)
	 * @param referenceLines the reference lines (ascending sort)
	 */
	private static void expandLongLines(List<? extends TimedLine> adjustedLines,
			List<? extends TimedLine> referenceLines, int delay) {

		TimedLinesAPI linesAPI = new TimedLinesAPI();
		for (int i = 0; i < adjustedLines.size(); i++) {

			TimedObject currentElement = adjustedLines.get(i).getTime();

			int index = linesAPI.findByTime(referenceLines, currentElement);
			if (index >= 0) {

				int nextReferenceIndex = index + 1;
				if (nextReferenceIndex < referenceLines.size() && i + 1 < adjustedLines.size()) {

					TimedObject nextReference = referenceLines.get(nextReferenceIndex).getTime();
					TimedObject nextElement = adjustedLines.get(i + 1).getTime();

					if (linesAPI.isEqualsOrAfter(currentElement, nextReference)
							&& linesAPI.getDelay(currentElement.getEnd(), nextReference.getStart()) < delay
							&& linesAPI.isEqualsOrAfter(nextReference, nextElement)) {

						currentElement.setEnd(nextReference.getEnd());
					}
				}
			}
		}
	}
}
