package org.fordes.subtitles.view.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import lombok.Getter;
import org.springframework.stereotype.Component;

/**
 * @author fordes on 2022/1/27
 */
@Component
public class SidebarBefore {

    @FXML
    @Getter
    private ToggleButton quickStart, subtitleSearch, toolBox;

    @FXML
    @Getter
    private ToggleGroup itemGroup;
}
