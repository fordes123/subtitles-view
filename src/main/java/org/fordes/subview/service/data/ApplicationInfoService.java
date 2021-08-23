package org.fordes.subview.service.data;

import org.fordes.subview.entity.PO.ApplicationInfo;
import org.fordes.subview.entity.PO.ApplicationSettings;
import org.fordes.subview.enums.ServerSupplierEnum;
import org.fordes.subview.enums.ServerTypeEnum;

import java.util.List;

/**
 * @author fordes on 2021/3/31
 */
public interface ApplicationInfoService {

    ApplicationInfo getInfo();

    boolean save(ApplicationSettings settings);

    List<ServerSupplierEnum> findSetServerType(ServerTypeEnum type);
}
