package org.fordes.subtitles.view.handler;

import javafx.event.EventHandler;
import org.fordes.subtitles.view.event.FileOpenEvent;

/**
 * @author fordes on 2022/4/8
 */
public abstract class FileOpenEventHandler implements EventHandler<FileOpenEvent> {

    public final static String ERROR_MESSAGE = "文件打开失败";

}
