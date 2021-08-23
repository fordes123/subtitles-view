package org.fordes.subview.service.data;

import cn.hutool.json.JSONObject;
import org.fordes.subview.entity.DTO.InterfaceInfoDTO;
import org.fordes.subview.entity.PO.InterfaceInfo;
import org.fordes.subview.enums.ServerSupplierEnum;

import java.util.List;
import java.util.Map;

/**
 * @author fordes on 2020/12/2
 */
public interface InterfaceInfoService{

    boolean isVoiceInterface();

    boolean updateOrInsert(InterfaceInfo entity);

    List<InterfaceInfoDTO> findInfo(Map<String, Object> params);

    Integer save(InterfaceInfo entity);

    JSONObject findParamBySupportId(Integer supportId);

    List<ServerSupplierEnum> findProviderByType(Integer type);
}
