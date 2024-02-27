package org.fordes.subtitles.view.service.translate;

import cn.hutool.core.map.MapUtil;
import org.fordes.subtitles.view.enums.ServiceProvider;
import org.fordes.subtitles.view.factory.TranslateServiceFactory;
import org.fordes.subtitles.view.model.DTO.TranslateResult;
import org.fordes.subtitles.view.model.PO.Version;
import org.fordes.subtitles.view.service.translate.thread.TencentTranslateThread;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author fordes on 2022/7/11
 */
@Service
public class TencentTranslateService extends TranslateService implements InitializingBean {

    static final String SECRET_ID = "Secret Id";

    static final String SECRET_KEY = "Secret Key";


    @Value("${service.translate.tencent.region: ap-shanghai}")
    private String region;

    @Override
    public void afterPropertiesSet() {
        TranslateServiceFactory.register(this, ServiceProvider.TENCENT.name());
    }


    @Override
    public Callable<TranslateResult> createTask(ThreadPoolExecutor executor, int serial, String segment, String target, String original, Version version, Map<String, Object> config) {
        String id = MapUtil.getStr(config, SECRET_ID);
        String key = MapUtil.getStr(config, SECRET_KEY);
        return new TencentTranslateThread(id ,key, region, serial, version.getServerUrl(), target, original, segment);
    }
}
