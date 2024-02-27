package org.fordes.subtitles.view.utils.submerge.subtitle.lrc;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fordes.subtitles.view.utils.submerge.subtitle.common.TimedLine;
import org.fordes.subtitles.view.utils.submerge.subtitle.common.TimedTextFile;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author fordes on 2022/7/21
 */
@Data
@NoArgsConstructor
public class LRCSub implements TimedTextFile, Serializable {

    private static final long serialVersionUID = -2909833789376537734L;

    private String fileName;
    private Set<LRCLine> lines = new TreeSet<>();

    public void add(LRCLine line) {
        this.lines.add(line);
    }

    public void remove(TimedLine line) {
        this.lines.remove((LRCLine) line);
    }

    public String toString() {
        return CollUtil.join(lines, StrUtil.EMPTY);
    }

    @Override
    public Set<? extends TimedLine> getTimedLines() {
        return this.lines;
    }
}
