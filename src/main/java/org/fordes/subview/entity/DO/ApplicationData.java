package org.fordes.subview.entity.DO;

import lombok.Data;
import org.fordes.subview.entity.DTO.data.Subtitles;
import org.fordes.subview.entity.DTO.data.Video;

/**
 * @author fordes on 2021/6/30
 */
@Data
public class ApplicationData {

    private Subtitles subtitles;

    private Video video;
}
