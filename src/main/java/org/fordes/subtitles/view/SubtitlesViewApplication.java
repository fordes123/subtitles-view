package org.fordes.subtitles.view;

import cn.hutool.core.lang.Singleton;
import cn.hutool.core.util.StrUtil;
import com.jthemedetecor.OsThemeDetector;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fordes.jfx.annotation.JFXApplication;
import org.fordes.jfx.annotation.Tray;
import org.fordes.jfx.core.ProxyApplication;
import org.fordes.jfx.core.ProxyLauncher;
import org.fordes.jfx.core.StageReadyEvent;
import org.fordes.subtitles.view.config.ApplicationConfig;
import org.fordes.subtitles.view.constant.StyleClassConstant;
import org.fordes.subtitles.view.event.ThemeChangeEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.awt.*;
import java.io.IOException;

/**
 * @author fordes
 */
@Slf4j
@AllArgsConstructor
@SpringBootApplication
@JFXApplication(value = "/fxml/main-view.fxml", title = "SubtitlesView Alpha", style = StageStyle.TRANSPARENT,
        css = {"/css/styles.css", "/css/font.css"}, osThemeDetector = true, darkStyleClass = "dark", icons = {"/icon/logo.ico"},
        systemTray = @Tray(value = true, image = "/icon/logo.png", toolTip = "SubtitlesView"))
public class SubtitlesViewApplication extends ProxyApplication {

    private final ApplicationConfig config;

    public static String applicationName;

    private static final long timeMillis = System.currentTimeMillis();

    @Value("${spring.application.name}")
    public void setApplicationName(String applicationName) {
        SubtitlesViewApplication.applicationName = applicationName;
    }

    public static void main(String[] args) {
        ProxyLauncher.run(SubtitlesViewApplication.class, args);
    }

    @Override
    public void handleEvent(StageReadyEvent event) throws IOException, AWTException {
        super.handleEvent(event);
        log.info("{} 启动成功! 耗时: {} ms", applicationName, System.currentTimeMillis() - timeMillis);
    }

    @Override
    public void loadFXMLBefore(Stage stage, JFXApplication property) {
        //stage存入单例池
        Singleton.put(stage);
        super.loadFXMLBefore(stage, property);
    }

    @Override
    public void initAfter(Stage stage) {
        stage.getScene().setFill(null);
        //监听全屏状态，切换样式
        stage.fullScreenProperty().addListener((observableValue, aBoolean, t1) -> {
            stage.getScene().getRoot().getStyleClass().remove(t1 ?
                    StyleClassConstant.NORMAL_SCREEN : StyleClassConstant.FULL_SCREEN);
            stage.getScene().getRoot().getStyleClass().add(t1 ?
                    StyleClassConstant.FULL_SCREEN : StyleClassConstant.NORMAL_SCREEN);
        });
        super.initAfter(stage);
    }

    @Override
    public void registerOsThemeDetector(OsThemeDetector detector, Stage stage, JFXApplication property) {
        Parent root = stage.getScene().getRoot();
        if (StrUtil.isNotEmpty(property.darkStyleClass())) {
            detector.registerListener(isDark -> {
                if (config.getTheme() == null) {
                    switchTheme(detector, root, property, isDark);
                }
            });
            //监听主题切换事件
            stage.addEventHandler(ThemeChangeEvent.EVENT_TYPE, event ->
                    switchTheme(detector, root, property, event.isDark()));
            //初始主题
            switchTheme(detector, root, property, config.getTheme());
        }
    }

    private void switchTheme(OsThemeDetector detector, Parent root, JFXApplication property, Boolean isDark) {
        if (isDark != null) {
            if (isDark) {
                if (!root.getStyleClass().contains(property.darkStyleClass())) {
                    root.getStyleClass().add(property.darkStyleClass());
                }
            } else {
                root.getStyleClass().remove(property.darkStyleClass());
            }
            config.setCurrentTheme(isDark);
        } else {
            switchTheme(detector, root, property, detector.isDark());
        }
    }

}
