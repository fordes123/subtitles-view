package org.fordes.subtitles.view.controller;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.lang.Singleton;
import cn.hutool.core.util.StrUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.fordes.subtitles.view.config.ApplicationConfig;
import org.fordes.subtitles.view.constant.CommonConstant;
import org.fordes.subtitles.view.constant.StyleClassConstant;
import org.fordes.subtitles.view.enums.EditToolEventEnum;
import org.fordes.subtitles.view.enums.FontIcon;
import org.fordes.subtitles.view.event.*;
import org.fordes.subtitles.view.model.DTO.Subtitle;
import org.fordes.subtitles.view.utils.SubtitleUtil;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.fxmisc.richtext.model.TwoDimensional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 语音转换 控制器
 *
 * @author fordes on 2022/4/8
 */
@Slf4j
@Component
public class MainEditor extends DelayInitController {

    @FXML
    private GridPane editTool;

    @FXML
    private Label indicator, editModeIcon;

    @FXML
    private ToggleButton editMode;

    @FXML
    private StyleClassedTextArea editor;

    @FXML
    private HBox toolbarPanel;

    @FXML
    private RowConstraints toolbarRow;

    private Subtitle subtitle;

    private final ApplicationConfig config;

    @Autowired
    public MainEditor(ApplicationConfig config) {
        this.config = config;
    }

    @Override
    public void delay() {
        Stage stage = Singleton.get(Stage.class);

        //工具栏按钮，点击按钮发送编辑工具事件 唤起编辑工具
        toolbarPanel.getChildren().forEach(node -> {
            if (node.getUserData() != null) {
                node.setOnMouseClicked(event -> {
                    if (node.getUserData() != null) {
                        EditToolEventEnum type = EditToolEventEnum.valueOf((String) node.getUserData());
                        stage.fireEvent(new EditToolEvent(editor, subtitle, editMode, type));
                    }
                });
            }
        });

        //编辑模式监听
        editMode.selectedProperty().addListener((observableValue, aBoolean, t1) -> {
            ctrlEditMode(t1);
            editor.clear();
            editor.append(SubtitleUtil.toStr(subtitle.getTimedTextFile(), t1), StrUtil.EMPTY);
        });
        //行列号监听
        editor.caretPositionProperty().addListener((observable, oldValue, newValue) -> {
            TwoDimensional.Position position = editor.offsetToPosition(newValue, TwoDimensional.Bias.Backward);
            indicator.setText(StrUtil.format((String) indicator.getUserData(), position.getMajor(), position.getMinor()));
        });

//        stage.addEventHandler(ThemeChangeEvent.EVENT_TYPE, event -> {
//            editor.setStyleClass(0, editor.getLength(),  config.isCurrentTheme()? "richtext_dark":"richtext_light");
//        });

        stage.addEventHandler(TranslateEvent.EVENT_TYPE, event -> {

            if (TranslateEvent.SUCCESS.equals(event.getMsg())) {
                editor.clear();
                editor.append(SubtitleUtil.toStr(subtitle.getTimedTextFile(),
                        editMode.isSelected()), "styled-text-area");
                editor.moveTo(0);
            }
            Platform.runLater(() -> {
                stage.fireEvent(new ToastConfirmEvent(event.getMsg(), event.getDetail()));
                stage.fireEvent(new LoadingEvent(false));
            });

        });

        //快捷键
        KeyCodeCombination ctrlT = new KeyCodeCombination(KeyCode.T, KeyCodeCombination.CONTROL_DOWN);
        stage.getScene().getAccelerators().put(ctrlT, this::ctrlToolbar);

        KeyCodeCombination ctrlF = new KeyCodeCombination(KeyCode.F, KeyCodeCombination.CONTROL_DOWN);
        stage.getScene().getAccelerators().put(ctrlF, ()
                -> stage.fireEvent(new EditToolEvent(editor, subtitle, editMode, EditToolEventEnum.SEARCH)));

        KeyCodeCombination ctrlR = new KeyCodeCombination(KeyCode.R, KeyCodeCombination.CONTROL_DOWN);
        stage.getScene().getAccelerators().put(ctrlR, ()
                -> stage.fireEvent(new EditToolEvent(editor, subtitle, editMode, EditToolEventEnum.REPLACE)));

    }

    @Override
    public void async() {
        Singleton.get(Stage.class).addEventHandler(FileOpenEvent.FILE_OPEN_EVENT, fileOpenEvent -> {
            if (fileOpenEvent.getRecord().getFormat().subtitle) {
                subtitle = (Subtitle) fileOpenEvent.getRecord();
                log.debug("主编辑器 => {}", subtitle.getFile().getPath());
                try {
                    Singleton.get(Stage.class).fireEvent(new LoadingEvent(true));
                    SubtitleUtil.parse(subtitle);
                    root.setVisible(true);
                } catch (Exception e) {
                    log.error(ExceptionUtil.stacktraceToString(e));
                    Singleton.get(Stage.class).fireEvent(new ToastConfirmEvent("读取失败！", "字幕文件已经损坏"));
                } finally {
                    Singleton.get(Stage.class).fireEvent(new LoadingEvent(false));
                }
            }
        });

        //载入设置
        root.visibleProperty().addListener((observableValue, aBoolean, t1) -> {
            if (t1) {
                editor.setStyle(StrUtil.format(StyleClassConstant.FONT_STYLE_TEMPLATE,
                        config.getFontSize(), config.getFontFace()));
                editor.clear();
                editor.append(SubtitleUtil.toStr(subtitle.getTimedTextFile(), editMode.isSelected()), "styled-text-area");
                //编辑器模式
                ctrlEditMode(config.getEditMode());
            }else {
                editTool.setVisible(false);
            }
        });
    }

    @FXML
    private void hideToolbar(ActionEvent actionEvent) {
        ctrlToolbar(false);
        actionEvent.consume();
    }

    /**
     * 控制工具栏显示/隐藏
     *
     * @param state 状态
     */
    private void ctrlToolbar(boolean state) {
        toolbarRow.setMaxHeight(state ? 60 : 0);
        toolbarRow.setMinHeight(state ? 60 : 0);
        toolbarRow.setPrefHeight(state ? 60 : 0);
        toolbarPanel.setVisible(state);
    }

    private void ctrlToolbar() {
        ctrlToolbar(!toolbarPanel.isVisible());
    }

    private void ctrlEditMode(Boolean mode) {
        if (mode == null) {
            mode = config.getEditMode();
        } else {
            config.setEditMode(mode);
        }
        editModeIcon.setText(mode ?
                FontIcon.SWITCH_ON_DARK.toString() :
                FontIcon.SWITCH_OFF_DARK.toString());
        editMode.setText(mode ? CommonConstant.FULL_MODE : CommonConstant.CONCISE_MODE);
        editMode.setSelected(mode);
    }

    @FXML
    private void changeEditMode(ActionEvent actionEvent) {
        actionEvent.consume();
    }

    @FXML
    private void onIndicatorClicked(MouseEvent mouseEvent) {
        Singleton.get(Stage.class).fireEvent(new EditToolEvent(editor, subtitle, editMode, EditToolEventEnum.JUMP));
        mouseEvent.consume();
    }
}
