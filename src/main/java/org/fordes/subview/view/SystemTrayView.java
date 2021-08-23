package org.fordes.subview.view;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 托盘菜单
 *
 * @author fordes on 2021/4/13
 */
@Component
@Slf4j
@FXMLView(value = "/fxml/SystemTrayMenu.fxml", stageStyle = "TRANSPARENT")
public class SystemTrayView extends AbstractFxmlView {

    public Stage stageTmp;

    @Override
    public void beforeShowView(final Stage stage) {
        stageTmp.show();
        stageTmp.setX(stage.getX());
        stageTmp.setY(stage.getY());


    }

    @Override
    public void afterInitView(final Stage stage) {
        stageTmp = new Stage();
        stageTmp.initStyle(StageStyle.UTILITY);
        stageTmp.setOpacity(0);
        stage.initOwner(stageTmp);
        stage.focusedProperty().addListener((observable, oldValue, newValue) -> {
                    if (!newValue) {
                        stage.hide();
                    }
                }
        );
    }



}
