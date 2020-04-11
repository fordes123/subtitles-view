package org.fordes.subview.util.ConfigUtil;

public class ConfigPathUtil {
    private static String lfasrInfoPath=System.getProperty("user.dir") + "\\resource\\config.properties";
    private static String BaiduTarnInfoPath=System.getProperty("user.dir") + "\\resource\\baiduTranInfo.properties";
    private static String tempPath=System.getProperty("user.dir") + "\\date\\temp";//缓存路径


    public String getLfasrInfoPath() {
        return lfasrInfoPath;
    }

    public String getTempPath() {
        return tempPath;
    }

    public String getBaiduTarnInfoPath() {
        return BaiduTarnInfoPath;
    }
}
