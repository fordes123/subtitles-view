package org.fordes.subview.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.fordes.subview.main.Launcher;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class titlesBarControl{
    @FXML
    private VBox top;
    @FXML
    private Button winMax,winMin,mode,winClose;
    @FXML
    private Label titles;

    
    private Object LightTheme=getClass().getClassLoader().getResource("css/TitlesBar_Light.css").toString();
    private Object DarkTheme=getClass().getClassLoader().getResource("css/TitlesBar_Dark.css").toString();
    private int type;

/*    private startControl controller;

    
    void injectMainController(startControl controller) {
        this.controller = controller;
    }*/


    public VBox getTop(){ return top; }

    public Button getWinClose() { return winClose; }

    public Button getMode() { return mode; }

    public Button getWinMax() { return winMax; }

    public Button getWinMin() { return winMin; }


    /**
     * 切换颜色模式
     * @param ModeState
     */
    public void modeChange(Boolean ModeState){
        /*移除所有样式表*/
        top.getStylesheets().remove(LightTheme);
        top.getStylesheets().remove(DarkTheme);
        if(ModeState){
            top.getStylesheets().add(LightTheme.toString());
            if(type==1){
                top.setStyle("-fx-background-color: #FFF;");
                titles.setStyle("    -fx-font-size: 16;\n" +
                        "    -fx-font-weight: 400;\n" +
                        "    -fx-text-fill: #000;");
            }
            else{
                top.setStyle("-fx-background-color: Transparent;");
                titles.setStyle("-fx-text-fill: Transparent;");
            }
        }
        else {
            top.getStylesheets().add(DarkTheme.toString());
            if(type==1){
                top.setStyle("-fx-background-color: #262626;");
                titles.setStyle("-fx-font-size: 16;\n" +
                        "    -fx-font-weight: 400;\n" +
                        "    -fx-text-fill: #FFF;");
            }
            else{
                top.setStyle("-fx-background-color: Transparent;");
                titles.setStyle("-fx-text-fill: Transparent;");
            }
        }
    }



    /**
     * 初始化，设置默认主题
     * @param type
     */
    public void initialization(int type){
        this.type=type;
        modeChange(true);
    }
}
