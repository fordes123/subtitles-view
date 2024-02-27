package org.fordes.subtitles.view.service.translate;

import cn.hutool.core.map.MapUtil;
import org.fordes.subtitles.view.enums.ServiceProvider;
import org.fordes.subtitles.view.factory.TranslateServiceFactory;
import org.fordes.subtitles.view.model.DTO.TranslateResult;
import org.fordes.subtitles.view.model.PO.Version;
import org.fordes.subtitles.view.service.translate.thread.BaiduTranslateThread;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author fordes on 2022/7/11
 */
@Service
public class BaiduTranslateService extends TranslateService implements InitializingBean {

    static final String APP_ID = "APP_ID";

    static final String APP_KEY = "APP_KEY";

    @Override
    public void afterPropertiesSet() {
        TranslateServiceFactory.register(this, ServiceProvider.BAIDU.name());
    }

    @Override
    public Callable<TranslateResult> createTask(ThreadPoolExecutor executor, int serial, String segment, String target, String original, Version version, Map<String, Object> config) {
        String app_id = MapUtil.getStr(config, APP_ID);
        String app_key = MapUtil.getStr(config, APP_KEY);
        return new BaiduTranslateThread(app_id, app_key, serial, version.getServerUrl(), target, original, segment);
    }
}
