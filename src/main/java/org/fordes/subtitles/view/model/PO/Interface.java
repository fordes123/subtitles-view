package org.fordes.subtitles.view.model.PO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName(value = "interface")
public class Interface implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField(value = "provider")
    private String provider;

    @TableField(value = "\"type\"")
    private String type;

    @TableField(value = "auth")
    private String auth;

    @TableField(value = "page")
    private String page;

    @TableField(value = "\"template\"")
    private String template;

    private static final long serialVersionUID = 1L;
}