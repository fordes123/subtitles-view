package org.fordes.subview.view;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

/**
 * 主程序
 *
 * @author fordes on 2020/10/2
 */
@Component
@FXMLView(value = "/fxml/Application.fxml", stageStyle = "TRANSPARENT")
public class ApplicationView extends AbstractFxmlView {

    @SneakyThrows
    @Override
    public void afterInitView(final Stage stage) {
        stage.getScene().setFill(null);
    }
}