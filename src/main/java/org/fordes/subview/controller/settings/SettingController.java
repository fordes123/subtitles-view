package org.fordes.subview.controller.settings;

import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.fordes.subview.controller.BasicController;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * 设置页面 控制器
 *
 * @author fordes on 2020/10/7
 */
@FXMLController
public class SettingController extends BasicController implements Initializable {

    @FXML
    private ToggleGroup listGroup;

    @FXML
    private ToggleButton baseSetting, advancedSetting, experimental;

    @FXML
    private GridPane baseSettingsPane, advancedSettingsPane, experimentalSettingsPane;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //默认功能
        this.init(baseSettingsPane);
        //注册事件
        baseSetting.setOnAction(event -> onBaseSetting());
        advancedSetting.setOnAction(event -> onAdvancedSetting());
        experimental.setOnAction(event -> onExperimentalSetting());


    }

    private void onBaseSetting() {
        listGroup.selectToggle(baseSetting);
        this.focus(baseSettingsPane);
    }

    private void onAdvancedSetting() {
        listGroup.selectToggle(advancedSetting);
        this.focus(advancedSettingsPane);
    }

    private void onExperimentalSetting() {
        listGroup.selectToggle(experimental);
        this.focus(experimentalSettingsPane);
    }



}