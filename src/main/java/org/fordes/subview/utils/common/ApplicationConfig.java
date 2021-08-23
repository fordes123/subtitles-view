package org.fordes.subview.utils.common;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.fordes.subview.entity.DTO.SystemConfigDTO;
import org.fordes.subview.entity.PO.ApplicationInfo;
import org.fordes.subview.entity.PO.ApplicationSettings;
import org.fordes.subview.entity.PO.DictLanguage;
import org.fordes.subview.service.data.ApplicationInfoService;
import org.fordes.subview.service.data.DictLanguageService;
import org.fordes.subview.utils.SystemUtils;
import org.fordes.subview.utils.constants.MessageConstants;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 程序配置
 *
 * @author fordes on 2021/2/2
 */
@Slf4j
@Component
public class ApplicationConfig implements ApplicationRunner {

    @Getter
    @Setter
    private ApplicationSettings applicationSettings = null;


    @Getter
    private ApplicationInfo applicationInfo = null;


    private SystemConfigDTO systemConfig = null;

    @Getter
    private Map<Integer, Map<Integer, Map<String, String>>> languageMap = Maps.newHashMap();

    @Resource
    private ApplicationInfoService applicationInfoService;

    @Resource
    private DictLanguageService languageService;

    private static ApplicationConfig instance = new ApplicationConfig();

    public static String LINE_FEED = StrUtil.CRLF;

    private ApplicationConfig() {
    }

    public static ApplicationConfig getInstance() {
        return instance;
    }

    @PostConstruct
    public void init() throws Exception {
        instance = this;
        instance.applicationInfoService = this.applicationInfoService;
        getSystemConfig();
        refreshSetting();
        if (systemConfig != null) {
            if (!systemConfig.getArch().equalsIgnoreCase("amd64")) {
                throw new Exception("不支持的系统架构：" + systemConfig.getArch());
            }
            if (!systemConfig.getName().name().equalsIgnoreCase("windows")) {
                throw new Exception("不支持的系统类型："+ systemConfig.getName().name());
            }
            LINE_FEED = StrUtil.CRLF;
        }else {
            throw new Exception("获取系统配置失败！");
        }
        if (applicationInfo == null || applicationSettings == null) {
            throw new Exception("初始化程序数据失败！");
        }
    }


    public SystemConfigDTO getSystemConfig() {
        try {
            if (systemConfig == null) {
                this.systemConfig = SystemUtils.getSysConfig();
            }
        }catch (Exception e) {
            log.error("系统配置获取失败 => {}", e.getMessage());
        }
        return systemConfig;
    }

    public void setApplicationInfo(ApplicationInfo info) {
        this.applicationInfo = info;
        this.applicationSettings = info.formatSettings();
    }


    /**
     * 刷新设置
     * @throws Exception 数据错误
     */
    public void refreshSetting() throws Exception {
        applicationInfo = applicationInfoService.getInfo();
        if (applicationInfo == null) {
            throw new RuntimeException(MessageConstants.FIELD_START_DATA_ERROR);
        }else {
            applicationSettings = applicationInfo.formatSettings();
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        ThreadUtil.execAsync(this::initLanguageMap);
    }

    private void initLanguageMap() {
        List<DictLanguage> list = languageService.findList(null);
        Map<Integer, List<DictLanguage>> typeMap = list.stream().collect(Collectors.groupingBy(DictLanguage::getServer_type));
        typeMap.forEach((k, v)-> {
            Map<Integer, Map<String, String>> supplierMap = Maps.newHashMap();
            v.stream().collect(Collectors.groupingBy(DictLanguage::getSupplier_id)).forEach((x, y)-> {
                Map<String, String> itemMap = Maps.newHashMap();
                y.forEach(item -> itemMap.put(item.getLanguage(), item.getCode()));
                supplierMap.put(x, itemMap);
            });
            languageMap.put(k, supplierMap);
        });
        list.clear();
    }
}
