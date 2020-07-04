package org.fordes.subview.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.fordes.subview.entity.FileSet;
import org.fordes.subview.entity.VoiceService;
import org.fordes.subview.entity.inputSet;
import org.fordes.subview.entity.settingSet;
import org.fordes.subview.main.mainApplication;
import org.fordes.subview.util.ConfigUtil.ConfigPathUtil;
import org.fordes.subview.util.ConfigUtil.ConfigRWUtil;
import org.fordes.subview.util.FileIOUtil.FileUtil;
import org.fordes.subview.util.FileIOUtil.OpenfileUtil;
import org.fordes.subview.util.FileIOUtil.SubFileUtil;
import org.fordes.subview.util.PopUpWindowUtil.LoadUtil;
import org.fordes.subview.util.PopUpWindowUtil.ToastUtil;
import org.fordes.subview.util.SettingUtil.SettingUtil;
import org.fordes.subview.util.SubtitlesUtil.TimelineUtil;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * 程序入口控制器
 * @author Fordes
 */

public class StartController extends RootController implements Initializable {

    @FXML
    private StackPane shadow;
    @FXML
    public GridPane root,content,ServicePanel,FilePanel;
    @FXML
    private Button open,news,apply;
    @FXML
    private ListView dragPanel;
    @FXML
    private ToggleButton pos_home,pos_local,pos_net;
    @FXML
    private ToggleGroup position;
    @FXML
    private Label tips,TutorialsTips;
    @FXML
    private TitlesBarController topController;
    @FXML
    private VBox top;
    @FXML
    private TextArea ServiceTips;
    @FXML
    private ChoiceBox User_type,Service_provider;
    @FXML
    private TextField Service_id,Service_key;

    private Stage stage;
    private double xOffset = 0;
    private double yOffset = 0;
    private int bit = 0;
    private static final double RESIZE_WIDTH = 5.00;
    private static final double MIN_WIDTH = 1060.00;
    private static final double MIN_HEIGHT = 740.00;

    private boolean FullScreen = false;//全屏状态
    private boolean ModeState = false;//模式状态
    private settingSet settings=new settingSet();
    public inputSet inputset=new inputSet();//输入参数集
    public String focus_indicator=new String("");//操作焦点指示器
    //全局主题
    private Object LightTheme=getClass().getClassLoader().getResource("css/startStyle_Light.css").toString();
    private Object DarkTheme=getClass().getClassLoader().getResource("css/startStyle_Dark.css").toString();
    private static String tempPath=new ConfigPathUtil().getTempPath();

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
        //清理缓存
        if(!new FileUtil().delAllFile(tempPath)){
            System.out.println("缓存清理失败");
        }
        //设置事件
        winClose.setOnAction(event -> { onClose(); });
        winMax.setOnAction(event -> { maxWindow(); });
        winMin.setOnAction(event -> { minWindow(); });
        mode.setOnAction(event -> { modeChange(); });

        //读取配置文件
        settings=new ConfigRWUtil().ReadSettingConfig();
        if(settings==null||settings.getBaseSettings().get("Recording").equals("1")){
            settings=new settingSet();
            settings.RecSetting();
            System.out.println("配置文件不存在或不记录配置文件！");
        }
/*        //
        HashMap<String,String> tranConfig=new ConfigRWUtil().getTranConfig();
        inputset.setTranConfigState(!tranConfig.get("type").equals("free"));*/
        //获取主题模式
        if(settings.getBaseSettings().get("TimingState").equals("0"))
            ModeState = settings.getBaseSettings().get("ThemeMode").equals("1") ? true : false;
        else
            ModeState=new SettingUtil().CheckTiming(Integer.valueOf(settings.getBaseSettings().get("ThemeModeHigh")),Integer.valueOf(settings.getBaseSettings().get("ThemeModeLow")));
        //主题初始化
        topController.initialization(1,ModeState);
       // Launcher.controllers.put(this.getClass().getSimpleName(), this);
        InitializeServicePanel();
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
        double nextWidth = primaryStage.getWidth();
        double nextHeight = primaryStage.getHeight();
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
        ModeState=!ModeState;
        inputset.setTheme(ModeState);
        ThemeApply();
    }

    private void ThemeApply(){
        topController.modeApply(ModeState);
        /*移除所有样式表*/
        getStage().getScene().getStylesheets().remove(LightTheme);
        getStage().getScene().getStylesheets().remove(DarkTheme);
        if(!ModeState)//深色->浅色模式
            getStage().getScene().getStylesheets().add(LightTheme.toString());
        else//浅色->深色模式
            getStage().getScene().getStylesheets().add(DarkTheme.toString());
    }

    private void ChangeMode(boolean state) {
        topController.modeApply(state);
        /*移除所有样式表*/
        getStage().getScene().getStylesheets().remove(LightTheme);
        getStage().getScene().getStylesheets().remove(DarkTheme);
        if(!state)//深色->浅色模式
            getStage().getScene().getStylesheets().add(LightTheme.toString());
        else//浅色->深色模式
            getStage().getScene().getStylesheets().add(DarkTheme.toString());
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
    private void LoadInterface(boolean newSubtitle){
        //读取文件信息
        FileSet fileSet=new FileSet(file,new FileUtil().getFileType(file));
        //设置启动参数
        inputset.setCode("UTF-8");
        inputset.setTheme(ModeState);
        inputset.setStage(new Stage());
        timelineUtil=new TimelineUtil(fileSet.getType());

        switch (fileSet.getType()<10?1:2){
            case 1://字幕文件
                inputset.setText(newSubtitle?" ":new SubFileUtil().Read(file,inputset.getCode()));
                inputset.setSubtitles(fileSet);
                inputset.setVideo(null);
                getStage().close();
                try {
                    new mainApplication().start(inputset.getStage());
                } catch (Exception e) {
                    new ToastUtil().toast(getStage(),"未知错误，程序启动失败",ModeState,2000,0,0);
                }
                return;
            case 2:
                HashMap<String,String> lfasrConfig=new ConfigRWUtil().getVoiceConfig();
                if(lfasrConfig==null) {
                    new ToastUtil().toast(getStage(),"检测到未配置语音接口...",ModeState,1000,0,0);
                    ServicePanel.setVisible(true);
                    FilePanel.setVisible(false);
                    return;
                }else {
                    VoiceService voiceService=new VoiceService();
                    voiceService.setFree(true);
                    voiceService.setState(true);
                    voiceService.setId(lfasrConfig.get("id"));
                    voiceService.setKey(lfasrConfig.get("key"));
                    inputset.setVoiceService(voiceService);
                    System.out.println("已存在语音接口配置，id："+voiceService.getId()+",key:"+voiceService.getKey());
                }
                File audio=new File(tempPath+"\\ExtractAudio_"+new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date())+".m4a");
                if(audio.exists()){
                    audio.delete();
                }
                inputset.setSubtitles(new FileSet(null,1));
                inputset.setVideo(fileSet);
                try{
                    new LoadUtil().load(getStage(),ModeState,audio);
                }catch (Exception e){
                    new ToastUtil().toast(getStage(),"转换出错~！请检查网络连接并尝试重试",ModeState,1000,0,0);
                }
                return;
        }
    }

    public settingSet getSettings() {
        return settings;
    }

    public boolean isModeState() {
        return ModeState;
    }

    private void InitializeServicePanel(){
        ServiceTips.setWrapText(true);
        ServiceTips.setFont(new Font("",16));
        ServiceTips.setText("◾ 视频生成字幕为在线服务，需保持联网;\n◾ 目前仅支持讯飞语音接口，其针对个人用户免费，更多接口正在努力适配中;\n◾ 本软件开源且完全免费，服务系第三方提供，任何收费行为与本软件无关.");
        Service_provider.getItems().addAll("科大讯飞","搜狗语音");
        User_type.getItems().addAll("免费用户","付费用户");
        Service_provider.getSelectionModel().selectFirst();
        User_type.getSelectionModel().selectFirst();
        Service_provider.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if(newValue.toString().equals("搜狗语音")){
                    Service_provider.getSelectionModel().selectFirst();
                    new ToastUtil().toast(getStage(),"该接口正在适配中，敬请期待...",ModeState,1000,0,0);
                }
            }
        });
    }

    //接口设置提示教程
    public void openTutorials(MouseEvent event) throws URISyntaxException, IOException {
        Desktop.getDesktop().browse(new URI("https://fordes.top/other/baidu&ifly.html"));
    }
    //应用接口设置
    public void ServiceApply(ActionEvent actionEvent) throws IOException {
        String id = Service_id.getText();
        String key = Service_key.getText();
        if(id!=null&&!id.equals("")&&key!=null&&!key.equals("")&&id.length()==8&&key.length()==32){
            if(new ConfigRWUtil().setLfasr(id,key)) {
                new ToastUtil().toast(getStage(), "接口配置成功~！", ModeState, 1000, 0, 0);
                ServicePanel.setVisible(false);
                FilePanel.setVisible(true);
                LoadInterface(false);
            }
        }else{
            new ToastUtil().toast(getStage(),"接口失效或格式不正确，请检查...",ModeState,1000,0,0);
            Service_id.setText("");
            Service_key.setText("");
        }
    }
}
