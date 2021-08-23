package org.fordes.subview.entity.DTO;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Collection;

/**
 * 有序结果集
 *
 * @author fordes on 2021/6/15
 */
@Data
@Accessors(chain = true)
public class OrderlyResult {

    private Integer serial;

    private Collection<String> content;

    @Override
    public String toString() {
        return CollUtil.join(content, StrUtil.LF);
    }
}
