package org.fordes.subview.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Toggle;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import org.fordes.subview.main.Launcher;
import org.fordes.subview.util.PopUpWindowUtil.ToastUtil;
import org.fordes.subview.util.SubtitlesUtil.editUtil;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class EditToolBarController extends RootController implements Initializable {
    @FXML
    private GridPane EditToolbar;
    @FXML
    private Label Title;
    @FXML
    private Button find,replace,Jump,code,style,addTimeline,newline,more;
    @FXML
    private Label pos_Indicator;

    private TextArea mainEditor;
    //private GridPane home;
    //private RowConstraints ToolBarSpace;
    private StartController startController = (StartController) Launcher.controllers.get(StartController.class.getSimpleName());
    private ToolPanelController toolPanelController = (ToolPanelController) Launcher.controllers.get(ToolPanelController.class.getSimpleName());
    private HomePanelController homePanelController = (HomePanelController) Launcher.controllers.get(HomePanelController.class.getSimpleName());

    private Boolean isHide=true;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /*监听焦点文本区改变*/
        homePanelController.getListGroup().selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if(newValue==homePanelController.getListGroup().getToggles().get(0)){
                    /*选择了基础编辑*/
                    mainEditor=homePanelController.getEditor();
                }else if(newValue==homePanelController.getListGroup().getToggles().get(1)){
                    /*选择了时间对齐*/
                    mainEditor=homePanelController.TimeAdj_Area;
                }else if(newValue==homePanelController.getListGroup().getToggles().get(2)){
                    /*选择了文字预览*/
                    mainEditor=homePanelController.getPreviewArea();
                }
                pos_Indicator.setText("第"+0+"行，"+"第"+0+"列");
                mainEditor.caretPositionProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                        ArrayList<Integer> res=new editUtil().changPos(mainEditor.getText(),newValue.intValue());
                        pos_Indicator.setText("第"+res.get(0)+"行，"+"第"+res.get(1)+"列");
                    }
                });
            }

        });
        setHotKey();

    }


    /**
     * 设置快捷键
     */
    private void setHotKey(){
        homePanelController.getHomePanel().addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
                /*显示/隐藏工具栏Ctrl+T*/
                if (ke.isControlDown()&&ke.getCode() == KeyCode.T) {
                    hideToolBar();
                    ke.consume();
                }
                /*查找Ctrl+F*/
                if (ke.isControlDown()&&ke.getCode() == KeyCode.F) {
                    if(toolPanelController.isCall==null||!toolPanelController.isCall.equals("find"))
                        onFind(null);
                    else
                        onClose();
                    ke.consume();
                }
                /*替换Ctrl+H*/
                if (ke.isControlDown()&&ke.getCode() == KeyCode.H) {
                    if(toolPanelController.isCall==null||!toolPanelController.isCall.equals("replace"))
                        onReplace(null);
                    else
                        onClose();
                    ke.consume();
                }
                /*跳转Ctrl+J*/
                if (ke.isControlDown()&&ke.getCode() == KeyCode.J) {
                    if(toolPanelController.isCall==null||!toolPanelController.isCall.equals("Jump"))
                        onJumpLine(null);
                    else
                        onClose();
                    ke.consume();
                }
            }
        });
    }

    public void hideToolBar(){
        String msg="";
        if(isHide) {
            homePanelController.getToolBarSpace().setMaxHeight(0);
            homePanelController.getToolBarSpace().setMinHeight(0);
            homePanelController.getToolBarSpace().setPrefHeight(0);
            msg="工具栏已收起，按Ctrl+T即可重新呼出";
        }else {
            homePanelController.getToolBarSpace().setMaxHeight(60);
            homePanelController.getToolBarSpace().setMinHeight(60);
            homePanelController.getToolBarSpace().setPrefHeight(60);
            msg="工具栏已显示，按Ctrl+T即可重新隐藏";
        }
        isHide=!isHide;
        EditToolbar.setVisible(isHide);
        new ToastUtil().toast(startController.inputset.getStage(),msg, startController.inputset.getTheme(),1000);
    }

    /*收起工具栏*/
    public void PutAway(ActionEvent actionEvent) { hideToolBar(); }

    /*查找*/
    public void onFind(ActionEvent actionEvent) {
        toolPanelController.Call(mainEditor,find.getId());
    }

    /*替换*/
    public void onReplace(ActionEvent actionEvent) {
        toolPanelController.Call(mainEditor,replace.getId());
    }

    /*新建普通行*/
    public void newline(ActionEvent actionEvent) { }

    /*行跳转*/
    public void onJumpLine(ActionEvent actionEvent) {
        toolPanelController.Call(mainEditor,Jump.getId());
    }

    /*编码转换*/
    public void onChangeCode(ActionEvent actionEvent) {
        toolPanelController.Call(mainEditor,code.getId());
    }

    /*文字样式*/
    public void ChangeStyle(ActionEvent actionEvent) {
        toolPanelController.Call(mainEditor,style.getId());
    }

    /*关闭功能小窗*/
    public void onClose(){
        toolPanelController.close(null);
    }
}
