package org.fordes.subtitles.view.model.PO;

import cn.hutool.json.JSONUtil;
import lombok.Data;
import lombok.experimental.Accessors;
import org.fordes.subtitles.view.model.search.Cases;

/**
 * @author fordes on 2022/2/15
 */
@Data
@Accessors(chain = true)
public class SearchCases {

    /**
     * 自增主键
     */
    private Integer id;

    /**
     * 名称
     */
    private String name;

    /**
     * 图标 {@link org.fordes.subtitles.view.enums.FontIcon}
     */
    private String icon;

    /**
     * 用例
     */
    private Cases cases;

    /**
     * 备注
     */
    private String remark;


    public void setCases(String cases) {
        this.cases = JSONUtil.toBean(cases, Cases.class);
    }
}
