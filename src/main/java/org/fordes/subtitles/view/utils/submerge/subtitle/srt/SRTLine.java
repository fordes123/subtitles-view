package org.fordes.subtitles.view.utils.submerge.subtitle.srt;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.fordes.subtitles.view.utils.submerge.subtitle.common.SubtitleLine;

import java.util.List;


/**
 * Class <SRTLine> represents an abstract line of SRT, meaning text, timecodes and index
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SRTLine extends SubtitleLine<SRTTime> {

	private static final long serialVersionUID = -1220593401999895814L;

	private static final String NEW_LINE = "\n";

	private int id;

	public SRTLine(int id, SRTTime time, List<String> textLines) {
		
		this.id = id;
		this.time = time;
		this.textLines = textLines;
	}

	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		sb.append(this.id).append(NEW_LINE);
		sb.append(this.time).append(NEW_LINE);
		this.textLines.forEach(textLine -> sb.append(textLine).append(NEW_LINE));
		return sb.append(NEW_LINE).toString();
	}

}
