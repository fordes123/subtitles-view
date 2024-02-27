package org.fordes.subtitles.view.utils.submerge.subtitle.config;


import lombok.Data;
import org.fordes.subtitles.view.utils.submerge.constant.FontName;

import java.io.Serializable;

@Data
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

}
