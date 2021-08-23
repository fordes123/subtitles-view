package org.fordes.subview.service.data.Impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.fordes.subview.dao.InterfaceSupportDao;
import org.fordes.subview.entity.PO.InterfaceSupport;
import org.fordes.subview.service.data.InterfaceSupportService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * @author fordes on 2021/3/31
 */
@Service
public class InterfaceSupportServiceImpl implements InterfaceSupportService {

    @Resource
    private InterfaceSupportDao supportDao;

    @Override
    public List<InterfaceSupport> findAll() {
        List<InterfaceSupport> result = supportDao.selectList(new QueryWrapper<>());
        return CollectionUtil.isEmpty(result)? Collections.emptyList(): result;
    }

    @Override
    public List<InterfaceSupport> findByQuery(Integer provider, Integer type) {
        QueryWrapper<InterfaceSupport> wrapper = new QueryWrapper<>();
        if (provider != null) {
            wrapper.lambda().eq(InterfaceSupport::getProvider_id, provider);
        }
        if (type != null) {
            wrapper.lambda().eq(InterfaceSupport::getServer_type, type);
        }
        return supportDao.selectList(wrapper);
    }
}
