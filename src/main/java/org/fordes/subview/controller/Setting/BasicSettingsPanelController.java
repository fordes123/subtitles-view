package org.fordes.subview.controller.Setting;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import org.fordes.subview.controller.ApplicationController;
import org.fordes.subview.controller.RootController;
import org.fordes.subview.controller.StartController;
import org.fordes.subview.main.Launcher;

import java.net.URL;
import java.util.ResourceBundle;

public class BasicSettingsPanelController extends RootController implements Initializable {

    @FXML
    private CheckBox TimingTheme,RecForm,RecFile,Recording;
    @FXML
    private Button Apply,Editor_Rec,Renounce;
    @FXML
    private ChoiceBox Theme_Mode,Timing_Start,Timing_End,Editor_Font,Editor_Size;
    @FXML
    private GridPane TimingTheme_Panel;
    private StartController startController = (StartController) Launcher.controllers.get(StartController.class.getSimpleName());
    private ApplicationController applicationController = (ApplicationController) Launcher.controllers.get(ApplicationController.class.getSimpleName());

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Theme_Mode.getItems().addAll("浅色模式","深色模式");
        for(int i=0;i<24;i++){
            String temp=i<10?("0"+Integer.toString(i)+":00"):(Integer.toString(i)+":00");
            Timing_Start.getItems().add(temp);
            Timing_End.getItems().add(temp);
        }

        Editor_Font.getItems().addAll(new Font(0).getFamilies());
        Editor_Size.getItems().addAll("8","9","10","11","12","13","14","15","16","17","18","19","20","22","24","26","28","36","48","72");
        SyncSettings();
        Editor_Font.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if(!newValue.toString().equals("System"))
                    Editor_Rec.setVisible(true);
                else if(Editor_Size.getSelectionModel().getSelectedItem().toString().equals("18"))
                    Editor_Rec.setVisible(false);
            }
        });
        Editor_Size.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if(!newValue.toString().equals("18"))
                    Editor_Rec.setVisible(true);
                else if(Editor_Font.getSelectionModel().getSelectedItem().toString().equals("System"))
                    Editor_Rec.setVisible(false);
            }
        });
    }

    public void onTimingTheme(ActionEvent actionEvent) {
        if(TimingTheme.isSelected()) {
            TimingTheme_Panel.setVisible(true);
            //Theme_Mode.setDisable(true);
        }
        else {
            TimingTheme_Panel.setVisible(false);
           // Theme_Mode.setDisable(false);
        }
    }

    /*恢复默认样式*/
    public void onEditor_Rec(ActionEvent actionEvent) {
        Editor_Font.getSelectionModel().select("System");
        Editor_Size.getSelectionModel().select("18");
    }

    public ChoiceBox getEditor_Font() {
        return Editor_Font;
    }

    public ChoiceBox getEditor_Size() {
        return Editor_Size;
    }

    /**
     * 从配置文件还原设置
     */
    public final void SyncSettings(){
        Editor_Font.getSelectionModel().select(startController.getSettings().getBaseSettings().get("Editor_Font"));
        Editor_Size.getSelectionModel().select(startController.getSettings().getBaseSettings().get("Editor_Size"));
        if(!startController.inputset.getTheme())
            Theme_Mode.getSelectionModel().selectFirst();
        else Theme_Mode.getSelectionModel().selectLast();
        if(startController.getSettings().getBaseSettings().get("TimingState").equals("1")){
            TimingTheme.setSelected(true);
            TimingTheme_Panel.setVisible(true);
            int high=Integer.valueOf(startController.getSettings().getBaseSettings().get("ThemeModeHigh").toString());
            int low=Integer.valueOf(startController.getSettings().getBaseSettings().get("ThemeModeLow").toString());
            Timing_Start.getSelectionModel().select(high);
            Timing_End.getSelectionModel().select(low);
        }else {
            TimingTheme.setSelected(false);
            TimingTheme_Panel.setVisible(false);
            Timing_Start.getSelectionModel().select(19);
            Timing_End.getSelectionModel().select(8);
        }
        if(startController.getSettings().getBaseSettings().get("Recording").equals("1"))
            Recording.setSelected(true);
        else
            Recording.setSelected(false);
    }

    /*应用设置*/
    public void Apply(ActionEvent actionEvent) {
        startController.getSettings().getBaseSettings().put("Editor_Font",Editor_Font.getSelectionModel().getSelectedItem().toString());
        startController.getSettings().getBaseSettings().put("Editor_Size",Editor_Size.getSelectionModel().getSelectedItem().toString());
        startController.getSettings().getBaseSettings().put("ThemeMode",Theme_Mode.getSelectionModel().getSelectedIndex()==0?"0":"1");
        //System.out.println(controller.getSettings().getBaseSettings().get("ThemeMode"));
        startController.inputset.setTheme(startController.getSettings().getBaseSettings().get("ThemeMode").equals("0")?false:true);
        applicationController.ThemeApply(startController.inputset.getTheme());
    }
}
