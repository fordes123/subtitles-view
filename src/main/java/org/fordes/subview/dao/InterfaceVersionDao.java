package org.fordes.subview.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.fordes.subview.entity.PO.InterfaceVersion;

import java.util.List;

/**
 * @author fordes on 2021/2/4
 */
@Mapper
public interface InterfaceVersionDao extends BaseMapper<InterfaceVersion> {

    List<InterfaceVersion> findByQuery(@Param("provider") Integer provider,
                                       @Param("type") Integer type);
}
