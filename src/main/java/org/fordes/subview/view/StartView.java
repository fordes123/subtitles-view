package org.fordes.subview.view;

import de.felixroske.jfxsupport.AbstractFxmlView;
import de.felixroske.jfxsupport.FXMLView;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

/**
 * 启动器
 *
 * @author fordes on 2020/10/8
 */
@Component
@FXMLView(value = "/fxml/StartFrom.fxml")
public class StartView extends AbstractFxmlView {

    @SneakyThrows
    @Override
    public void afterInitView(final Stage stage) {
        stage.getScene().setFill(null);
    }
}