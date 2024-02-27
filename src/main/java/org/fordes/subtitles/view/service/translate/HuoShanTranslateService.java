package org.fordes.subtitles.view.service.translate;

import cn.hutool.core.map.MapUtil;
import org.fordes.subtitles.view.enums.ServiceProvider;
import org.fordes.subtitles.view.factory.TranslateServiceFactory;
import org.fordes.subtitles.view.model.DTO.TranslateResult;
import org.fordes.subtitles.view.model.PO.Version;
import org.fordes.subtitles.view.service.translate.thread.HuoShanTranslateThread;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author fordes on 2022/7/31
 */
@Service
public class HuoShanTranslateService extends TranslateService implements InitializingBean {

    static final String AccessKeyID = "AccessKeyID";

    static final String SecretAccessKey = "SecretAccessKey";

    @Value("${service.translate.huoshan.region: cn-north-1}")
    private String region;

    @Value("${service.translate.huoshan.version-date: 2020-06-01}")
    private String versionDate;

    @Override
    public void afterPropertiesSet() {
        TranslateServiceFactory.register(this, ServiceProvider.HUOSHAN.name());
    }

    @Override
    public Callable<TranslateResult> createTask(ThreadPoolExecutor executor, int serial, String segment, String target,
                                                String original, Version version, Map<String, Object> config) {
        return new HuoShanTranslateThread(versionDate, region, MapUtil.getStr(config, AccessKeyID), MapUtil.getStr(config, SecretAccessKey),
                serial, version.getServerUrl(), target, original, segment);
    }
}
