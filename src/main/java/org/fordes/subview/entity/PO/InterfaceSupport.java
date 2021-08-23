package org.fordes.subview.entity.PO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author fordes on 2021/2/2
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@TableName(value = "interface_support")
public class InterfaceSupport {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 服务提供者id
     */
    private Integer provider_id;

    /**
     * 服务类型id
     */
    private Integer server_type;

    /**
     * 申请页面
     */
    private String page;

    /**
     * 配置参数模板
     */
    private String param;

    /**
     * 备注
     */
    private String remark;
}
