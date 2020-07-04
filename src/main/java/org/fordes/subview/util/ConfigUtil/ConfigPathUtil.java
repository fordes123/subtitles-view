package org.fordes.subview.util.ConfigUtil;

public class ConfigPathUtil {
    private static final String lfasrInfoPath = System.getProperty("user.dir") + "\\data\\config\\config.properties";
    private static final String lfasrInfoTargetPath = System.getProperty("user.dir") + "\\target\\classes\\config.properties";
    private static final String BaiduTarnInfoPath = System.getProperty("user.dir") + "\\data\\config\\baiduTranInfo.properties";
    private static final String tempPath = System.getProperty("user.dir") + "\\data\\temp";
    private static final String settingConfigPath = System.getProperty("user.dir") + "\\data\\config\\settings.properties";

    public String getLfasrInfoTargetPath() {
        return lfasrInfoTargetPath;
    }

    public String getLfasrInfoPath() {
        return lfasrInfoPath;
    }

    public String getTempPath() {
        return tempPath;
    }

    public String getBaiduTarnInfoPath() {
        return BaiduTarnInfoPath;
    }

    public String getSettingConfigPath() { return settingConfigPath; }
}
