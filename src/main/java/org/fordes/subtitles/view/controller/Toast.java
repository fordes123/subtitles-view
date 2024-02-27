package org.fordes.subtitles.view.controller;

import cn.hutool.core.lang.Singleton;
import cn.hutool.core.util.StrUtil;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.fordes.subtitles.view.event.AbstractToastEvent;
import org.fordes.subtitles.view.handler.ToastEventHandler;
import org.fordes.subtitles.view.handler.ToastHandler;
import org.springframework.stereotype.Component;

/**
 * @author fordes on 2022/1/28
 */
@Component
public class Toast extends DelayInitController {

    @FXML
    private JFXButton _perform, _choose1, _choose2;

    @FXML
    private Label _caption, _text;

    @FXML
    private GridPane root;

    @Override
    public void async() {
        //选择型toast和确认型toast互斥
        _perform.visibleProperty().addListener((observableValue, aBoolean, t1) -> {
            _choose1.setVisible(!t1);
            _choose2.setVisible(!t1);
        });
        //为stage添加toast事件处理
        Singleton.get(Stage.class).addEventHandler(AbstractToastEvent.TOAST_EVENT_TYPE, new ToastEventHandler() {
            @Override
            public void onConfirmEvent(String caption, String text, String perform, ToastHandler handler) {
                _caption.setText(caption);
                _text.setText(text);
                if (StrUtil.isNotEmpty(perform)) {
                    _perform.setText(perform);
                }
                _perform.setOnAction(actionEvent -> {
                    handler.handle();
                    root.setVisible(false);
                });
                _perform.setVisible(true);
                root.setVisible(true);
            }

            @Override
            public void onChooseEvent(String caption, String text, String choose1, String choose2,
                                      ToastHandler handler1, ToastHandler handler2) {
                _caption.setText(caption);
                _text.setText(text);
                if (StrUtil.isNotEmpty(choose1)) {
                    _choose1.setText(choose1);
                }
                if (StrUtil.isNotEmpty(choose2)) {
                    _choose2.setText(choose2);
                }
                _choose1.setOnAction(event -> {
                    handler1.handle();
                    root.setVisible(false);
                });
                _choose2.setOnAction(event -> {
                    handler2.handle();
                    root.setVisible(false);
                });
                _perform.setVisible(false);
                root.setVisible(true);
            }
        });
    }


}
