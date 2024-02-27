package org.fordes.subtitles.view.enums;

import lombok.Getter;

/**
 * 7zip结束码枚举
 *
 * @author fordes on 2021/1/7
 */
@Getter
public enum SevenZipEnum {

    NORMAL(0, "未发生错误"),
    WARNING(1,"警告，发生部分错误"),
    FATAL_ERROR(2, "致命错误"),
    COMMAND_ERROR(7, "命令错误"),
    OUT_OF_MEMORY_ERROR(8, "内存不足"),
    TERMINATION(255, "操作终止"),
    UNKNOWN_ERROR(-1, "未知错误");

    SevenZipEnum(int code, String status) {
        this.code = code;
        this.status = status;
    }

    private final int code;

    private final String status;

    public static String getStatus(int code){
        switch (code){
            case 0:
                return NORMAL.status;
            case 1:
                return WARNING.status;
            case 2:
                return FATAL_ERROR.status;
            case  7:
                return COMMAND_ERROR.status;
            case 8:
                return OUT_OF_MEMORY_ERROR.status;
            case 255:
                return TERMINATION.status;
            default:
                return UNKNOWN_ERROR.status;
        }
    }
}
