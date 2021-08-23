package org.fordes.subview.entity.PO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;



/**
 * 服务(套餐)版本
 *
 * @author fordes on 2021/2/4
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@TableName(value = "interface_version")

public class InterfaceVersion {

    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 支持id
     */
    private Integer support_id;

    /**
     * 套餐名
     */
    private String name;

    /**
     * 并发量（qps）
     */
    private Integer concurrency;

    /**
     * 处理量
     */
    private Integer processing;

    /**
     * 备注
     */
    private String remark;

    @Override
    public String toString() {
        return name;
    }
}
