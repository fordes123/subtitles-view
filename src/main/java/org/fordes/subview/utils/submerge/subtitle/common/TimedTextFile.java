package org.fordes.subview.utils.submerge.subtitle.common;


import java.io.Serializable;
import java.util.Set;

/**
 * Object that represents a text file containing timed lines
 */
public interface TimedTextFile extends Serializable {

	/**
	 * Get the filename
	 * 
	 * @return the filename
	 */
	String getFileName();

	/**
	 * Set the filename
	 * 
	 * @param fileName: the filename
	 */
	void setFileName(String fileName);

	/**
	 * Get the timed lines
	 * 
	 * @return lines
	 */
	Set<? extends TimedLine> getTimedLines();

}