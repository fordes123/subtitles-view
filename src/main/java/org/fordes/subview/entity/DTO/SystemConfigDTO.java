package org.fordes.subview.entity.DTO;

import lombok.Data;
import lombok.experimental.Accessors;
import org.fordes.subview.enums.SystemTypeEnum;

/**
 * @author fordes on 2021/4/7
 */
@Data
@Accessors(chain = true)
public class SystemConfigDTO {

    /**
     * 用户名
     */
    private String user;

    /**
     * ip
     */
    private String ip;

    /**
     * 主机名
     */
    private String host;

    /**
     * 用户主目录
     */
    private String home;

    /**
     * 当前工作目录
     */
    private String work;

    /**
     * 默认临时目录
     */
    private String tmpdir;

    /**
     * 系统名
     */
    private SystemTypeEnum name;

    /**
     * 系统版本
     */
    private String version;

    /**
     * 系统架构
     */
    private String arch;

    /**
     * java运行环境版本
     */
    private String java;
}
