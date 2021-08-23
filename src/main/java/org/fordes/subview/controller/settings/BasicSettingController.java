package org.fordes.subview.controller.settings;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.GridPane;
import org.fordes.subview.controller.BasicController;
import org.fordes.subview.controller.ToastController;
import org.fordes.subview.entity.PO.ApplicationSettings;
import org.fordes.subview.enums.ServerSupplierEnum;
import org.fordes.subview.enums.ServerTypeEnum;
import org.fordes.subview.enums.ToastEnum;
import org.fordes.subview.service.data.ApplicationInfoService;
import org.fordes.subview.utils.common.ApplicationConfig;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.awt.*;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * @author fordes on 2021/3/18
 */
@Component
@FXMLController
public class BasicSettingController extends BasicController implements Initializable {

    @FXML
    private Button apply, cancel;

    @FXML
    private GridPane timerPane, root;

    @FXML
    private RadioButton editMode_concise, editMode_intact;

    @FXML
    private CheckBox autoSaveChange, autoOpenLast, autoSwitchTheme;

    @FXML
    private ChoiceBox<String> fontFace, timerStart, timerEnd;

    @FXML
    private ChoiceBox<ServerSupplierEnum> translationService, voiceService;

    @FXML
    private ChoiceBox<Integer> fontSize;

    @Resource
    private ToastController toastController;

    @Resource
    private ApplicationInfoService applicationInfoService;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fontFace.getItems().addAll(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        fontSize.getItems().addAll(Arrays.stream(NumberUtil.range(10, 48)).boxed().toArray(Integer[]::new));
        timerStart.getItems().addAll(Arrays.stream(NumberUtil.range(0,23)).boxed().map(e -> e+ ": 00").collect(Collectors.toList()));
        timerEnd.getItems().addAll(timerStart.getItems());
        editMode_concise.selectedProperty().addListener((observable, oldValue, newValue) -> editMode_intact.setSelected(!newValue));
        editMode_intact.selectedProperty().addListener((observable, oldValue, newValue) -> editMode_concise.setSelected(!newValue));
        autoSwitchTheme.selectedProperty().addListener((observable, oldValue, newValue) -> timerPane.setVisible(newValue));
        root.focusedProperty().addListener((observable, oldValue, newValue) -> refresh(newValue));
        //注册按钮事件
        autoSwitchTheme.setOnAction(event -> timerPane.setVisible(true));
        apply.setOnAction(event -> onBaseSettingApply());
        cancel.setOnAction(event -> refresh());
        refresh();
    }

    private void refresh(boolean state) {
        if (state) {
            refresh();
        }
    }

    private void refresh() {
        linkage(translationService, ServerTypeEnum.LITERAL_TRANSLATION);
        linkage(voiceService, ServerTypeEnum.PHONETIC_TRANSCRIPTION);

        fontFace.getSelectionModel().select(ApplicationConfig.getInstance().getApplicationSettings().getFont_face());
        fontSize.getSelectionModel().select(ApplicationConfig.getInstance().getApplicationSettings().getFont_size());
        editMode_intact.setSelected(ApplicationConfig.getInstance().getApplicationSettings().isEdit_mode());
        editMode_concise.setSelected(!ApplicationConfig.getInstance().getApplicationSettings().isEdit_mode());
        autoOpenLast.setSelected(ApplicationConfig.getInstance().getApplicationSettings().isAuto_open());
        autoSwitchTheme.setSelected(ApplicationConfig.getInstance().getApplicationSettings().isAuto_theme());
        autoSaveChange.setSelected(ApplicationConfig.getInstance().getApplicationSettings().isAuto_save());
        if (ObjectUtil.isNotNull(ApplicationConfig.getInstance().getApplicationSettings().getTranslation_preferred())) {
            translationService.setValue(ServerSupplierEnum.get(ApplicationConfig.getInstance().getApplicationSettings().getTranslation_preferred()));
        }
        if (ObjectUtil.isNotNull(ApplicationConfig.getInstance().getApplicationSettings().getVoice_preferred())) {
            voiceService.setValue(ServerSupplierEnum.get(ApplicationConfig.getInstance().getApplicationSettings().getVoice_preferred()));
        }
    }

    private void linkage(ChoiceBox<ServerSupplierEnum> choice, ServerTypeEnum type) {
        ServerSupplierEnum original = choice.getValue();
        choice.getItems().clear();
        choice.getItems().addAll(applicationInfoService.findSetServerType(type));
        if (original != null && choice.getItems().contains(original)) {
            choice.getSelectionModel().select(original);
        }
    }

    public void onBaseSettingApply() {
        ApplicationSettings setting = ApplicationConfig.getInstance().getApplicationSettings();
        setting.setFont_face(fontFace.getValue());
        setting.setFont_size(fontSize.getValue());
        setting.setAuto_open(autoOpenLast.isSelected());
        setting.setAuto_save(autoSaveChange.isSelected());
        setting.setEdit_mode(editMode_intact.isSelected());
        setting.setAuto_theme(autoSwitchTheme.isSelected());
        if (setting.isAuto_theme() && StrUtil.isAllNotBlank(timerStart.getValue(), timerEnd.getValue())) {
            setting.setAuto_theme_start(Integer.valueOf(ReUtil.replaceAll(timerStart.getValue(), "[:,\\s,0]+", StrUtil.EMPTY)))
                    .setAuto_theme_end(Integer.valueOf(ReUtil.replaceAll(timerEnd.getValue(), "[:,\\s,0]+", StrUtil.EMPTY)));
        }
        if (translationService.getValue()!=null) {
            setting.setTranslation_preferred(translationService.getValue().getCode());
        }
        if (voiceService.getValue() != null) {
            setting.setVoice_preferred(voiceService.getValue().getCode());
        }
        if (applicationInfoService.save(setting)) {
            toastController.pushMessage(ToastEnum.SUCCESS, "已保存~");
        }else {
            toastController.pushMessage(ToastEnum.FAIL, "保存失败~");
        }
    }
}
