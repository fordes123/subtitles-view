package org.fordes.subtitles.view.utils.submerge.subtitle.ass;

import lombok.Data;
import org.fordes.subtitles.view.utils.submerge.subtitle.common.TimedLine;
import org.fordes.subtitles.view.utils.submerge.subtitle.common.TimedTextFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * The class <code>ASSSub</code> represents a SubStation Alpha subtitle
 *
 */
@Data
public class ASSSub implements TimedTextFile, Serializable {

	/**
	 * Serial
	 */
	private static final long serialVersionUID = 8812933867812351549L;

	
	/**
	 * Format
	 */
	public static final String FORMAT = "Format";

	/**
	 * Events section
	 */
	private static final String EVENTS = "[Events]";

	/**
	 * Styles section
	 */
	private static final String V4_STYLES = "[V4+ Styles]";

	/**
	 * Script info section
	 */
	private static final String SCRIPT_INFO = "[Script Info]";

	/**
	 * Line separator
	 */
	private static final String NEW_LINE = "\n";

	/**
	 * Key / Value info separator. Ex : "Color: red"
	 */
	public static final String SEP = ": ";

	/**
	 * Subtitle name
	 */
	private String filename;

	/**
	 * Headers and general information about the script
	 */
	private ScriptInfo scriptInfo = new ScriptInfo();

	/**
	 * Style definitions required by the script
	 */
	private List<V4Style> style = new ArrayList<>();

	/**
	 * Events for the script - all the subtitles, comments, pictures, sounds, movies and
	 * commands
	 */
	private Set<Events> events = new TreeSet<>();

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		// [Script Info]
		sb.append(SCRIPT_INFO).append(NEW_LINE).append(this.scriptInfo.toString());
		sb.append(NEW_LINE).append(NEW_LINE);

		// [V4 Styles]
		sb.append(V4_STYLES).append(NEW_LINE);
		sb.append(FORMAT).append(SEP).append(V4Style.FORMAT_STRING).append(NEW_LINE);
		this.style.forEach(s -> sb.append(s.toString()).append(NEW_LINE));
		sb.append(NEW_LINE);

		// [Events]
		sb.append(EVENTS).append(NEW_LINE);
		sb.append(FORMAT).append(SEP).append(Events.FORMAT_STRING).append(NEW_LINE);
		this.events.forEach(e -> sb.append(e.toString()).append(NEW_LINE));
		return sb.toString();
	}

	/**
	 * Get the ass file as an input stream
	 * 
	 * @return the file
	 */
	public InputStream toInputStream() {
		return new ByteArrayInputStream(toString().getBytes());
	}


	@Override
	public void setFileName(String fileName) {
		this.filename = fileName;
	}

	@Override
	public String getFileName() {
		return this.filename;
	}

	@Override
	public Set<? extends TimedLine> getTimedLines() {
		return this.events;
	}

}
