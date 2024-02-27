package org.fordes.subtitles.view.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.fordes.subtitles.view.enums.ServiceProvider;
import org.fordes.subtitles.view.enums.ServiceType;
import org.fordes.subtitles.view.model.DTO.AvailableServiceInfo;
import org.fordes.subtitles.view.model.PO.ServiceInterface;
import org.fordes.subtitles.view.model.PO.Version;

import java.util.List;

/**
 * 接口服务
 *
 * @author fordes on 2022/4/17
 */
public interface InterfaceService extends IService<ServiceInterface> {


    /**
     * 获取指定接口的版本信息
     *
     * @param type     服务类型 {@link ServiceType}
     * @param provider 服务提供商 {@link ServiceProvider}
     * @return { @link Version}
     */
    List<Version> getVersions(ServiceType type, ServiceProvider provider);

    ServiceInterface getInterface(ServiceType type, ServiceProvider provider);


    /**
     * 获取可用的服务接口
     *
     * @param type 服务类型 {@link ServiceType}
     * @return {@link AvailableServiceInfo}
     */
    List<AvailableServiceInfo> getAvailableService(ServiceType type);
}
