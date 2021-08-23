package org.fordes.subview.controller;

import cn.hutool.core.swing.DesktopUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import org.fordes.subview.entity.PO.ApplicationInfo;
import org.fordes.subview.service.data.UpdateLogService;
import org.fordes.subview.utils.common.ApplicationConfig;
import org.fordes.subview.utils.constants.CommonConstants;

import javax.annotation.Resource;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * 关于页面 控制器
 *
 * @author fordes on 2020/10/6
 */
@FXMLController
public class AboutController extends BasicController implements Initializable {

    @FXML
    private TextArea remark, useHelpArea, updateArea;

    @FXML
    private ToggleButton updateLog, info, usingHelp;

    @FXML
    private Label build_time, build_ver, app_name, version, home_page, issues, right_titles, developer, license, terms;

    @FXML
    private ToggleGroup listGroup;

    @FXML
    private GridPane infoPanel, usingHelpPanel, updatePanel, aboutContent;

    @Resource
    private AboutContentController contentPanel;

    @Resource
    private UpdateLogService updateLogService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        final ApplicationInfo appInfo = ApplicationConfig.getInstance().getApplicationInfo();
        //初始化信息
        build_time.setText("编译日期："+ appInfo.getBuild_time());
        build_ver.setText("内部版本号："+ appInfo.getBuild_version());
        app_name.setText(appInfo.getName());
        version.setText(StrUtil.concat(false, "V ", appInfo.getVersion(), ".", appInfo.getType(), "（", appInfo.getType_cn(),"）"));
        home_page.setText(appInfo.getHome_page());
        issues.setText(appInfo.getIssues());
        useHelpArea.setText(CommonConstants.USE_HELP_TEXT);
        //默认功能项
        this.focus(infoPanel);
        //注册事件
        info.setOnAction(event -> onInfo());
        updateLog.setOnAction(event -> onUpdateLog());
        usingHelp.setOnAction(event -> onLicense());
        developer.setOnMouseClicked(event -> contentPanel.onDeveloper());
        terms.setOnMouseClicked(event -> contentPanel.onPrivacy());
        license.setOnMouseClicked(event -> contentPanel.onLicense());
    }

    /*许可协议*/
    private void onLicense() {
        listGroup.selectToggle(usingHelp);
        if (!this.focus(usingHelpPanel)) {

        }
    }

    /*版本信息*/
    public void onInfo() {
        listGroup.selectToggle(info);
        if (!this.focus(infoPanel)) {

        }
    }

    /*更新日志*/
    public void onUpdateLog() {
        listGroup.selectToggle(updateLog);
        if (!this.focus(updatePanel)) {
            if (StrUtil.isEmpty(updateArea.getText())) {
                updateArea.setText(getUpdateLog());
            }
        }
    }

    public void onClick(MouseEvent event) {
        DesktopUtil.browse(((Label) event.getSource()).getText());
    }

    private String getUpdateLog() {
        StringBuilder builder = new StringBuilder();
        updateLogService.findList(new QueryWrapper<>()).forEach(item -> {
            builder.append("\uD83C\uDFC6 V")
                    .append(item.getVersion())
                    .append("（").append(item.getVersion_type()).append("）")
                    .append("\n\n").append(item.getDetail()).append("\n\n\n");
        });
        return StrUtil.isNotBlank(builder)? builder.toString() :StrUtil.EMPTY;
    }
}