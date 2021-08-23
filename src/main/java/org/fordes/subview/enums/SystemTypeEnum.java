package org.fordes.subview.enums;

import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Sets;
import org.springframework.lang.NonNull;

import java.util.Set;

/**
 * 操作系统类型枚举
 *
 * @author fordes on 2021/4/6
 */
public enum SystemTypeEnum {

    Mac("Mac OS"),
    Windows("Windows"),
    Linux("Linux or Unix");

    private String name;

    SystemTypeEnum(String name) {
        this.name = name;
    }


    @Override
    public String toString() {
        return name;
    }

    /**
     * 获取系统类型
     * @param value os.name
     * @return 类型枚举
     */
    public static SystemTypeEnum getSysType(@NonNull String value) {
        if (StrUtil.startWithIgnoreCase(value, Mac.name)) {
            return Mac;
        }else if (StrUtil.startWithIgnoreCase(value, Windows.name)){
            return Windows;
        }
        return Linux;
    }
}
