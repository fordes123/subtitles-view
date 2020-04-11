package org.fordes.subview.util;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.fordes.subview.controller.startControl;
import org.fordes.subview.main.Launcher;

import java.util.Timer;
import java.util.TimerTask;

public class ToastUtil{

    private Stage stage=new Stage();
    private Label label=new Label();
    private startControl controller= (startControl) Launcher.controllers.get(startControl.class.getSimpleName());

    public void toast(Stage top,String msg,Boolean theme){
        //默认时间为3秒，居中显示,浅色模式
        toast(top,msg,theme,3000,0,0);
    }

    public void toast(Stage top,String msg,Boolean theme,int time){
        //默认时间为3秒，居中显示,浅色模式
        toast(top,msg,theme,time,0,0);
    }

    public void toast(String msg,int time){
        //默认时间为3秒，居中显示,浅色模式
        toast(controller.inputset.getStage(),msg,controller.inputset.getTheme(),time,0,0);
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
        //设置位置
        stage.setX(top.getX()+(top.getWidth()-label.getWidth())/2+x);
        stage.setY(top.getY()+(top.getHeight()-label.getHeight())/2+y);
        //定时器
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
        stage.initStyle(StageStyle.TRANSPARENT);//舞台透明
        Label label=new Label(msg);//默认信息
        if(!theme) //深色
            label.setStyle("-fx-background: #dff9dd;-fx-border-radius: 10;-fx-background-radius: 10;-fx-text-fill:#71C647;");//label透明,圆角
        else
            label.setStyle("-fx-background: #2D2D2D;-fx-border-radius: 10;-fx-background-radius: 10;-fx-text-fill:#71C647;");//label透明,圆角
        label.setPrefHeight(50);
        label.setPadding(new Insets(15));
        label.setAlignment(Pos.CENTER);//居中
        label.setFont(new Font(19));//字体大小
        Scene scene=new Scene(label);
        scene.setFill(null);//场景透明
        stage.setScene(scene);
    }
}
