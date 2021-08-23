package org.fordes.subview.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import lombok.extern.slf4j.Slf4j;
import org.fordes.subview.enums.ServerSupplierEnum;
import org.fordes.subview.enums.ServerTypeEnum;
import org.fordes.subview.enums.ToastEnum;
import org.fordes.subview.service.data.ApplicationInfoService;
import org.fordes.subview.utils.SubtitlesUtil;
import org.fordes.subview.utils.TranslationUtil;
import org.fordes.subview.utils.common.ApplicationConfig;
import org.fordes.subview.utils.constants.MessageConstants;

import javax.annotation.Resource;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * 翻译功能 控制器
 *
 * @author fordes on 2020/10/6
 */
@FXMLController
@Slf4j
public class TranslationController extends BasicController implements Initializable {

    @FXML
    private ToggleButton tranOnline, tranOffline;

    @FXML
    private ToggleGroup listGroup;

    @FXML
    private GridPane tranOnlinePanel, tranOfflinePanel, tranPanel;

    @FXML
    private TextArea tran_online_split_left_text, tran_online_split_right_text;

    @FXML
    private ChoiceBox<ServerSupplierEnum> tran_online_choose;

    @FXML
    private ComboBox<String> tran_online_original, tran_online_target;

    @FXML
    private Button tran_online_start;

    @Resource
    private ToastController toastController;

    @Resource
    private ApplicationInfoService applicationInfoService;


    private Map<String, String> languageMap;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tranPanel.visibleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                refresh();
            }
        });
        tran_online_choose.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            languageMap = ApplicationConfig.getInstance().getLanguageMap()
                    .get(ServerTypeEnum.LITERAL_TRANSLATION.getCode()).get(newValue.getCode());

            linkage(false, tran_online_original);
            linkage(true, tran_online_target);

        });

        tranOnline.setOnAction(event -> onTranOnline());
        tranOffline.setOnAction(event -> onTranOffline());
        tran_online_start.setOnAction(event -> onTranOnlineStart());
        this.focus(tranOnlinePanel);
    }

    private void linkage(boolean isTarget, ComboBox<String> comboBox) {

        String original = comboBox.getValue();
        comboBox.getItems().clear();
        comboBox.getItems().addAll(languageMap.keySet());
        if (original != null && languageMap.containsKey(original)) {
            comboBox.getSelectionModel().select(original);
        }
        if (isTarget) {
            comboBox.getItems().remove("自动检测");
        } else if (original == null) {
            comboBox.getSelectionModel().select("自动检测");
        }

    }

    private void refresh() {
        try {
            Font font = new Font(ApplicationConfig.getInstance().getApplicationSettings().getFont_face(),
                    ApplicationConfig.getInstance().getApplicationSettings().getFont_size());
            tran_online_split_left_text.setText(SubtitlesUtil.subtitleDisplay());
            tran_online_choose.getItems().clear();
            tran_online_choose.getItems().addAll(applicationInfoService.findSetServerType(ServerTypeEnum.LITERAL_TRANSLATION));
            tran_online_split_left_text.setFont(font);
            tran_online_split_right_text.setFont(font);
            if (tran_online_choose.getItems().size() == 1) {
                tran_online_choose.getSelectionModel().select(0);
            } else {
                ServerSupplierEnum supplierEnum = ServerSupplierEnum
                        .get(ApplicationConfig.getInstance().getApplicationSettings().getTranslation_preferred());
                tran_online_choose.getSelectionModel().select(supplierEnum);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            toastController.pushMessage(ToastEnum.ERROR, "字幕读取失败");
        }
    }

    /*在线翻译*/
    public void onTranOnline() {
        listGroup.selectToggle(tranOnline);
        if (!this.focus(tranOnlinePanel)) {

        }
    }

    /*离线翻译*/
    public void onTranOffline() {
        listGroup.selectToggle(tranOffline);
        if (!this.focus(tranOfflinePanel)) {

        }
    }

    /*开始翻译*/
    public void onTranOnlineStart() {
        if (StrUtil.isAllNotEmpty(tran_online_target.getValue(), tran_online_original.getValue()) &&
                languageMap.containsKey(tran_online_target.getValue()) &&
                languageMap.containsKey(tran_online_original.getValue())) {
            try {

                List<String> targetList = StrUtil.split(tran_online_split_left_text.getText(), StrUtil.LF);
                String original = languageMap.get(tran_online_original.getValue());
                String target = languageMap.get(tran_online_target.getValue());

                List<String> list = TranslationUtil.translation(targetList, original, target, "20200614000495109",
                        "9PnFDnIdAW9NbxaGjpA5", ServerSupplierEnum.BAIDU, 8000);
                tran_online_split_right_text.setText(CollUtil.join(list, StrUtil.LF));

            } catch (Exception e) {
                log.error(e.getMessage());
            }
        } else toastController.pushMessage(ToastEnum.WARN, MessageConstants.CHOOSE_WRONG);
    }
}