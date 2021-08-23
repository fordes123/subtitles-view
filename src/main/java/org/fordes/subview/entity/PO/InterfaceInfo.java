package org.fordes.subview.entity.PO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 服务接口实体
 *
 * @author fordes on 2020/12/2
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@TableName(value = "interface_info")
public class InterfaceInfo {

    /**
     * 类型编码
     */
    @TableId(type = IdType.INPUT)
    private Integer support_id;

    /**
     * 版本编码
     */
    private Integer version_id;

    /**
     * 配置参数
     */
    private String param;


    /**
     * 并发量（qps）
     */
    private Integer concurrency;

    /**
     * 是否内置 0、是，1 否
     */
    private boolean inner;

    /**
     * 单次最大处理量
     */
    private Integer processing;
}
