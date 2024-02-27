package org.fordes.subtitles.view.event;

import javafx.event.Event;
import javafx.event.EventType;

/**
 * 主题切换事件
 *
 * @author fordes on 2022/4/13
 */
public class ThemeChangeEvent extends Event {

    public static final EventType<ThemeChangeEvent> EVENT_TYPE = new EventType(ANY, "themeChangeEvent");

    private Boolean dark;

    public Boolean isDark() {
        return dark;
    }

    public ThemeChangeEvent(Boolean dark) {
        super(EVENT_TYPE);
        this.dark = dark;
    }
}
