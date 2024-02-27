package org.fordes.subtitles.view.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import lombok.Getter;
import org.springframework.stereotype.Component;

/**
 * @author fordes on 2022/4/8
 */
@Component
public class SidebarAfter {

    @FXML
    @Getter
    private ToggleButton mainEditor, syncEditor, export;

    @FXML
    @Getter
    private ToggleGroup itemGroup;
}
