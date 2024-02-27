package org.fordes.subtitles.view.model.PO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;
import org.fordes.subtitles.view.enums.ServiceProvider;
import org.fordes.subtitles.view.enums.ServiceType;

import java.io.Serializable;

/**
 * @author fordes on 2022/4/19
 */
@Data
@Accessors(chain = true)
@TableName(value = "interface")
public class ServiceInterface implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 服务提供商 {@link ServiceProvider}
     */

    @TableField(value = "provider")
    private ServiceProvider provider;

    /**
     * 服务类型 {@link ServiceType}
     */
    @TableField(value = "type")
    private ServiceType type;

    /**
     * 授权信息
     */
    @TableField(value = "auth")
    private String auth;

    /**
     * 授权信息模板
     */
    @TableField(value = "template")
    private String template;


    /**
     * 主页
     */
    @TableField(value = "page")
    private String page;

    private static final long serialVersionUID = 1L;
}