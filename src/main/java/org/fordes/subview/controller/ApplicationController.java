package org.fordes.subview.controller;

import cn.hutool.core.swing.DesktopUtil;
import com.google.common.collect.Maps;
import de.felixroske.jfxsupport.FXMLController;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.fordes.subview.entity.DTO.data.Subtitles;
import org.fordes.subview.enums.ApplicationModeEnum;
import org.fordes.subview.enums.SubtitlesTypeEnum;
import org.fordes.subview.utils.SubtitlesUtil;
import org.fordes.subview.utils.common.ApplicationCommon;
import org.fordes.subview.utils.constants.CommonConstants;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * 主程序控制器
 *
 * @author fordes on 2020/10/2
 */
@Component
@Getter
@Slf4j
@FXMLController
public class ApplicationController extends BasicController implements Initializable{


    @FXML
    private Label logo, titles;

    @FXML
    private GridPane root, homePanel, syncPanel, tranPanel, buildPanel, aboutPanel, settingPanel, side;

    @FXML
    private ToggleGroup function;

    @FXML
    private ToggleButton home, setting, build, about, translation, sync;

    @FXML
    private Circle i_home, i_sync, i_tran, i_build, i_about, i_setting;

    @FXML
    private VBox top;

    public Stage stage;

    @Resource
    private HomeController homePanelController;

    @Resource
    private TitlesBarController titlesBarController;

    private static Map<GridPane, Node> nodeMap;

    @SneakyThrows
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //初始化
        ApplicationCommon.getInstance().initialize(ApplicationModeEnum.MAIN, root, event -> {
            ApplicationCommon.getInstance().showStage();
            loadSetting(null);
        });
        nodeMap = Maps.newHashMap();
        nodeMap.put(homePanel, i_home);
        nodeMap.put(settingPanel, i_setting);
        nodeMap.put(syncPanel, i_sync);
        nodeMap.put(tranPanel, i_tran);
        nodeMap.put(aboutPanel, i_about);
        nodeMap.put(buildPanel, i_build);
        ApplicationCommon.getInstance().setIndicator(nodeMap);

        //默认功能
        loadHome(null);
        //读取字幕内容
        readService.restart();
        readService.setOnSucceeded(e->{
            try {
                ApplicationCommon.getInstance().setSubtitles(readService.getValue());
                homePanelController.refreshEditor(true);
                homePanelController.setComboBox((SubtitlesTypeEnum)ApplicationCommon.getInstance().getSubtitles().getFormat());
            }catch (Exception exception) {
                log.error(exception.getMessage());
                //结束程序
            }
        });
    }

    /**
     * 字幕文件读取服务
     */
    Service<Subtitles> readService = new Service<Subtitles>() {

        @Override
        protected Task<Subtitles> createTask() {
            return new Task<Subtitles>() {
                @Override
                protected Subtitles call() throws Exception {
                    return SubtitlesUtil.readSubtitleFile(ApplicationCommon.getInstance().getSubtitles());
                };
            };
        }
    };



    public Scene getScene(){
        return root.getScene();
    }

    public Stage getStage() {
        if (stage == null) {
            stage = (Stage) getScene().getWindow();
        }
        return stage;
    }

    public void loadHome(ActionEvent actionEvent) {
        this.focus(homePanel);
    }

    public void loadSync(ActionEvent actionEvent) {
        this.focus(syncPanel);
    }

    public void loadTranslation(ActionEvent actionEvent) {
        this.focus(tranPanel);
    }

    public void loadBuild(ActionEvent actionEvent) {
        this.focus(buildPanel);
    }

    public void loadAbout(ActionEvent actionEvent) {
        this.focus(aboutPanel);
    }

    public void loadSetting(ActionEvent actionEvent) {
        this.focus(settingPanel);
    }

    public void onLogo(MouseEvent mouseEvent) {
        try {
            DesktopUtil.browse(CommonConstants.URL_AUTHOR_HOME);
        }catch (Exception e){
            log.error(e.getMessage());
        }
    }

}