package org.fordes.subview.enums;

import lombok.Getter;

/**
 * 消息提示枚举
 *
 * @author fordes on 2021/1/28
 */
@Getter
public enum ToastEnum {

    SUCCESS(2000, "操作成功"),
    FAIL(2000, "操作失败"),
    ERROR(3000,"错误"),
    WARN(3000, "警告"),
    INFO(2000, "提示");

    ToastEnum(int time, String remark){
        this.time = time;
        this.remark = remark;
    }

    private final int time;

    private final String remark;


}
