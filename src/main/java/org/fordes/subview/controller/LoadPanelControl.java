package org.fordes.subview.controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.fordes.subview.main.Launcher;
import org.fordes.subview.main.mainApplication;
import org.fordes.subview.test.TestSubtitles;
import org.fordes.subview.util.AudioUtil.VoiceTranslationUtil;
import org.fordes.subview.util.SubtitlesUtil.TimelineUtil;
import org.fordes.subview.util.ToastUtil;
import org.fordes.subview.util.VideoUtil.FFMpegUtil;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class LoadPanelControl implements Initializable {
    @FXML
    private ChoiceBox Language,Number;
    @FXML
    private VBox LoadTop;
    @FXML
    private AnchorPane LoadRoot;
    @FXML
    private HBox TaskPanel,OptionsPanel;
    @FXML
    private Button cancel,start;
    @FXML
    private Label Message,titles;
    @FXML
    private ProgressBar pb;

    private Stage stage;
    private double xOffset = 0;
    private double yOffset = 0;

    private Stage top;
    private Boolean mode;
    private Task work=null,last=null;//加载任务
    private File audio;
    private startControl controller= (startControl) Launcher.controllers.get(startControl.class.getSimpleName());
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Language.getItems().addAll("中文","英语");
        Number.getItems().addAll("自动识别","1","2","3","4","5","6","7","8","9","10");
        Language.getSelectionModel().selectFirst();
        Number.getSelectionModel().selectFirst();

        TaskPanel.setVisible(true);
        titles.setText("准备视频转换");
        Message.setText("等待开始，请手动确认");

        //获取鼠标按下时的位置
        LoadRoot.setOnMousePressed(event -> {
            event.consume();
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        //计算拖动并重设位置
        LoadTop.setOnMouseDragged(event -> {
            event.consume();
            stage = getStage();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
    }

    public Stage getStage() {
        if (stage == null)
            stage = (Stage) LoadRoot.getScene().getWindow();
        return stage;
    }

    //初始化加载任务
    public void initialization(Stage top, Boolean mode, File audio){
        this.top=top;
        this.mode=mode;
        this.audio=audio;
        this.last=last;
        createWork();
    }

    //初始化加载任务
    public void initialization(Stage top, Boolean mode,Task work,Task last,String titles){
        this.top=top;
        this.mode=mode;
        this.work=work;
        this.last=last;
        this.titles.setText(titles);
        Message.setText("即将开始...");
        OptionsPanel.setVisible(false);
        LoadRoot.setPrefSize(600,110);
        LoadRoot.setMaxSize(600,110);
        getStage().setWidth(600);
        getStage().setHeight(110);
        onStart(null);
    }


    //取消任务
    public void onCancel(ActionEvent actionEvent) {
        if(work==null){
            getStage().close();
            return;
        }
        work.cancel(true);
        pb.progressProperty().unbind();
        pb.setProgress(0);
        getStage().close();
        new ToastUtil().toast(top,"任务已被取消~",mode);
    }

    //开始任务
    public void onStart(ActionEvent actionEvent) {
        pb.progressProperty().unbind();
        pb.progressProperty().bind(work.progressProperty());
        work.messageProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> observable, String oldValue,
                                String newValue) {
                Message.setText(newValue);
                //加载结束
                if(newValue.equals("done")){
                    new Thread(last).start();
                    getStage().close();
                }
            }
        });
        new Thread(work).start();
    }

    private String getLanguage(){
        switch (Language.getSelectionModel().getSelectedItem().toString()){
            case "中文":
                return "cn";
            case "英语":
                return "en";
        }
       return "cn";
    }

    private String getSpeakersNumber(){
        switch (Number.getSelectionModel().getSelectedItem().toString()){
            case "自动识别":
                return "0";
            case "1":
                return "1";
            case "2":
                return "2";
            case "3":
                return "3";
            case "4":
                return "4";
            case "5":
                return "5";
            case "6":
                return "6";
            case "7":
                return "7";
            case "8":
                return "8";
            case "9":
                return "9";
            case "10":
                return "10";
        }
        return "2";
    }

    //创建任务
    private void createWork(){

        work= new Task() {
            @Override
            protected Object call() throws Exception {

                updateMessage("正在提取音轨...");
                updateProgress(0.3, 1);
                new FFMpegUtil().SegmentedAudio(controller.inputset.getVideoFile().getPath(), audio.getPath());//提取音轨

                updateMessage("开始语音转换，请保持联机...");
                updateProgress(0.6, 1);
                String res=new VoiceTranslationUtil(audio.getPath()).start(getLanguage(),getSpeakersNumber());

                updateMessage("转换完成，正在解析结果...");
                updateProgress(0.9, 1);
                controller.inputset.setText(new TimelineUtil(1).parsing(res));

                updateMessage("done");
                return true;
            }
        };


        last= new Task(){

            @Override
            protected Object call() throws Exception {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            new mainApplication().start(controller.inputset.getStage());
                            top.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                return 0;
            }
        };
    }
}
