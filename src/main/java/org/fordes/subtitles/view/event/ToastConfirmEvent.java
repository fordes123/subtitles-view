package org.fordes.subtitles.view.event;

import javafx.event.EventType;
import org.fordes.subtitles.view.handler.ToastEventHandler;
import org.fordes.subtitles.view.handler.ToastHandler;

/**
 * @author fordes on 2022/2/2
 */
public class ToastConfirmEvent extends AbstractToastEvent {

    public static final EventType<AbstractToastEvent> TOAST_CONFIRM_EVENT_TYPE = new EventType(TOAST_EVENT_TYPE, "confirmToastEvent");

    private final String caption;

    private final String text;

    private final String perform;

    private final ToastHandler handler;

    public ToastConfirmEvent(String caption, String text, String perform, ToastHandler handler) {
        super(TOAST_CONFIRM_EVENT_TYPE);
        this.caption = caption;
        this.text = text;
        this.perform = perform;
        this.handler = handler;
    }

    public ToastConfirmEvent(String caption, String text) {
        super(TOAST_CONFIRM_EVENT_TYPE);
        this.caption = caption;
        this.text = text;
        this.perform = AbstractToastEvent.CONFIRM;
        this.handler = () -> {};
    }

    @Override
    public void invokeHandler(ToastEventHandler handler) {
        handler.onConfirmEvent(caption, text, perform, this.handler);
    }
}
