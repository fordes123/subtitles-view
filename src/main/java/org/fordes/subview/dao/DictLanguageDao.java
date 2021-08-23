package org.fordes.subview.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.fordes.subview.entity.PO.DictLanguage;

import java.util.Collection;

/**
 * @author fordes on 2021/5/31
 */
@Mapper
public interface DictLanguageDao extends BaseMapper<DictLanguage> {

    void batchInsert(@Param("list") Collection<DictLanguage> list);
}
