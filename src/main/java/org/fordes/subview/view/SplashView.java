package org.fordes.subview.view;

import de.felixroske.jfxsupport.SplashScreen;
import org.springframework.stereotype.Component;

/**
 * 默认启动页面隐藏
 *
 * @author Fordes on 2021/4/23
 */
@Component
public class SplashView extends SplashScreen {

    @Override
    public boolean visible() {
        return false;
    }
}
