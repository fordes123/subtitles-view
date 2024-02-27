package org.fordes.subtitles.view.event;

import javafx.event.Event;
import javafx.event.EventType;
import lombok.Getter;

/**
 * loading事件
 *
 * @author fordes on 2022/7/20
 */
public class LoadingEvent extends Event {

    public final static EventType<LoadingEvent> EVENT_TYPE = new EventType<>(ANY, "loadingEvent");

    @Getter
    private final boolean alive;

    public LoadingEvent(boolean alive) {
        super(EVENT_TYPE);
        this.alive = alive;
    }

    public LoadingEvent() {
        super(EVENT_TYPE);
        this.alive = false;
    }
}
