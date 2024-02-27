package org.fordes.subtitles.view.utils.search;

import org.fordes.subtitles.view.model.search.Selector;

/**
 * 解析器抽象
 *
 * @author fordes on 2022/3/29
 */
public abstract class Parsing {

    public Parsing(Object data) {

    }

    public abstract Object parsing(Selector selector);
}
