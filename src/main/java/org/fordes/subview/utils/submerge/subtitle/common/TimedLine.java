package org.fordes.subview.utils.submerge.subtitle.common;


import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

/**
 * Simple object that contains a text line with a time
 *
 */
public interface TimedLine extends Serializable, Comparable<TimedLine>, Comparator<TimedLine> {

	/**
	 * Get the text lines
	 * 
	 * @return textLines
	 */
	List<String> getTextLines();

	/**
	 * Get the timed object
	 * 
	 * @return the time
	 */
	TimedObject getTime();

}