package org.fordes.subview.utils.submerge.subtitle.config;

import org.fordes.subview.utils.submerge.constant.FontName;

import java.io.Serializable;


public class Font implements Serializable {

	/**
	 * Serial
	 */
	private static final long serialVersionUID = -3711480706383195193L;

	/**
	 * Font name
	 */
	private String name = FontName.Arial.toString();

	/**
	 * Font size
	 */
	private int size = 16;

	/**
	 * Font color
	 */
	private String color = "#fffff9";

	/**
	 * Outline color
	 */
	private String outlineColor = "#000000";

	/**
	 * Outline width
	 */
	private int outlineWidth = 2;

	// ===================== getter and setter start =====================

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSize() {
		return this.size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getColor() {
		return this.color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getOutlineColor() {
		return this.outlineColor;
	}

	public void setOutlineColor(String outlineColor) {
		this.outlineColor = outlineColor;
	}

	public int getOutlineWidth() {
		return this.outlineWidth;
	}

	public void setOutlineWidth(int outlineWidth) {
		this.outlineWidth = outlineWidth;
	}

}
