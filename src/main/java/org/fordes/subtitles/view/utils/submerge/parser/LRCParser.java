package org.fordes.subtitles.view.utils.submerge.parser;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.fordes.subtitles.view.utils.submerge.subtitle.lrc.LRCLine;
import org.fordes.subtitles.view.utils.submerge.subtitle.lrc.LRCSub;
import org.fordes.subtitles.view.utils.submerge.subtitle.lrc.LRCTime;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

/**
 * @author fordes on 2022/7/21
 */
@Slf4j
public final class LRCParser extends BaseParser<LRCSub> {
    @Override
    protected void parse(BufferedReader br, LRCSub sub) throws IOException {

        boolean found = true;
        String lineStr = readFirstTimeLine(br);

        while (found) {

            String timeStr = StrUtil.subBetween(lineStr, StrUtil.BRACKET_START, StrUtil.BRACKET_END);
            LRCTime time = LRCTime.fromString(timeStr);

            List<String> texts = CollUtil.newArrayList(time == null ?
                    lineStr : lineStr.substring(10));
            LRCLine line = new LRCLine(time, texts);

            try {
                lineStr = br.readLine();
                while (lineStr != null && !StrUtil.startWith(lineStr, StrUtil.C_BRACKET_START)) {
                    texts.add(lineStr);
                    lineStr = br.readLine();
                }
                sub.add(line);
                found = (lineStr != null);
            } catch (Exception e) {
                log.error(ExceptionUtil.stacktraceToString(e));
                found = false;
            }

        }
    }


    /**
     * 获得首个有效行，即第一个形如：[00:00:00.000]的行
     *
     * @param br
     * @return
     * @throws IOException
     */
    private String readFirstTimeLine(BufferedReader br) throws IOException {
        String lineStr = br.readLine();
        while (lineStr != null && !StrUtil.startWith(lineStr, StrUtil.C_BRACKET_START)) {
            lineStr = br.readLine();
        }
        return lineStr;
    }
}
