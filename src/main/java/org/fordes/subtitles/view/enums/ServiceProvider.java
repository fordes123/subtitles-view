package org.fordes.subtitles.view.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ServiceProvider implements IEnum<String> {

    BAIDU("百度"),

    TENCENT("腾讯"),
    ALI("阿里"),

    HUOSHAN("火山");

    private final String desc;

    @Override
    public String toString() {
        return this.desc;
    }

    @Override
    public String getValue() {
        return this.name();
    }
}
