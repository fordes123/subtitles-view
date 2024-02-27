package org.fordes.subtitles.view.utils.submerge.subtitle.config;


import lombok.Data;
import org.fordes.subtitles.view.utils.submerge.subtitle.common.TimedTextFile;

import java.io.Serializable;


@Data
public class SimpleSubConfig implements Serializable {

	private static final long serialVersionUID = -485125721913729063L;

	private String styleName;
	private TimedTextFile sub;
	private Font fontconfig = new Font();
	private int alignment;
	private int verticalMargin = 10;

	public SimpleSubConfig() {
	}

	public SimpleSubConfig(TimedTextFile sub, Font fontConfig) {
		this.sub = sub;
		this.fontconfig = fontConfig;
	}

}
