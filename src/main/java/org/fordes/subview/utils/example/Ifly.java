package org.fordes.subview.utils.example;

import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.fordes.subview.entity.DTO.OrderlyResult;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author fordes on 2021/6/28
 */
@Slf4j
public class Ifly {

    public static final String API_TRANS_URL = "https://ntrans.xfyun.cn/v2/ots";

    /**
     * 讯飞NiuTrans翻译调用
     * @param text 待翻译文本
     * @param original 原语言
     * @param target 目标语言
     * @param app_id APPID
     * @param secret APISecret
     * @param key APIKey
     * @return 翻译结果
     */
    public static OrderlyResult translation(String text, String original, String target, String app_id,
                                            String key, String secret, Integer serial) throws Exception {
        JSONObject body = new JSONObject();
        byte[] textByte = text.getBytes(StandardCharsets.UTF_8);
        String textBase64 = Base64.getEncoder().encodeToString(textByte);
        body.putOpt("common", ImmutableMap.of("app_id", app_id))
                .putOpt("business", ImmutableMap.of("from", original, "to", target))
                .putOpt("data", ImmutableMap.of("text", textBase64));
        String bodyStr = body.toString();
        HttpRequest request = HttpUtil.createPost(API_TRANS_URL)
                .headerMap(buildHttpHeader(bodyStr, secret, key), true)
                .charset(StandardCharsets.UTF_8)
                .body(bodyStr);
        TimeInterval timer = new TimeInterval();
        HttpResponse response = request.execute();
        if (response.isOk()) {
            long time = System.currentTimeMillis();
            JSONObject obj = JSONUtil.parseObj(response.body());
            if (obj.getInt("code") == 0) {
                log.debug("讯飞NiuTrans翻译POST请求 => {}, 耗时 => {} ms", response.isOk()?"成功":"失败", timer.interval());
                OrderlyResult result = new OrderlyResult()
                        .setSerial(serial)
                        .setContent(StrUtil.split(obj.getJSONObject("data").getJSONObject("result")
                                .getJSONObject("trans_result").getStr("dst"), StrUtil.LF));
//                if (time+ 1000 > System.currentTimeMillis()) {
//                    Thread.sleep(time+ 1000 - System.currentTimeMillis());
//                }
                return result;
            }else {
                log.error("讯飞NiuTrans翻译出现错误, 错误代码 => {}", obj.getStr("error_code"));
            }
        }
        System.out.println(response.body());
        return new OrderlyResult()
                .setSerial(serial).setContent(StrUtil.split(text, StrUtil.C_LF));
    }






    public static void main(String[] args) throws Exception {
//        System.out.println(SecureUtil.sha256(Base64.encode("I'm sorry, but there's nothing I can do.")));
        OrderlyResult result = translation("Gideon, she's still blind.\n"+
                "I'm sorry, but there's nothing I can do.", "auto", "cn",
                "5e0f24fb", "cf651e77f0678e2dba3fb1e101b3de30", "ee5065d31b288ecebbeaccfb24e78056", 1);
        System.out.println(result);
    }




    /**
     * 组装http请求头
     */
    private static Map<String, String> buildHttpHeader(String body, final String API_SECRET, final String API_KEY) throws Exception {
        Map<String, String> header = new HashMap<>();
        URL url = new URL(API_TRANS_URL);

        //时间戳
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date dateD = new Date();
        String date = format.format(dateD);
        //对body进行sha256签名,生成digest头部，POST请求必须对body验证
        String digestBase64 = "SHA-256=" + signBody(body);
        //hmacsha256加密原始字符串
        StringBuilder builder = new StringBuilder("host: ").append(url.getHost()).append("\n").//
                append("date: ").append(date).append("\n").//
                append("POST ").append(url.getPath()).append(" HTTP/1.1").append("\n").//
                append("digest: ").append(digestBase64);
        String sha = hmacsign(builder.toString(), API_SECRET);

        //组装authorization
        String authorization = String.format("api_key=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"", API_KEY, "hmac-sha256", "host date request-line digest", sha);
        header.put("Authorization", authorization);
        header.put("Content-Type", "application/json");
        header.put("Accept", "application/json,version=1.0");
        header.put("Host", url.getHost());
        header.put("Date", date);
        header.put("Digest", digestBase64);
        return header;
    }





    /**
     * 对body进行SHA-256加密
     */
    private static String signBody(String body) throws Exception {
        MessageDigest messageDigest;
        String encodestr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(body.getBytes("UTF-8"));
            encodestr = Base64.getEncoder().encodeToString(messageDigest.digest());
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodestr;
    }

    /**
     * hmacsha256加密
     */
    private static String hmacsign(String signature, String apiSecret) throws Exception {
        Charset charset = Charset.forName("UTF-8");
        Mac mac = Mac.getInstance("hmacsha256");
        SecretKeySpec spec = new SecretKeySpec(apiSecret.getBytes(charset), "hmacsha256");
        mac.init(spec);
        byte[] hexDigits = mac.doFinal(signature.getBytes(charset));
        return Base64.getEncoder().encodeToString(hexDigits);
    }


}
