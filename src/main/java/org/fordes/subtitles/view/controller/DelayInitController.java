package org.fordes.subtitles.view.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 控制器抽象，继承并实现delayInit()方法即可在面板首次显示时进行初始化操作
 *
 * @author fordes on 2022/4/22
 */
@Component
public abstract class DelayInitController implements Initializable {

    @FXML
    public Pane root;

    @Resource
    public ThreadPoolExecutor globalExecutor;

    private boolean isInit = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        root.visibleProperty().addListener((observableValue, aBoolean, t1) -> {
            if (!isInit && t1) {
                delay();
                isInit = true;
            }
        });
        globalExecutor.execute(this::async);
    }

    public Scene getScene() {
        return root.getScene();
    }

    /**
     * 懒加载，在面板首次显示时执行
     */
    public void delay() {};

    /**
     * 异步方法，在线程池中执行，避免主线程阻塞
     */
    public void async() {};
}
