package org.fordes.subtitles.view.service.translate.thread;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.date.format.FastDateFormat;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.fordes.subtitles.view.model.DTO.TranslateResult;
import org.fordes.subtitles.view.utils.TranslateUtil;

import java.net.URL;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * @author fordes on 2022/7/31
 */
@Slf4j
public class HuoShanTranslateThread extends TranslateThread implements Callable<TranslateResult> {

    private final String accessKeyId;

    private final String secretAccessKey;

    private  final String versionDate;

    private final String region;

    static final String Action = "TranslateText";

    static final String Service = "translate";

    static final String Version = "1.0.16";

    static final String Algorithm = "HMAC-SHA256";


    public HuoShanTranslateThread(String versionDate, String region, String accessKeyId, String secretAccessKey,
                                  Integer serial, String serviceURL, String target, String original, String content) {
        super(serial, serviceURL, target, original, content);
        this.versionDate = versionDate;
        this.region = region;
        this.accessKeyId = accessKeyId;
        this.secretAccessKey = secretAccessKey;
    }


    @Override
    public TranslateResult call() throws Exception {
        TimeInterval interval = DateUtil.timer();
        //请求路径
        URL url = URLUtil.url(serviceURL);

        //请求体
        String body = new JSONObject()
                .putOnce("SourceLanguage", original) //原语言
                .putOnce("TargetLanguage", target) //目标语言
                .putOnce("TextList", CollUtil.newArrayList(content)).toString(); //待翻译文本列表，长度不大于128
        String bodyHash = SecureUtil.sha256(body);

        //时间 (必须使用UTC时间)
        DateTime now = DateTime.now();
        String nowDate = now.toString(FastDateFormat.getInstance(DatePattern.PURE_DATE_PATTERN, TimeZone.getTimeZone("UTC")));
        String nowTime = now.toString(FastDateFormat.getInstance(DatePattern.PURE_TIME_PATTERN, TimeZone.getTimeZone("UTC")));
        String requestDate = nowDate + "T" + nowTime + "Z";

        //构造需要计入签名的部分请求头
        Map<String, String> signHeadMap = MapUtil.newHashMap();
        signHeadMap.put(Header.CONTENT_TYPE.getValue(), ContentType.JSON.getValue());
        signHeadMap.put(Header.HOST.getValue(), url.getHost());
        signHeadMap.put("X-Date", requestDate);
        signHeadMap.put("X-Content-Sha256", bodyHash);
        //按照ASCII也即字母序排序
        TreeMap<String, String> signHeadMapSort = MapUtil.sort(signHeadMap);

        // 正规化请求
        String requestMethod = "POST";
        String canonicalURI = "/";
        String canonicalQueryString = StrUtil.format("Action={}&Version={}", Action, versionDate);
        StringBuilder canonicalHeaders = new StringBuilder();
        signHeadMapSort.forEach((key, value) -> canonicalHeaders.append(key.trim().toLowerCase())
                .append(StrUtil.COLON).append(value.trim()).append(StrUtil.LF));
        String SignedHeaders = CollUtil.join(signHeadMapSort.keySet(), ";").trim().toLowerCase();
        String canonicalRequest = StrUtil.concat(false, requestMethod, StrUtil.LF, canonicalURI, StrUtil.LF,
                canonicalQueryString, StrUtil.LF, canonicalHeaders, StrUtil.LF, SignedHeaders, StrUtil.LF, bodyHash);

        // 签名
        String CredentialScope = StrUtil.concat(false, nowDate, StrUtil.SLASH, region,
                StrUtil.SLASH, Service, "/request");
        String StringToSign = StrUtil.concat(false, Algorithm, StrUtil.LF, requestDate, StrUtil.LF,
                CredentialScope, StrUtil.LF, SecureUtil.sha256(canonicalRequest));

        //计算签名密钥
        byte[] kDate = TranslateUtil.hmac256(secretAccessKey, nowDate);
        byte[] kRegion = TranslateUtil.hmac256(kDate, region);
        byte[] kService = TranslateUtil.hmac256(kRegion, Service);
        byte[] kSigning = TranslateUtil.hmac256(kService, "request");

        //计算签名
        String Signature = HexUtil.encodeHexStr(TranslateUtil.hmac256(kSigning, StringToSign));
        //拼接出授权头
        String Authorization = StrUtil.format("{} Credential={}/{}, SignedHeaders={}, Signature={}",
                Algorithm, accessKeyId, CredentialScope, SignedHeaders, Signature);

        //创建真实请求
        HttpResponse response = HttpUtil.createPost(serviceURL)
                .header(Header.CONTENT_TYPE, ContentType.JSON.getValue())
                .header(Header.ACCEPT, ContentType.JSON.getValue())
                .header(Header.HOST, url.getHost())
                .header(Header.USER_AGENT, "volc-sdk-java/v" + Version)
                .header("X-Date", requestDate)
                .header("X-Content-Sha256", bodyHash)
                .header(Header.AUTHORIZATION, Authorization)
                .setFollowRedirects(true)
                .body(body)
                .execute();
        //解析结果
        JSONObject resp = JSONUtil.parseObj(response.body());
        TranslateResult result = TranslateResult.builder().serial(serial).build();
        if (response.isOk() && resp.containsKey("TranslationList")) {
            result.setSuccess(true);
            result.setData(resp.getJSONArray("TranslationList").stream()
                    .map(e -> JSONUtil.parseObj(e).getStr("Translation"))
                    .collect(Collectors.joining(StrUtil.LF)));
        } else {
            result.setSuccess(false);
            result.setData(resp.getJSONObject("ResponseMetadata")
                    .getJSONObject("Error").getStr("Message"));
        }
        log.debug("序号：{} 请求 {}，耗时：{} ms", serial, result.isSuccess() ? "成功" : "失败", interval.intervalMs());
//        log.debug(resp.toStringPretty());
        return result;
    }
}
