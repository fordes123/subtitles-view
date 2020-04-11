package org.fordes.subview.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import org.fordes.subview.main.Launcher;
import org.fordes.subview.util.ToastUtil;

import java.net.URL;
import java.util.ResourceBundle;

public class EditToolBarControl implements Initializable {
    @FXML
    private GridPane toolbar;
    @FXML
    private Label Title;
    @FXML
    private Button find,replace,Jump,code,style,addTimeline,newline,more;
    @FXML
    public Label pos_Indicator;

    private TextArea editor;
    private GridPane home;
    private RowConstraints ToolBarSpace;
    private startControl controller= (startControl) Launcher.controllers.get(startControl.class.getSimpleName());
    public ToolPanelControl toolPanelControl;//工具面板

    private Boolean isHide=true;//工具栏显示状态

    /**
     * 初始化
     * @param home
     * @param ToolBarSpace
     * @param editor
     */
    public void setToolBarControl(ToolPanelControl toolPanelControl,GridPane home,RowConstraints ToolBarSpace,TextArea editor){
        this.toolPanelControl=toolPanelControl;
        this.home=home;
        this.editor=editor;
        this.ToolBarSpace=ToolBarSpace;
    }


    //设置工具栏显隐
    public void hideToolBar(){
        String msg="";
        if(isHide) {//隐藏工具栏
            ToolBarSpace.setMaxHeight(0);
            ToolBarSpace.setMinHeight(0);
            ToolBarSpace.setPrefHeight(0);
            msg="工具栏已收起，按Ctrl+T即可重新呼出";
        }else {//显示工具栏
            ToolBarSpace.setMaxHeight(60);
            ToolBarSpace.setMinHeight(60);
            ToolBarSpace.setPrefHeight(60);
            msg="工具栏已显示，按Ctrl+T即可重新隐藏";
        }
        isHide=!isHide;
        toolbar.setVisible(isHide);
        new ToastUtil().toast(controller.inputset.getStage(),msg,controller.inputset.getTheme(),1000);
    }


    public void PutAway(ActionEvent actionEvent) { hideToolBar(); }



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //setHotKey();
    }

    //查找按钮
    public void onFind(ActionEvent actionEvent) {
        toolPanelControl.Call(editor,find.getId());
    }

    //替换按钮
    public void onReplace(ActionEvent actionEvent) {
        toolPanelControl.Call(editor,replace.getId());
    }

    public void newline(ActionEvent actionEvent) {
    }

    //跳转指定行
    public void onJumpLine(ActionEvent actionEvent) {
        toolPanelControl.Call(editor,Jump.getId());
    }

    //改变编码
    public void onChangeCode(ActionEvent actionEvent) {
        toolPanelControl.Call(editor,code.getId());
    }

    //改变样式
    public void ChangeStyle(ActionEvent actionEvent) {
        toolPanelControl.Call(editor,style.getId());
    }

    //关闭
    public void onClose(){
        toolPanelControl.close(null);
    }
}
