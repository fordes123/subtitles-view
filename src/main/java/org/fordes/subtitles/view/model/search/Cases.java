package org.fordes.subtitles.view.model.search;

import cn.hutool.http.ContentType;
import lombok.Builder;

import java.io.Serializable;
import java.util.Map;

/**
 * @author fordes on 2022/3/28
 */
@Builder
public class Cases implements Serializable {

    public static final String CAPTION = "caption";

    public static final String TEXT = "text";

    public static final String PAGE = "page";

    public String[] keys;

    public Object url;

    public ContentType type;

    public Map<String, Selector> params;

    public Cases next;

    public void setType(String val) {
        this.type = ContentType.valueOf(val);
    }
}
