package org.fordes.subview.entity.DTO.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.fordes.subview.entity.DO.FileAbstract;

/**
 * 视频类
 *
 * @author fordes on 2020/12/4
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class Video extends FileAbstract {

    /**
     * 帧宽
     */
    private int width;

    /**
     * 帧高
     */
    private int height;
}
