package org.fordes.subview.controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import org.fordes.subview.main.Launcher;
import org.fordes.subview.util.PopUpWindowUtil.ToastUtil;
import org.fordes.subview.util.TransUtil.Translation;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

public class TranPanelController extends RootController implements Initializable {
    @FXML
    private GridPane Tran;
    @FXML
    private GridPane Tran_Online_Panel,Tran_Offline_Panel;
    @FXML
    private ToggleButton Tran_Online,Tran_Offline;
    @FXML
    private ToggleGroup ListGroup;
    @FXML
    private ChoiceBox TranOnline_Original,TranOnline_Target;
    @FXML
    private Button TranOnline_Start;
    @FXML
    private Label TarnOnline_image;
    @FXML
    private SplitPane TranOnline_Split;
    @FXML
    private AnchorPane TranOnline_Split_Left,TranOnline_Split_Right;
    @FXML
    private TextArea TranOnline_Split_Left_Text,TranOnline_Split_Right_Text;
    private Object LightTheme=getClass().getClassLoader().getResource("css/mainStyle_Light.css").toString();
    private Object DarkTheme=getClass().getClassLoader().getResource("css/mainStyle_Dark.css").toString();
    private StartController startController = (StartController) Launcher.controllers.get(StartController.class.getSimpleName());
    private boolean Tran_Online_State=false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Tran.getStylesheets().add(LightTheme.toString());
        TranOnline_Split_Left_Text.prefWidthProperty().bind(TranOnline_Split_Left.widthProperty());
        TranOnline_Split_Left_Text.prefHeightProperty().bind(TranOnline_Split_Left.heightProperty());
        TranOnline_Split_Right_Text.prefWidthProperty().bind(TranOnline_Split_Right.widthProperty());
        TranOnline_Split_Right_Text.prefHeightProperty().bind(TranOnline_Split_Right.heightProperty());
    }

    /**
     * 切换颜色模式
     * @param ModeState
     */
    public void ThemeApply(Boolean ModeState){
        /*移除所有样式表*/
        Tran.getStylesheets().remove(LightTheme);
        Tran.getStylesheets().remove(DarkTheme);
        if(!ModeState)
            Tran.getStylesheets().add(LightTheme.toString());
        else
            Tran.getStylesheets().add(DarkTheme.toString());
    }

    
    private void Hide(){
        Tran_Offline_Panel.setVisible(false);
        Tran_Online_Panel.setVisible(false);
    }

    
    public void onTran_Online(ActionEvent actionEvent) {
        ListGroup.selectToggle(Tran_Online);
        Tran_Online.setSelected(true);
        
        if(startController.focus_indicator.equals(Tran_Online.getId()))
            return;
        startController.focus_indicator=Tran_Online.getId();
        Hide();
        if(!Tran_Online_State){
            ArrayList<String> LanguageList=new Translation().getLanguageList();
            TranOnline_Target.setItems(FXCollections.observableArrayList(LanguageList));
            TranOnline_Original.setItems(FXCollections.observableArrayList(LanguageList));
            TranOnline_Target.getItems().remove(0);
            TranOnline_Original.getSelectionModel().selectFirst();
            TranOnline_Target.getSelectionModel().selectFirst();
        }
        TranOnline_Split_Left_Text.setText(startController.inputset.getText());
        TranOnline_Split_Right_Text.setText("");
        Tran_Online_Panel.setVisible(true);
    }

    
    public void onTran_Offline(ActionEvent actionEvent) {
        ListGroup.selectToggle(Tran_Offline);
        Tran_Offline.setSelected(true);
        
        if(startController.focus_indicator.equals(Tran_Offline.getId()))
            return;
        startController.focus_indicator=Tran_Offline.getId();
        Hide();
        Tran_Offline_Panel.setVisible(true);
    }

    public void onTranOnline_Start(ActionEvent actionEvent) throws ExecutionException, InterruptedException {
        String Original=(String)TranOnline_Original.getValue();
        String Target=(String)TranOnline_Target.getValue();
        /* 输入语言与输出语言相同，直接返回*/
        if(Original.equals(Target)) {
            new ToastUtil().toast("错误！无法从" + Original + "翻译至" + Target, 1000);
            return;
        }
        String TranRes=new Translation().TransText_Baidu(startController.inputset.getText(),(String)TranOnline_Original.getValue(),(String)TranOnline_Target.getValue(), startController.inputset.getSubtitles().getType());
        if(!(TranRes==null)){
            TranOnline_Split_Right_Text.setText(TranRes);
            startController.inputset.setText(TranOnline_Split_Right_Text.getText());
            new ToastUtil().toast("翻译完成，已自动保存结果~",1000);
        }else{
            new ToastUtil().toast("未知错误！请检查网络或接口配置~",1000);
        }
    }
}
