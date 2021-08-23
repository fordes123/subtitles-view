package org.fordes.subview.entity.PO;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 应用信息
 *
 * @author fordes on 2021/4/6
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@TableName(value = "application_info")
public class ApplicationInfo implements Serializable {

    @TableId(type = IdType.INPUT)
    private Integer id;

    private String version;

    private String name;

    private String type;

    private String type_cn;

    private String build_version;

    private String settings;

    private String build_time;

    private String home_page;

    private String issues;

    public ApplicationSettings formatSettings() {
        return JSONUtil.toBean(this.settings, ApplicationSettings.class);
    }
}
