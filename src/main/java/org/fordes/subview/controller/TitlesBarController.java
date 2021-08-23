package org.fordes.subview.controller;

import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * 标题栏 控制器
 *
 * @author fordes on 2020/10/8
 */
@Component
@Getter
@FXMLController
public class TitlesBarController implements Initializable {

    @FXML
    private VBox top;

    @FXML
    private Button closed, maximize, minimize, mode_switch;

    @FXML
    public Label window_title;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}