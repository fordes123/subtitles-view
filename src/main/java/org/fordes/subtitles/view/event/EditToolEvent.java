package org.fordes.subtitles.view.event;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.control.ToggleButton;
import lombok.Getter;
import lombok.NonNull;
import org.fordes.subtitles.view.enums.EditToolEventEnum;
import org.fordes.subtitles.view.model.DTO.Subtitle;
import org.fxmisc.richtext.StyleClassedTextArea;


/**
 * 编辑工具 事件
 *
 * @author fordes on 2022/7/15
 */
public class EditToolEvent extends Event {

    public static final EventType<EditToolEvent> EVENT_TYPE = new EventType<>(ANY, "editToolEvent");

    @Getter
    private final StyleClassedTextArea source;

    @Getter
    private final Subtitle subtitle;

    @Getter
    private final ToggleButton editMode;

    @Getter
    private final EditToolEventEnum type;

    public EditToolEvent(@NonNull StyleClassedTextArea source,
                         @NonNull Subtitle subtitle,
                         @NonNull ToggleButton editMode,
                         @NonNull EditToolEventEnum type) {
        super(EVENT_TYPE);
        this.source = source;
        this.subtitle = subtitle;
        this.editMode = editMode;
        this.type = type;
    }

}
