package org.fordes.subtitles.view.event;

import javafx.event.Event;
import javafx.event.EventType;
import lombok.Getter;

/**
 * 翻译服务事件
 *
 * @author fordes on 2022/8/1
 */
public class TranslateEvent extends Event {

    public static final EventType<TranslateEvent> EVENT_TYPE = new EventType<>(ANY, "translateEvent");

    public static final String SUCCESS = "翻译完成";

    public static final String FAIL = "翻译失败";

    public TranslateEvent(EventType<? extends Event> eventType) {
        super(eventType);
    }

    @Getter
    private String msg;

    @Getter
    private String detail;

    public TranslateEvent(String msg, String detail) {
        super(EVENT_TYPE);
        this.msg = msg;
        this.detail = detail;
    }
}
