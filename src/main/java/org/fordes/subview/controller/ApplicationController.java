package org.fordes.subview.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.fordes.subview.controller.Setting.SettingPanelController;
import org.fordes.subview.main.Launcher;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class ApplicationController extends RootController implements Initializable{

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
    private TitlesBarController topController;
    /*@FXML
    private homePanelController HomePanelController;
    @FXML
    private SyncPanelController SyncPanelController;
    @FXML
    private TranPanelController TranPanelController;
    @FXML
    private BuildPanelController BuildPanelController;
    @FXML
    private AboutPanelController AboutPanelController;
    @FXML
    private SettingPanelController settingPanelController;*/
    @FXML
    private VBox top;

    private Stage stage;
    private double xOffset = 0;
    private double yOffset = 0;
    private int bit = 0;//left,right,top,bottom
    private static final double RESIZE_WIDTH = 5.00;
    private static final double MIN_WIDTH = 1360.00;
    private static final double MIN_HEIGHT = 860.00;

    private boolean FullScreen = false;
    private boolean ModeState = false;
    private StartController startController;
    //private TitlesBarController titlesBarController =(TitlesBarController) Context.controllers.get(TitlesBarController.class.getSimpleName());
    private HomePanelController homePanelController;
    private SyncPanelController syncPanelController;
    private TranPanelController tranPanelController;
    private BuildPanelController buildPanelController;
    private AboutPanelController aboutPanelController;
    private SettingPanelController settingPanelController;
    private Object LightTheme=getClass().getClassLoader().getResource("css/mainStyle_Light.css").toString();
    private Object DarkTheme=getClass().getClassLoader().getResource("css/mainStyle_Dark.css").toString();
    private Button winMax,winMin,mode,winClose;

    /**
     *初始化任务
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //获取控制器
        startController = (StartController) Launcher.controllers.get(StartController.class.getSimpleName());
        homePanelController = (HomePanelController) Launcher.controllers.get(HomePanelController.class.getSimpleName());
        syncPanelController = (SyncPanelController) Launcher.controllers.get(SyncPanelController.class.getSimpleName());
        tranPanelController = (TranPanelController) Launcher.controllers.get(TranPanelController.class.getSimpleName());
        buildPanelController = (BuildPanelController) Launcher.controllers.get(BuildPanelController.class.getSimpleName());
        aboutPanelController  = (AboutPanelController) Launcher.controllers.get(AboutPanelController.class.getSimpleName());
        settingPanelController = (SettingPanelController) Launcher.controllers.get(SettingPanelController.class.getSimpleName());
        //加载组件
        top= topController.getTop();
        mode = topController.getMode();
        winClose = topController.getWinClose();
        winMin = topController.getWinMin();
        winMax = topController.getWinMax();
        root.setOnMousePressed(event -> {
            event.consume();
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

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
        mode.setOnAction(event -> { ThemeModeChange(); });
        ModeState= startController.inputset.getTheme();
        ThemeApply(ModeState);
        loadHome(null);
        //shadow.setPickOnBounds(false);
        /*shadow.setMouseTransparent(true);*/
        //标题栏主题初始化
        topController.initialization(0,ModeState);
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
        double nextWidth = stage.getWidth();
        double nextHeight = stage.getHeight();
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
        syncPanelController.stopManualSync();//终止返听任务
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
     * 切换颜色模式
     */
    private void ThemeModeChange(){
        ModeState=!ModeState;
        startController.inputset.setTheme(ModeState);
        startController.getSettings().getBaseSettings().put("ThemeMode",ModeState?"1":"0");
        ThemeApply(ModeState);
    }

    /**
     * 颜色模式应用
     */
    public void ThemeApply(boolean modeState) {
        topController.modeApply(modeState);
        syncPanelController.ThemeApply(modeState);
        homePanelController.ThemeApply(modeState);
        tranPanelController.ThemeApply(modeState);
        buildPanelController.ThemeApply(modeState);
        aboutPanelController.ThemeApply(modeState);
        settingPanelController.ThemeApply(modeState);
        shadow.getStylesheets().remove(LightTheme);
        shadow.getStylesheets().remove(DarkTheme);
        if(!modeState)
            shadow.getStylesheets().add(LightTheme.toString());
        else
            shadow.getStylesheets().add(DarkTheme.toString());

    }

    /**
     * 窗口最大化标准化切换
     */
    private void FullScreenChange(){
        winMax.getStyleClass().removeAll("winMax","winMax_Full");
        shadow.getStyleClass().removeAll("shadow","shadow_full");
        if(FullScreen) {
            winMax.getStyleClass().add("winMax");
            shadow.getStyleClass().add("shadow");
            root.setPadding(new Insets(30,30,30,30));
        }else{
            winMax.getStyleClass().add("winMax_Full");
            shadow.getStyleClass().add("shadow_full");
            root.setPadding(new Insets(0,0,0,0));
        }

        FullScreen = !FullScreen;
    }

    public void loadHome(ActionEvent actionEvent) {
        home.setSelected(true);
        function.selectToggle(home);
        if(i_home.isVisible())
            return;
        setIndicator();
        i_home.setVisible(true);
        HomePanel.setVisible(true);
        homePanelController.DoBasicEdit(null);
    }


    public void loadSync(ActionEvent actionEvent) {
        sync.setSelected(true);
        function.selectToggle(sync);
        if(i_sync.isVisible())
            return;
        setIndicator();
        i_sync.setVisible(true);
        SyncPanel.setVisible(true);
        syncPanelController.onManualSync(null);
    }

    public void loadTranslation(ActionEvent actionEvent) {
        translation.setSelected(true);
        function.selectToggle(translation);
        if(i_tran.isVisible())
            return;
        setIndicator();
        i_tran.setVisible(true);
        TranPanel.setVisible(true);
        tranPanelController.onTran_Online(null);
    }

    public void loadBuild(ActionEvent actionEvent) {
        build.setSelected(true);
        function.selectToggle(build);
        if(i_build.isVisible())
            return;
        setIndicator();
        i_build.setVisible(true);
        BuildPanel.setVisible(true);
        buildPanelController.onBuild_Manual(null);
    }

    public void loadAbout(ActionEvent actionEvent) {
        about.setSelected(true);
        function.selectToggle(about);
        if(i_about.isVisible())
            return;
        setIndicator();
        i_about.setVisible(true);
        AboutPanel.setVisible(true);
        aboutPanelController.onInfo(null);
    }

    public void loadSetting(ActionEvent actionEvent) {
        setting.setSelected(true);
        function.selectToggle(setting);
        if(i_setting.isVisible())
            return;
        setIndicator();
        i_setting.setVisible(true);
        SettingPanel.setVisible(true);
        settingPanelController.onBaseSetting(null);
    }

    public void onLogo(MouseEvent mouseEvent) throws URISyntaxException, IOException {
        Desktop.getDesktop().browse(new URI("https://fordes.top"));
    }
}
