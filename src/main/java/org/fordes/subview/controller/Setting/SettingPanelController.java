package org.fordes.subview.controller.Setting;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import org.fordes.subview.controller.RootController;
import org.fordes.subview.controller.StartController;
import org.fordes.subview.main.Launcher;

import java.net.URL;
import java.util.ResourceBundle;

public class SettingPanelController extends RootController implements Initializable {
    @FXML
    private GridPane Setting;
    @FXML
    private GridPane BasicSettingsPanel,AdvancedSettingsPanel,ExperimentalSettingPanel;
    @FXML
    private ToggleGroup Setting_list_Bt;
    @FXML
    private ToggleButton Base,Advanced,Lab;
    @FXML
    private org.fordes.subview.controller.Setting.BasicSettingsPanelController BasicSettingsPanelController;

    //全局主题
    private Object LightTheme=getClass().getClassLoader().getResource("css/mainStyle_Light.css").toString();
    private Object DarkTheme=getClass().getClassLoader().getResource("css/mainStyle_Dark.css").toString();
    private StartController startController = (StartController) Launcher.controllers.get(StartController.class.getSimpleName());
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Setting.getStylesheets().add(LightTheme.toString());
    }

    /**
     * 切换颜色模式
     * @param ModeState
     */
    public void ThemeApply(Boolean ModeState){
        /*移除所有样式表*/
        Setting.getStylesheets().remove(LightTheme);
        Setting.getStylesheets().remove(DarkTheme);
        if(!ModeState)
            Setting.getStylesheets().add(LightTheme.toString());
        else
            Setting.getStylesheets().add(DarkTheme.toString());

    }

    /**
     * 隐藏所有组件
     */
    private void hide(){
        BasicSettingsPanel.setVisible(false);
        AdvancedSettingsPanel.setVisible(false);
        ExperimentalSettingPanel.setVisible(false);
    }

    /**
     * 基础设置
     * @param actionEvent
     */
    public void onBaseSetting(ActionEvent actionEvent) {
        Setting_list_Bt.selectToggle(Base);
        Base.setSelected(true);

        if(startController.focus_indicator.equals(Base.getId()))
            return;
        startController.focus_indicator=Base.getId();
        hide();
        BasicSettingsPanel.setVisible(true);
        BasicSettingsPanelController.SyncSettings();
    }

    /**
     * 进阶设置
     * @param actionEvent
     */
    public void onAdvancedSetting(ActionEvent actionEvent) {
        Setting_list_Bt.selectToggle(Advanced);
        Advanced.setSelected(true);

        if(startController.focus_indicator.equals(Advanced.getId()))
            return;
        startController.focus_indicator=Advanced.getId();
        hide();
        AdvancedSettingsPanel.setVisible(true);
    }

    /**
     * 实验性功能
     * @param actionEvent
     */
    public void onLabSetting(ActionEvent actionEvent) {
        Setting_list_Bt.selectToggle(Lab);
        Lab.setSelected(true);

        if(startController.focus_indicator.equals(Lab.getId()))
            return;
        startController.focus_indicator=Lab.getId();
        hide();
        ExperimentalSettingPanel.setVisible(true);
    }
}
