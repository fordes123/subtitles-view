package org.fordes.subview.service;

import com.google.common.collect.Lists;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import org.fordes.subview.entity.DTO.search.SearchItem;
import org.fordes.subview.entity.DTO.search.SearchResult;
import org.fordes.subview.enums.SubtitlesSearchEnum;
import org.fordes.subview.utils.SubSearchUtil;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author fordes on 2021/7/21
 */
@Component
public class SearchSearch extends Service<SearchResult> {

    private SubtitlesSearchEnum engine;

    private String keyword;

    private Object request;

    private boolean isDown = false;

    private String fileName;

    private List<SearchItem> data = Lists.newArrayList();

    @Override
    protected Task<SearchResult> createTask() {
        return new Task<SearchResult>() {
            @Override
            protected SearchResult call() {
                if (!isDown || request == null) {
                    return SubSearchUtil.search(engine, keyword, request);
                }else {
                    return SubSearchUtil.download(engine, request, fileName);
                }
            }
        };
    }


    public void search(SubtitlesSearchEnum engine, String srt) {
        this.engine = engine;
        this.keyword = srt;
        this.request = null;
        data.clear();
        restart();
    }

    public void download(Object request, String fileName) {
        this.request = request;
        isDown = true;
        this.fileName = fileName;
        data.clear();
        restart();
    }

    public void next(Object request) {
        this.request = request;
        isDown = false;
        data.clear();
        restart();
    }

    public void page() {
        this.request = getValue().getNext();
        restart();
    }

    public void archive(List<SearchItem> list) {
        data.addAll(list);
    }

    public List<SearchItem> get() {
        return data;
    }
}
