package org.fordes.subtitles.view.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import org.fordes.subtitles.view.enums.ServiceProvider;
import org.fordes.subtitles.view.enums.ServiceType;
import org.fordes.subtitles.view.mapper.InterfaceMapper;
import org.fordes.subtitles.view.model.PO.Language;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 缓存
 *
 * @author fordes on 2022/7/27
 */
public class CacheUtil {

    public static final Map<ServiceType, Map<ServiceProvider, List<Language>>> languageMap = MapUtil.newHashMap();

    /**
     * 初始化语言字典
     *
     * @param data 数据
     */
    public static void initLanguageDict(List<Dict> data) {
        data.stream().collect(Collectors.groupingBy(e -> ServiceType.valueOf(e.getStr(Language.COL_TYPE))))
                .forEach((k, v) -> {
                    Map<ServiceProvider, List<Language>> providerMap = MapUtil.newHashMap();
                    Arrays.stream(ServiceProvider.values()).forEach(p -> {
                        Map<String, List<Language>> idMap = v.stream()
                                .filter(q -> q.containsKey(p.name().toLowerCase()))
                                .map(q -> {
                                    String target = MapUtil.getStr(q, p.name().toLowerCase() + Language.TARGET);
                                    return new Language()
                                            .setId(q.getInt(Language.COL_ID))
                                            .setName(q.getStr(Language.COL_NAME))
                                            .setCode(q.getStr(p.name().toLowerCase()))
                                            .setGeneral(q.getBool(Language.COL_GENERAL))
                                            .set_target(StrUtil.split(target, StrUtil.COMMA, true, true));
                                })
                                .collect(Collectors.groupingBy(Language::getCode, Collectors.toList()));

                        List<Language> languageList = CollUtil.newArrayList();
                        idMap.forEach((x, y) -> {
                            Language item = y.get(0);
                            if (item.get_target().isEmpty()) {
                                item.setTarget(idMap.values().stream().map(e -> e.get(0)).collect(Collectors.toList()));
                            } else {
                                item.setTarget(item.get_target().stream()
                                        .map(e -> idMap.get(e).get(0))
                                        .collect(Collectors.toList()));
                            }
                            languageList.add(item);
                        });
                        providerMap.put(p, languageList);
                    });
                    languageMap.put(k, providerMap);
                });
    }

    /**
     * 获取语言字典
     *
     * @param type     {@link ServiceType}
     * @param provider {@link ServiceProvider}
     * @param general  是否只获取常用语言
     * @return {@link List<Language>}
     */
    public static List<Language> getLanguageDict(ServiceType type, ServiceProvider provider, boolean general) {
        if (languageMap.isEmpty()) {
            CacheUtil.initLanguageDict(SpringUtil.getBean(InterfaceMapper.class).getLanguageList());
        }
        List<Language> result = languageMap.get(type).get(provider);
        return general ?
                result.stream().filter(Language::isGeneral).collect(Collectors.toList()) : result;
    }
}
