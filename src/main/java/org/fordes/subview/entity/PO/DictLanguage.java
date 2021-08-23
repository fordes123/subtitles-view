package org.fordes.subview.entity.PO;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author fordes on 2021/5/31
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@TableName(value = "dict_language")
public class DictLanguage {

    @TableId(type = IdType.INPUT)
    private Integer supplier_id;

    private String language;

    private String code;

    private Integer server_type;

    @Override
    public String toString() {
        return language;
    }
}
