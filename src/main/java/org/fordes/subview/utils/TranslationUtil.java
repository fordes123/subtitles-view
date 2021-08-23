package org.fordes.subview.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.fordes.subview.entity.DTO.OrderlyResult;
import org.fordes.subview.enums.ServerSupplierEnum;
import org.fordes.subview.utils.common.ApplicationConfig;
import org.fordes.subview.utils.example.Baidu;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * @author fordes on 2021/6/15
 */
@Slf4j
public class TranslationUtil {

    /**
     * 将多行文本裁切分段
     * @param text 文本行号对应内容
     * @param limit 单段最大长度, 字节数
     * @return 分组结果
     */
    private static List<String> segmented(List<String> text, long limit) {
        List<String> result = Lists.newArrayList();
        List<String> builder = Lists.newArrayList();
        long pc = 0;
        for (String item : text) {
            //TODO 可能需要考虑单句大于上限值的情况
            int length = StrUtil.byteLength(item, Charset.defaultCharset());
            if ((pc+ length+ 2) >= limit) {
                result.add(CollUtil.join(builder, ApplicationConfig.LINE_FEED));
                builder.clear();
                pc = 0;
            }else {
                builder.add(item);
                pc += (length + 2);
            }
        }
        if (!builder.isEmpty()) {
            result.add(CollUtil.join(builder, ApplicationConfig.LINE_FEED));
        }
        return result;
    }

    public static void main(String[] args) {
        List<String> seg = segmented(Lists.newArrayList("His name is Marchosias.", "Astra, you dick!", "No!"), 10);
        System.out.println(seg.size());
    }


    public static List<String> translation(List<String> text, String original, String target, String app_id, String key,
                                     ServerSupplierEnum supplier, long limit) throws Exception {
        log.debug("输入文本：{}行", text.size());
        TimeInterval timer = DateUtil.timer();
        List<String> segments = segmented(text, limit);
        List<String> result = Lists.newArrayList();
        List<Callable<OrderlyResult>> futures = Lists.newArrayList();

        switch (supplier) {
            case BAIDU:
                for (int i = 0; i < segments.size(); i++) {
                    final int serial = i;
                    futures.add(() -> Baidu.translation(segments.get(serial), original, target, app_id, key, serial));
                    log.debug("百度翻译：{}， 请求行数：{}", serial, StrUtil.split(segments.get(serial), StrUtil.LF).size());
                }
                break;
            case IFLYTEK:
                break;
            case GOOGLE:
                break;
            case SOUGOU:
                break;
            default:
                throw new RuntimeException();
        }
        List<Future<OrderlyResult>> list = ServiceExecutor.getInstance().getExecutor().invokeAll(futures);
        List<OrderlyResult> temp = Lists.newArrayList();
        for (Future<OrderlyResult> future : list) {
            temp.add(future.get());
        }
        temp.stream().sorted(Comparator.comparingInt(OrderlyResult::getSerial)).forEach(item
                -> result.addAll(item.getContent()));
        log.debug("翻译文本长度 => {} 字节， 总耗时 => {} 毫秒", text.toString().getBytes(StandardCharsets.UTF_8).length, timer.interval());
        log.debug("输出文本：{}行", result.size());
        return result;
    }



}
