package org.fordes.subtitles.view.model.search;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author fordes on 2022/2/12
 */
@Data
@Accessors(chain = true)
public class Engine {

    private String id;

    private String name;

    private String url;

    private Cases cases;
}
