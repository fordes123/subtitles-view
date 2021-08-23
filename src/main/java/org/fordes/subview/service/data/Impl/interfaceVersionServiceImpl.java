package org.fordes.subview.service.data.Impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.fordes.subview.dao.InterfaceVersionDao;
import org.fordes.subview.entity.PO.InterfaceVersion;
import org.fordes.subview.service.data.InterfaceVersionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * @author fordes on 2021/3/31
 */
@Service
public class interfaceVersionServiceImpl implements InterfaceVersionService {

    @Resource
    private InterfaceVersionDao versionDao;

    @Override
    public List<InterfaceVersion> findAll() {
        List<InterfaceVersion> result = versionDao.selectList(new QueryWrapper<>());
        return CollectionUtil.isEmpty(result) ?
                Collections.emptyList(): result;
    }

    @Override
    public List<InterfaceVersion> findByQuery(Integer provider, Integer type) {
        return versionDao.findByQuery(provider, type);
    }


}
