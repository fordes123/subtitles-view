package org.fordes.subview.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class mainApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {

        stage.initStyle(StageStyle.TRANSPARENT);
        StackPane stackPane = new FXMLLoader(getClass().getClassLoader().getResource("resources/from/Application.fxml")).load();
        stage.setTitle("SubView Base");
        stage.setFullScreenExitHint("");//去除全屏提示
        stage.getIcons().add(new Image(
                Launcher.class.getResourceAsStream("/resources/images/public/Logo_Win.png")));
        Scene scene = new Scene(stackPane,1360,860);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.getScene().getStylesheets().add(getClass().getClassLoader().getResource("resources/css/mainStyle_Light.css").toString());
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}