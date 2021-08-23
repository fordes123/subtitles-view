package org.fordes.subview.enums;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Locale;


/**
 * 视频文件类型枚举
 *
 * @author fordes on 2020/12/4
 */
@Getter
public enum VideoTypeEnum implements EnumBasic {

    MP4("mp4", "MP4"),
    FLV("flv", "FLV"),
    MOV("mov", "MOV"),
    RMVB("rmvb", "RMVB"),
    MKV("mkv", "MKV");


    VideoTypeEnum(String format, String remark){
        this.suffix = format;
        this.remark = remark;
    }

    private final String suffix;

    private final String remark;

    public static boolean isVideo(String suffix){
        return StrUtil.equalsAny(suffix, Arrays.stream(VideoTypeEnum.values())
                .map(VideoTypeEnum::getSuffix).distinct().toArray(String[]::new));
    }

    public static VideoTypeEnum getType(@Nonnull String suffix) {
        suffix = suffix.toLowerCase(Locale.ROOT);
        switch(suffix) {
            case "mp4": return MP4;
            case "mkv": return MKV;
            case "flv": return FLV;
            case "rmvb": return RMVB;
            case "mov": return MOV;
        }
        return null;
    }
}
