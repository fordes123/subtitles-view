package org.fordes.subtitles.view.model.PO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author fordes on 2022/4/17
 */
@Data
@Accessors(chain = true)
@TableName(value = "version")
public class Version implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField(value = "interface_id")
    private Integer interfaceId;

    @TableField(value = "\"name\"")
    private String name;

    @TableField(value = "concurrent")
    private Integer concurrent;

    @TableField(value = "carrying")
    private Integer carrying;

    @TableField(value = "server_url")
    private String serverUrl;

    @TableField(value = "remark")
    private String remark;

    public String toString() {
        return this.name;
    }

    private static final long serialVersionUID = 1L;
}