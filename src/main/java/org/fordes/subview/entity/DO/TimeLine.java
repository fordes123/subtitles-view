package org.fordes.subview.entity.DO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 时间轴
 *
 * @author fordes on 2020/12/15
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
public class TimeLine {

    /**
     * 开始时间，毫秒时间戳
     */
    private Long start;

    /**
     * 结束时间，毫秒时间戳
     */
    private Long end;


}
