package org.fordes.subview.util;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import org.fordes.subview.util.ConfigUtil.ConfigPathUtil;
import org.fordes.subview.util.ConfigUtil.ConfigRWUtil;

import java.util.Optional;

public class InterfaceInformationUtil {
    public void OnlineServeInputPrompt(int type){
        // Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("请设置服务接口");
        if(type==1)
            dialog.setHeaderText("视频生成字幕为在线服务，需保持联机\n请输入服务提供商 科大讯飞 语音转写 ID 及 Key 以启用\n(服务由第三方提供，与本软件无关)");
        else
            dialog.setHeaderText("在线翻译需保持联机\n请输入服务提供商 百度在线翻译接口 ID 及 Key 以启用\n(服务由第三方提供，与本软件无关)");
        // Set the icon (must be included in the project).
        //dialog.setGraphic(new ImageView(this.getClass().getResource("login.png").toString()));

        // Set the button types.
        ButtonType loginButtonType = new ButtonType("开始", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

        // Create the username and password labels and fields.
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

        // Enable/Disable login button depending on whether a username was entered.
        Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
        loginButton.setDisable(true);

        // Do some validation (using the Java 8 lambda syntax).
        username.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(() -> username.requestFocus());

        // Convert the result to a username-password-pair when the login button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(username.getText(), password.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(usernamePassword -> {
            if(type==1)
                new ConfigRWUtil().setLfasrInfo(usernamePassword.getKey(),usernamePassword.getValue(),new ConfigPathUtil().getLfasrInfoPath());
            else
                new ConfigRWUtil().setBaiduTranInfo(usernamePassword.getKey(),usernamePassword.getValue(),new ConfigPathUtil().getBaiduTarnInfoPath());
            //System.out.println("Username=" + usernamePassword.getKey() + ", Password=" + usernamePassword.getValue());
        });
    }
}
