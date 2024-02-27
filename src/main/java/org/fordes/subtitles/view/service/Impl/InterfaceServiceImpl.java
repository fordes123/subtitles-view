package org.fordes.subtitles.view.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.fordes.subtitles.view.enums.ServiceProvider;
import org.fordes.subtitles.view.enums.ServiceType;
import org.fordes.subtitles.view.mapper.InterfaceMapper;
import org.fordes.subtitles.view.mapper.VersionMapper;
import org.fordes.subtitles.view.model.DTO.AvailableServiceInfo;
import org.fordes.subtitles.view.model.PO.ServiceInterface;
import org.fordes.subtitles.view.model.PO.Version;
import org.fordes.subtitles.view.service.InterfaceService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 接口服务
 *
 * @author fordes on 2022/4/17
 */
@Service
@AllArgsConstructor
public class InterfaceServiceImpl extends ServiceImpl<InterfaceMapper, ServiceInterface> implements InterfaceService {

    private final InterfaceMapper interfaceMapper;

    private final VersionMapper versionMapper;

//    private final DictMapper dictMapper;


    @Override
    public List<Version> getVersions(ServiceType type, ServiceProvider provider) {
        return interfaceMapper.getVersions(type.name(), provider.name());
    }

    @Override
    public ServiceInterface getInterface(ServiceType type, ServiceProvider provider) {
        return interfaceMapper.selectOne(new LambdaQueryWrapper<ServiceInterface>()
                .eq(ServiceInterface::getType, type)
                .eq(ServiceInterface::getProvider, provider));
    }


    @Override
    public List<AvailableServiceInfo> getAvailableService(ServiceType type) {
        return interfaceMapper.serviceInfo(type.name());
    }
}

