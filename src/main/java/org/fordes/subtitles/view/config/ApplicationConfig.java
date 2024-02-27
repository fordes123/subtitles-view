package org.fordes.subtitles.view.config;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.lang.Dict;
import cn.hutool.json.JSONUtil;
import cn.hutool.setting.yaml.YamlUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import javafx.scene.text.Font;
import lombok.Data;
import org.fordes.subtitles.view.constant.CommonConstant;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.nio.charset.Charset;

/**
 * @author fordes on 2022/4/17
 */
@Data
@Component
@ConfigurationProperties(prefix = "config")
public class ApplicationConfig implements Serializable {

    /**
     * 主题模式 false-浅色、true-深色、null-跟随系统
     */
    private Boolean theme = null;

    /**
     * 字体
     */
    private String fontFace = Font.getDefault().getFamily();;

    /**
     * 字体大小
     */
    private Integer fontSize = 18;

    /**
     * 编辑模式 false-简洁模式、true-完整模式
     */
    private Boolean editMode = Boolean.FALSE;

    /**
     * 退出模式 false-直接退出、true-最小化至托盘
     */
    private Boolean exitMode = Boolean.FALSE;

    /**
     * 默认文件输出路径
     */
    private String outPath = CommonConstant.PATH_HOME;

    /**
     * 语言列表选项 false-完整、true-精简
     */
    private Boolean languageListMode = Boolean.TRUE;

    @TableField(exist = false)
    private boolean currentTheme;

    private static final long serialVersionUID = 1L;

    private static final ClassPathResource resource = new ClassPathResource("application.yml");

    /**
     * 写入配置文件
     */
    public void dump() {
        Dict all = YamlUtil.load(resource.getReader(Charset.defaultCharset()));
        all.put("config", JSONUtil.parseObj(this));
        YamlUtil.dump(all, FileUtil.getWriter(resource.getFile(), Charset.defaultCharset(), false));
    }
}