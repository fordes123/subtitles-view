package org.fordes.subtitles.view.service.translate.thread;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.crypto.digest.MD5;
import cn.hutool.http.ContentType;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.fordes.subtitles.view.model.DTO.TranslateResult;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * @author fordes on 2022/7/27
 */
@Slf4j
public class BaiduTranslateThread extends TranslateThread implements Callable<TranslateResult> {

    static final String SALT = "subview-proxy";

    private final String app_id;

    private final String app_key;

    public BaiduTranslateThread(String app_id, String app_key, Integer serial, String serviceURL,
                                String target, String original, String content) {
        super(serial, serviceURL, target, original, content);
        this.app_id = app_id;
        this.app_key = app_key;
    }


    @Override
    public TranslateResult call() {
        TimeInterval interval = DateUtil.timer();
        HttpResponse response = HttpUtil.createPost(serviceURL)
                .form("q", content)
                .form("from", original)
                .form("to", target)
                .form("appid", app_id)
                .form("salt", SALT)
                .form("sign", MD5.create().digestHex(app_id+ content + SALT + app_key))
                .contentType(ContentType.FORM_URLENCODED.getValue())
                .charset("UTF-8")
                .setFollowRedirects(true)
                .execute();
        JSONObject resp = JSONUtil.parseObj(response.body());
        TranslateResult result = TranslateResult.builder().serial(serial).build();
        if (response.isOk() && !resp.containsKey("error_code")) {
            result.setSuccess(true);
            List<JSONObject> dataList = resp.getJSONArray("trans_result").toList(JSONObject.class);
            result.setData(dataList.stream().map(e -> e.getStr("dst")).collect(Collectors.joining()));
        } else {
            result.setSuccess(false);
            result.setData(resp.getStr("error_msg"));
        }
        log.debug("序号：{} 请求 {}，耗时：{} ms", serial,result.isSuccess()? "成功":"失败", interval.intervalMs());
//        log.debug(resp.toStringPretty());
        return result;
    }
}
