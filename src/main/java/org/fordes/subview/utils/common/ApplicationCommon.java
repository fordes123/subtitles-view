package org.fordes.subview.utils.common;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import org.fordes.subview.controller.SystemTrayMenuController;
import org.fordes.subview.entity.DTO.data.Subtitles;
import org.fordes.subview.entity.DTO.data.Video;
import org.fordes.subview.enums.ApplicationModeEnum;
import org.fordes.subview.utils.constants.CommonConstants;
import org.fordes.subview.utils.constants.StyleConstants;
import org.fordes.subview.view.SystemTrayView;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.util.Map;

/**
 * 单例模式初始化程序
 *
 * @author fordes on 2020/10/5
 */
public class ApplicationCommon {

    private volatile static ApplicationCommon instance;

    private ApplicationCommon () {}

    public static ApplicationCommon getInstance() {
        if (instance == null) {
            synchronized (ApplicationCommon.class) {
                if (instance == null) {
                    instance = new ApplicationCommon();
                }
            }
        }
        return instance;
    }

    private static ApplicationModeEnum modeEnum;

    @Setter
    public static boolean styleState = false;

    private static Pane root;

    private static Stage stage;

    private static double xOffset = 0;
    private static double yOffset = 0;
    private static int bit = 0;
    private final static double RESIZE_WIDTH = 5.00;
    private static boolean isFull = false;

    @Getter
    @Setter
    private Subtitles subtitles;

    @Getter
    @Setter
    private Video video;

    @Getter
    private TrayIcon trayIcon = null;

    private SystemTrayMenuController trayMenuController;

    private SystemTrayView systemTrayView;

    private ChangeListener<String> editorListener;

    public void initialize(ApplicationModeEnum modeEnum, final Pane root, EventHandler<MouseEvent> eventHandler) throws Exception {
        trayMenuController.getSetting().setOnMouseClicked(eventHandler);
        initialize(modeEnum, root);
    }

    public void initialize(ApplicationModeEnum modeEnum, final Pane root) throws Exception {
        ApplicationCommon.modeEnum = modeEnum;
        ApplicationCommon.root = root;
        ApplicationStyleRefresh();
        //注册窗口与标题栏事件
        root.setOnMousePressed(this::mousePressedHandle);
        root.setOnMouseMoved(this::mouseMoveHandle);
        root.setOnMouseDragged(this::mouseDraggedHandle);
        root.lookup(StyleConstants.TOP_HASH).setOnMouseDragged(this::titlesMouseDraggedHandle);
        root.lookup(StyleConstants.TOP_HASH).setOnMouseClicked(this::onTitlesClick);
        ((Button)root.lookup(StyleConstants.TITLES_BAR_CLOSED_HASH)).setOnAction(this::onClose);
        ((Button)root.lookup(StyleConstants.TITLES_BAR_MAXIMIZE_HASH)).setOnAction(this::onMaxWindow);
        ((Button)root.lookup(StyleConstants.TITLES_BAR_MINIMIZE_HASH)).setOnAction(this::onMinWindow);
        ((Button)root.lookup(StyleConstants.TITLES_BAR_MODE_HASH)).setOnAction(this::onModeChange);
        //标题栏样式
        if (modeEnum.equals(ApplicationModeEnum.START)) {
            root.lookup(StyleConstants.TOP_HASH).getStyleClass().add(StyleConstants.TITLES_BACKGROUND_PURE);
            ((Label)root.lookup(StyleConstants.TITLES_BAR_TITLE_HASH)).setText(StrUtil.concat(false,
                    ApplicationConfig.getInstance().getApplicationInfo().getName(), StrUtil.SPACE, ApplicationConfig.getInstance().getApplicationInfo().getType()));
        }else {
            root.lookup(StyleConstants.TOP_HASH).getStyleClass().add(StyleConstants.TITLES_BACKGROUND_TRANSPARENT);

        }
    }

    public Scene getScene(){
        return root.getScene();
    }

    public Stage getStage() {
        return ObjectUtil.isNull(stage)?
                (Stage) getScene().getWindow(): stage;
    }


    /**
     * 刷新当前主题
     */
    public void ApplicationStyleRefresh(){
        root.getStylesheets().remove((styleState ? modeEnum.getTheme_light(): modeEnum.getTheme_dark()));
        root.getStylesheets().add((styleState ? modeEnum.getTheme_dark() : modeEnum.getTheme_light()));
    }

    //窗口关闭（至托盘）
    private void onClose(ActionEvent actionEvent) {
        if (ApplicationConfig.getInstance().getApplicationSettings().getQuick_exit()) {
            systemExit();
        }
        stage.hide();
    }

    /**
     * 托盘点击事件
     */
    private void showStage(Stage stage) {
        if (stage.isIconified()) {
            Platform.runLater(() -> stage.setIconified(false));
        }

        if (!stage.isShowing()) {
            Platform.runLater(() -> {
                stage.show();
                stage.toFront();
            });
        }


    }

    public void showStage() {
        showStage(getStage());
    }

    //结束程序
    public void systemExit() {
        System.exit(0);
    }

    //窗口 最大化/标准化切换
    private void onMaxWindow(ActionEvent actionEvent) {
        root.getStyleClass().removeAll(StyleConstants.ROOT_NORMAL, StyleConstants.ROOT_FULL);
        root.getStyleClass().add(isFull? StyleConstants.ROOT_NORMAL :StyleConstants.ROOT_FULL);
        isFull = !isFull;
        getStage().setFullScreen(isFull);
    }

    //窗口最小化
    public void onMinWindow(ActionEvent actionEvent) {
        getStage().setIconified(true);
    }

    //标题栏双击
    public void onTitlesClick(MouseEvent event) {
        if (event.getButton().equals(MouseButton.PRIMARY)) {
            if (event.getClickCount() == 2) {
                onMaxWindow(null);
            }}
    }

    //获取鼠标坐标位置，进行光标变换
    private void mouseMoveHandle(MouseEvent event) {
        event.consume();
        double x = event.getSceneX();
        double y = event.getSceneY();
        stage = getStage();
        double width = stage.getWidth() - 20;
        double height = stage.getHeight() - 20;
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

    //处理窗口缩放
    private void mouseDraggedHandle(MouseEvent event) {
        event.consume();
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
        if (nextWidth <= modeEnum.getWidth()) {
            nextWidth = modeEnum.getWidth();
        }
        if (nextHeight <= modeEnum.getHeight()) {
            nextHeight = modeEnum.getHeight();
        }
        stage.setX(nextX);
        stage.setY(nextY);
        stage.setWidth(nextWidth);
        stage.setHeight(nextHeight);
    }

    //标题栏拖动事件
    private void titlesMouseDraggedHandle(MouseEvent mouseEvent) {
        mouseEvent.consume();
        stage.setX(mouseEvent.getScreenX() - xOffset);
        stage.setY(mouseEvent.getScreenY() - yOffset);

        if (isFull) {
            onMaxWindow(null);


        }
    }

    //获取光标按下位置
    private void mousePressedHandle(MouseEvent mouseEvent) {
        mouseEvent.consume();
        xOffset = mouseEvent.getSceneX();
        yOffset = mouseEvent.getSceneY();

    }

    //颜色模式切换
    private void onModeChange(ActionEvent actionEvent) {
        styleState = !styleState;
        ApplicationStyleRefresh();
    }

    //绑定导航栏按钮与指示器
    public void setIndicator(Map<GridPane, Node> targetMap) {
        targetMap.forEach((k,v)->{
            k.visibleProperty().addListener((observable, oldValue, newValue) -> {
                if (v.getClass().equals(ToggleButton.class)){
                    ((ToggleButton)v).setSelected(newValue);
                }else {
                    v.setVisible(newValue);
                }
            });
        });
    }

    //初始化系统托盘
    public void initSysTray(final SystemTrayView systemTrayView, final SystemTrayMenuController trayMenuController) throws AWTException {
        if (SystemTray.isSupported()) {
            this.trayMenuController = trayMenuController;
            trayIcon = new TrayIcon(Toolkit.getDefaultToolkit().getImage(CommonConstants.APPLICATION_TRAY_ICON_URL));
            trayIcon.setToolTip(ApplicationConfig.getInstance().getApplicationInfo().getName());
            trayIcon.setImageAutoSize(true);
            trayIcon.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (e.getButton() == java.awt.event.MouseEvent.BUTTON1) {
                        //左键点击
                        showStage(stage);
                    } else if (e.getButton() == java.awt.event.MouseEvent.BUTTON3) {
                        //右键点击
                        Platform.runLater(() -> {
                            systemTrayView.showView(Modality.NONE);
                            trayMenuController.show(e.getX(), e.getY());
                        });

                    }
                }
            });
            SystemTray.getSystemTray().add(trayIcon);
            systemTrayView.initView(Modality.NONE);
            Platform.setImplicitExit(false);
        }
    }


}