package org.fordes.subtitles.view.factory;

import org.fordes.subtitles.view.service.translate.TranslateService;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author fordes on 2022/7/11
 */
public class TranslateServiceFactory {

    private static final Map<String, TranslateService> services = new ConcurrentHashMap<>();

    public static TranslateService getService(String provider) {
        return services.getOrDefault(provider, null);
    }

    public static void register(TranslateService service, String provider) {
        services.put(provider, service);
    }
}
