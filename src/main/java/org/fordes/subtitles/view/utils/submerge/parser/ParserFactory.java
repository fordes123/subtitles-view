package org.fordes.subtitles.view.utils.submerge.parser;


import cn.hutool.core.util.StrUtil;

public final class ParserFactory {

    /**
     * Return the subtitle parser for the subtitle format matching the extension
     *
     * @param extension the subtitle extention
     * @return the subtitle parser, null if no matching parser
     */
    public static SubtitleParser getParser(String extension) throws Exception {

        SubtitleParser parser = null;
        if (StrUtil.equalsAnyIgnoreCase(extension, "ass", "ssa")) {
            return new ASSParser();
        } else if (StrUtil.equalsIgnoreCase(extension, "srt")) {
            return new SRTParser();
        } else if (StrUtil.equalsIgnoreCase(extension, "lrc")) {
            return new LRCParser();
        }
        throw new Exception(extension + " format not supported");
    }

    /**
     * Private constructor
     */
    private ParserFactory() {

        throw new AssertionError();
    }

}
