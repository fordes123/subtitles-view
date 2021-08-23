package org.fordes.subview.enums;

import lombok.Getter;

/**
 * 版本阶段枚举
 *
 * @author fordes on 2021/4/6
 */
@Getter
public enum VersionPhaseEnum {

    BASE("Base", "开发预览版"),
    ALPHA("Alpha", "内部测试版"),
    BETA("Beta", "测试版"),
    RC("RC", "稳定先行版"),
    RELEASE("Release", "正式版");

    private String name;

    private String remark;

    VersionPhaseEnum(String name, String remark) {
        this.name = name;
        this.remark = remark;
    }


}
