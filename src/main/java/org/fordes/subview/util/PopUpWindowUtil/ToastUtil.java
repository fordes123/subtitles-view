package org.fordes.subview.util.PopUpWindowUtil;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.fordes.subview.controller.StartController;
import org.fordes.subview.main.Launcher;

import java.util.Timer;
import java.util.TimerTask;

public class ToastUtil{

    private Stage stage=new Stage();
    private Label label=new Label();
    private StartController startController = (StartController) Launcher.controllers.get(StartController.class.getSimpleName());

    public void toast(Stage top,String msg,Boolean theme){

        toast(top,msg,theme,3000,0,0);
    }

    public void toast(Stage top,String msg,Boolean theme,int time){

        toast(top,msg,theme,time,0,0);
    }

    public void toast(String msg,int time){

        toast(startController.inputset.getStage(),msg, startController.inputset.getTheme(),time,0,0);
    }


    /**
     * @param msg ,消息内容
     * @param time ,持续时间
     * @param top ,归属窗口
     * @param x,y ,位置偏移量，中心为0
     */
    public void toast(Stage top,String msg,Boolean theme, int time,int x, int y) {
        init(msg,theme);
        stage.setAlwaysOnTop(true);
        stage.initOwner(top);

        stage.setX(top.getX()+(top.getWidth()-label.getWidth())/2+x);
        stage.setY(top.getY()+(top.getHeight()-label.getHeight())/2+y);

        TimerTask task= new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(()->stage.close());
            }
        };
        Timer timer=new Timer();
        timer.schedule(task,time);
        stage.show();
    }

    private void init(String msg,Boolean theme){
        stage.initStyle(StageStyle.TRANSPARENT);
        Label label=new Label(msg);
        if(!theme)
            label.setStyle("-fx-background: #dff9dd;-fx-border-radius: 10;-fx-background-radius: 10;-fx-text-fill:#71C647;");
        else
            label.setStyle("-fx-background: #2D2D2D;-fx-border-radius: 10;-fx-background-radius: 10;-fx-text-fill:#71C647;");
        label.setPrefHeight(50);
        label.setPadding(new Insets(15));
        label.setAlignment(Pos.CENTER);
        label.setFont(new Font(19));
        Scene scene=new Scene(label);
        scene.setFill(null);
        stage.setScene(scene);
    }
}
