package org.fordes.subtitles.view.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import lombok.extern.slf4j.Slf4j;
import org.fordes.subtitles.view.constant.CommonConstant;
import org.fordes.subtitles.view.model.search.Cases;
import org.fordes.subtitles.view.model.search.Result;
import org.fordes.subtitles.view.utils.ArchiveUtil;
import org.fordes.subtitles.view.utils.search.ParsingFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 在线字幕搜索服务
 *
 * @author fordes on 2022/2/15
 */
@Slf4j
public class SearchService extends Service<Result> {

    private Result.Type type;

    private Cases cases;

    private Map<String, Object> params = MapUtil.newHashMap();

    @Override
    protected Task<Result> createTask() {
        return new Task<>() {
            @Override
            protected Result call() {
                Result result = Result.builder()
                        .type(type)
                        .data(CollUtil.newArrayList()).build();
                try {
                    List<Object> paramList = new ArrayList<>(params.size());
                    if (null != cases.keys) {
                        for (String key : cases.keys) {
                            paramList.add(params.get(key));
                        }
                    }
                    String url = StrUtil.format((CharSequence) cases.url, paramList.toArray());
                    HttpResponse response = HttpUtil
                            .createGet(url, true)
                            .execute();
                    if (ObjectUtil.isEmpty(cases.next)) {
                        File outFile = response.completeFileNameFromHeader(FileUtil.mkdir(CommonConstant.DOWNLOAD_PATH));
                        FileUtil.writeFromStream(response.bodyStream(), outFile);
                        log.debug("下载文件成功！{}", outFile.getPath());
                        for (File l : ArchiveUtil.unArchiveToCurrentPath(outFile)) {
                            result.getData().add(Result.Item.builder()
                                    .caption(l.getName())
                                    .text(l.getPath())
                                    .isFile(true)
                                    .build());
                        }
                    } else {

                        ContentType contentType = ContentType.get(StrUtil.trimStart(response.body()));
                        if (contentType != null) {
                            //根据类型，创建解析器
                            ParsingFactory parsing = new ParsingFactory(response.body(), contentType);
                            //遍历解析，获取结果
                            Map<String, List<String>> displayMap = MapUtil.newHashMap();
                            Map<String, Object> otherMap = MapUtil.newHashMap();
                            cases.params.forEach((k, v) -> {
                                switch (k) {
                                    case Cases.CAPTION:
                                    case Cases.TEXT:
                                        displayMap.put(k, Convert.toList(String.class, parsing.getResult(v)));
                                        break;
                                    case Cases.PAGE:
                                        if (ObjectUtil.isNotEmpty(parsing.getResult(v))) {
                                            result.setPage(Cases.builder()
                                                    .keys(cases.keys)
                                                    .next(cases.next)
                                                    .type(cases.type)
                                                    .params(cases.params)
                                                    .url(parsing.getResult(v))
                                                    .build());
                                        }
                                        break;
                                    default:
                                        otherMap.put(k, parsing.getResult(v));
                                }
                            });
                            //拼装结果
                            List<String> captions = displayMap.get("caption");
                            List<String> texts = displayMap.get("text");
                            for (int i = 0; i < captions.size(); i++) {
                                result.getData().add(Result.Item.builder()
                                        .caption(CollUtil.get(captions, i))
                                        .text(CollUtil.get(texts, i))
                                        .params(MapUtil.newHashMap())
                                        .next(cases.next)
                                        .build());
                            }

                            otherMap.forEach((k, v) -> {
                                if (v instanceof Collection) {
                                    List<String> list = Convert.toList(String.class, v);
                                    for (int i = 0; i < result.getData().size(); i++) {
                                        result.getData().get(i).params.put(k, list.get(i));
                                    }
                                } else {
                                    for (Result.Item value : result.getData()) {
                                        value.params.put(k, v);
                                    }
                                }
                            });
                        }
                    }

                } catch (Exception e) {
                    log.error(ExceptionUtil.stacktraceToString(e));
                    throw new RuntimeException();
                }
                return result;
            }
        };
    }


    public void search(Result.Type type, Cases cases, Map<String, Object> params) {
        this.type = type;
        this.cases = cases;
        this.params = params;
        this.restart();
    }
}
