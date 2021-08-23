package org.fordes.subview.entity.DTO.search;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.File;
import java.util.Set;

/**
 * @author fordes on 2021/7/20
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class SearchItem {

    private Boolean is_down;

    private String title;

    private String secondary;

    private String img_url;

    private Set<String> tags;

    private String file_name;

    private File file;

    private Object request;
}
