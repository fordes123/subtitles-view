package org.fordes.subview.util;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.fordes.subview.controller.LoadPanelControl;

import java.io.File;
import java.io.IOException;

public class LoadUtil {
    public Stage stage=new Stage();
    private LoadPanelControl loadPanelControl;

    public void load(Stage top, Boolean mode,Task work,Task last,String titles) throws IOException {
        init(mode);
        
        stage.initOwner(top);
        stage.setX(top.getX()+top.getWidth()/2-300);
        stage.setY(top.getY()+top.getHeight()/2-63);
        stage.show();
        
        loadPanelControl.initialization(top,mode,work,last,titles);
    }

    /**
     * @param mode ,颜色模式
     * @param top ,归属窗口
     */
    public void load(Stage top, Boolean mode, File audio) throws IOException {
        init(mode);
        
        stage.initOwner(top);
        stage.setX(top.getX()+top.getWidth()/2-300);
        stage.setY(top.getY()+top.getHeight()/2-63);
        stage.show();
        
        loadPanelControl.initialization(top,mode,audio);
    }

    /**
     * 设置加载窗体样式
     * @param mode 主题模式
     */
    private void init(Boolean mode) throws IOException {

        FXMLLoader fxmlLoader=new FXMLLoader(getClass().getClassLoader().getResource("from/LoadPanel.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setAlwaysOnTop(true);
        stage.setScene(scene);
        if(!mode)
            stage.getScene().getStylesheets().add(getClass().getClassLoader().getResource("css/mainStyle_Light.css").toString());
        else
            stage.getScene().getStylesheets().add(getClass().getClassLoader().getResource("css/mainStyle_Dark.css").toString());
        loadPanelControl=fxmlLoader.getController();   

    }
}
