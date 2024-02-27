package org.fordes.subtitles.view.event;

import javafx.event.Event;
import javafx.event.EventType;
import org.fordes.subtitles.view.handler.ToastEventHandler;

/**
 * @author fordes on 2022/2/2
 */
public abstract class AbstractToastEvent extends Event {

    public static final String CONFIRM = "确定";

    public static final String CANCEL = "取消";

    public static final EventType<AbstractToastEvent> TOAST_EVENT_TYPE = new EventType(ANY);

    public AbstractToastEvent(EventType<? extends Event> eventType) {
        super(eventType);
    }

    public abstract void invokeHandler(ToastEventHandler handler);
}
