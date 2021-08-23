package org.fordes.subview.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.swing.DesktopUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.fordes.subview.entity.PO.InterfaceInfo;
import org.fordes.subview.entity.PO.InterfaceSupport;
import org.fordes.subview.entity.PO.InterfaceVersion;
import org.fordes.subview.enums.ServerSupplierEnum;
import org.fordes.subview.enums.ServerTypeEnum;
import org.fordes.subview.enums.ToastEnum;
import org.fordes.subview.service.data.InterfaceInfoService;
import org.fordes.subview.service.data.InterfaceSupportService;
import org.fordes.subview.service.data.InterfaceVersionService;

import javax.annotation.Resource;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author fordes on 2021/2/2
 */
@FXMLController
public class InterfaceController implements Initializable {

    @FXML
    private GridPane infoPanel;

    @FXML
    private ChoiceBox<ServerSupplierEnum> service_provider;

    @FXML
    private ChoiceBox<ServerTypeEnum> service_type;

    @FXML
    private ChoiceBox<InterfaceVersion> user_type;

    @FXML
    private Button apply, jump;

    @FXML
    private GridPane interfacePanel;

    @Resource
    private ToastController toastController;

    @Resource
    private StartController startController;

    @Resource
    private InterfaceSupportService supportService;

    @Resource
    private InterfaceVersionService versionService;

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        service_provider.getItems().addAll(ServerSupplierEnum.values());

        service_provider.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                ServerTypeEnum original = service_type.getValue();
                List<InterfaceSupport> options = supportService.findByQuery(newValue.getCode(), null);

                service_type.getItems().clear();
                service_type.getItems().addAll(options.stream().map(item ->
                        ServerTypeEnum.get(item.getServer_type())).collect(Collectors.toList()));
                if (original != null && service_type.getItems().contains(original)) {
                    service_type.getSelectionModel().select(original);
                }
            }
        });

        service_type.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && service_provider.getValue() != null) {
                InterfaceVersion original = user_type.getValue();

                List<InterfaceVersion> options = versionService.findByQuery(service_provider.getValue().getCode(),
                        service_type.getValue().getCode());
                user_type.getItems().clear();
                user_type.getItems().addAll(options);
                if (original != null && options.contains(original)) {
                    user_type.getSelectionModel().select(original);
                }
                dynamicGenerateParams();

            }
        });

        jump.setOnAction(event -> onInterfacePageJump());
        apply.setOnAction(event -> serviceApply());
    }

    /**
     * 动态参数面板生成
     *
     */
    private void dynamicGenerateParams() {
        infoPanel.getChildren().clear();
        InterfaceSupport support = getSelectInterface();
        JSONObject json = interfaceInfoService.findParamBySupportId(support.getId());
        JSONObject paramJson = json == null?JSONUtil.parseObj(support.getParam()): json;
        List<String> keyList = new ArrayList<>(paramJson.keySet());
        for (int i = 0; i < keyList.size(); i++) {
            TextField textField = new TextField();
            textField.getStyleClass().add("TextField");
            textField.setPrefSize(180.0, 36.0);
            textField.setId(keyList.get(i));
            textField.setText(paramJson.getStr(keyList.get(i)));
            Label label = new Label(keyList.get(i));
            label.setPrefWidth(80);
            label.setMaxWidth(80);
            label.setMinWidth(80);
            label.getStyleClass().add("GlobalLabel");
            infoPanel.add(label, 0, i);
            infoPanel.add(textField, 1, i);
            GridPane.setHalignment(label, HPos.CENTER);
            GridPane.setValignment(label, VPos.CENTER);
            GridPane.setHalignment(textField, HPos.LEFT);
            GridPane.setValignment(textField, VPos.CENTER);
            GridPane.setMargin(textField, new Insets(0, 20, 0, 20));
        }


    }

    /**
     * 应用并保存
     */
    public void serviceApply() {
        if (ObjectUtil.isAllNotEmpty(service_provider.getValue(), service_type.getValue(),
                user_type.getValue())) {
            Set<Node> set = infoPanel.lookupAll(".TextField");
            if (set != null) {
                JSONObject param = new JSONObject();
                for (Node item : set) {
                    if (StrUtil.isEmpty(((TextField) item).getText())) {
                        toastController.pushMessage(ToastEnum.INFO, item.getId() + "不能为空，请检查输入内容~");
                        return;
                    } else {
                        param.putOpt(item.getId(), ((TextField) item).getText());
                    }
                }
                InterfaceSupport support = getSelectInterface();
                if (ObjectUtil.isNotNull(support)) {
                    InterfaceInfo data = new InterfaceInfo()
                            .setSupport_id(support.getId())
                            .setInner(false)
                            .setParam(param.toString())
                            .setVersion_id(user_type.getValue().getId())
                            .setConcurrency(user_type.getValue().getConcurrency())
                            .setProcessing(user_type.getValue().getProcessing());
                    interfaceInfoService.save(data);
                    toastController.pushMessage(ToastEnum.SUCCESS, "保存成功~");
                    interfacePanel.setVisible(false);
                    startController.localChoose();
                }
            }
        } else {
            toastController.pushMessage(ToastEnum.INFO, "格式错误，请检查输入内容~");
        }
    }


    public void onInterfacePageJump() {
        if (ObjectUtil.isAllNotEmpty(service_provider.getValue(), service_type.getValue())) {
            InterfaceSupport support = getSelectInterface();
            if (ObjectUtil.isNotNull(support)) {
                if (StrUtil.isNotBlank(support.getPage())) {
                    DesktopUtil.browse(support.getPage());
                } else {
                    toastController.pushMessage(ToastEnum.INFO, "啊哦~暂无申请页面");
                }
            }
        } else {
            toastController.pushMessage(ToastEnum.INFO, "选择具体服务商与类型后方可跳转哦~");
        }
    }

    private InterfaceSupport getSelectInterface() {
        List<InterfaceSupport> options = supportService.findByQuery(service_provider.getValue().getCode(), service_type.getValue().getCode());
        if (CollectionUtil.isNotEmpty(options)) {
            return CollectionUtil.getFirst(options);
        } else {
            toastController.pushMessage(ToastEnum.ERROR, "内部数据错误! 最好反馈给开发者~");
        }
        return null;
    }
}
