package org.fordes.subview.utils.submerge.utils;

import org.fordes.subview.utils.submerge.parser.exception.InvalidColorCode;

import java.awt.*;



public final class ColorUtils {

	/**
	 * Convert the hexadecimal color code to BGR code
	 * 
	 * @param hex
	 * @return
	 */
	public static int hexToBGR(String hex) {
		Color color = Color.decode(hex);
		int in = Integer.decode(Integer.toString(color.getRGB()));
		int red = (in >> 16) & 0xFF;
		int green = (in >> 8) & 0xFF;
		int blue = (in >> 0) & 0xFF;
		return (blue << 16) | (green << 8) | (red << 0);
	}

	/**
	 * Convert a &HAABBGGRR to hexadecimal
	 * 
	 * @param haabbggrr: the color code
	 * @throws InvalidColorCode
	 * @return the hexadecimal code
	 */
	public static String HAABBGGRRToHex(String haabbggrr) {
		if (haabbggrr.length() != 10) {
			throw new InvalidColorCode("Invalid pattern, must be &HAABBGGRR");
		}
		StringBuilder sb = new StringBuilder();
		sb.append("#");
		sb.append(haabbggrr.substring(8));
		sb.append(haabbggrr.substring(6, 7));
		sb.append(haabbggrr.substring(4, 5));
		sb.append(haabbggrr.substring(2, 3));
		return sb.toString().toLowerCase();
	}

	/**
	 * Convert a &HBBGGRR to hexadecimal
	 * 
	 * @param haabbggrr: the color code
	 * @throws InvalidColorCode
	 * @return the hexadecimal code
	 */
	public static String HBBGGRRToHex(String hbbggrr) {
		if (hbbggrr.length() != 8) {
			throw new InvalidColorCode("Invalid pattern, must be &HBBGGRR");
		}
		StringBuilder sb = new StringBuilder();
		sb.append("#");
		sb.append(hbbggrr.substring(6));
		sb.append(hbbggrr.substring(4, 5));
		sb.append(hbbggrr.substring(2, 3));
		return sb.toString().toLowerCase();
	}

	/**
	 * Convert a &HAABBGGRR to BGR
	 * 
	 * @param haabbggrr: the color code
	 * @throws InvalidColorCode
	 * @return the BGR code
	 */
	public static int HAABBGGRRToBGR(String haabbggrr) {
		return hexToBGR(HAABBGGRRToHex(haabbggrr));
	}

	/**
	 * Convert a &HBBGGRR to BGR
	 * 
	 * @param haabbggrr: the color code
	 * @throws InvalidColorCode
	 * @return the BGR code
	 */
	public static int HBBGGRRToBGR(String hbbggrr) {
		return hexToBGR(HBBGGRRToHex(hbbggrr));
	}

}
