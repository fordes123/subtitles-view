package org.fordes.subview.controller;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import org.fordes.subview.main.Launcher;
import org.fordes.subview.util.ConfigUtil.ConfigPathUtil;
import org.fordes.subview.util.FileIOUtil.OpenfileUtil;
import org.fordes.subview.util.FileIOUtil.SubFileUtil;
import org.fordes.subview.util.LoadUtil;
import org.fordes.subview.util.ToastUtil;
import org.fordes.subview.util.VideoUtil.FFMpegUtil;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;

public class BuildPanelControl implements Initializable {
    @FXML
    private GridPane Build;
    @FXML
    private GridPane BuildPanel,PreviewPanel;
    @FXML
    private ToggleGroup ListGroup;
    @FXML
    private ToggleButton Build_Manual,Build_Preview;
    @FXML
    private ChoiceBox Build_VideoOutputFormat,Build_Mode,Build_SubEncoding,Build_SubtitlesSynthetic,Build_SubtitlesGraphics;
    @FXML
    private TextField Build_OutPath,Build_FileName;
    @FXML
    private CheckBox Build_Replace,Build_End;
    @FXML
    private Label Build_SubtitlesGraphics_Label,Build_SubtitlesSynthetic_Label,Build_SubEncoding_Label;

    //全局主题
    private Object LightTheme=getClass().getClassLoader().getResource("css/mainStyle_Light.css").toString();
    private Object DarkTheme=getClass().getClassLoader().getResource("css/mainStyle_Dark.css").toString();
    private startControl controller= (startControl) Launcher.controllers.get(startControl.class.getSimpleName());
    private ArrayList<String> outType1,outType2;
    private boolean BuildManual_initialization_state=false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //默认主题
        Build.getStylesheets().add(LightTheme.toString());
    }

    /**
     * 切换颜色模式
     * @param ModeState
     */
    public void modeChange(Boolean ModeState){
        /*移除所有样式表*/
        Build.getStylesheets().remove(LightTheme);
        Build.getStylesheets().remove(DarkTheme);
        if(ModeState)//深色->浅色模式
            Build.getStylesheets().add(LightTheme.toString());
        else//浅色->深色模式
            Build.getStylesheets().add(DarkTheme.toString());
        //ModeState=!ModeState;
    }

    //隐藏所有组件
    private void hide(){
        BuildPanel.setVisible(false);
        PreviewPanel.setVisible(false);
    }

    public void onBuild_Manual(ActionEvent actionEvent) {
        ListGroup.selectToggle(Build_Manual);
        Build_Manual.setSelected(true);
        //询问焦点，如已在此选项上，则操作无效化，避免重复加载
        if(controller.focus_indicator.equals(Build_Manual.getId()))
            return;
        controller.focus_indicator=Build_Manual.getId();//设置焦点
        hide();
        //初始化
        if(!BuildManual_initialization_state)
            BuildManual_initialization();
        //导出模式选项更改设置
        Build_Mode.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if(((String)Build_Mode.getValue()).equals("合成视频")){
                    if(controller.inputset.getVideoFile()==null) {//打开的文件为字幕，且并未选择视频
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("提示");
                        alert.setHeaderText("需选择视频才可使用此项");
                        alert.setContentText("合成视频需要一个目标视频才可开始，点击确定以选择，否则将取消");
                        Optional result = alert.showAndWait();
                        if (result.get() == ButtonType.OK) {
                            File videoFile = new OpenfileUtil().LoadVideo().showOpenDialog(controller.inputset.getStage());
                            if(videoFile!=null)
                                ChangeOutputType(2);
                            else{
                                new ToastUtil().toast("操作被取消或出错 ...",2000);
                                ChangeOutputType(1);
                                return;
                            }
                        } else{
                            ChangeOutputType(1);
                            return;
                        }
                    }
                    ChangeOutputType(2);
                }else{
                    ChangeOutputType(1);
                    /*if (controller.inputset.getType() == 2)
                        Build_Replace.setSelected(false);*/
                }
                Build_VideoOutputFormat.getSelectionModel().selectFirst();//默认输出格式
            }

        });
        //默认项
        if(controller.inputset.getVideoFile()==null)//打开的文件为字幕，且并未选择视频
            Build_Mode.getSelectionModel().selectLast();
        else
            Build_Mode.getSelectionModel().selectFirst();
        Build_SubEncoding.getSelectionModel().selectFirst();//字幕编码
        Build_SubtitlesGraphics.getSelectionModel().selectFirst();//图形质量
        Build_SubtitlesSynthetic.getSelectionModel().selectFirst();//处理方式
        Build_OutPath.setText(((String)Build_Mode.getValue()).equals("合成视频")?controller.inputset.getVideoFile().getParent():controller.inputset.getSubFile().getParent());

        String temp = Build_Mode.getValue().toString().equals("合成视频")?controller.inputset.getVideoFile().getName().substring(0, controller.inputset.getVideoFile().getName().lastIndexOf(".")):controller.inputset.getSubFile().getName().substring(0, controller.inputset.getSubFile().getName().lastIndexOf("."));
        //((String)Build_Mode.getValue()).equals("合成视频")?controller.inputset.getVideoFile().getName().substring(0, controller.inputset.getSubFile().getName().lastIndexOf(".")):controller.inputset.getSubFile().getName().substring(0, controller.inputset.getSubFile().getName().lastIndexOf("."));
                //controller.inputset.getFile().getName().substring(0, controller.inputset.getFile().getName().lastIndexOf("."));
        if (temp.length() > 20)
            temp = temp.substring(0, 10) + "..." + temp.substring(temp.length() - 10, temp.length());
        Build_FileName.setText(temp + "_SubViewExport");
        BuildPanel.setVisible(true);
    }

    public void onBuild_Preview(ActionEvent actionEvent) {
        ListGroup.selectToggle(Build_Preview);
        Build_Preview.setSelected(true);
        //询问焦点，如已在此选项上，则操作无效化，避免重复加载
        if(controller.focus_indicator.equals(Build_Preview.getId()))
            return;
        controller.focus_indicator=Build_Preview.getId();//设置焦点
        hide();
        PreviewPanel.setVisible(true);
    }

    //初始化
    private void BuildManual_initialization(){
        outType1 = new ArrayList<String>(Arrays.asList("SRT", "ASS", "LRC"));
        outType2 = new ArrayList<String>(Arrays.asList("MP4", "MKV", "MOV"));
        Build_Mode.getItems().addAll("合成视频", "字幕文件");
        Build_SubEncoding.getItems().addAll("UTF-8", "ANSI（GBK）","Unicode","ASCII");
        Build_SubtitlesSynthetic.getItems().addAll("流式（合成速度更快）", "嵌入式（兼容性更好）");
        Build_SubtitlesGraphics.getItems().addAll("保证画质","节省空间","自定义");
        BuildManual_initialization_state=!BuildManual_initialization_state;
    }

    //构建导出——>保存选项,更改导出模式
    private void ChangeOutputType(int type){
        if(type==2){ //导出视频
            Build_Mode.getSelectionModel().selectFirst();
            Build_VideoOutputFormat.setItems(FXCollections.observableList(outType2));
            Build_SubtitlesGraphics_Label.setVisible(true);
            Build_SubtitlesSynthetic_Label.setVisible(true);
            Build_SubtitlesGraphics.setVisible(true);
            Build_SubtitlesSynthetic.setVisible(true);
            Build_SubEncoding.setVisible(false);
            Build_SubEncoding_Label.setVisible(false);
        }else{//导出字幕
            Build_Mode.getSelectionModel().selectLast();
            Build_VideoOutputFormat.setItems(FXCollections.observableList(outType1));
            Build_SubtitlesGraphics_Label.setVisible(false);
            Build_SubtitlesSynthetic_Label.setVisible(false);
            Build_SubtitlesGraphics.setVisible(false);
            Build_SubtitlesSynthetic.setVisible(false);
            Build_SubEncoding.setVisible(true);
            Build_SubEncoding_Label.setVisible(true);
        }
    }

    public void Build_Replace_Selected(MouseEvent mouseEvent) {
    }

    public void Build_ChoosePath(ActionEvent actionEvent) {
    }


    //开始导出
    public void onStartBuild(ActionEvent actionEvent) throws IOException {
        int mode;
        String input = "",output,filePath;
        String oldFilePath = "";//原文件路径
        Task task;
        //获取输出路径
        if(Build_Replace.isSelected())//替换原文件
            output=System.getProperty("user.dir") + "\\date\\"+Build_FileName.getText()+"."+Build_VideoOutputFormat.getValue().toString().toLowerCase();
        else
            output=Build_OutPath.getText()+"\\"+Build_FileName.getText()+"."+Build_VideoOutputFormat.getValue().toString().toLowerCase();
        if(((String)Build_Mode.getValue()).equals("合成视频")){
            if(controller.inputset.getSubFile()==null) {//以视频开始进入程序，先生成字幕文件
                File tempSubtitle=new File(new ConfigPathUtil().getTempPath() + "\\tempSubtitle.srt");
                if(tempSubtitle.exists())//检测文件是否存在，若已经存在，删除之
                    tempSubtitle.delete();
                new SubFileUtil().GenerateSubtitles(controller.inputset.getText(),tempSubtitle,1,null,true);//生成文件
                controller.inputset.setSubtitles(tempSubtitle,1);
            }
            input=controller.inputset.getVideoFile().getPath();
            filePath=controller.inputset.getSubFile().getPath();
            oldFilePath=controller.inputset.getVideoFile().getPath();
            mode=Build_SubtitlesSynthetic.getValue().toString().equals("流式（合成速度更快）")?1:2;
            String finalInput = input;
            System.out.println("原文件路径"+input);
            task= new Task() {
                @Override
                protected Object call() throws Exception {
                    if(mode==1)
                        updateMessage("处理中，任务不会耗费很多时间...");
                    else
                        updateMessage("处理中，可能需要一些时间，请保持耐心...");
                    updateProgress(0.5, 1);
                    new FFMpegUtil().CompositeSubtitles(finalInput,output,filePath,mode);
                    updateMessage("done");
                    return true;
                }
            };
        }else{
            oldFilePath=controller.inputset.getVideoFile()==null?controller.inputset.getSubFile().getPath():controller.inputset.getVideoFile().getPath();


            task= new Task() {
                @Override
                protected Object call() throws Exception {
                    updateMessage("准备中，即将开始...");
                    updateProgress(0.3, 1);

                    File tempSubtitle=new File(output);
                    if(tempSubtitle.exists())
                        tempSubtitle.delete();
                    String coding=Build_SubEncoding.getValue().toString();
                    if(coding.equals("ANSI（GBK）"))
                        coding="GBK";

                    updateMessage("生成中，这不会需要太久...");
                    updateProgress(0.6, 1);

                    new SubFileUtil().GenerateSubtitles(controller.inputset.getText(),tempSubtitle,controller.inputset.getSubType(),coding,false);
                    updateMessage("done");
                    return true;
                }
            };

        }


        String finalOldFilePath = oldFilePath;
        Task last= new Task() {
            @Override
            protected Object call() throws Exception {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {

                        if(Build_Replace.isSelected()){
                            new SubFileUtil().ReplaceOldFile(finalOldFilePath, output);
                        }

                        if(Build_End.isSelected()){
                            new ToastUtil().toast("任务完成，程序将在3秒后退出 ...",3000);
                            try {
                                Thread.sleep(3000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            /**
                             * 待完善
                             */
                            System.exit(0);
                        }else
                            new ToastUtil().toast("导出完成，请前往目标路径查看 ...",3000);
                    }
                });

                return 0;
            }
        };

        new LoadUtil().load(controller.inputset.getStage(),controller.inputset.getTheme(),task,last,"处理中");
        return;
    }


}
