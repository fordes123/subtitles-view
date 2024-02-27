package org.fordes.subtitles.view.enums;

import cn.hutool.core.util.StrUtil;
import lombok.AllArgsConstructor;

import java.util.Arrays;

/**
 * 文件类型枚举
 *
 * @author fordes on 2022/2/9
 */
@AllArgsConstructor
public enum FileEnum {

    //视频
    MP4("mp4", true, true, false),
    MKV("mkv", true, true, false),
    AVI("avi", true, true, false),
    RMVB("rmvb", true, true, false),
    TS("ts", true, true, false),

    //音频
    MP3("mp3", true, false, false),
    FLAC("flac", true, false, false),
    AAC("aac", true, false, false),

    //字幕
    LRC("lrc", true, false, true),
    SRT("srt", true, false, true),
    ASS("ass", true, false, true);

    public final String suffix;

    public final boolean support;

    public final boolean media;

    public final boolean subtitle;

    public static final String[] SUPPORT_SUBTITLE = Arrays.stream(FileEnum.values())
            .filter(e -> e.subtitle && e.support).map(e -> e.suffix).toArray(String[]::new);

    public static final String[] SUPPORT_MEDIA = Arrays.stream(FileEnum.values())
            .filter(e -> e.media && e.support).map(e -> e.suffix).toArray(String[]::new);

    public static boolean isMedia(String suffix) {
        return Arrays.stream(FileEnum.values())
                .filter(e -> e.media)
                .anyMatch(e -> StrUtil.equalsIgnoreCase(e.suffix, suffix));
    }

    public static boolean isSupport(String suffix) {
        return Arrays.stream(FileEnum.values())
                .filter(e -> e.support)
                .anyMatch(e -> StrUtil.equalsIgnoreCase(e.suffix, suffix));
    }

    public static boolean check(String suffix, boolean isSupport, boolean isMedia, boolean isSubtitle) {
        FileEnum val = of(suffix);
        return val != null && (val.support == isSupport) && (val.media == isMedia) && (val.subtitle == isSubtitle);
    }

    public static FileEnum of(String name) {
        for (FileEnum value : FileEnum.values()) {
            if (StrUtil.equalsIgnoreCase(name, value.suffix)) {
                return value;
            }
        }
        return null;
    }
}