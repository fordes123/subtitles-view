package org.fordes.subview.utils.submerge.subtitle.srt;

import org.fordes.subview.utils.submerge.subtitle.common.TimedLine;
import org.fordes.subview.utils.submerge.subtitle.common.TimedTextFile;

import java.util.Set;
import java.util.TreeSet;


/**
 * Class <SRTLine> represents an SRT file, meandin a complete set of subtitle lines
 *
 */
public class SRTSub implements TimedTextFile {

	private static final long serialVersionUID = -2909833999376537734L;

	private String fileName;
	private Set<SRTLine> lines = new TreeSet<>();

	// ======================== Public methods ==========================

	public void add(SRTLine line) {
		
		this.lines.add(line);
	}

	public void remove(TimedLine line) {
		
		this.lines.remove(line);
	}

	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		this.lines.forEach(srtLine -> sb.append(srtLine));
		return sb.toString();
	}

	// ===================== getter and setter start =====================

	public Set<SRTLine> getLines() {
		return this.lines;
	}

	@Override
	public Set<? extends TimedLine> getTimedLines() {
		return this.lines;
	}

	public void setLines(Set<SRTLine> lines) {
		this.lines = lines;
	}

	@Override
	public String getFileName() {
		return this.fileName;
	}

	@Override
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
