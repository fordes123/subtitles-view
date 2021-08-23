package org.fordes.subview.entity.PO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 *
 * @author fordes on 2021/3/17
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class ApplicationSettings {

    /**
     * 默认字体名称（仅编辑器）
     */
    private String font_face;

    /**
     * 默认字体大小（仅编辑器）
     */
    private Integer font_size;

    /**
     * 颜色模式
     */
    private boolean edit_mode;

    /**
     * 语言转写首选项
     */
    private Integer voice_preferred;

    /**
     * 文字翻译首选项
     */
    private Integer translation_preferred;

    /**
     * 自动保存修改
     */
    private boolean auto_save;

    /**
     * 自动打开最后编辑文件
     */
    private boolean auto_open;

    /**
     * 定时切换至深色模式
     */
    private boolean auto_theme;

    /**
     * 定时切换-开始时间
     */
    private Integer auto_theme_start;

    /**
     * 定时切换-结束时间
     */
    private Integer auto_theme_end;

    /**
     * 快速退出
     */
    private Boolean quick_exit;
}
