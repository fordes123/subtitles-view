package org.fordes.subview.enums;

import lombok.Getter;

/**
 * @author fordes on 2021/7/21
 */
public enum SearchEnum {

    CLEAR_REFRESH(0, "清除刷新"),
    ADDITIONAL(1, "添加到末尾"),
    FILE_VIEW(3, "文件浏览"),
    FILE_OPEN(4, "打开文件");

    private Integer code;

    @Getter
    private String remark;


    SearchEnum(Integer code, String remark) {
        this.code = code;
        this.remark = remark;
    }
}
