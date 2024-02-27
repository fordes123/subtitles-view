package org.fordes.subtitles.view.service.translate.thread;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.crypto.digest.HMac;
import cn.hutool.crypto.digest.HmacAlgorithm;
import cn.hutool.crypto.digest.MD5;
import cn.hutool.http.*;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.fordes.subtitles.view.model.DTO.TranslateResult;

import java.net.URL;
import java.util.UUID;
import java.util.concurrent.Callable;

/**
 * @author fordes on 2022/7/26
 */
@Slf4j
public class AliTranslateThread extends TranslateThread  implements Callable<TranslateResult> {

    static final String CONTENT_MD5 = "Content-MD5";

    static final String CONTENT_TYPE = "application/json;chrset=utf-8";

    static final String X_ACS_SIGNATURE_NONCE = "x-acs-signature-nonce";

    static final String X_ACS_SIGNATURE_METHOD = "x-acs-signature-method";

    static final String X_ACS_VERSION = "x-acs-version";

    static final String HMAC_SHA1 = "HMAC-SHA1";

    static final String VERSION = "2019-01-02";

    private final String ak_id;

    private final String ak_secret;

    public AliTranslateThread(String ak_id, String ak_secret, Integer serial, String serviceURL,
                              String target, String original, String content) {
        super(serial, serviceURL, target, original, content);
        this.ak_id = ak_id;
        this.ak_secret = ak_secret;
    }


    @Override
    public TranslateResult call() {
        TimeInterval interval = DateUtil.timer();
        URL url = URLUtil.url(serviceURL);
        Dict param = Dict.of(
                "FormatType", "text",
                "SourceLanguage", original,
                "TargetLanguage", target,
                "SourceText", content,
                "Scene", "general"
        );
        String postBody = JSONUtil.parseObj(param).toString();
        String bodyMd5 = Base64.encode(MD5.create().digest(postBody));
        String uuid = UUID.randomUUID().toString();
        String date = DateTime.now().toString(DatePattern.HTTP_DATETIME_FORMAT);


        String stringToSign = Method.POST.name() + StrUtil.LF +
                ContentType.JSON.getValue() + StrUtil.LF +
                bodyMd5 + StrUtil.LF +
                CONTENT_TYPE + StrUtil.LF +
                date + StrUtil.LF +
                X_ACS_SIGNATURE_METHOD + StrUtil.COLON + HMAC_SHA1 + StrUtil.LF +
                X_ACS_SIGNATURE_NONCE + StrUtil.COLON + uuid + StrUtil.LF +
                X_ACS_VERSION + StrUtil.COLON + VERSION + StrUtil.LF +
                url.getFile();

        String signature = new HMac(HmacAlgorithm.HmacSHA1, ak_secret.getBytes()).digestBase64(stringToSign, false);
        String authHeader = "acs " + ak_id + ":" + signature;

        HttpResponse response = HttpUtil.createPost(serviceURL)
                .header(Header.ACCEPT, ContentType.JSON.getValue())
                .header(Header.CONTENT_TYPE, CONTENT_TYPE)
                .header(CONTENT_MD5, bodyMd5)
                .header(Header.DATE, date)
                .header(Header.HOST, url.getHost())
                .header(Header.AUTHORIZATION, authHeader)
                .header(X_ACS_SIGNATURE_NONCE, uuid)
                .header(X_ACS_SIGNATURE_METHOD, HMAC_SHA1)
                .header(X_ACS_VERSION, VERSION)
                .setFollowRedirects(true)
                .body(postBody)
                .execute();

        JSONObject resp = JSONUtil.parseObj(response.body());
        TranslateResult result = TranslateResult.builder().serial(serial).build();
        if (response.isOk() && resp.containsKey("Data")) {
            result.setSuccess(true);
            result.setData(resp.getJSONObject("Data").getStr("Translated"));
        } else {
            result.setSuccess(false);
            result.setData(resp.getStr("errorMsg"));
        }
        log.debug("序号：{} 请求 {}，耗时：{} ms", serial,result.isSuccess()? "成功":"失败", interval.intervalMs());
//        log.debug(resp.toStringPretty());
        return result;
    }
}
