package org.fordes.subtitles.view.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 服务类型枚举
 *
 * @author fordes on 2022/4/17
 */
@Getter
@AllArgsConstructor
public enum ServiceType implements IEnum<String> {

    VOICE("语音转写"),

    TRANSLATE("翻译");

    private final String desc;

    @Override
    public String toString() {
        return this.getDesc();
    }

    public String getValue() {
        return this.name();
    }
}