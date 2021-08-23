package org.fordes.subview.enums;

import lombok.Getter;

/**
 * 在线服务类型枚举
 *
 * @author fordes on 2021/3/18
 */
@Getter
public enum ServerTypeEnum {

    LITERAL_TRANSLATION("文字翻译", 1, "将多种语言文字互相翻译"),
    PHONETIC_TRANSCRIPTION("语音转写", 2, "将语音转换为文字"),
    SPEECH_SYNTHESIS("语音合成", 3, "将文字合成为语音");

    private Integer code;

    private String name;

    private String remark;

    @Override
    public String toString() {
        return name;
    }

    ServerTypeEnum(String name, Integer code, String remark) {
        this.code = code;
        this.name = name;
        this.remark = remark;
    }

    public static ServerTypeEnum get(Integer code) {
        switch (code) {
            case 1:
                return LITERAL_TRANSLATION;
            case 2:
                return PHONETIC_TRANSCRIPTION;
            case 3:
                return SPEECH_SYNTHESIS;
            default:
                return null;
        }
    }

    public static ServerTypeEnum get(String name) {
        switch (name) {
            case "文字翻译":
                return LITERAL_TRANSLATION;
            case "语音转写":
                return PHONETIC_TRANSCRIPTION;
            case "语音合成":
                return SPEECH_SYNTHESIS;
            default:
                return null;
        }
    }
}
