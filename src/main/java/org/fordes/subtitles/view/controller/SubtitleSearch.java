package org.fordes.subtitles.view.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.lang.Singleton;
import cn.hutool.core.swing.DesktopUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXNodesList;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.skins.JFXListViewSkin;
import com.sun.javafx.scene.control.VirtualScrollBar;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.fordes.subtitles.view.constant.CommonConstant;
import org.fordes.subtitles.view.constant.StyleClassConstant;
import org.fordes.subtitles.view.event.FileOpenEvent;
import org.fordes.subtitles.view.event.LoadingEvent;
import org.fordes.subtitles.view.event.ToastChooseEvent;
import org.fordes.subtitles.view.event.ToastConfirmEvent;
import org.fordes.subtitles.view.mapper.SearchCasesMapper;
import org.fordes.subtitles.view.model.PO.SearchCases;
import org.fordes.subtitles.view.model.search.Cases;
import org.fordes.subtitles.view.model.search.Result;
import org.fordes.subtitles.view.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author fordes on 2022/2/6
 */
@Slf4j
@Component
public class SubtitleSearch extends DelayInitController {

    @FXML
    private JFXListView<StackPane> listView;

    @FXML
    private JFXTextField searchField;

    @FXML
    private JFXNodesList nodesList;

    private ToggleGroup engineGroup;

    private static final SearchService SERVICE = new SearchService();

    private static final Dict SEARCH_KEY = Dict.create();

    static final String KEYWORD = "keyword";

    private final SearchCasesMapper casesMapper;

    @Autowired
    public SubtitleSearch(SearchCasesMapper casesMapper) {
        this.casesMapper = casesMapper;
    }

    @Override
    public void delay() {
        //选择默认接口
        if (engineGroup.getToggles().isEmpty()) {
            Singleton.get(Stage.class).fireEvent(new ToastConfirmEvent("未找到搜索源", "字幕搜索无法使用！"));
            searchField.setDisable(true);
        } else {
            Toggle engine = CollUtil.getFirst(engineGroup.getToggles());
            engine.setSelected(true);
            searchField.setPromptText(StrUtil
                    .format("从{}搜索", ((SearchCases) engine.getUserData()).getName()));
        }
    }

    @Override
    public void async() {
        //读取字幕搜索接口
        engineGroup = new ToggleGroup();
        casesMapper.selectList(new QueryWrapper<>()).forEach(e -> {
            ToggleButton engine = new ToggleButton();
            engine.getStyleClass().addAll(StyleClassConstant.SUBTITLE_SEARCH_ENGINE,
                    StyleClassConstant.SUBTITLE_SEARCH_ENGINE_ITEM);
            engine.setToggleGroup(engineGroup);
            engine.setUserData(e);
            engine.setTooltip(new Tooltip(e.getName()));
            engine.setText(e.getIcon());
            engine.selectedProperty().addListener((observableValue, aBoolean, t1) -> {
                if (t1) {
                    SearchCases cases = (SearchCases) engine.getUserData();
                    searchField.setPromptText(StrUtil.format("从{}搜索", cases.getName()));
                    listView.getItems().clear();
                    SERVICE.cancel();
                    nodesList.animateList(false);
                }
            });
            nodesList.addAnimatedNode(engine);
        });

        //监听搜索服务运行状态，控制loading
        SERVICE.runningProperty().addListener((observableValue, aBoolean, t1)
                -> Singleton.get(Stage.class).fireEvent(new LoadingEvent(t1)));
        //搜索完成，载入新结果
        SERVICE.setOnSucceeded(event -> {
            Result val = SERVICE.getValue();
            if (ObjectUtil.isNotNull(val) && !val.getData().isEmpty()) {
                if (Result.Type.SEARCH.equals(val.getType())) {
                    listView.getItems().clear();
                }
                listView.setUserData(val.getPage());
                val.getData().forEach(result -> listView.getItems().add(buildItem(result)));
            }else {
                Singleton.get(Stage.class).fireEvent(new ToastConfirmEvent("暂无结果", "换一个资源试试吧~", "确定", () -> {}));
            }
        });
        //搜索出错
        SERVICE.setOnFailed(event -> Singleton.get(Stage.class).fireEvent(new ToastChooseEvent("搜索出错",
                "请等待后尝试重试\n或者前往项目主页反馈", "去反馈",
                () -> DesktopUtil.browse(CommonConstant.URL_ISSUES))));

        //为listview添加skin，反射获取垂直滚动条，监听滚动条判断分页
        JFXListViewSkin<StackPane> skin = new JFXListViewSkin<>(listView);
        listView.setSkin(skin);

        VirtualFlow<?> virtualFlow = (VirtualFlow<?>) ReflectUtil.getFieldValue(skin, "flow");
        VirtualScrollBar vbar = (VirtualScrollBar) ReflectUtil.getFieldValue(virtualFlow, "vbar");
        vbar.valueProperty().addListener((observableValue, number, t1) -> {
            if (t1.floatValue() == 1 && listView.getUserData() != null) {
                SERVICE.search(Result.Type.PAGE, (Cases) listView.getUserData(), SEARCH_KEY);
            }
        });
    }

    /**
     * 输入框监听，提交新的搜索
     * @param event source
     */
    @FXML
    private void searchBeginHandle(ActionEvent event) {
        JFXTextField field = (JFXTextField) event.getSource();
        if (StrUtil.isNotBlank(field.getText())) {
            SearchCases cases = (SearchCases) engineGroup.getSelectedToggle().getUserData();
            SEARCH_KEY.clear();
            SEARCH_KEY.set(KEYWORD, field.getText());
            SERVICE.search(Result.Type.SEARCH, cases.getCases(), SEARCH_KEY);
        }
    }


    private StackPane buildItem(Result.Item rsi) {
        StackPane root = new StackPane();
        root.getStyleClass().add(StyleClassConstant.SUBTITLE_SEARCH_ITEM);
        Label caption = new Label(rsi.caption);
        caption.getStyleClass().add(StyleClassConstant.SUBTITLE_SEARCH_ITEM_CAPTION);
        Label text = new Label(rsi.text);
        text.getStyleClass().add(StyleClassConstant.SUBTITLE_SEARCH_ITEM_TEXT);
        root.getChildren().addAll(caption, text);

        StackPane.setAlignment(caption, Pos.TOP_LEFT);
        StackPane.setMargin(caption, new Insets(5, 0, 0, 0));
        StackPane.setAlignment(text, Pos.BOTTOM_LEFT);
        StackPane.setMargin(caption, new Insets(0, 0, 5, 0));
        root.setUserData(rsi);
        root.setOnMouseClicked(e -> {
            if (MouseButton.PRIMARY.equals(e.getButton()) && 2 == e.getClickCount()) {
                StackPane item = (StackPane) e.getSource();
                Result.Item data = (Result.Item)item.getUserData();
                if (ObjectUtil.isNull(data.next)) {
                    if (StrUtil.isNotEmpty(data.text)) {
                        Singleton.get(Stage.class).fireEvent(new FileOpenEvent(data.text));
                    }
                }else {
                    SERVICE.search(Result.Type.SEARCH, data.next, data.params);
                }
            }
        });
        return root;
    }
}
