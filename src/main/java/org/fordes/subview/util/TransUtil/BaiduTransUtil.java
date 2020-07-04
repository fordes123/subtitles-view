package org.fordes.subview.util.TransUtil;



import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BaiduTransUtil implements Callable{
    private static final String TRANS_API_HTTP = "http://api.fanyi.baidu.com/api/trans/vip/translate";
    private static final String TRANS_API_HTTPS = "https://fanyi-api.baidu.com/api/trans/vip/translate";
    protected static final int SOCKET_TIMEOUT = 10000;
    protected static final String GET = "GET";
    protected static final String POST = "POST";
    private String appid;
    private String securityKey;
    private String res="";


    public static String get(String host, Map<String, String> params) {
        try {

            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, new TrustManager[] { myX509TrustManager }, null);

            String sendUrl = getUrlWithQueryString(host, params);



            URL uri = new URL(sendUrl);
            HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
            if (conn instanceof HttpsURLConnection) {
                ((HttpsURLConnection) conn).setSSLSocketFactory(sslcontext.getSocketFactory());
            }

            conn.setConnectTimeout(SOCKET_TIMEOUT);
            conn.setRequestMethod(GET);
            int statusCode = conn.getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK) {
                System.out.println("Http错误码：" + statusCode);
            }


            InputStream is = conn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }

            String text = builder.toString();

            close(br);
            close(is);
            conn.disconnect();

            return text;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getUrlWithQueryString(String url, Map<String, String> params) {
        if (params == null) {
            return url;
        }

        StringBuilder builder = new StringBuilder(url);
        if (url.contains("?")) {
            builder.append("&");
        } else {
            builder.append("?");
        }

        int i = 0;
        for (String key : params.keySet()) {
            String value = params.get(key);
            if (value == null) {
                continue;
            }

            if (i != 0) {
                builder.append('&');
            }

            builder.append(key);
            builder.append('=');
            builder.append(encode(value));

            i++;
        }

        return builder.toString();
    }

    protected static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 对输入的字符串进行URL编码, 转换为unicode
     *
     * @param input 原文
     * @return URL编码. 如果编码失败, 则返回原文
     */
    public static String encode(String input) {
        if (input == null) {
            return "";
        }

        try {
            return URLEncoder.encode(input, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return input;
    }

    /**
     * post方式请求
     * @param url
     * @param param
     * @return
     * @throws Exception
     */
    public static String post(String url, Map<String,String> param) throws Exception {

        URL localURL = new URL(url);
        URLConnection connection = localURL.openConnection();
        HttpURLConnection httpURLConnection = (HttpURLConnection) connection;

        httpURLConnection.setDoOutput(true);
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setRequestProperty("Accept-Charset", "utf-8");
        httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        httpURLConnection.setRequestProperty("Content-Length", String.valueOf("5000"));
        httpURLConnection.setConnectTimeout(10000);

        OutputStream outputStream = null;
        OutputStreamWriter outputStreamWriter = null;
        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader reader = null;
        String resultBuffer = "";

        try {
            outputStream = httpURLConnection.getOutputStream();
            outputStreamWriter = new OutputStreamWriter(outputStream);


            int i = 0;
            StringBuilder builder = new StringBuilder();
            for (String key : param.keySet()) {
                String value = param.get(key);
                if (value == null) { // 过滤空的key
                    continue;
                }

                if (i != 0) {
                    builder.append('&');
                }

                builder.append(key);
                builder.append('=');
                builder.append(encode(value));

                i++;
            }
            outputStreamWriter.write(builder.toString());
            outputStreamWriter.flush();

            if (httpURLConnection.getResponseCode() >= 300) {
                throw new Exception(
                        "HTTP Request is not success, Response code is " + httpURLConnection.getResponseCode());
            }

            inputStream = httpURLConnection.getInputStream();
            resultBuffer = convertStreamToString(inputStream);
            //System.out.println(resultBuffer);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (outputStreamWriter != null) {
                outputStreamWriter.close();
            }

            if (outputStream != null) {
                outputStream.close();
            }

            if (reader != null) {
                reader.close();
            }

            if (inputStreamReader != null) {
                inputStreamReader.close();
            }

            if (inputStream != null) {
                inputStream.close();
            }

        }

        return resultBuffer;
    }

    /**
     * 处理post请求返回结果
     * @param is
     * @return
     */
    public static String convertStreamToString(InputStream is) {
        StringBuilder sb1 = new StringBuilder();
        byte[] bytes = new byte[4096];
        int size = 0;
        try {
            while ((size = is.read(bytes)) > 0) {
                String str = new String(bytes, 0, size, "UTF-8");
                sb1.append(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb1.toString();
    }

    private static TrustManager myX509TrustManager = new X509TrustManager() {

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }
    };

    public BaiduTransUtil(String query, String from, String to,String id,String key,Boolean get) {
        this.appid = id;
        this.securityKey = key;
        res=getTransResult(query,from,to,false);
    }

    public String getTransResult(String query, String from, String to,Boolean get){
        Map<String, String> params = buildParams(query, from, to);
        try {
            return get?get(TRANS_API_HTTPS, params):post(TRANS_API_HTTPS, params);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Map<String, String> buildParams(String query, String from, String to) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("q", query);
        params.put("from", from);
        params.put("to", to);
        params.put("appid", appid);

        String salt = String.valueOf(System.currentTimeMillis());
        params.put("salt", salt);

        String src = appid + query + salt + securityKey;
        params.put("sign", CodeUtil.md5(src));
        return params;
    }

    /**
     * 解析百度翻译结果
     * @return
     */
    private String AnalyticalBaiduTransResults(){
        String Result="";
        String pattern = "(\"dst\":\")(.*?)(\")";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(res);
        while (m.find())/*
             自动遍历打印所有结果   group方法打印捕获的组内容，以正则的括号角标从1开始计算，我们这里要第2个括号里的
             值， 所以取 m.group(2)， m.group(0)取整个表达式的值，如果越界取m.group(4),则抛出异常
           */
            Result+=CodeUtil.unicodeToChina(m.group(2))+"\n";
        return Result;
    }

    @Override
    public Object call() throws Exception {
        return AnalyticalBaiduTransResults();
    }


}
