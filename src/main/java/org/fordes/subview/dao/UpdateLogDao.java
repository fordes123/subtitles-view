package org.fordes.subview.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.fordes.subview.entity.PO.UpdateLog;

/**
 * @author fordes on 2021/5/12
 */
@Mapper
public interface UpdateLogDao extends BaseMapper<UpdateLog> {
}
