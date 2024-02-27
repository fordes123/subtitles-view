package org.fordes.subtitles.view.model.DTO;

import lombok.Builder;
import lombok.Data;

/**
 * 翻译
 *
 * @author fordes on 2022/7/27
 */
@Data
@Builder
public class TranslateResult {

    private Integer serial;

    private boolean success;

    private String data;
}
