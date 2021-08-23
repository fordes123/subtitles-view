package org.fordes.subview.service.data;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.fordes.subview.entity.PO.UpdateLog;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author fordes on 2021/5/12
 */
public interface UpdateLogService {

    List<UpdateLog> findList(@Nonnull QueryWrapper<UpdateLog> wrapper);
}
