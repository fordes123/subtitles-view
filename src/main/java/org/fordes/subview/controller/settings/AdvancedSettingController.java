package org.fordes.subview.controller.settings;

import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.GridPane;
import lombok.extern.slf4j.Slf4j;
import org.fordes.subview.controller.BasicController;
import org.fordes.subview.controller.ToastController;
import org.fordes.subview.entity.PO.ApplicationSettings;
import org.fordes.subview.enums.ToastEnum;
import org.fordes.subview.service.data.ApplicationInfoService;
import org.fordes.subview.utils.common.ApplicationConfig;

import javax.annotation.Resource;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author fordes on 2021/3/18
 */
@FXMLController
@Slf4j
public class AdvancedSettingController extends BasicController implements Initializable {

    @FXML
    private Button apply, cancel;

    @FXML
    private CheckBox quickExit;

    @FXML
    private GridPane root;

    @Resource
    private ToastController toastController;


    @Resource
    private ApplicationInfoService applicationInfoService;



    @Override
    public void initialize(URL location, ResourceBundle resources) {

        quickExit.setSelected(ApplicationConfig.getInstance().getApplicationSettings().getQuick_exit());
        apply.setOnAction(event -> onAdvancedSettingApply());
        cancel.setOnAction(event -> refresh());
        root.visibleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                refresh();
            }
        });
    }

    private void refresh() {
        quickExit.setSelected(ApplicationConfig.getInstance().getApplicationSettings().getQuick_exit());
    }


    private void onAdvancedSettingApply() {

        ApplicationSettings setting = ApplicationConfig.getInstance().getApplicationSettings();
        setting.setQuick_exit(quickExit.isSelected());
        boolean result = applicationInfoService.save(setting);
        if (result) {
            toastController.pushMessage(ToastEnum.SUCCESS, "保存成功~");
        }else {
            toastController.pushMessage(ToastEnum.FAIL, "保存失败~");
        }
    }

}
