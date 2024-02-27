package org.fordes.subtitles.view.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.fordes.subtitles.view.model.DTO.Subtitle;
import org.fordes.subtitles.view.utils.submerge.subtitle.common.TimedLine;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.StringReader;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

/**
 * 翻译工具
 *
 * @author fordes on 2022/7/26
 */
@Slf4j
public class TranslateUtil {

    private final static String QUESTION_MARK = "?";

    public final static String SEPARATIST = "><";

    /**
     * 字符串切分，按照指定的分隔符切分字符串为长度不超过{@code maxLength}的集合
     * @param content 内容
     * @param maxLength 最大长度
     * @return 切分后的集合
     */
    public static List<String> segmented(String content, int maxLength) {
        List<String> result = CollUtil.newArrayList();
        try (StringReader reader = StrUtil.getReader(content)) {
            StringBuilder builder = StrUtil.builder();
            StringBuilder temp = StrUtil.builder();
            CharBuffer buffer = CharBuffer.allocate(1);
            while (-1 != reader.read(buffer)) {
                CharSequence s = buffer.flip();
                temp.append(s);
                if (StrUtil.equalsAny(s, StrUtil.COMMA, StrUtil.DOT, StrUtil.LF, QUESTION_MARK)) {
                    if (builder.length() + temp.length() >= maxLength) {
                        result.add(builder.toString());
                        builder.setLength(0);
                    }
                    builder.append(temp);
                    temp.setLength(0);
                }
            }
            result.add(builder.toString());
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
        return result;
    }


    /**
     * @see #segmented(String, int)
     * 重载方法 多行文本使用\n分割
     */
    public static List<String> segmented(List<String> content, int maxLength) {
        return segmented(CollUtil.join(content, StrUtil.LF), maxLength);
    }

    /**
     * @see #segmented(String, int)
     * 重载方法 每段字幕使用 {@link #SEPARATIST} 分割
     */
    public static List<String> segmented(Subtitle subtitle, int maxLength) {
        List<String> data = CollUtil.newArrayList();
        subtitle.getTimedTextFile().getTimedLines().forEach(e
                -> data.add(CollUtil.join(e.getTextLines(), SEPARATIST)));
        return segmented(data, maxLength);
    }

    /**
     * 将经过 {@link #segmented(Subtitle, int)} 切分后的结构还原至字幕文件中
     * @param subtitle  字幕
     * @param data  数据
     * @param mode  模式 f-覆盖模式，t-追加模式
     */
    public static void reduction(Subtitle subtitle, List<String> data, boolean mode) {
        StringBuilder builder = StrUtil.builder();
        data.forEach(builder::append);

        List<String> lines = StrUtil.split(builder.toString(), StrUtil.LF);
        for (int part = 0; part < lines.size(); part++) {
            TimedLine line = CollUtil.get(subtitle.getTimedTextFile().getTimedLines(), part);
            List<String> second =  StrUtil.split(lines.get(part), SEPARATIST);
            if (mode) {
                List<String> temp = new ArrayList<>(line.getTextLines().size());
                List<String> first = line.getTextLines();
                for (int i = 0; i < first.size(); i++) {
                    temp.add(CollUtil.get(first, i));
                    temp.add(CollUtil.get(second, i));
                }
                line.setTextLines(temp);
            }else {
                line.setTextLines(second);
            }
        }
    }

    public static byte[] hmac256(byte[] key, String msg) throws Exception {
        return hmac256(key, msg.getBytes(StandardCharsets.UTF_8));
    }

    public static byte[] hmac256(byte[] key, byte[] msg) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, mac.getAlgorithm());
        mac.init(secretKeySpec);
        return mac.doFinal(msg);
    }

    public static byte[] hmac256(String key, String msg) throws Exception {
        return hmac256(key.getBytes(StandardCharsets.UTF_8), msg.getBytes(StandardCharsets.UTF_8));
    }

    public static String sha256Hex(String s) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] d = md.digest(s.getBytes(StandardCharsets.UTF_8));
        return HexUtil.encodeHexStr(d).toLowerCase();
    }

}
