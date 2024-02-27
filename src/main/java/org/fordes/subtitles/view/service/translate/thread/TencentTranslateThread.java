package org.fordes.subtitles.view.service.translate.thread;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.date.format.FastDateFormat;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.fordes.subtitles.view.model.DTO.TranslateResult;
import org.fordes.subtitles.view.utils.TranslateUtil;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.TimeZone;
import java.util.concurrent.Callable;

/**
 * @author fordes on 2022/7/29
 */
@Slf4j
public class TencentTranslateThread extends TranslateThread implements Callable<TranslateResult> {


    static final String CT_JSON = "application/json; charset=utf-8";

    static final String SERVICE = "tmt";

    static final String ACTION = "TextTranslate";

    static final String VERSION = "2018-03-21";

    static final String ALGORITHM = "TC3-HMAC-SHA256";

    private final String secretId;

    private final String secretKey;

    private final String region;

    public TencentTranslateThread(String secretId, String secretKey, String region, Integer serial, String serviceURL,
                                  String target, String original, String content) {
        super(serial, serviceURL, target, original, content);
        this.region = region;
        this.secretId = secretId;
        this.secretKey = secretKey;
    }

    @Override
    public TranslateResult call() throws Exception {
        TimeInterval interval = DateUtil.timer();
        URL url = URLUtil.url(serviceURL);

        //时间
        long now = DateUtil.currentSeconds();
        String timestamp = String.valueOf(now);
        String date = DateTime.of(now * 1000)
                .toString(FastDateFormat.getInstance(DatePattern.NORM_DATE_PATTERN, TimeZone.getTimeZone("UTC")));

        //拼接规范请求串
        String httpRequestMethod = "POST";
        String canonicalUri = "/";
        String canonicalQueryString = "";
        String canonicalHeaders = "content-type:application/json; charset=utf-8\n" + "host:" + url.getHost() + "\n";
        String signedHeaders = "content-type;host";

        //整合参数
        Dict param = Dict.of(
                "SourceText", content,
                "Source", original,
                "Target", target,
                "ProjectId", 0);
        String payload = JSONUtil.toJsonStr(param);
        String hashedRequestPayload = TranslateUtil.sha256Hex(payload);
        String canonicalRequest = httpRequestMethod + "\n" + canonicalUri + "\n" + canonicalQueryString + "\n"
                + canonicalHeaders + "\n" + signedHeaders + "\n" + hashedRequestPayload;

        //拼接待签名字符串
        String credentialScope = date + "/" + SERVICE + "/" + "tc3_request";
        String hashedCanonicalRequest = TranslateUtil.sha256Hex(canonicalRequest);
        String stringToSign = ALGORITHM + "\n" + timestamp + "\n" + credentialScope + "\n" + hashedCanonicalRequest;

        //计算签名
        byte[] secretDate = TranslateUtil.hmac256(("TC3" + secretKey).getBytes(StandardCharsets.UTF_8), date);
        byte[] secretService = TranslateUtil.hmac256(secretDate, SERVICE);
        byte[] secretSigning = TranslateUtil.hmac256(secretService, "tc3_request");
        String signature = HexUtil.encodeHexStr(TranslateUtil.hmac256(secretSigning, stringToSign)).toLowerCase();

        //拼接 Authorization
        String authorization = ALGORITHM + " " + "Credential=" + secretId + "/" + credentialScope + ", "
                + "SignedHeaders=" + signedHeaders + ", " + "Signature=" + signature;

        HttpResponse response = HttpUtil.createPost(serviceURL)
                .header("Authorization", authorization)
                .header("Content-Type", CT_JSON)
                .header("Host", url.getHost())
                .header("X-TC-Action", ACTION)
                .header("X-TC-Timestamp", timestamp)
                .header("X-TC-Version", VERSION)
                .header("X-TC-Region", region)
                .setFollowRedirects(true)
                .body(payload)
                .execute();
        JSONObject resp = JSONUtil.parseObj(response.body());
        TranslateResult result = TranslateResult.builder().serial(serial)
                .success(false).data("翻译失败！").build();
        if (response.isOk() && resp.containsKey("Response")) {
            JSONObject respJson = resp.getJSONObject("Response");
            if (respJson.containsKey("TargetText")) {
                result.setSuccess(true);
                result.setData(respJson.getStr("TargetText"));
            }else {
                result.setSuccess(false);
                result.setData(respJson.getJSONObject("Error").getStr("Message"));
            }
        }
        long intervalTime = interval.intervalMs();
        log.debug("序号：{} 请求 {}，耗时：{} ms", serial,result.isSuccess()? "成功":"失败", intervalTime);
//        log.debug(resp.toStringPretty());
        return result;
    }

}
