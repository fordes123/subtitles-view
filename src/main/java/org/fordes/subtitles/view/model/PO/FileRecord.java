package org.fordes.subtitles.view.model.PO;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.Accessors;
import org.fordes.subtitles.view.enums.FileEnum;

import java.io.File;
import java.util.Date;

/**
 * 文件抽象类
 *
 * @author fordes on 2020/12/4
 */
@Data
@Accessors(chain = true)
public class FileRecord {

    @JsonIgnore
    private File file;

    /**
     * 名称
     */
    private String file_name;

    /**
     * 格式
     */
    private FileEnum format;

    /**
     * 语言
     */
    private String language;

    /**
     * 文件路径
     */
    private String path;


    /**
     * 文件字节大小
     */
    private Long size_byte;

    /**
     * 文件大小
     */
    private String size;

    /**
     * 长度，字幕为时间轴起止，视频为时长
     */
    private Long duration;

    /**
     * 编码
     */
    private String charset;

    /**
     * 文件最后修改时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date file_modify_time;

}
