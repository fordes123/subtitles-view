package org.fordes.subview.entity.PO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 文件信息实体
 *
 * @author Fordes on 2020/11/8
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@TableName(value = "file_info")
public class FileInfo {

    @TableId(value = "id", type = IdType.AUTO)//指定自增策略
    private Integer id;

    /**
     * 文件路径
     */
    private String file_path;

    /**
     * 文件名
     */
    private String file_name;

    /**
     * 文件后缀名
     */
    private String file_suffix;

    /**
     * 文件字节大小
     */
    private Long file_size_byte;

    /**
     * 文件大小
     */
    private String file_size;


    /**
     * 文件状态 0 正常， 1 被删除或移动
     */
    private Integer status;


    /**
     * 文件最后修改时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date file_modify_time;

    /**
     * 创建时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date gmtCreate;

    /**
     * 更新时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date gmtModified;
}
