package org.fordes.subview.enums;

import lombok.Getter;

/**
 * @author fordes on 2021/7/19
 */
public enum DataTypeEnum {

    VIDEO("视频", VideoTypeEnum.class),
    SUBTITLES("字幕", SubtitlesTypeEnum.class);

    @Getter
    private String remark;

    @Getter
    private Class<?> typeClass;

    DataTypeEnum(String remark, Class<?> typeClass) {
        this.remark = remark;
        this.typeClass = typeClass;
    }

    @Override
    public String toString() {
        return this.remark;
    }
}
