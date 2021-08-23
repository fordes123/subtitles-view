package org.fordes.subview.controller;

import cn.hutool.core.util.ObjectUtil;
import javafx.scene.Node;
import org.springframework.lang.NonNull;

/**
 * @author fordes on 2021/3/1
 */
public class BasicController {

    public Node features;

    public void init(@NonNull Node target) {
        this.features = target;
    }

    public boolean focus(@NonNull Node target) {
        boolean result = false;
        if (ObjectUtil.isNotNull(features)) {
            features.setVisible(false);
            result = features.equals(target);
        }
        target.setVisible(true);
        this.features = target;
        return result;
    }
}
