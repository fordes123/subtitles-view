package org.fordes.subtitles.view.utils.search;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.fordes.subtitles.view.model.search.Selector;

import java.util.List;

/**
 * JSON解析器
 *
 * @author fordes on 2022/3/29
 */
public class JSONParsing extends Parsing {

    private JSONObject json;

    public JSONParsing(Object data) {
        super(data);
        this.json = JSONUtil.parseObj(data);
    }

    //jsonKey > regular > foramt
    //TODO 未测试
    @Override
    public Object parsing(Selector selector) {
        List<String> keys = StrUtil.split(selector.jsonKey, StrUtil.C_DOT);
        for (int i = 0; i < keys.size(); i++) {
            if (i == keys.size()-1) {
                return json.get(keys.get(i));
            }else {
                json = json.getJSONObject(keys.get(i));
            }
        }
        return json;
    }
}
