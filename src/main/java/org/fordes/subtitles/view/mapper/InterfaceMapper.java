package org.fordes.subtitles.view.mapper;

import cn.hutool.core.lang.Dict;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.fordes.subtitles.view.model.DTO.AvailableServiceInfo;
import org.fordes.subtitles.view.model.PO.ServiceInterface;
import org.fordes.subtitles.view.model.PO.Version;

import java.util.List;

/**
 * @author fordes on 2022/4/17
 */
@Mapper
public interface InterfaceMapper extends BaseMapper<ServiceInterface> {


    List<AvailableServiceInfo> serviceInfo(@Param("type") String type);

    List<Version> getVersions(@Param("type") String serviceType, @Param("provider") String provider);

    List<Dict> getLanguageList();
}