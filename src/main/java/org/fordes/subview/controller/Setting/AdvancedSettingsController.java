package org.fordes.subview.controller.Setting;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import org.fordes.subview.controller.StartController;
import org.fordes.subview.main.Launcher;
import org.fordes.subview.util.ConfigUtil.ConfigRWUtil;
import org.fordes.subview.util.PopUpWindowUtil.ToastUtil;

import java.net.URL;
import java.util.ResourceBundle;

public class AdvancedSettingsController implements Initializable {
    @FXML
    private CheckBox VoiceService,TranService,Reset;;
    @FXML
    private Button Apply,Cancel;
    @FXML
    private RowConstraints VoiceServiceRow,TranServiceRow;
    @FXML
    private GridPane VoiceServicePanel,TranServicePanel;
    @FXML
    private ChoiceBox Tran_Service_provider,Tran_User_type,Voice_Service_provider,Voice_User_type;
    @FXML
    private TextField Tran_Service_id,Tran_Service_key,Voice_Service_id,Voice_Service_key;
    private StartController startController = (StartController) Launcher.controllers.get(StartController.class.getSimpleName());
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Tran_Service_provider.getItems().addAll("百度翻译");
        Voice_Service_provider.getItems().addAll("科大讯飞");
        Tran_User_type.getItems().addAll("免费用户","付费用户");
        Voice_User_type.getItems().addAll("免费用户","付费用户");
        Tran_Service_provider.getSelectionModel().selectFirst();
        Voice_Service_provider.getSelectionModel().selectFirst();
        Tran_User_type.getSelectionModel().selectFirst();
        Voice_User_type.getSelectionModel().selectFirst();
        //VoiceService.setSelected(startController.inputset.getVoiceConfigState());
        VoiceService.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(newValue)
                    TranServiceRow.setPrefHeight(200);
                else TranServiceRow.setPrefHeight(80);
                TranServicePanel.setVisible(newValue);

            }
        });
        TranService.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(newValue)
                    VoiceServiceRow.setPrefHeight(200);
                else VoiceServiceRow.setPrefHeight(80);
                VoiceServicePanel.setVisible(newValue);
            }
        });
        Reset.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                new ToastUtil().toast("此项设置暂时无效",1000);
                Reset.setSelected(false);
            }
        });

    }

    public void Cancel(ActionEvent actionEvent) {
    }

    public void Apply(ActionEvent actionEvent) {
        if(Reset.isSelected()){
            new ToastUtil().toast("即将重置软件，需要重新打开软件",3000);
        }else{
            if(VoiceService.isSelected()&&startController.inputset.getVoiceService().getState()){
                //重设语音接口
                String id = Voice_Service_id.getText().trim();
                String key = Voice_Service_key.getText().trim();
                if(id==null||key==null||key.equals("")||id.equals("")||id.length()!=8||key.length()!=32){
                    new ToastUtil().toast("语音接口无效或错误，设置应用失败",3000);
                    return;
                }
                new ConfigRWUtil().setLfasr(id,key);
            }
            if(TranService.isSelected()){
                //重设翻译接口
                String id = Tran_Service_id.getText().trim();
                String key = Tran_Service_key.getText().trim();
                if(id==null||key==null||key.equals("")||id.equals("")||id.length()!=17||key.length()!=20){
                    System.out.println(id.length()+","+key);
                    new ToastUtil().toast("翻译接口无效或错误，将继续使用默认免费接口",3000);
                }else{
                    new ConfigRWUtil().setBaiduTranInfo(id,key);
                }

            }
        }
        new ToastUtil().toast("已应用，部分设置可能需要重启生效~",3000);
    }
}
