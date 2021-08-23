package org.fordes.subview.utils.common;

import com.google.common.collect.Lists;
import org.fordes.subview.entity.DTO.InterfaceInfoDTO;
import org.fordes.subview.entity.DTO.SystemConfigDTO;
import org.fordes.subview.entity.PO.ApplicationInfo;
import org.fordes.subview.entity.PO.ApplicationSettings;
import org.fordes.subview.entity.PO.InterfaceSupport;
import org.fordes.subview.entity.PO.InterfaceVersion;

import java.util.List;

/**
 * @author fordes on 2021/2/2
 */

public class ConfigurationCommon {

    /**
     * 在线服务接口套餐版本
     */
    public static List<InterfaceVersion> INTERFACE_VERSION = Lists.newArrayList();

    public static List<InterfaceSupport> INTERFACE_SUPPORT = Lists.newArrayList();

    /**
     * 已设置服务接口信息
     */
    public static List<InterfaceInfoDTO> INTERFACE_INFO = Lists.newArrayList();

    /**
     * 程序设置
     */
    public static ApplicationSettings APPLICATION_SETTING = new ApplicationSettings();

    /**
     * 程序信息
     */
    public static ApplicationInfo APPLICATION_INFO = new ApplicationInfo();

    /**
     * 系统配置
     */
    public static SystemConfigDTO SYSTEM_CONFIG = new SystemConfigDTO();

}
