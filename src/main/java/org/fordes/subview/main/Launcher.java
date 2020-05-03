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

    
    public static Map<String, Object> controllers = new HashMap<String, Object>();

    @Override
    public void start(Stage stage) throws Exception {

        stage.initStyle(StageStyle.TRANSPARENT);
        StackPane stackPane = FXMLLoader.load(getClass().getClassLoader().getResource("from/startFrom.fxml"));
        stage.setTitle("SubView Alpha");
        stage.setFullScreenExitHint("");
        stage.getIcons().add(new Image(getClass().getClassLoader().getResource("images/public/Logo_Win.png").toString()));
        Scene scene = new Scene(stackPane);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        scene.getStylesheets().add(getClass().getClassLoader().getResource("css/startStyle_Light.css").toExternalForm());
        
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
