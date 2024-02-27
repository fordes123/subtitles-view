package org.fordes.subtitles.view.event;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Singleton;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.stage.Stage;
import lombok.Getter;
import org.fordes.subtitles.view.model.PO.FileRecord;
import org.fordes.subtitles.view.utils.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * @author fordes on 2022/4/8
 */
public class FileOpenEvent extends Event {

    public static final EventType<FileOpenEvent> FILE_OPEN_EVENT = new EventType(ANY, "fileOpenEvent");

    @Getter
    private FileRecord record;

    public FileOpenEvent(File openFile) {
        super(FILE_OPEN_EVENT);
        try {
            this.record = FileUtils.readFileInfo(openFile);
        }catch (IOException e) {
            Singleton.get(Stage.class).fireEvent(new ToastConfirmEvent("出错了","打开文件失败！"));
        }
    }

    public FileOpenEvent(String filePath) {
        super(FILE_OPEN_EVENT);
        try {
            this.record = FileUtils.readFileInfo(FileUtil.file(filePath));
        }catch (IOException e) {
            Singleton.get(Stage.class).fireEvent(new ToastConfirmEvent("出错了","打开文件失败！"));
        }
    }
}
