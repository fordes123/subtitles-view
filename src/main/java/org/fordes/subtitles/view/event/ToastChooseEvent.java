package org.fordes.subtitles.view.event;

import javafx.event.EventType;
import org.fordes.subtitles.view.handler.ToastEventHandler;
import org.fordes.subtitles.view.handler.ToastHandler;

/**
 * toast选择事件
 *
 * @author fordes on 2022/2/2
 */
public class ToastChooseEvent extends AbstractToastEvent {

    public static final EventType<AbstractToastEvent> TOAST_CHOOSE_EVENT_TYPE = new EventType(TOAST_EVENT_TYPE, "toastChooseEvent");

    private final String caption;

    private final String text;

    private final String choose1;

    private final String choose2;

    private final ToastHandler handler1;

    private final ToastHandler handler2;

    public ToastChooseEvent(String caption, String text, String choose1, String choose2, ToastHandler handler1, ToastHandler handler2) {
        super(TOAST_CHOOSE_EVENT_TYPE);
        this.caption = caption;
        this.text = text;
        this.choose1 = choose1;
        this.choose2 = choose2;
        this.handler1 = handler1;
        this.handler2 = handler2;
    }

    public ToastChooseEvent(String caption, String text, String choose1, ToastHandler handler1) {
        super(TOAST_CHOOSE_EVENT_TYPE);
        this.caption = caption;
        this.text = text;
        this.choose1 = choose1;
        this.choose2 = AbstractToastEvent.CANCEL;
        this.handler1 = handler1;
        this.handler2 = () -> {};
    }

    @Override
    public void invokeHandler(ToastEventHandler handler) {
        handler.onChooseEvent(caption, text, choose1, choose2, handler1, handler2);
    }
}
