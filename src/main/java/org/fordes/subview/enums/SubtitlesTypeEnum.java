package org.fordes.subview.enums;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;

/**
 * 字幕文件类型枚举类
 *
 * @author fordes on 2020/12/4
 */
@Getter
public enum SubtitlesTypeEnum implements EnumBasic {

    ASS("ass", "ASS特效字幕", "{}:{}:{}.{}"),
    SRT("srt", "SRT普通字幕","{}:{}:{},{}"),
    LRC("lrc", "LRC歌词", "");


    SubtitlesTypeEnum(String suffix, String remark, String format){
        this.suffix = suffix;
        this.remark = remark;
        this.format = format;
    }

    private final String suffix;

    private final String remark;

    private final String format;

    public static boolean isSubtitles(String suffix){
        return StrUtil.equalsAnyIgnoreCase(suffix, ASS.suffix, SRT.suffix, LRC.suffix);
    }


    public static SubtitlesTypeEnum getType(String suffix){
        if (suffix.equals(ASS.suffix)){
            return ASS;
        }
        if (suffix.equals(SRT.suffix)){
            return SRT;
        }
        if (suffix.equals(LRC.suffix)){
            return LRC;
        }
        return null;
    }

    @Override
    public String toString() {
        return this.suffix;
    }
}
