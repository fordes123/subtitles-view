package org.fordes.subview.controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.ScrollEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import org.fordes.subview.entity.FileSet;
import org.fordes.subview.main.Launcher;
import org.fordes.subview.util.FileIOUtil.FileUtil;
import org.fordes.subview.util.FileIOUtil.OpenfileUtil;
import org.fordes.subview.util.PopUpWindowUtil.ToastUtil;
import org.fordes.subview.util.VideoUtil.VideoInfoUtil;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class SyncPanelController extends RootController implements Initializable {
    @FXML
    private GridPane Sync,ManualSyncPanel,AutomaticSyncPanel,MSP_Split_Left_Content,WaitingLoadingPanel,WaitingLoadingPanel_Content;
    @FXML
    private ToggleGroup ListGroup;
    @FXML
    private ToggleButton ManualSync,AutomaticSync;
    @FXML
    private SplitPane MSP_Split;
    @FXML
    private AnchorPane MSP_Split_Left,MSP_Split_Right;
    @FXML
    private MediaView MSP_Split_Left_MediaView;
    @FXML
    private ListView MSP_Split_Right_ListView;
    @FXML
    private Button WaitingLoadingPanel_Content_Image;
    @FXML
    private GridPane mediaContent;
    @FXML
    private RowConstraints mediaRowConstraints;
    @FXML
    private Label video_vol;
    
    private Object LightTheme=getClass().getClassLoader().getResource("css/mainStyle_Light.css").toString();
    private Object DarkTheme=getClass().getClassLoader().getResource("css/mainStyle_Dark.css").toString();
    private StartController startController = (StartController) Launcher.controllers.get(StartController.class.getSimpleName());
    private boolean ManualSync_initialize_state=false;
    private ArrayList SubtitleList=null;
    private MediaPlayer mediaplayer;
    private int VideoWidth,VideoHeight;
    private Boolean ManualPlayState=false;
    private Timer timer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Sync.getStylesheets().add(LightTheme.toString());

        MSP_Split_Right_ListView.prefWidthProperty().bind(MSP_Split_Right.widthProperty());
        MSP_Split_Right_ListView.prefHeightProperty().bind(MSP_Split_Right.heightProperty());
        MSP_Split_Left_Content.prefWidthProperty().bind(MSP_Split_Left.widthProperty());
        MSP_Split_Left_Content.prefHeightProperty().bind(MSP_Split_Left.heightProperty());
        MSP_Split_Left_MediaView.fitWidthProperty().bind(mediaContent.widthProperty());
        MSP_Split_Left_MediaView.fitHeightProperty().bind(mediaContent.heightProperty());

/*        mediaContent.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                MSP_Split_Left_MediaView.setFitWidth((double)newValue);
            }
        });
        mediaContent.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                MSP_Split_Left_MediaView.setFitHeight((double)newValue-6);

            }
        });*/
    }

    /**
     * 切换颜色模式
     * @param ModeState
     */
    public void ThemeApply(Boolean ModeState){
        /*移除所有样式表*/
        Sync.getStylesheets().remove(LightTheme);
        Sync.getStylesheets().remove(DarkTheme);
        if(!ModeState)
            Sync.getStylesheets().add(LightTheme.toString());
        else
            Sync.getStylesheets().add(DarkTheme.toString());
        
    }

    
    public void onManualSync(ActionEvent actionEvent) {
        ListGroup.selectToggle(ManualSync);
        ManualSync.setSelected(true);
        
        if(startController.focus_indicator.equals(ManualSync.getId()))
            return;
        startController.focus_indicator=ManualSync.getId();
        Hide();
        if(startController.inputset.getVideo()==null)
            WaitingLoadingPanel.setVisible(true);
        /*检查初始化是否完成*/
        else if(!ManualSync_initialize_state)
            ManualSync_initialize();
        else{
            
            RefreshMSP_ListView();
            ManualSyncPanel.setVisible(true);
        }

    }

    
    public void onAutomaticSync(ActionEvent actionEvent) {
        ListGroup.selectToggle(AutomaticSync);
        AutomaticSync.setSelected(true);
        
        if(startController.focus_indicator.equals(AutomaticSync.getId()))
            return;
        startController.focus_indicator=AutomaticSync.getId();
        Hide();
        AutomaticSyncPanel.setVisible(true);
    }

    
    private void Hide(){
        ManualSyncPanel.setVisible(false);
        AutomaticSyncPanel.setVisible(false);
        WaitingLoadingPanel.setVisible(false);
        stopManualSync();
    }

    /*拖拽触发*/
    public void onDragOver(DragEvent dragEvent) {
        Dragboard db = dragEvent.getDragboard();
        if (db.hasFiles()) {
            File file=db.getFiles().get(0);
            int Type=new FileUtil().getFileType(file);
            if(Type>=10){
                dragEvent.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                startController.inputset.setVideo(new FileSet(file,Type));
            }
        }
        dragEvent.consume();
    }

    /*拖拽完成*/
    public void onDragDropped(DragEvent dragEvent) {
        ManualSync_initialize();
    }

    public void chooseVideoFile(ActionEvent actionEvent) {
        File videoFile = new OpenfileUtil().LoadVideo().showOpenDialog(startController.inputset.getStage());
        if (videoFile != null) {
            startController.inputset.setVideo(new FileSet(videoFile,new FileUtil().getFileType(videoFile)));
            ManualSync_initialize();
            


        }
    }

    private void RefreshMSP_ListView(){
        if(ManualSync_initialize_state)
            MSP_Split_Right_ListView.getItems().removeAll();
                    
        SubtitleList = new ArrayList<>();
        for(String i: startController.inputset.getText().split("\n"))
            SubtitleList.add(i+"\n");
        
        try {
            MSP_Split_Right_ListView.setItems(FXCollections.observableList(SubtitleList));
        }catch (Exception e){

        }
    }


    
    public void stopManualSync(){
        /*if(timer==null)
            return;
        timer.cancel();
        mediaplayer.stop();
        ManualPlayState=false;*/
        if(ManualPlayState) {
            timer.cancel();
            mediaplayer.stop();
            ManualPlayState=!ManualPlayState;
        }
    }

    
    private void ManualSync_initialize(){
        Hide();
        ManualSyncPanel.setVisible(true);
        
        RefreshMSP_ListView();
        
        mediaplayer = new MediaPlayer(new Media(startController.inputset.getVideo().getFile().toURI().toString()));
        MSP_Split_Left_MediaView.setMediaPlayer(mediaplayer);
        
        VideoInfoUtil vu=new VideoInfoUtil(startController.inputset.getVideo().getFile());
        VideoWidth=vu.getWidth();
        VideoHeight=vu.getHeight();
        
        dynamic((double)MSP_Split_Left_Content.getWidth());
        
        MSP_Split_Left_Content.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                dynamic((double)newValue);
            }
        });
        
        mediaplayer.setVolume(0.50);
        video_vol.setText("音量：50");
        
        MSP_Split_Right_ListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                
                if (startController.timelineUtil.TimelineCheck(newValue.toString().trim(),1)) {
                    if (ManualPlayState) 
                        timer.cancel();
                    mediaplayer.stop();
                    
                    long first = startController.timelineUtil.TimelineToMillisecond(newValue.toString().substring(0, 12));
                    
                    long last = startController.timelineUtil.TimelineToMillisecond(newValue.toString().substring(17, 29));
                    
                    long time = last - first;
                    if (time <= 0) {
                        new ToastUtil().toast("此行时间轴存在错误，检查一下~", 2000);
                        return;
                    }
                    
                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            Platform.runLater(() -> mediaplayer.stop());
                            ManualPlayState = false;
                        }
                    };
                    mediaplayer.setStartTime(new Duration(first));
                    timer=new Timer();
                    timer.schedule(task, time);

                    
                    mediaplayer.play();
                    ManualPlayState = true;
                }
            }
        });
        MSP_Split_Right_ListView.setCellFactory(TextFieldListCell.forListView());
        MSP_Split_Right_ListView.setEditable(true);
        ManualSync_initialize_state=true;

    }

    
    private void saveListView(){
        String str="";
        String[] temp=MSP_Split_Right_ListView.getItems().toString().split("\n");
        for(int i=0;i<temp.length-1;i++){
            str+=MSP_Split_Right_ListView.getItems().get(i);
        }
        startController.inputset.setText(str);
    }

    
    private void dynamic(double value){
/*        MSP_Split_Left_MediaView.setFitWidth(value-40);
        double proportion=(VideoHeight*value)/VideoWidth;
        mediaRowConstraints.setMaxHeight(proportion);
        MSP_Split_Left_MediaView.setFitHeight(proportion-3);*/

        double proportion=(double)VideoWidth/VideoHeight;
        double trueHeight=(value-40)/proportion+15;
        mediaRowConstraints.setMaxHeight(trueHeight);
        mediaContent.setMinWidth(value-40);
        mediaContent.setMinHeight(trueHeight-15);
        
    }

    
    public void mediaScroll(ScrollEvent scrollEvent) {
        double vol;
        if(scrollEvent.getDeltaY()>0){
            vol=mediaplayer.getVolume()+0.02;
            if(vol>1.0){
                vol=(double)1;
                new ToastUtil().toast("音量已最大，继续提升请尝试调整系统音量",1000);
            }
        }
        else {
            vol=mediaplayer.getVolume()-0.02;
            if(vol<0){
                vol=0;
                new ToastUtil().toast("音量已关闭",2000);
            }

        }
        mediaplayer.setVolume(vol);
        video_vol.setText("音量："+Math.round(vol*100));
    }

    
    public void ManualListEditCommit(ListView.EditEvent editEvent) {
        new ToastUtil().toast("完成编辑", 1000);
        MSP_Split_Right_ListView.getItems().set(editEvent.getIndex(), editEvent.getNewValue()+"\n");
        saveListView();
    }

    
    public void ManualListEditStart(ListView.EditEvent editEvent) {
        new ToastUtil().toast("开始编辑", 1000);
    }
}
