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

import java.util.HashMap;
import java.util.Map;

/**
 * 程序入口
 * @author Fordes
 */
public class Launcher extends Application {

    //创建一个Controller容器
    public static Map<String, Object> controllers = new HashMap<String, Object>();


    @Override
    public void start(Stage stage) throws Exception {

        stage.initStyle(StageStyle.TRANSPARENT);
        StackPane stackPane = new FXMLLoader(getClass().getClassLoader().getResource("resources/from/startFrom.fxml")).load();
        stage.setTitle("SubView Base");
        stage.setFullScreenExitHint("");//去除全屏提示
        stage.getIcons().add(new Image(
                Launcher.class.getResourceAsStream("/resources/images/public/Logo_Win.png")));
        Scene scene = new Scene(stackPane);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.getScene().getStylesheets().add(getClass().getClassLoader().getResource("resources/css/startStyle_Light.css").toString());
        //stage.setAlwaysOnTop(true);
        stage.show();
    }
}
