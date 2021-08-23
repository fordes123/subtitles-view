package org.fordes.subview.service.data;


import org.fordes.subview.entity.PO.DictLanguage;

import java.util.List;

/**
 * @author fordes on 2021/5/31
 */
public interface DictLanguageService {

    List<DictLanguage> findList(DictLanguage entity);
}
