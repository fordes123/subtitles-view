package org.fordes.subview.controller;

import de.felixroske.jfxsupport.FXMLController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * 构建导出 控制器
 *
 * @author fordes on 2020/10/6
 */
@FXMLController
public class BuildExportController extends BasicController implements Initializable {

    @FXML
    private GridPane buildPanel, previewPanel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    /*合成导出*/
    public void onBuildManual(ActionEvent actionEvent) {
        this.focus(buildPanel);
    }

    /*效果预览*/
    public void onBuildPreview(ActionEvent actionEvent) {
        this.focus(previewPanel);
    }

    /*替换原文件*/
    public void onBuildReplaceSelected(MouseEvent mouseEvent) {
    }

    /*开始导出*/
    public void onStartBuild(ActionEvent actionEvent) {
    }

    /*选择输出路径*/
    public void onBuildChoosePath(ActionEvent actionEvent) {
    }
}