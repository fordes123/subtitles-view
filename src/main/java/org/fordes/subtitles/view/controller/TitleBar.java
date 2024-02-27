package org.fordes.subtitles.view.controller;

import cn.hutool.core.lang.Singleton;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.fordes.subtitles.view.enums.FontIcon;
import org.springframework.stereotype.Component;

/**
 * @author fordes on 2022/1/19
 */
@Slf4j
@Component
public class TitleBar {

    @FXML
    private Button closed, maximize, minimize;

    @FXML
    private VBox root;

    @FXML
    private Label title;

    @FXML
    private void closed(ActionEvent actionEvent) {
        //TODO
        Singleton.get(Stage.class).close();
        Platform.exit();
        System.exit(0);
    }

    @FXML
    private void maximize(ActionEvent actionEvent) {
        Stage stage = Singleton.get(Stage.class);
        stage.setFullScreen(!stage.isFullScreen());
        maximize.setText(stage.isFullScreen() ?
                FontIcon.EXIT_FULL_SCREEN.toString() : FontIcon.FULL_SCREEN.toString());
        actionEvent.consume();

    }

    @FXML
    private void minimize(ActionEvent actionEvent) {
        Singleton.get(Stage.class).setIconified(true);
        actionEvent.consume();
    }
}
