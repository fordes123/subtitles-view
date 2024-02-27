package org.fordes.subtitles.view.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.fordes.subtitles.view.model.PO.Version;

/**
 * @author fordes on 2022/4/17
 */
@Mapper
public interface VersionMapper extends BaseMapper<Version> {
}