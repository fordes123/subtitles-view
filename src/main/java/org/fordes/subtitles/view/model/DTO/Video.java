package org.fordes.subtitles.view.model.DTO;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.fordes.subtitles.view.model.PO.FileRecord;

/**
 * 视频类
 *
 * @author fordes on 2020/12/4
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class Video extends FileRecord {

    /**
     * 帧宽
     */
    private int width;

    /**
     * 帧高
     */
    private int height;
}
