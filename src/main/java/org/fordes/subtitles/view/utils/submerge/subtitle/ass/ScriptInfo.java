package org.fordes.subtitles.view.utils.submerge.subtitle.ass;

import lombok.Data;

import java.io.Serializable;
import java.text.DecimalFormat;

/**
 * The <code>ScriptInfo</code> section contains headers and general information about the
 * script
 */
@Data
public class ScriptInfo implements Serializable {

	/**
	 * Serial
	 */
	private static final long serialVersionUID = -6613873382621648995L;

	/**
	 * Timer declaration
	 */
	private static final String TIMER = "Timer";

	/**
	 * PlayDepth declaration
	 */
	private static final String PLAY_DEPTH = "PlayDepth";

	/**
	 * PlayResX declaration
	 */
	private static final String PLAY_RES_X = "PlayResX";

	/**
	 * PlayResY declaration
	 */
	private static final String PLAY_RES_Y = "PlayResY";

	/**
	 * Collisions declaration
	 */
	private static final String COLLISIONS = "Collisions";

	/**
	 * Script Type declaration
	 */
	private static final String SCRIPT_TYPE = "ScriptType";

	/**
	 * Update Details declaration
	 */
	private static final String UPDATE_DETAILS = "Update Details";

	/**
	 * Script Updated By declaration
	 */
	private static final String SCRIPT_UPDATED_BY = "Script Updated By";

	/**
	 * Synch Point declaration
	 */
	private static final String SYNCH_POINT = "Synch Point";

	/**
	 * Original Timing declaration
	 */
	private static final String ORIGINAL_TIMING = "Original Timing";

	/**
	 * Original Editing declaration
	 */
	private static final String ORIGINAL_EDITING = "Original Editing";

	/**
	 * Original Translation declaration
	 */
	private static final String ORIGINAL_TRANSLATION = "Original Translation";

	/**
	 * Original Script declaration
	 */
	private static final String ORIGINAL_SCRIPT = "Original Script";

	/**
	 * Title declaration
	 */
	private static final String TITLE = "Title";

	/**
	 * Separator
	 */
	public static final String SEP = ": ";

	/**
	 * New line separator
	 */
	private static final String NEW_LINE = "\n";

	/**
	 * Decimal time formater
	 */
	private static final DecimalFormat timeFormatter = new DecimalFormat("#.0000");

	public enum Collision {

		/**
		 * position subtitles in the position specified by the "margins"
		 */
		NORMAL("Normal"),

		/**
		 * subtitles will be shifted upwards to make room for subsequent overlapping
		 * subtitles
		 */
		REVERSE("Reverse");

		private String type;

		Collision(String type) {
			this.type = type;
		}

		@Override
		public String toString() {
			return this.type;
		}

	}

	/**
	 * This is a description of the script. If the original author(s) did not provide this
	 * information then <untitled> is automatically substituted.
	 */
	private String title;

	/**
	 * The original author(s) of the script. If the original author(s) did not provide
	 * this information then <unknown> is automatically substituted.
	 */
	private String originalScript;

	/**
	 * (optional) The original translator of the dialogue. This entry does not appear if
	 * no information was entered by the author.
	 */
	private String originalTranslation;

	/**
	 * (optional) The original script editor(s), typically whoever took the raw
	 * translation and turned it into idiomatic english and reworded for readability. This
	 * entry does not appear if no information was entered by the author.
	 */
	private String originalEditing;

	/**
	 * (optional) Whoever timed the original script. This entry does not appear if no
	 * information was entered by the author.
	 */
	private String originalTiming;

	/**
	 * (optional) Description of where in the video the script should begin playback.
	 */
	private String synchPoint;
	/**
	 * (optional) The original script editor(s), typically whoever took the raw
	 * translation and turned it into idiomatic english and reworded for readability. This
	 * entry does not appear if no information was entered by the author.
	 */
	private String originalScriptChecking;

	/**
	 * (optional) Names of any other subtitling groups who edited the original script.
	 */
	private String scriptUpdatedBy;

	/**
	 * The details of any updates to the original script made by other subtilting groups.
	 */
	private String userDetails;

	/**
	 * This is the SSA script format version eg. "V4.00". It is used by SSA to give a
	 * warning if you are using a version of SSA older than the version that created the
	 * script.
	 */
	private String scriptType = "v4.00+";

	/**
	 * This determines how subtitles are moved, when automatically preventing onscreen
	 * collisions.
	 * 
	 * If the entry says "Normal" then SSA will attempt to position subtitles in the
	 * position specified by the "margins". However, subtitles can be shifted vertically
	 * to prevent onscreen collisions. With "normal" collision prevention, the subtitles
	 * will "stack up" one above the other - but they will always be positioned as close
	 * the vertical (bottom) margin as possible - filling in "gaps" in other subtitles if
	 * one large enough is available.
	 * 
	 * If the entry says "Reverse" then subtitles will be shifted upwards to make room for
	 * subsequent overlapping subtitles. This means the subtitles can nearly always be
	 * read top-down - but it also means that the first subtitle can appear half way up
	 * the screen before the subsequent overlapping subtitles appear. It can use a lot of
	 * screen area.
	 */
	private Collision collisions = Collision.NORMAL;

	/**
	 * This is the height of the screen used by the script's author(s) when playing the
	 * script. SSA v4 will automatically select the nearest enabled setting, if you are
	 * using Directdraw playback.
	 */
	private int playResY;

	/**
	 * This is the width of the screen used by the script's author(s) when playing the
	 * script. SSA will automatically select the nearest enabled, setting if you are using
	 * Directdraw playback.
	 */
	private int playResX;

	/**
	 * This is the colour depth used by the script's author(s) when playing the script.
	 * SSA will automatically select the nearest enabled setting if you are using
	 * Directdraw playback.
	 */
	private int playDepth;

	/**
	 * This is the Timer Speed for the script, as a percentage.
	 */
	private double timer = 100.0000;

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		appendNotNull(sb, TITLE, this.title);
		appendNotNull(sb, ORIGINAL_SCRIPT, this.originalScript);
		appendNotNull(sb, ORIGINAL_TRANSLATION, this.originalTranslation);
		appendNotNull(sb, ORIGINAL_EDITING, this.originalEditing);
		appendNotNull(sb, ORIGINAL_TIMING, this.originalTiming);
		appendNotNull(sb, SYNCH_POINT, this.synchPoint);
		appendNotNull(sb, SCRIPT_UPDATED_BY, this.scriptUpdatedBy);
		appendNotNull(sb, UPDATE_DETAILS, this.userDetails);
		appendNotNull(sb, SCRIPT_TYPE, this.scriptType);
		appendNotNull(sb, COLLISIONS, this.collisions.toString());
		appendPositive(sb, PLAY_RES_Y, this.playResY);
		appendPositive(sb, PLAY_RES_X, this.playResX);
		appendPositive(sb, PLAY_DEPTH, this.playDepth);
		sb.append(TIMER).append(SEP).append(timeFormatter.format(this.timer));
		return sb.toString();
	}

	// ======================= private methods =======================

	/**
	 * Append a value in a <code>StringBuilder</code> if the value is not null
	 * 
	 * @param sb: the string builder
	 * @param desc: the description
	 * @param val: the value
	 */
	private static void appendNotNull(StringBuilder sb, String desc, String val) {
		if (val != null) {
			sb.append(desc).append(SEP).append(val).append(NEW_LINE);
		}
	}

	/**
	 * Append a value in a <code>StringBuilder</code> if the value is positive
	 * 
	 * @param sb: the string builder
	 * @param desc: the description
	 * @param val: the value
	 */
	private static void appendPositive(StringBuilder sb, String desc, int val) {
		if (val > 0) {
			sb.append(desc).append(SEP).append(val).append(NEW_LINE);
		}
	}


}
