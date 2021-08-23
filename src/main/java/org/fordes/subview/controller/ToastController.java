package org.fordes.subview.controller;

import de.felixroske.jfxsupport.FXMLController;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import lombok.extern.slf4j.Slf4j;
import org.fordes.subview.enums.ToastEnum;
import org.fordes.subview.utils.common.ApplicationCommon;
import org.fordes.subview.utils.constants.StyleConstants;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author fordes on 2021/1/23
 */
@Component
@FXMLController
@Slf4j
public class ToastController implements Initializable {

    @FXML
    private HBox root;

    @FXML
    private Label message;

    @FXML
    private Pane image;


    //默认消息显示时间
    private static int time = 3000;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        push.setOnSucceeded(e -> root.setVisible(false));
        push.setOnCancelled(e -> root.setVisible(false));
    }


    public void pushMessage(ToastEnum type, String msg) {
        if (push.isRunning()) {
            push.cancel();
        }
        image.getStyleClass().removeAll(image.getStyleClass());
        root.getStyleClass().removeAll(root.getStyleClass());
        switch (type) {
            case FAIL:
                image.getStyleClass().add(StyleConstants.FAIL);
                break;
            case INFO:
                image.getStyleClass().add(StyleConstants.INFO);
                break;
            case WARN:
                image.getStyleClass().add(StyleConstants.WARN);
                break;
            case ERROR:
                image.getStyleClass().add(StyleConstants.ERROR);
                break;
            case SUCCESS:
                image.getStyleClass().add(StyleConstants.SUCCESS);
                break;
        }
        root.getStyleClass().add(ApplicationCommon.styleState ?
                StyleConstants.DARK : StyleConstants.LIGHT);
        message.setText(msg);
        message.autosize();
        root.setMinWidth(message.getWidth() + 50);
        root.setMaxWidth(message.getWidth() + 50);
        time = type.getTime();
        push.restart();
    }

    private final Service<Integer> push = new Service<Integer>() {
        @Override
        protected Task<Integer> createTask() {
            return new Task<Integer>() {
                @Override
                protected Integer call() throws Exception {
                    root.setVisible(true);
                    Thread.sleep(time);
                    return 0;
                }

                ;
            };
        }

    };

}
