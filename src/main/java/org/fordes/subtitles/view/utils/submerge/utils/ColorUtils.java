package org.fordes.subtitles.view.utils.submerge.utils;


import cn.hutool.core.util.StrUtil;
import org.fordes.subtitles.view.utils.submerge.parser.exception.InvalidColorCode;

import java.awt.*;


public final class ColorUtils {

    /**
     * Convert the hexadecimal color code to BGR code
     *
     * @param hex
     * @return rgb
     */
    public static int hexToBGR(String hex) {
        Color color = Color.decode(hex);
        int in = Integer.decode(Integer.toString(color.getRGB()));
        int red = (in >> 16) & 0xFF;
        int green = (in >> 8) & 0xFF;
        int blue = (in) & 0xFF;
        return (blue << 16) | (green << 8) | (red);
    }

    /**
     * Convert a &HAABBGGRR to hexadecimal
     *
     * @param haabbggrr: the color code
     * @return the hexadecimal code
     * @throws InvalidColorCode
     */
    public static String HAABBGGRRToHex(String haabbggrr) {
        if (haabbggrr.length() != 10) {
            throw new InvalidColorCode("Invalid pattern, must be &HAABBGGRR");
        }
        StringBuilder sb = new StringBuilder();
        sb.append("#");
        sb.append(haabbggrr.substring(8));
        sb.append(haabbggrr.charAt(6));
        sb.append(haabbggrr.charAt(4));
        sb.append(haabbggrr.charAt(2));
        return sb.toString().toLowerCase();
    }

    /**
     * Convert a &HBBGGRR to hexadecimal
     *
     * @param hbbggrr: the color code
     * @return the hexadecimal code
     */
    public static String HBBGGRRToHex(String hbbggrr) {
        if (hbbggrr.length() != 8) {
            throw new InvalidColorCode("Invalid pattern, must be &HBBGGRR");
        }
        return StrUtil.concat(false, "#", hbbggrr.substring(6),
				hbbggrr.substring(4, 5), hbbggrr.substring(2, 3)).toLowerCase();
    }

    /**
     * Convert a &HAABBGGRR to BGR
     *
     * @param haabbggrr: the color code
     * @return the BGR code
     * @throws InvalidColorCode
     */
    public static int HAABBGGRRToBGR(String haabbggrr) {
        return hexToBGR(HAABBGGRRToHex(haabbggrr));
    }

    /**
     * Convert a &HBBGGRR to BGR
     *
     * @param hbbggrr: the color code
     * @return the BGR code
     * @throws InvalidColorCode
     */
    public static int HBBGGRRToBGR(String hbbggrr) {
        return hexToBGR(HBBGGRRToHex(hbbggrr));
    }

}
