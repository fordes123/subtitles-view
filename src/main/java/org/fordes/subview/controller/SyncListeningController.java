package org.fordes.subview.controller;

import de.felixroske.jfxsupport.FXMLController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.DragEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * 同步返听 控制器
 *
 * @author fordes on 2020/10/6
 */
@FXMLController
public class SyncListeningController extends BasicController implements Initializable {

    @FXML
    private GridPane manualSyncPanel, autoSyncPanel, waitingLoadingPanel;

    @FXML
    private ToggleGroup listGroup;

    @FXML
    private ToggleButton manualSync, automaticSync;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //初始化信息


        //默认功能项
        this.focus(autoSyncPanel);
        //注册事件
        manualSync.setOnAction(event -> onManualSync());
        automaticSync.setOnAction(event -> onAutomaticSync());
    }

    /*手动同步*/
    private void onManualSync() {
        listGroup.selectToggle(manualSync);
        if (!this.focus(manualSyncPanel)) {

        }

    }

    /*自动同步*/
    private void onAutomaticSync() {
        listGroup.selectToggle(automaticSync);
        if (!this.focus(autoSyncPanel)) {

        }
    }

    /*结束编辑*/
    public void ManualListEditCommit(ListView.EditEvent editEvent) {
    }

    /*开始编辑*/
    public void ManualListEditStart(ListView.EditEvent editEvent) {
    }

    /*选择视频文件*/
    public void chooseVideoFile(ActionEvent actionEvent) {
    }

    /*拖拽结束*/
    public void onDragOver(DragEvent dragEvent) {
    }

    /*拖拽事件*/
    public void onDragDropped(DragEvent dragEvent) {
    }

    /*视频进度条*/
    public void mediaScroll(ScrollEvent scrollEvent) {
    }
}