package org.fordes.subview.enums;

import lombok.Getter;
import org.springframework.lang.NonNull;

/**
 * 在线服务提供商枚举
 *
 * @author fordes on 2021/3/18
 */
@Getter
public enum ServerSupplierEnum {

    BAIDU("百度", 1),
    SOUGOU("搜狗", 2),
    GOOGLE("谷歌", 3),
    IFLYTEK("讯飞", 4);

    private Integer code;

    private String name;

    ServerSupplierEnum(String name, Integer code) {
        this.code = code;
        this.name = name;
    }

    public static ServerSupplierEnum get(@NonNull String name) {
        switch (name) {
            case "百度":
                return BAIDU;
            case "搜狗":
                return SOUGOU;
            case "谷歌":
                return GOOGLE;
            case "讯飞":
                return IFLYTEK;
            default:
                return null;
        }
    }

    public static ServerSupplierEnum get(@NonNull Integer code) {
        switch (code) {
            case 1:
                return BAIDU;
            case 2:
                return SOUGOU;
            case 3:
                return GOOGLE;
            case 4:
                return IFLYTEK;
            default:
                return null;
        }
    }

    @Override
    public String toString() {
        return name;
    }
}
