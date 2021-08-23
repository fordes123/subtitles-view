package org.fordes.subview.utils.constants;

/**
 * 字幕常量
 *
 * @author fordes on 2020/12/16
 */
public class SubtitlesConstant {

    public final static String REG_STR_TIME_LINE_DETAIL = "(\\d+):(\\d+):(\\d+),(\\d+)\\s-->\\s(\\d+):(\\d+):(\\d+),(\\d+)";

    public final static String REG_STR_TIME_LINE = "(\\d+:\\d+:\\d+,\\d+)\\s-->\\s(\\d+:\\d+:\\d+,\\d+)";

    public final static String REG_STR_PART = "\uFEFF?(\\d+)\\r\\n([\\d,,:]+)\\s-->\\s([\\d,,:]+)\\r\\n([\\s\\S]*)";

    public final static String REG_SRT_SERIAL = "^\\d+$";

    public final static String REG_SRT_SPLIT = "\r\n\r\n";

    public final static String FORMAT_SRT_TIME_LINE = "HH:mm:ss,SSS";

    public final static String CONNECTION_SRT = " --> ";

    public final static String REG_ASS_TIME_LINE_DETAIL = "Dialogue:\\s\\d\\,(\\d+):(\\d+):(\\d+)\\.(\\d+),(\\d+):(\\d+):(\\d+)\\.(\\d+)\\,(.*)";

    public final static String REG_ASS_TIME_LINE = "(Dialogue:\\s\\d\\,(\\d+:\\d+:\\d+\\.\\d+),(\\d+:\\d+:\\d+\\.\\d+)(.*)\\,\\,)(.*)";

    public final static String FORMAT_ASS_TIME_LINE = "HH:mm:ss.SS";

    public final static String REGEX_ASS_TIME_LINE = "(\\d+:\\d+:\\d+\\.\\d+)";

    public final static String FORMAT_ASS_CONTENT = "\\,.*\\,\\,(.*)";

    public final static String PLACEHOLDER = "{}";

    public final static String TEMPLATE_ASS_DEFAULT = "Dialogue: 0,{},{},*Default,NTP,0000,0000,0000,,{}";

    public final static String FORMAT_LRC_LINE = "\\[(\\d+:\\d+.\\d+)\\](.*)";

    public final static String FORMAT_LRC_TIME_LINE = "mm:ss.SS";

    public final static String SRT_CONTENT_PARSING_REG = "^(<.*>)(.*)([</\\w+>]?)$";

    public final static String UNKNOWN = "unknown";
}