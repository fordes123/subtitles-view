package org.fordes.subview.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.fordes.subview.entity.PO.ApplicationInfo;

import java.util.List;


/**
 * @author fordes on 2021/3/17
 */
@Mapper
public interface ApplicationInfoDao extends BaseMapper<ApplicationInfo> {

    List<Integer> findSetServerType(@Param("code") Integer code);
}
