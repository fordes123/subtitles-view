package org.fordes.subview.service.data;

import org.fordes.subview.entity.PO.InterfaceVersion;

import java.util.List;

/**
 * @author fordes on 2021/3/31
 */
public interface InterfaceVersionService {

    List<InterfaceVersion> findAll();

    /**
     * 通过服务提供商和服务类型获得支持版本
     * @param provider 服务提供者
     * @param type 服务类型
     * @return 支持版本
     */
    List<InterfaceVersion> findByQuery(Integer provider, Integer type);
}
