package org.fordes.subview.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.fordes.subview.entity.DTO.InterfaceInfoDTO;
import org.fordes.subview.entity.PO.InterfaceInfo;

import java.util.List;
import java.util.Map;

/**
 * @author fordes on 2020/12/2
 */
@Mapper
public interface InterfaceInfoDao extends BaseMapper<InterfaceInfo> {

    int selectInterfaceCount(Map<String, Object> params);

    List<InterfaceInfoDTO> selectInterfaceInfo(Map<String, Object> params);

    int insertOne(@Param("entity") InterfaceInfo entity);

    List<Integer> findProviderByType(@Param("server_type") Integer server_type);
}
