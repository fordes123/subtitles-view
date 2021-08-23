package org.fordes.subview;

import cn.hutool.core.util.StrUtil;
import de.felixroske.jfxsupport.AbstractJavaFxApplicationSupport;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.SneakyThrows;
import org.fordes.subview.utils.constants.CommonConstants;
import org.fordes.subview.view.SplashView;
import org.fordes.subview.view.StartView;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Collection;
import java.util.Collections;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class SubtitlesViewApplication extends AbstractJavaFxApplicationSupport {

    private static final Image logoImage = new Image(CommonConstants.APPLICATION_LOGO_ICON_URL.toExternalForm());


    public static void main(String[] args) {
        launch(SubtitlesViewApplication.class, StartView.class, new SplashView(), args);
    }


    @SneakyThrows
    @Override
    public void beforeInitialView(Stage stage, ConfigurableApplicationContext ctx) {
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setTitle("SubView Alpha");
        stage.setFullScreenExitHint(StrUtil.EMPTY);
        stage.getIcons().add(logoImage);

    }



    @Override
    public Collection<Image> loadDefaultIcons() {
        return Collections.singletonList(logoImage);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }

}
