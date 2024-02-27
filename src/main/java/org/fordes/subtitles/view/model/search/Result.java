package org.fordes.subtitles.view.model.search;

import cn.hutool.core.map.MapUtil;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author fordes on 2022/2/12
 */
@Data
@Builder
public class Result {

    private Type type;

    private Cases page;

    private List<Item> data;

    @Builder
    public static class Item {

        public Cases next;

        public String caption;

        public String text;

        public boolean isFile = false;

        public Map<String, Object> params = MapUtil.newHashMap();
    }

    public static enum Type {
        //普通搜索
        SEARCH(),
        //分页
        PAGE()
    }
}
