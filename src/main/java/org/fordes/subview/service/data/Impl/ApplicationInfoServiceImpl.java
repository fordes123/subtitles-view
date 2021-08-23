package org.fordes.subview.service.data.Impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import org.fordes.subview.dao.ApplicationInfoDao;
import org.fordes.subview.entity.PO.ApplicationInfo;
import org.fordes.subview.entity.PO.ApplicationSettings;
import org.fordes.subview.enums.ServerSupplierEnum;
import org.fordes.subview.enums.ServerTypeEnum;
import org.fordes.subview.service.data.ApplicationInfoService;
import org.fordes.subview.utils.common.ApplicationConfig;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


/**
 * @author fordes on 2021/3/31
 */
@Service
public class ApplicationInfoServiceImpl implements ApplicationInfoService {

    @Resource
    private ApplicationInfoDao settingsDao;

    @Override
    public ApplicationInfo getInfo() {
        return settingsDao.selectOne(new QueryWrapper<ApplicationInfo>()
                .lambda().eq(ApplicationInfo::getId, 1));
    }

    @Override
    public boolean save(ApplicationSettings settings) {
        ApplicationInfo old = ObjectUtil.clone(ApplicationConfig.getInstance().getApplicationInfo());
        System.out.println(JSONUtil.toJsonStr(settings));
        old.setSettings(JSONUtil.toJsonStr(settings));
        if (settingsDao.updateById(old) > 0) {
            ApplicationConfig.getInstance().setApplicationInfo(old);
            return true;
        }
        return false;
    }

    @Override
    public List<ServerSupplierEnum> findSetServerType(ServerTypeEnum type) {
        List<ServerSupplierEnum> result = Lists.newArrayList();
        settingsDao.findSetServerType(type.getCode()).forEach(item -> {
            result.add(ServerSupplierEnum.get(item));
        });
        return  result;
    }
}
