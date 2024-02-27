package org.fordes.subtitles.view.handler;

import javafx.event.EventHandler;
import org.fordes.subtitles.view.event.AbstractToastEvent;

/**
 * toast事件抽象
 *
 * @author fordes on 2022/2/2
 */
public abstract class ToastEventHandler implements EventHandler<AbstractToastEvent> {

    /**
     * 确认型 toast事件
     * @param caption   标题
     * @param text  内容
     * @param perform   确认按钮文本
     * @param handler   回调
     */
    public abstract void onConfirmEvent(String caption, String text, String perform, ToastHandler handler);

    /**
     * 选择型 toast事件
     * @param caption   标题
     * @param text  内容
     * @param choose1  选择1
     * @param choose2  选择2
     * @param handler1  选择1回调
     * @param handler2  选择2回调
     */
    public abstract void onChooseEvent(String caption, String text, String choose1, String choose2, ToastHandler handler1, ToastHandler handler2);

    @Override
    public void handle(AbstractToastEvent event) {
        event.invokeHandler(this);
    }
}
