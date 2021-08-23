package org.fordes.subview.controller;

import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.fordes.subview.utils.common.ApplicationCommon;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author fordes on 2021/4/13
 */
@FXMLController
@Slf4j
public class SystemTrayMenuController implements Initializable {


    @FXML
    private Label exit, open;

    @Getter
    @FXML
    private Label setting;

    @FXML
    private GridPane trayMenu;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        exit.setOnMouseClicked(event ->  ApplicationCommon.getInstance().systemExit());
        open.setOnMouseClicked(event -> ApplicationCommon.getInstance().showStage());
    }



    private Stage getStage() {
        return (Stage) trayMenu.getScene().getWindow();
    }

    public void show(int x, int y) {
        getStage().setX(x);
        getStage().setY(y- trayMenu.getHeight());
        getStage().setAlwaysOnTop(true);
        trayMenu.requestFocus();
    }
}
