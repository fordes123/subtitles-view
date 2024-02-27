package org.fordes.subtitles.view.utils.submerge.subtitle.lrc;

import cn.hutool.core.util.StrUtil;
import lombok.NoArgsConstructor;
import org.fordes.subtitles.view.utils.submerge.subtitle.common.SubtitleLine;

import java.util.List;

/**
 * @author fordes on 2022/7/21
 */
@NoArgsConstructor
public class LRCLine extends SubtitleLine<LRCTime> {

    private static final long serialVersionUID = -5787808773967579723L;


    public LRCLine(LRCTime time, List<String> textLines) {
        this.time = time;
        this.textLines = textLines;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.time == null ? StrUtil.EMPTY: this.time);
        textLines.forEach(line -> sb.append(line).append(StrUtil.CR));
        return sb.toString();
    }
}
