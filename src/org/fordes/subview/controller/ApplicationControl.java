package org.fordes.subview.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import org.fordes.subview.main.Launcher;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

public class ApplicationControl implements Initializable {

    @FXML
    private Label logo;
    @FXML
    private StackPane shadow;
    @FXML
    private GridPane root,content,HomePanel,SyncPanel,TranPanel,BuildPanel,AboutPanel,SettingPanel,side;
    @FXML
    private ToggleGroup function;
    @FXML
    private ToggleButton home,setting,build,about,translation,sync;
    @FXML
    private Circle i_home,i_sync,i_tran,i_build,i_about,i_setting;
    @FXML
    private titlesBarControl topController;
    @FXML
    private homePanelControl HomePanelController;
    @FXML
    private SyncPanelControl SyncPanelController;
    @FXML
    private TranPanelControl TranPanelController;
    @FXML
    private BuildPanelControl BuildPanelController;
    @FXML
    private AboutPanelControl AboutPanelController;
    @FXML
    private SettingPanelControl SettingPanelController;
    @FXML
    private VBox top;

    private Stage stage;
    private double xOffset = 0;
    private double yOffset = 0;
    private int bit = 0;//left,right,top,bottom
    private static final double RESIZE_WIDTH = 5.00;
    private static final double MIN_WIDTH = 1360.00;
    private static final double MIN_HEIGHT = 860.00;

    private boolean FullScreen = false;//全屏状态指示器
    private boolean ModeState = false;//模式状态指示器
    private startControl controller= (startControl) Launcher.controllers.get(startControl.class.getSimpleName());
    //全局主题
    private Object LightTheme=getClass().getClassLoader().getResource("resources/css/mainStyle_Light.css").toString();
    private Object DarkTheme=getClass().getClassLoader().getResource("resources/css/mainStyle_Dark.css").toString();
    private Button winMax,winMin,mode,winClose;


    /**
     *初始化任务
     * @param location
     * @param resources
     */
    @Override
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
        Tooltip.install(logo,new Tooltip("想要看看作者的博客吗？点一下试试~"));

        winClose.setOnAction(event -> { onClose(); });
        winMax.setOnAction(event -> { maxWindow(); });
        winMin.setOnAction(event -> { minWindow(); });
        mode.setOnAction(event -> { modeChange(); });
        //标题栏初始化
        topController.initialization(2);
        //默认启动项
        loadHome(null);
        //还原设置
        ModeState=!controller.inputset.getTheme();
        modeChange();
        //边框点击穿透
        //shadow.setPickOnBounds(false);
        /*shadow.setMouseTransparent(true);*/

    }


    /**
     * 获取鼠标坐标位置，进行光标变换
     * @param event
     */
    private void mouseMoveHandle(MouseEvent event) {
        event.consume();
        double x = event.getSceneX();
        double y = event.getSceneY();
        stage = getStage();
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
        //Stage primaryStage = getStage();
        double x = event.getSceneX();
        double y = event.getSceneY();
        double nextX = stage.getX();
        double nextY = stage.getY();
        double nextWidth = stage.getWidth()-30;
        double nextHeight = stage.getHeight()-30;
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
        stage.setX(nextX);
        stage.setY(nextY);
        stage.setWidth(nextWidth);
        stage.setHeight(nextHeight);
        //HomePanelController.zoom();
    }


    /**
     * 获取窗口
     * @return
     */
    private Stage getStage() {
        if (stage == null)
            stage = (Stage) root.getScene().getWindow();
        return stage;
    }

    //清除项目设置焦点指示器、隐藏所有窗格组件
    private void setIndicator(){
        i_build.setVisible(false);
        i_tran.setVisible(false);
        i_sync.setVisible(false);
        i_home.setVisible(false);
        i_about.setVisible(false);
        i_setting.setVisible(false);
        HomePanel.setVisible(false);
        SyncPanel.setVisible(false);
        TranPanel.setVisible(false);
        BuildPanel.setVisible(false);
        SettingPanel.setVisible(false);
        AboutPanel.setVisible(false);
        SyncPanelController.stopManualSync();//终止返听任务
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
        //HomePanelController.zoom();
    }

    /**
     * 窗口最小化
     */
    private void minWindow() { stage.setIconified(true);}

    /**
     * 颜色模式切换
     */
    public void modeChange() {
        topController.modeChange(ModeState);
        HomePanelController.modeChange(ModeState);
        SyncPanelController.modeChange(ModeState);
        TranPanelController.modeChange(ModeState);
        BuildPanelController.modeChange(ModeState);
        AboutPanelController.modeChange(ModeState);
        SettingPanelController.modeChange(ModeState);
        /*移除所有样式表*/
        shadow.getStylesheets().remove(LightTheme);
        shadow.getStylesheets().remove(DarkTheme);
        if(ModeState)//深色->浅色模式
            shadow.getStylesheets().add(LightTheme.toString());
        else//浅色->深色模式
            shadow.getStylesheets().add(DarkTheme.toString());
        ModeState=!ModeState;
        controller.inputset.setTheme(ModeState);
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
            root.setPadding(new javafx.geometry.Insets(30,30,30,30));
//            HomePanelController.getTimeAdjPanel().setDividerPositions(HomePanelController.DividerPositions);
        }else{//窗口标准化->最大化
            winMax.getStyleClass().add("winMax_Full");
            shadow.getStyleClass().add("shadow_full");
            root.setPadding(new Insets(0,0,0,0));
//            HomePanelController.DividerPositions=HomePanelController.getTimeAdjPanel().getDividerPositions()[0];
//            HomePanelController.getTimeAdjPanel().setDividerPositions(0.2348993288590604);
        }

        FullScreen = !FullScreen;
    }

    /*主页选项*/
    public void loadHome(ActionEvent actionEvent) {
        home.setSelected(true);
        function.selectToggle(home);
        if(i_home.isVisible())
            return;
        setIndicator();
        i_home.setVisible(true);
        HomePanel.setVisible(true);
        HomePanelController.DoBasicEdit(null);

    }

    /*同步返听选项*/
    public void loadSync(ActionEvent actionEvent) {
        sync.setSelected(true);
        function.selectToggle(sync);
        if(i_sync.isVisible())
            return;
        setIndicator();
        i_sync.setVisible(true);
        SyncPanel.setVisible(true);
        SyncPanelController.onManualSync(null);
    }

    /*在线翻译选项*/
    public void loadTranslation(ActionEvent actionEvent) {
        translation.setSelected(true);
        function.selectToggle(translation);
        if(i_tran.isVisible())
            return;
        setIndicator();
        i_tran.setVisible(true);
        TranPanel.setVisible(true);
        TranPanelController.onTran_Online(null);
    }

    /*构建导出选项*/
    public void loadBuild(ActionEvent actionEvent) {
        build.setSelected(true);
        function.selectToggle(build);
        if(i_build.isVisible())
            return;
        setIndicator();
        i_build.setVisible(true);
        BuildPanel.setVisible(true);
        BuildPanelController.onBuild_Manual(null);
    }

    /*关于程序选项*/
    public void loadAbout(ActionEvent actionEvent) {
        about.setSelected(true);
        function.selectToggle(about);
        if(i_about.isVisible())
            return;
        setIndicator();
        i_about.setVisible(true);
        AboutPanel.setVisible(true);
        AboutPanelController.onInfo(null);
    }

    /*偏好设置选项*/
    public void loadSetting(ActionEvent actionEvent) {
        setting.setSelected(true);
        function.selectToggle(setting);
        if(i_setting.isVisible())
            return;
        setIndicator();
        i_setting.setVisible(true);
        SettingPanel.setVisible(true);
    }

    /*Logo点击事件*/
    public void onLogo(MouseEvent mouseEvent) throws URISyntaxException, IOException {
        Desktop.getDesktop().browse(new URI("https://fordes.top"));
    }
}
