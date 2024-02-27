package org.fordes.subtitles.view.utils.submerge;


import org.fordes.subtitles.view.utils.submerge.subtitle.common.SubtitleLine;
import org.fordes.subtitles.view.utils.submerge.subtitle.common.SubtitleTime;
import org.fordes.subtitles.view.utils.submerge.subtitle.common.TimedLine;
import org.fordes.subtitles.view.utils.submerge.subtitle.common.TimedObject;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class TimedLinesAPI {

	/**
	 * Search the line that has the closest start time compared to a specified time. If
	 * the gap beetween the two start times is greater than the toleranceDelay (in ms) the
	 * line will be ignored.
	 * 
	 * @param tolerance the maximum gap in millis
	 * @param lines the lines (ascending sort)
	 * @param time the target start time
	 * @return
	 */
	public TimedLine closestByStart(List<? extends TimedLine> lines, final LocalTime time, int tolerance) {

		// Binary search will find the first "random" match
		int iAnyMatch = Collections.binarySearch(lines, new SubtitleLine<>(new SubtitleTime(time, null)),
				(compare, base) -> {

					LocalTime search = base.getTime().getStart();
					LocalTime start = compare.getTime().getStart();

					if (getDelay(search, start) < tolerance) {
						return 0;
					}

					return start.compareTo(search);
				});

		if (iAnyMatch < 0) {
			return null;
		}

		// Search for other matches
		Set<TimedLine> matches = new TreeSet<>();
		matches.add(lines.get(iAnyMatch));

		int i = iAnyMatch;
		while (i > 0) {
			TimedLine previous = lines.get(--i);
			if (getDelay(time, previous.getTime().getStart()) >= tolerance) {
				break;
			}
			matches.add(previous);
		}

		i = iAnyMatch;
		while (i < lines.size() -1) {
			TimedLine next = lines.get(++i);
			if (getDelay(time, next.getTime().getStart()) >= tolerance) {
				break;
			}
			matches.add(next);
		}

		// return the closest match
		return matches.stream().min((m1, m2) -> getDelay(m1.getTime().getStart(), time) - getDelay(m2.getTime().getStart(), time)).get();
	}

	/**
	 * Get the absolute delay beetween 2 times
	 * 
	 * @return the absolute delay beetween 2 times
	 */
	public int getDelay(LocalTime start, LocalTime end) {

		return (int) Math.abs(ChronoUnit.MILLIS.between(start, end));
	}

	/**
	 * Check if a timed object appear before or at the same time as an other timed object
	 * 
	 * @param elementToCompare
	 * @param comparedElement
	 * @return
	 */
	public boolean isEqualsOrAfter(TimedObject elementToCompare, TimedObject comparedElement) {

		return comparedElement.getStart().isAfter(elementToCompare.getEnd())
				|| comparedElement.getStart().equals(elementToCompare.getEnd());
	}

	/**
	 * Find the line displayed at <code>targetTime</code>
	 * 
	 * @param lines the lines (ascending sort)
	 * @param time the target time
	 * @return
	 */
	public TimedLine intersected(List<? extends TimedLine> lines, LocalTime time) {

		int index = Collections.binarySearch(lines, new SubtitleLine<>(new SubtitleTime(time, null)),
				(compare, base) -> {

					LocalTime search = base.getTime().getStart();
					LocalTime start = compare.getTime().getStart();
					LocalTime end = compare.getTime().getEnd();

					if ((start.isBefore(search) || start.equals(search))
							&& (end.isAfter(search) || start.equals(search))) {
						return 0;
					}

					return start.compareTo(search);
				});

		return index < 0 ? null : lines.get(index);
	}

	/**
	 * Find a line displayed between 2 times
	 * 
	 * @param lines the lines (ascending sort)
	 * @param
	 * 
	 * @return
	 */
	public TimedLine intersected(List<? extends TimedLine> lines, LocalTime start, LocalTime end) {

		int index = Collections.binarySearch(lines, new SubtitleLine<>(new SubtitleTime(start, end)),
				(compare, base) -> {

					LocalTime searchStart = base.getTime().getStart();
					LocalTime searchEnd = base.getTime().getEnd();

					LocalTime start1 = compare.getTime().getStart();
					LocalTime end1 = compare.getTime().getEnd();

					if (searchStart.isBefore(start1) && searchEnd.isAfter(end1)) {
						return 0;
					}

					return compare.compareTo(base);
				});

		return index < 0 ? null : lines.get(index);
	}

	/**
	 * Find a sublitle line from it's time
	 * 
	 * @param lines the subtitle lines
	 * @param time the timed object
	 * @return
	 */
	public int findByTime(List<? extends TimedLine> lines, TimedObject time) {

		return Collections.binarySearch(lines, new SubtitleLine<>(time), SubtitleLine.timeComparator);
	}
}
