package org.fordes.subview.utils.submerge.subtitle.config;

import org.fordes.subview.utils.submerge.subtitle.common.TimedTextFile;

import java.io.Serializable;


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

	// ===================== getter and setter start =====================

	public TimedTextFile getSub() {
		return this.sub;
	}

	public void setSub(TimedTextFile sub) {
		this.sub = sub;
	}

	public Font getFontconfig() {
		return this.fontconfig;
	}

	public void setFontconfig(Font fontconfig) {
		this.fontconfig = fontconfig;
	}

	public int getAlignment() {
		return this.alignment;
	}

	public void setAlignment(int alignment) {
		this.alignment = alignment;
	}

	public String getStyleName() {
		return this.styleName;
	}

	public void setStyleName(String styleName) {
		this.styleName = styleName;
	}

	public int getVerticalMargin() {
		return this.verticalMargin;
	}

	public void setVerticalMargin(int verticalMargin) {
		this.verticalMargin = verticalMargin;
	}

}
