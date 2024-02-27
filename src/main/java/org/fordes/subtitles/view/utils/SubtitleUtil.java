package org.fordes.subtitles.view.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.lang.Singleton;
import cn.hutool.core.util.StrUtil;
import javafx.scene.control.IndexRange;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.fordes.subtitles.view.enums.FileEnum;
import org.fordes.subtitles.view.handler.CallBackHandler;
import org.fordes.subtitles.view.model.DTO.Subtitle;
import org.fordes.subtitles.view.utils.submerge.parser.ParserFactory;
import org.fordes.subtitles.view.utils.submerge.parser.SubtitleParser;
import org.fordes.subtitles.view.utils.submerge.subtitle.common.TimedLine;
import org.fordes.subtitles.view.utils.submerge.subtitle.common.TimedObject;
import org.fordes.subtitles.view.utils.submerge.subtitle.common.TimedTextFile;
import org.fxmisc.richtext.StyleClassedTextArea;

import java.time.LocalTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author fordes on 2022/7/19
 */
@Slf4j
public class SubtitleUtil {


    /**
     * 纯文本搜索 使用 {@link SearchCache} 单例作为缓存
     *
     * @see SubtitleUtil#search(SearchCache, StyleClassedTextArea, String, boolean, boolean)
     */
    public static void search(StyleClassedTextArea area, String target, boolean isIgnoreCase, boolean isRegular) {
        search(Singleton.get(SearchCache.class), area, target, isIgnoreCase, isRegular);
    }

    /**
     * 文本替换 前置 搜索 使用 {@link ReplaceCache} 单例作为缓存
     *
     * @see SubtitleUtil#search(SearchCache, StyleClassedTextArea, String, boolean, boolean) (ReplaceCache, StyleClassedTextArea, String, String, boolean, boolean)
     */
    public static void find(StyleClassedTextArea area, String target, boolean isIgnoreCase, boolean isRegular) {
        search(Singleton.get(ReplaceCache.class), area, target, isIgnoreCase, isRegular);
    }

    /**
     * 简易文本搜索
     *
     * @param area         被搜索文本
     * @param target       目标关键字
     * @param isIgnoreCase 忽略大小写
     * @param isRegular    正则搜索
     */
    public static <T extends SearchCache> void search(T cache, StyleClassedTextArea area, String target,
                                                      boolean isIgnoreCase, boolean isRegular) {
        int cursor;
        String text;
        if (StrUtil.equals(cache.getTarget(), target)) {
            cursor = cache.getAnchor() + 1;
            text = area.getText(cursor, area.getLength());
        } else {
            cache.reset();
            cursor = 0;
            text = area.getText();
        }
        int start = 0, end = 0;
        for (String line : text.split(StrUtil.LF)) {
            if (isRegular) {
                Matcher matcher = Pattern.compile(target).matcher(line);
                if (matcher.find()) {
                    start = cursor + line.indexOf(matcher.group(0));
                    end = cursor + line.indexOf(matcher.group(0)) + matcher.group(0).length();
                    break;
                }
            } else {
                int pos = StrUtil.indexOf(line, target, 0, isIgnoreCase);
                if (pos >= 0) {
                    start = cursor + pos;
                    end = cursor + pos + target.length();
                    break;
                }
            }
            cursor += line.length() + 1;
        }
        if (start != 0 && end != 0) {
            area.moveTo(end);
            area.requestFollowCaret();
            area.selectRange(start, end);
            cache.setAnchor(start);
            cache.setTarget(target);
            if (cache instanceof ReplaceCache) {
                ((ReplaceCache) cache).setCaretPosition(end);
            }
        } else cache.reset();
    }


    /**
     * 文本替换
     *
     * @param area         被处理文本区
     * @param subtitle     对应字幕文件
     * @param searchStr    被替换内容
     * @param replaceStr   替换内容
     * @param isAll        是否替换全部
     * @param isIgnoreCase 是否忽略大小写
     * @param isRegular    （searchStr）是否为正则表达式
     */
    public static void replace(StyleClassedTextArea area, Subtitle subtitle, String searchStr, String replaceStr,
                               boolean isAll, boolean isIgnoreCase, boolean isRegular) throws Exception {
        if (isAll) {
            String text = area.getText();
            if (isRegular) {
                Matcher matcher = Pattern.compile(searchStr).matcher(text);
                if (matcher.find()) {
                    text = matcher.replaceAll(replaceStr);
                }
            } else {
                text = isIgnoreCase ?
                        StrUtil.replaceIgnoreCase(text, searchStr, replaceStr) :
                        StrUtil.replace(text, searchStr, replaceStr);
            }
            area.clear();
            area.append(text, StrUtil.EMPTY);
        } else {
            ReplaceCache cache = Singleton.get(ReplaceCache.class);
            if (StrUtil.equals(searchStr, area.getText(cache.getAnchor(), cache.getCaretPosition()))) {
                area.replace(cache.getAnchor(), cache.getCaretPosition(), replaceStr, StrUtil.EMPTY);
            } else {
                search(cache, area, searchStr, isIgnoreCase, isRegular);
                if (StrUtil.equals(searchStr, area.getText(cache.getAnchor(), cache.getCaretPosition()))) {
                    area.replace(cache.getAnchor(), cache.getCaretPosition(), replaceStr, StrUtil.EMPTY);
                }else return;
            }
        }

        TimedTextFile timedTextFile = SubtitleUtil.parse(area.getText(), subtitle.getFormat());
        subtitle.setTimedTextFile(timedTextFile);
    }

    /**
     * 时间轴位移
     *
     * @param timedTextFile 字幕
     * @param begin         开始时间
     * @param range         位移范围
     * @param mode          显示模式
     * @return 时间轴位移后的字幕
     */
    public static TimedTextFile revise(TimedTextFile timedTextFile, LocalTime begin, IndexRange range, boolean mode) {
        LocalTime start = CollUtil.getFirst(timedTextFile.getTimedLines()).getTime().getStart();
        long poor = begin.toNanoOfDay() - start.toNanoOfDay();
        if (range != null) {
            long sort = 0;
            for (TimedLine item : timedTextFile.getTimedLines()) {
                sort += toStr(item, mode).length();
                if (sort > range.getEnd()) {
                    break;
                } else if (sort >= range.getStart()) {
                    item.getTime().setStart(LocalTime.ofNanoOfDay(item.getTime().getStart().toNanoOfDay() + poor));
                    item.getTime().setEnd(LocalTime.ofNanoOfDay(item.getTime().getEnd().toNanoOfDay() + poor));
                }
            }
        } else {
            for (TimedLine item : timedTextFile.getTimedLines()) {
                revise(item.getTime(), poor);
            }
        }
        return timedTextFile;
    }

    /**
     * @see #revise(TimedTextFile, LocalTime, IndexRange, boolean)
     */
    private static void revise(TimedObject timedLine, long poor) {
        timedLine.setStart(LocalTime.ofNanoOfDay(timedLine.getStart().toNanoOfDay() + poor));
        timedLine.setEnd(LocalTime.ofNanoOfDay(timedLine.getEnd().toNanoOfDay() + poor));
    }

    /**
     * @see #revise(TimedTextFile, LocalTime, IndexRange, boolean)
     */
    public static TimedTextFile revise(TimedTextFile timedTextFile, LocalTime begin, boolean mode) {
        return revise(timedTextFile, begin, null, mode);
    }

    /**
     * 从文件解析字幕
     *
     * @param subtitle 字幕文件
     * @throws Exception 异常
     */
    public static void parse(Subtitle subtitle) throws Exception {
        TimeInterval timer = DateUtil.timer();
        SubtitleParser parser = ParserFactory.getParser(subtitle.getFormat().suffix);
        TimedTextFile content = parser.parse(subtitle.getFile(), subtitle.getCharset());
        log.debug("解析字幕耗时：{} ms", timer.interval());
        subtitle.setTimedTextFile(content);
    }

    /**
     * 从文本字幕解析字幕
     *
     * @param str  字幕文本
     * @param type 字幕格式
     * @return 字幕结构
     * @throws Exception 异常
     */
    public static TimedTextFile parse(String str, FileEnum type) throws Exception {
        return ParserFactory.getParser(type.suffix).parse(str, StrUtil.EMPTY);
    }

    /**
     * 字幕结构转换为字符串
     *
     * @param mode 解析模式 f-简洁模式 t-完整模式
     * @return 字符串
     */
    public static String toStr(TimedTextFile subtitle, boolean mode) {
        if (!mode) {
            StringBuilder content = new StringBuilder();
            subtitle.getTimedLines().forEach(item
                    -> content.append(CollUtil.join(item.getTextLines(), StrUtil.CRLF)).append(StrUtil.CRLF));
            return content.toString();
        } else {
            return subtitle.toString();
        }
    }

    /**
     * 字幕结构转换为字符串
     *
     * @param mode 解析模式 f-简洁模式 t-完整模式
     * @return 字符串
     */
    public static String toStr(TimedLine timedLine, boolean mode) {
        return mode ? timedLine.toString() : CollUtil.join(timedLine.getTextLines(), StrUtil.CRLF);
    }

    /**
     * 写入字幕结构到源文件
     *
     * @param subtitle 字幕
     * @param handler  回调
     */
    public static void write(Subtitle subtitle, CallBackHandler<Boolean> handler) {
        try {
            FileUtils.write(subtitle.getFile(), subtitle.getTimedTextFile().toString(), subtitle.getCharset());
        } catch (RuntimeException e) {
            handler.handle(false);
        }
        handler.handle(true);
    }

    /**
     * 搜索操作 上一步结果缓存
     */
    @Data
    static class SearchCache {

        private String target;
        private int anchor;

        public SearchCache() {
            reset();
        }

        public void reset() {
            this.target = StrUtil.EMPTY;
            this.anchor = 0;
        }

    }

    /**
     * 替换操作 上一步结果缓存
     */
    @Data
    @EqualsAndHashCode(callSuper = false)
    static class ReplaceCache extends SearchCache {
        private int caretPosition;

        @Override
        public void reset() {
            this.caretPosition = 0;
            super.reset();
        }
    }
}
