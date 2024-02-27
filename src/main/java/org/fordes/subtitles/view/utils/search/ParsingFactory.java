package org.fordes.subtitles.view.utils.search;

import cn.hutool.http.ContentType;
import org.fordes.subtitles.view.model.search.Selector;

/**
 * 解析器工厂
 *
 * @author fordes on 2022/3/29
 */
public class ParsingFactory {

    private Parsing parsing;

    public ParsingFactory(Object data, ContentType contentType) {
        parsing = ContentType.JSON.equals(contentType)?
                new JSONParsing(data): new HTMLParsing(data);
    }

    public Object getResult(Selector selector) {
        return parsing.parsing(selector);
    }
}
