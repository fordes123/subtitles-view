package org.fordes.subview.controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.fordes.subview.entity.inputSet;
import org.fordes.subview.main.Launcher;
import org.fordes.subview.main.mainApplication;
import org.fordes.subview.util.*;
import org.fordes.subview.util.AudioUtil.VoiceTranslationUtil;
import org.fordes.subview.util.ConfigUtil.ConfigPathUtil;
import org.fordes.subview.util.ConfigUtil.ConfigRWUtil;
import org.fordes.subview.util.FileIOUtil.FileUtil;
import org.fordes.subview.util.FileIOUtil.OpenfileUtil;
import org.fordes.subview.util.FileIOUtil.SubFileUtil;
import org.fordes.subview.util.SubtitlesUtil.TimelineUtil;
import org.fordes.subview.util.VideoUtil.FFMpegUtil;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * 程序入口控制器
 * @author Fordes
 */

public class startControl implements Initializable {

    @FXML
    private StackPane shadow;
    @FXML
    public GridPane root,content;
    @FXML
    private Button open,news;
    @FXML
    private ListView dragPanel;
    @FXML
    private ToggleButton pos_home,pos_local,pos_net;
    @FXML
    private ToggleGroup position;
    @FXML
    private Label tips;
    @FXML
    private titlesBarControl topController;

    private Stage stage;
    private double xOffset = 0;
    private double yOffset = 0;
    private int bit = 0;
    private static final double RESIZE_WIDTH = 5.00;
    private static final double MIN_WIDTH = 1060.00;
    private static final double MIN_HEIGHT = 740.00;

    private boolean FullScreen = false;//全屏状态
    private boolean ModeState = false;//模式状态
    private int fileType;
    public inputSet inputset=new inputSet();//输入参数集
    public String focus_indicator=new String("");//操作焦点指示器
    //全局主题
    private Object LightTheme=getClass().getClassLoader().getResource("resources/css/startStyle_Light.css").toString();
    private Object DarkTheme=getClass().getClassLoader().getResource("resources/css/startStyle_Dark.css").toString();
    private static String tempPath=System.getProperty("user.dir") + "\\date\\temp";//缓存路径
    @FXML
    private VBox top;
    private Button winClose,winMin,mode,winMax;
    public TimelineUtil timelineUtil;//全局字幕工具类
    private File file;
    /**
     * 获取窗口
     * @return
     */
    public Stage getStage() {
        if (stage == null)
            stage = (Stage) root.getScene().getWindow();
        return stage;
    }

    /**
     *初始化任务
     * @param location
     * @param resources
     */
    public void initialize(URL location, ResourceBundle resources) {
        //加载组件
        top=topController.getTop();
        mode = topController.getMode();
        winClose = topController.getWinClose();
        winMin = topController.getWinMin();
        winMax = topController.getWinMax();

        //获取鼠标按下时的位置
        root.setOnMousePressed(event -> {
            event.consume();
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        //计算拖动并重设位置
        top.setOnMouseDragged(event -> {
            event.consume();
            stage = getStage();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
        root.setOnMouseMoved(this::mouseMoveHandle);
        root.setOnMouseDragged(this::mouseDraggedHandle);
        pos_home.setSelected(true);
        //将此Controller添加到容器中
        Launcher.controllers.put(this.getClass().getSimpleName(), this);
        //清理缓存
        if(!new FileUtil().delAllFile(tempPath)){
            System.out.println("缓存清理失败(code:10001)，详情请查阅文档");
            //new ToastUtil().toast(getStage(),"缓存清理失败(code:10001)，详情请查阅文档",3000,0,-100);
        }
        //设置事件
        winClose.setOnAction(event -> { onClose(); });
        winMax.setOnAction(event -> { maxWindow(); });
        winMin.setOnAction(event -> { minWindow(); });
        mode.setOnAction(event -> { modeChange(); });
        //主题初始化
        topController.initialization(1);
    }


    /**
     * 获取鼠标坐标位置，进行光标变换
     * @param event
     */
    private void mouseMoveHandle(MouseEvent event) {
        event.consume();
        double x = event.getSceneX();
        double y = event.getSceneY();
        stage=getStage();
        double width = stage.getWidth()-30;
        double height = stage.getHeight()-30;
        Cursor cursorType = Cursor.DEFAULT;
        bit = 0;
        if (y >= height - RESIZE_WIDTH) {
            if (x <= RESIZE_WIDTH) {
                bit |= 1 << 3;
            } else if (x >= width - RESIZE_WIDTH) {
                bit |= 1;
                bit |= 1 << 2;
                cursorType = Cursor.SE_RESIZE;
            } else {
                bit |= 1;
                cursorType = Cursor.S_RESIZE;
            }
        } else if (x >= width - RESIZE_WIDTH) {
            bit |= 1 << 2;
            cursorType = Cursor.E_RESIZE;
        }
        root.setCursor(cursorType);
    }

    /**
     * 处理窗口缩放
     * @param event
     */
    private void mouseDraggedHandle(MouseEvent event) {
        event.consume();
        Stage primaryStage = getStage();
        double x = event.getSceneX();
        double y = event.getSceneY();
        double nextX = primaryStage.getX();
        double nextY = primaryStage.getY();
        double nextWidth = primaryStage.getWidth()-30;
        double nextHeight = primaryStage.getHeight()-30;
        if ((bit & 1 << 2) != 0) {
            nextWidth = x;
        }
        if ((bit & 1) != 0) {
            nextHeight = y;
        }
        if (nextWidth <= MIN_WIDTH) {
            nextWidth = MIN_WIDTH;
        }
        if (nextHeight <= MIN_HEIGHT) {
            nextHeight = MIN_HEIGHT;
        }
        primaryStage.setX(nextX);
        primaryStage.setY(nextY);
        primaryStage.setWidth(nextWidth);
        primaryStage.setHeight(nextHeight);
    }


    /**
     * 颜色模式切换
     */
    private void modeChange() {
        topController.modeChange(ModeState);
        /*移除所有样式表*/
        getStage().getScene().getStylesheets().remove(LightTheme);
        getStage().getScene().getStylesheets().remove(DarkTheme);
        if(ModeState)//深色->浅色模式
            getStage().getScene().getStylesheets().add(LightTheme.toString());
        else//浅色->深色模式
            getStage().getScene().getStylesheets().add(DarkTheme.toString());
        ModeState=!ModeState;
    }

    /**
     * 窗口最大化标准化切换
     */
    private void FullScreenChange(){

        /*清除已有样式*/
        winMax.getStyleClass().removeAll("winMax","winMax_Full");
        shadow.getStyleClass().removeAll("shadow","shadow_full");
        /*根据情况重设样式*/
        if(FullScreen) {//窗口最大化->标准化
            winMax.getStyleClass().add("winMax");
            shadow.getStyleClass().add("shadow");
            root.setPadding(new Insets(30,30,30,30));
        }else{//窗口标准化->最大化
            winMax.getStyleClass().add("winMax_Full");
            shadow.getStyleClass().add("shadow_full");
            root.setPadding(new Insets(0,0,0,0));
        }
        FullScreen = !FullScreen;
    }

    /**
     * 主页选项切换（主页、此电脑、网络）
     */
    private void OptionSwitch(){

    }

    /**
     * 窗口关闭
     */
    private void onClose() {
        stage.close();//关闭窗口
        System.exit(0);
    }

    /**
     * 窗口最大化
     */
    private void maxWindow() {
        FullScreenChange();
        stage.setFullScreen(FullScreen);
    }

    /**
     * 窗口最小化
     */
    private void minWindow() { stage.setIconified(true);}

    /**
     * 位置选项——>主页
     * @param actionEvent
     */
    public void home(ActionEvent actionEvent) {
        tips.setText("拖放或点此选择文件以开始");
        tips.setDisable(false);
    }

    /**
     * 位置选项——>此电脑
     * @param actionEvent
     */
    public void local(ActionEvent actionEvent) {
        tips.setText("该功能暂未完成 ...");
        tips.setDisable(true);

    }

    /**
     * 位置选项->网络
     * @param actionEvent
     */
    public void net(ActionEvent actionEvent) {
        tips.setText("该功能暂未完成 ...");
        tips.setDisable(true);
    }

    /**
     * 打开文件
     * @param mouseEvent
     */
    public void openFile(MouseEvent mouseEvent) throws IOException {
        file = new OpenfileUtil().Load().showOpenDialog(stage);
        if(file!=null){
            if(!new FileUtil().Do(file)) {
                new ToastUtil().toast(getStage(),"暂不支持该类型文件",ModeState,3000,0,-100);
            }else {
                LoadInterface(false);
            }
        }
    }
    public void open(ActionEvent actionEvent) throws IOException {openFile(null);}

    /**
     * 新建文件
     * @param actionEvent
     * @throws InterruptedException
     */
    public void news(ActionEvent actionEvent) throws InterruptedException, IOException {

        //new ToastUtil().toast(getStage(),"此功能尚在调试中，请悉知",ModeState,1000);
        file=new File(tempPath+"\\newSubtitles_"+new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date())+".srt");
        if(file.exists()){
            file.delete();
        }
        LoadInterface(true);
        //任务
        /*Task work= new Task() {
            @Override
            protected Object call() throws Exception {

                updateMessage("该功能还没开发 ...");
                updateProgress(0.25, 1);
                Thread.sleep(2000);

                updateMessage("这是测试一下加载窗口好使不 ...");
                updateProgress(0.5, 1);
                Thread.sleep(2000);

                updateMessage("不要动，就快好了 ...");
                updateProgress(0.75, 1);
                Thread.sleep(2000);

                updateMessage("已经测完，再见 ...");
                updateProgress(1, 1);
                Thread.sleep(1000);

                updateMessage("done");
                return true;
            }

        };
       new LoadUtil().load(getStage(),ModeState,work,null,"加载中");*/
    }

    /**
     * 主面板文件拖拽触发事件-判定拖拽文件类型
     * @param dragEvent
     */
    public void onDragOver(DragEvent dragEvent) {
        Dragboard db = dragEvent.getDragboard();
        if (db.hasFiles()) {
            file=db.getFiles().get(0);
            if(new FileUtil().Do(file))
                dragEvent.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
        dragEvent.consume();
    }

    /**
     * 主面板文件拖拽结束事件-判定拖拽文件类型
     * @param dragEvent
     */
    public void onDragDropped(DragEvent dragEvent) throws IOException {
        dragEvent.consume();
        LoadInterface(false);
    }

    /**
     * 根据打开（拖拽）的文件加载主界面
     */
    private void LoadInterface(boolean newSubtitle) throws IOException {
        int Type=new FileUtil().getFileType(file);//获取文件类型
        //设置启动参数
        //inputset.setFile(file);
        inputset.setCode("UTF-8");
        inputset.setTheme(ModeState);
        inputset.setStage(new Stage());
        //inputset.setType(new FileUtil().getFileType(file));//设置文件类型
        //timelineUtil=new TimelineUtil(inputset.getType());
        switch (Type<10?1:2){
            case 1://字幕
                inputset.setText(newSubtitle?" ":new SubFileUtil().Read(file,inputset.getCode()));
                inputset.setSubtitles(file,Type);
                inputset.setVideo(null,0);
                timelineUtil=new TimelineUtil(Type);
                getStage().close();
                try {
                    new mainApplication().start(inputset.getStage());
                } catch (Exception e) {
                    //异常处理有待完成
                    System.out.println(e);
                }
                return;
            case 2://视频
                String id=ConfigRWUtil.GetInterfaceInformation("app_id",new ConfigPathUtil().getLfasrInfoPath());
                String key=ConfigRWUtil.GetInterfaceInformation("secret_key",new ConfigPathUtil().getLfasrInfoPath());
                if(id==null||key==null||key.equals("")||id.equals("")) {
                    new InterfaceInformationUtil().OnlineServeInputPrompt(1);
                    return;
                }
                File audio=new File(tempPath+"\\ExtractAudio_"+new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date())+".m4a");
                if(audio.exists()){
                    //检测文件是否存在，若已经存在，删除之
                    /**
                     * 待优化，
                     * 1、根据视频文件名和当前时间来对音频进行命名，保证不重复 ✔
                     * 2、程序退出前删除所有缓存文件
                     * 3、程序进入前检查缓存文件夹，并清空 ✔
                     */
                    audio.delete();
                }
                inputset.setSubtitles(null,1);
                inputset.setVideo(file,Type);
                timelineUtil=new TimelineUtil(inputset.getSubType());
                //启动任务并显示加载窗口
                new LoadUtil().load(getStage(),ModeState,audio);
                return;
        }
    }
}
