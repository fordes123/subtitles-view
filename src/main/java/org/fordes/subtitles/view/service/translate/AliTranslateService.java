package org.fordes.subtitles.view.service.translate;

import cn.hutool.core.map.MapUtil;
import lombok.extern.slf4j.Slf4j;
import org.fordes.subtitles.view.enums.ServiceProvider;
import org.fordes.subtitles.view.factory.TranslateServiceFactory;
import org.fordes.subtitles.view.model.DTO.TranslateResult;
import org.fordes.subtitles.view.model.PO.Version;
import org.fordes.subtitles.view.service.translate.thread.AliTranslateThread;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author fordes on 2022/7/25
 */
@Slf4j
@Service
public class AliTranslateService extends TranslateService implements InitializingBean {


    static final String APP_ID = "Accesskey ID";

    static final String APP_KEY = "AccessKey Secret";


    @Override
    public void afterPropertiesSet() {
        TranslateServiceFactory.register(this, ServiceProvider.ALI.name());
    }


    @Override
    public Callable<TranslateResult> createTask(ThreadPoolExecutor executor, int serial, String segment, String target, String original, Version version, Map<String, Object> config) {
        String id = MapUtil.getStr(config, APP_ID);
        String secret = MapUtil.getStr(config, APP_KEY);
        return new AliTranslateThread(id, secret, serial, version.getServerUrl(), target, original, segment);
    }
}
