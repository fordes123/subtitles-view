package org.fordes.subtitles.view.utils.search;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import org.fordes.subtitles.view.model.search.Selector;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * HTML解析器
 *
 * @author fordes on 2022/3/29
 */
public class HTMLParsing extends Parsing {

    private Document doc;

    public HTMLParsing(Object data) {
        super(data);
        this.doc = Jsoup.parse((String) data);
    }

    @Override
    public Object parsing(Selector selector) {
        List<String> fields = getFields(doc, selector);
        return selector.only ? CollUtil.getFirst(fields): fields;
    }

    private static List<String> getFields(Document doc, Selector selector) {
        if (ObjectUtil.isNotEmpty(selector)) {
            return doc.select(selector.css).stream()
                    .map(e -> getField(e, selector.attr, selector.regular, selector.format))
                    .collect(Collectors.toList());
        }else {
            return Collections.emptyList();
        }
    }


    private static String getField(Element element, String attr, String regular, String format) {
        String attrField = StrUtil.isBlank(attr) ?
                element.text() : element.attr(attr);
        String regField = StrUtil.isBlank(regular) ?
                StrUtil.trim(attrField) : CollUtil.join(ReUtil.findAll(regular, attrField, 1), StrUtil.EMPTY);
        return StrUtil.isBlank(format) ? regField : StrUtil.format(format, regField);
    }
}
