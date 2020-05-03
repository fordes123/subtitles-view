package org.fordes.subview.util;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.fordes.subview.main.Launcher;
import org.fordes.subview.main.mainApplication;
import org.fordes.subview.util.ConfigUtil.ConfigPathUtil;
import org.fordes.subview.util.ConfigUtil.ConfigRWUtil;

import java.util.Optional;

public class InterfaceInformationUtil {
    public void OnlineServeInputPrompt(int type){
        
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("请设置服务接口");
        if(type==1)
            dialog.setHeaderText("视频生成字幕为在线服务，需保持联机\n请输入服务提供商 科大讯飞 语音转写 ID 及 Key 以启用\n(服务由第三方提供，与本软件无关)");
        else
            dialog.setHeaderText("在线翻译需保持联机\n请输入服务提供商 百度在线翻译接口 ID 及 Key 以启用\n(服务由第三方提供，与本软件无关)");
        
        

        
        ButtonType loginButtonType = new ButtonType("开始", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField username = new TextField();
        username.setPromptText("app_id");
        PasswordField password = new PasswordField();
        password.setPromptText("secret_key");

        grid.add(new Label("ID:"), 0, 0);
        grid.add(username, 1, 0);
        grid.add(new Label("KEY:"), 0, 1);
        grid.add(password, 1, 1);

        
        Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
        loginButton.setDisable(true);

        
        username.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);

        
        Platform.runLater(() -> username.requestFocus());

        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(username.getText(), password.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(usernamePassword -> {
            if(type==1) {
                new ConfigRWUtil().setLfasrInfo(usernamePassword.getKey(), usernamePassword.getValue(), new ConfigPathUtil().getLfasrInfoPath());
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("输入完成");
                alert.setHeaderText("接口需重新启动才可生效，点击确定以执行");
                
                alert.showAndWait();
                System.exit(0);
            }else
                new ConfigRWUtil().setBaiduTranInfo(usernamePassword.getKey(),usernamePassword.getValue(),new ConfigPathUtil().getBaiduTarnInfoPath());
            

            
        });
    }
}
