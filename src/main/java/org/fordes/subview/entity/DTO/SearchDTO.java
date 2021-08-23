package org.fordes.subview.entity.DTO;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author fordes on 2021/3/5
 */
@Data
@Accessors(chain = true)
public class SearchDTO {

    @Value(value = "false")
    private boolean success;

    private int cursor_start;

    private int cursor_end;

    private String content;
}
