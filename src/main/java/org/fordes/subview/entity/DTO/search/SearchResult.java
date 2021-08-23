package org.fordes.subview.entity.DTO.search;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 搜索结果集
 *
 * @author fordes on 2021/7/20
 */
@Data
@Accessors(chain = true)
public class SearchResult {

    //数据
    private List<SearchItem> data;

    //下一页
    private Object next;

    //状态码
    private boolean isFile;

    public boolean hasNext() {
        return next!=null;
    }

    public boolean hasData() { return !data.isEmpty(); }
}
