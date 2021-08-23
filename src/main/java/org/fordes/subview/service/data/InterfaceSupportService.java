package org.fordes.subview.service.data;

import org.fordes.subview.entity.PO.InterfaceSupport;

import java.util.List;

/**
 * @author fordes on 2021/3/31
 */
public interface InterfaceSupportService {

    List<InterfaceSupport> findAll();

    List<InterfaceSupport> findByQuery(Integer provider, Integer type);
}
