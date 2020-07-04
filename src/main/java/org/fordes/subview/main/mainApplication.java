package org.fordes.subview.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
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
        StackPane stackPane =FXMLLoader.load(getClass().getClassLoader().getResource("from/Application.fxml"));
        stage.setTitle("SubView Base");
        stage.setFullScreenExitHint("");
        stage.getIcons().add(new Image(getClass().getClassLoader().getResource("images/public/Logo_Win.png").toString()));
        Scene scene = new Scene(stackPane,1360,860);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);
        stage.getScene().getStylesheets().add(getClass().getClassLoader().getResource("css/mainStyle_Light.css").toString());
        stage.show();
    }


}