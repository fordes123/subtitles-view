package org.fordes.subview.service.data.Impl;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.fordes.subview.dao.InterfaceInfoDao;
import org.fordes.subview.entity.DTO.InterfaceInfoDTO;
import org.fordes.subview.entity.PO.InterfaceInfo;
import org.fordes.subview.enums.ServerSupplierEnum;
import org.fordes.subview.service.data.InterfaceInfoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 服务接口信息业务层
 *
 * @author fordes on 2020/12/2
 */
@Service
@Slf4j
public class InterfaceInfoServiceImpl implements InterfaceInfoService {

    @Resource
    private InterfaceInfoDao infoDao;

    @Override
    public boolean isVoiceInterface() {
        int result = infoDao.selectInterfaceCount(ImmutableMap.of("server_type", 2));
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateOrInsert(InterfaceInfo entity) {
        Integer count = infoDao.selectCount(new QueryWrapper<InterfaceInfo>().lambda()
                .eq(InterfaceInfo::getSupport_id, entity.getSupport_id()));
        if (count != null && count <= 1) {
            if (count == 1) {
                infoDao.updateById(entity);
            } else infoDao.insert(entity);
            return true;
        }
        return false;
    }

    @Override
    public List<InterfaceInfoDTO> findInfo(Map<String, Object> params) {
        List<InterfaceInfoDTO> result = infoDao.selectInterfaceInfo(params);
        return CollectionUtil.isEmpty(result)? Collections.emptyList(): result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer save(InterfaceInfo entity) {
        int count = infoDao.selectCount(new QueryWrapper<InterfaceInfo>().lambda().eq(InterfaceInfo::getSupport_id, entity.getSupport_id()));
        if (count > 0) {
            return infoDao.updateById(entity);
        }else return infoDao.insert(entity);

    }

    @Override
    public JSONObject findParamBySupportId(Integer supportId) {
        InterfaceInfo info = infoDao.selectOne(new QueryWrapper<InterfaceInfo>().lambda().eq(InterfaceInfo::getSupport_id, supportId));
        if (info != null) {
            return JSONUtil.parseObj(info.getParam());
        }
        return null;
    }

    @Override
    public List<ServerSupplierEnum> findProviderByType(Integer type) {
        List<Integer> providers = infoDao.findProviderByType(type);
        return providers.stream().map(ServerSupplierEnum::get).collect(Collectors.toList());
    }
}
