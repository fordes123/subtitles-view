package org.fordes.subtitles.view.model.DTO;

/**
 * @author fordes on 2022/7/19
 */

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.fordes.subtitles.view.model.PO.FileRecord;
import org.fordes.subtitles.view.utils.submerge.subtitle.common.TimedTextFile;

/**
 * @author fordes on 2021/6/30
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class Subtitle extends FileRecord {

    private TimedTextFile timedTextFile;
}
