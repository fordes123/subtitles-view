package org.fordes.subtitles.view.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Singleton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import org.fordes.subtitles.view.constant.CommonConstant;
import org.fordes.subtitles.view.constant.StyleClassConstant;
import org.fordes.subtitles.view.enums.FileEnum;
import org.fordes.subtitles.view.event.FileOpenEvent;
import org.fordes.subtitles.view.utils.FileUtils;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * @author fordes on 2022/2/6
 */
@Slf4j
@Component
public class QuickStart {
    @FXML
    private Label clues;

    @FXML
    private GridPane root;

    private static File dragFile;

    private static final String UNSUPPORTED_FILE_TYPE = "不支持的文件类型";

    private static final String DRAG_SUPPORT = "松手以打开文件";

    private static final String TIPS_DEFAULT = "拖放或选择文件以继续";

    private static final String OPEN_FILE_ERROR = "打开文件出错";

    @FXML
    private void chooseFile(ActionEvent event) {
        File file = FileUtils.chooseFile(CommonConstant.TITLE_ALL_FILE, FileEnum.values())
                .showOpenDialog(Singleton.get(Stage.class));

        //读取文件信息
        if (FileUtil.exist(file) && FileEnum.isSupport(FileUtil.getSuffix(file))) {
            Singleton.get(Stage.class).fireEvent(new FileOpenEvent(dragFile));
        } else {
            root.getStyleClass().clear();
            clues.setText(TIPS_DEFAULT);
        }
        event.consume();
    }

    @FXML
    private void onDragOver(DragEvent dragEvent) {
        Dragboard db = dragEvent.getDragboard();
        if (db.hasFiles()) {
            dragFile = db.getFiles().get(0);
            if (FileUtil.exist(dragFile) && FileEnum.isSupport(FileUtil.getSuffix(dragFile))) {
                dragEvent.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                clues.setText(DRAG_SUPPORT);
                root.getStyleClass().add(StyleClassConstant.QUICK_START_FILE_CHOOSE_SUCCESS);
            } else {
                clues.setText(UNSUPPORTED_FILE_TYPE);
                root.getStyleClass().add(StyleClassConstant.QUICK_START_FILE_CHOOSE_WARNING);
                dragFile = null;
            }
        }
        dragEvent.consume();
    }

    @FXML
    private void onDragExited(DragEvent dragEvent) {
        clues.setText(TIPS_DEFAULT);
        root.getStyleClass().clear();
        dragEvent.consume();
    }

    @FXML
    private void onDragDropped(DragEvent dragEvent) {
        try {
            if (dragFile != null) {
                Singleton.get(Stage.class).fireEvent(new FileOpenEvent(dragFile));
            }
        } catch (Exception e) {
            clues.setText(OPEN_FILE_ERROR);
            root.getStyleClass().add(StyleClassConstant.QUICK_START_FILE_CHOOSE_ERROR);
        } finally {
            dragEvent.consume();
        }
    }
}
