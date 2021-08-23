package org.fordes.subview.controller.settings;

import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import lombok.extern.slf4j.Slf4j;
import org.fordes.subview.controller.BasicController;
import org.fordes.subview.controller.ToastController;
import org.fordes.subview.enums.ToastEnum;

import javax.annotation.Resource;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author fordes on 2021/3/18
 */
@FXMLController
@Slf4j
public class ExperimentSettingController extends BasicController implements Initializable {

    @FXML
    private CheckBox subtitles_search, custom_background, format_fix, original_content;

    @Resource
    private ToastController toastController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        subtitles_search.selectedProperty().addListener((observable, oldValue, newValue) -> {
            subtitles_search.setSelected(true);
            toastController.pushMessage(ToastEnum.INFO, "不可以哦~");
        });

        custom_background.selectedProperty().addListener((observable, oldValue, newValue) -> {
            custom_background.setSelected(false);
            toastController.pushMessage(ToastEnum.INFO, "不可以哦~");
        });

    }






}
