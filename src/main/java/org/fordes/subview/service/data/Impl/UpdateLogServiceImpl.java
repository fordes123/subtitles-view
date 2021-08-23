package org.fordes.subview.service.data.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.fordes.subview.dao.UpdateLogDao;
import org.fordes.subview.entity.PO.UpdateLog;
import org.fordes.subview.service.data.UpdateLogService;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
import java.util.List;

/**
 * @author fordes on 2021/5/12
 */
@Service
public class UpdateLogServiceImpl implements UpdateLogService {

    @Resource
    private UpdateLogDao updateLogDao;

    @Override
    public List<UpdateLog> findList(@Nonnull QueryWrapper<UpdateLog> wrapper) {
        return updateLogDao.selectList(wrapper.lambda().orderByDesc(UpdateLog::getId));
    }
}
