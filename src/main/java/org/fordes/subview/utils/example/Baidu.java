package org.fordes.subview.utils.example;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.MD5;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.fordes.subview.entity.DTO.OrderlyResult;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fordes on 2021/6/15
 */
@Slf4j
@Component
public class Baidu {

    public static final String API_URL = "https://fanyi-api.baidu.com/api/trans/vip/translate";

    public static final String SALT = "subview-proxy";




    /**
     * 百度翻译调用
     * @param text 待翻译文本
     * @param original 原语言
     * @param target 目标语言
     * @param app_id 应用id
     * @param key 密钥
     * @return 翻译结果
     */
//    @Async("universalTask")
    public static OrderlyResult translation(String text, String original, String target, String app_id, String key, Integer serial) throws InterruptedException {
        HttpRequest request = HttpUtil.createPost(API_URL);
        request.contentType("application/x-www-form-urlencoded")
                .form("q", text)
                .form("from", original)
                .form("to", target)
                .form("appid", app_id)
                .form("salt", SALT)
                .form("sign", sign(text, app_id, key))
                .charset(StandardCharsets.UTF_8);
        TimeInterval timer = new TimeInterval();
        HttpResponse response = request.execute();
        if (response.isOk()) {
            long time = System.currentTimeMillis();
            JSONObject obj = JSONUtil.parseObj(response.body());
            if (!obj.containsKey("error_code")) {
                log.debug("百度翻译POST请求 => {}, 耗时 => {} ms", response.isOk()?"成功":"失败", timer.interval());
                List<JSONObject> list = obj.getJSONArray("trans_result").toList(JSONObject.class);
                OrderlyResult result = new OrderlyResult()
                        .setSerial(serial)
                        .setContent(list.stream().map(item -> item.getStr("dst") ).collect(Collectors.toList()));
                log.debug("百度翻译：{}， 返回结果行数：{}", serial, result.getContent().size());
                if (time+ 1000 > System.currentTimeMillis()) {
                    Thread.sleep(time+ 1000 - System.currentTimeMillis());
                }
                return result;
            }else {
                log.error("百度翻译出现错误, 错误代码 => {}", obj.getStr("error_code"));
            }
        }
        return new OrderlyResult()
                .setSerial(serial).setContent(StrUtil.split(text, StrUtil.C_LF));
    }

    public static OrderlyResult translation(Collection<String> text, String original, String target,
                                            String app_id, String key, Integer serial) throws InterruptedException {
        return translation(CollUtil.join(text, StrUtil.CRLF), original, target, app_id, key, serial);
    }

    private static String sign(final String text, final String app_id, final String key) {
        String sign = StrUtil.concat(false, app_id, text, SALT, key);
        return MD5.create().digestHex(sign);
    }


}
