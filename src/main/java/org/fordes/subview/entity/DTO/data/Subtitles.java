package org.fordes.subview.entity.DTO.data;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.fordes.subview.entity.DO.FileAbstract;
import org.fordes.subview.utils.submerge.subtitle.common.TimedTextFile;

/**
 * @author fordes on 2021/6/30
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class Subtitles extends FileAbstract {

    private TimedTextFile timedTextFile;
}
