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
 * @author fordes on 2021/5/12
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@TableName(value = "update_log")
public class UpdateLog {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String version;

    private String version_type;

    private String detail;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date gmt_create;
}
