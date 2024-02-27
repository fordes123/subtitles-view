package org.fordes.subtitles.view.model.search;

import java.io.Serializable;

/**
 * 字段解析器
 * @author fordes on 2022/3/28
 */
public class Selector implements Serializable {

    /**
     * 唯一性标识
     * false：按条件提取结果集，true：按条件提取唯一结果
     */
    public boolean only = false;

    /**
     * 正则提取，提取匹配正则的内容
     * 高优先级
     */
    public String regular;

    /**
     * 内容格式化模板，参考{@see cn.hutool.core.util.StrUtil.format()}
     */
    public String format;

    /**
     * key选择 多层级使用"."连接 如：a.c.b
     */
    public String jsonKey;

    /**
     * css选择器 参考Jsoup css选择器
     * 高优先级
     */
    public String css;

    /**
     * 属性选择 提取指定属性，为空时使用text()
     */
    public String attr;

}
