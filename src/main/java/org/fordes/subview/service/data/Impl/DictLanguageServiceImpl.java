package org.fordes.subview.service.data.Impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.fordes.subview.dao.DictLanguageDao;
import org.fordes.subview.entity.PO.DictLanguage;
import org.fordes.subview.service.data.DictLanguageService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author fordes on 2021/5/31
 */
@Service
public class DictLanguageServiceImpl implements DictLanguageService {

    @Resource
    private DictLanguageDao dictLanguageDao;


    @Override
    public List<DictLanguage> findList(DictLanguage entity) {
        QueryWrapper<DictLanguage> queryWrapper = new QueryWrapper<>();
        if (entity != null) {
            if (entity.getSupplier_id() != null) {
                queryWrapper.lambda().eq(DictLanguage::getSupplier_id, entity.getSupplier_id());
            }
            if (entity.getServer_type() != null) {
                queryWrapper.lambda().eq(DictLanguage::getSupplier_id, entity.getServer_type());
            }
            if (StrUtil.isNotEmpty(entity.getLanguage())) {
                queryWrapper.lambda().eq(DictLanguage::getLanguage, entity.getLanguage());
            }
            if (StrUtil.isNotEmpty(entity.getCode())) {
                queryWrapper.lambda().eq(DictLanguage::getCode, entity.getCode());
            }
        }
        return  dictLanguageDao.selectList(queryWrapper);
    }
}
