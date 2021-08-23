package org.fordes.subview.enums;

import lombok.Getter;

/**
 * 字幕搜索枚举
 *
 * @author fordes on 2020/12/4
 */
@Getter
public enum SubtitlesSearchEnum {

    ASSRT("102", "伪射手网"),
    ZMK("103", "字幕库"),
//    SubHD("101", "SubHD"),
    DDZM("104", "点点字幕");


    SubtitlesSearchEnum(String code, String value){
        this.code = code;
        this.value = value;
    }

    private final String code;

    private final String value;

    @Override
    public String toString() {
        return this.value;
    }


}
