package org.fordes.subtitles.view.utils.submerge.subtitle.lrc;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fordes.subtitles.view.utils.submerge.subtitle.common.SubtitleTime;
import org.fordes.subtitles.view.utils.submerge.subtitle.srt.SRTTime;

import java.io.Serializable;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;

/**
 * @author fordes on 2022/7/21
 */
@Slf4j
@NoArgsConstructor
public class LRCTime extends SubtitleTime implements Serializable {

    private static final long serialVersionUID = -5787808223967579723L;

    public static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(SRTTime.PATTERN);

    public static final String PATTERN = "mm:ss.SS";

    private static final String TS_PATTERN = "%02d:%02d.%02d";

    public LRCTime(LocalTime start) {
        this.start = start;
    }

    @Override
    public String toString() {
        return StrUtil.format("[{}]", format(start));
    }

    public static String format(LocalTime time) {
        int min = time.get(ChronoField.MINUTE_OF_HOUR);
        int sec = time.get(ChronoField.SECOND_OF_MINUTE);
        int ms = time.get(ChronoField.MILLI_OF_SECOND);

        return String.format(TS_PATTERN, min, sec, ms);
    }

    public static LRCTime fromString(String times) {
        try {
            LocalTime time = LocalDateTimeUtil.parse(times, PATTERN).toLocalTime();
            return new LRCTime(time);
        }catch (Exception e) {
            return null;
        }
    }
}
