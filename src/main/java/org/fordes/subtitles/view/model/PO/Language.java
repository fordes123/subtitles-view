package org.fordes.subtitles.view.model.PO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@TableName(value = "\"language\"")
public class Language implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String name;

    @TableField(exist = false)
    private String code;

    @TableField(exist = false)
    private boolean general;

    @TableField(exist = false)
    private List<String> _target;

    @TableField(exist = false)
    private List<Language> target;

    @TableField("huoshan")
    private String huoshan;


    public String toString() {
        return this.name;
    }

    private static final long serialVersionUID = 1L;

    public static final String COL_ID = "id";

    public static final String COL_TYPE = "type";

    public static final String COL_NAME = "name";

    public static final String COL_GENERAL = "general";

    public static final String TARGET = "_target";
}