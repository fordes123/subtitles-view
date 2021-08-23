package org.fordes.subview.utils.constants;

import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;

import java.net.URL;
import java.util.List;

/**
 * 程序常量类
 *
 * @author fordes on 2020/12/25
 */
public class CommonConstants {

    /**
     * 提示语
     */
    public static String TIPS_DRAG_DEFAULT = "拖放或点此选择文件以开始";

    public static String TIPS_DRAG_OPEN = "松手即可打开文件";

    public static String TIPS_DRAG_NOT_SUPPORT = "暂不支持该类型";

    /**
     * URL常量
     */
    public static String URL_AUTHOR_HOME = "https://blog.fordes.top/";

    public static String DEFAULT_CHARSET = "UTF-8";

    /**
     * 支持的压缩文件类型
     */
    public static String RAR = "rar";

    public static String ZIP = "zip";

    /**
     * 全局路径
     */
    public static final String ROOT_PATH = System.getProperty("user.dir");

    public static final String TEMP_PATH = StrUtil.concat(false, ROOT_PATH,  "\\temp\\");

    public static final String LIB_PATH = StrUtil.concat(false, ROOT_PATH,  "\\lib\\");

    public static final String SEVEN_ZIP_PATH = StrUtil.concat(false, LIB_PATH, "7z.exe");

    /**
     * 格式化模板
     */
    public static final String EDIT_INDICATOR_FORMAT = "行{},列{}";
    public static final String GITHUB_LINK_FORMAT = "https://github.com/{}";

    /**
     * 系统相关
     */
    public static final String MAC_OS_FILE_MANAGER = "com.apple.eio.FileManager";

    public static final String OPEN_URL = "openURL";

    public static final String FILE_PROTOCOL_HANDLER = "rundll32 url.dll,FileProtocolHandler";

    public static final List<String> BROWSERS = Lists.newArrayList("firefox", "opera", "konqueror", "epiphany",
            "mozilla", "netscape");

    /**
     * 程序相关
     */
    public static final URL APPLICATION_TRAY_ICON_URL = CommonConstants.class.getResource("/images/public/logo16x.png");

    public static final URL APPLICATION_LOGO_ICON_URL = CommonConstants.class.getResource("/images/public/logo.png");

    public static final URL APPLICATION_SYSTEM_EXIT_ICON_URL = CommonConstants.class.getResource("/images/application/system/exit.png");

    public static final URL APPLICATION_SYSTEM_OPEN_ICON_URL = CommonConstants.class.getResource("/images/application/system/open.png");
    /**
     * 其他常量
     */
    public static final String EDIT_SIMPLE_MODE = " 简洁模式";

    public static final String EDIT_SENIOR_MODE = " 完整模式";

    public static final String TRANSLATION_AUTO = "自动检测";

    public static final String TRANSLATION_CN = "中文";

    public static final String TITLE_BAR_ID = "top";

    public static final String INTERFACE_PROMPT = "预置账号，如需覆盖请直接修改";

    public static final String USE_HELP_TEXT = new StringBuffer()
            .append("Subtitles View 是围绕音视频字幕生成、查看、编辑的工具型软件；\n")
            .append("软件提供字幕文件检索、便捷修改、视频转字幕（第三方支持）、字幕翻译（第三方支持）、视频字幕合成等功能；\n\n")
            .append("注意，语音转写、文字翻译服务均为第三方提供，其质量、效率、费用等均与本软件无关；\n")
            .append("下面是一些常用的问题与解答，如有其他问题请移步项目主页：\n\n\n")
            .append("Q：支持哪些字幕格式？\n")
            .append("A：目前支持SRT、ASS、LRC三种字幕格式。\n\n")
            .append("Q：支持哪些音视频格式？\n")
            .append("A：目前版本基于MediaPlayer，支持包含VP6视频和MP3音频的FLV、使用H.264/AVC视频压缩的MPEG-4以及非压缩PCM的AIFF、非压缩PCM的WAV、使用AAC音频的MPEG-4 。\n\n")
            .append("Q：视频转换出的字幕错误很多？\n")
            .append("A：语音转换由第三方提供，如出现精度较低，可尝试检查视频语言和选择的原语言是否一致；或者更换语言服务接口重试；\n\n")
            .append("Q：视频转换、字幕翻译 失败或出错？\n")
            .append("A：可先尝试检查接口剩余额度，软件功能异常可至项目主页反馈，并附上日志文件。\n\n")
            .append("Q：可不可以支持xx语言的转换？\n")
            .append("A：如果有第三方平台支持此种语言并开放接口，可以反馈至项目主页，酌情安排适配。\n\n")
            .append("Q：是否有计划支持同步翻译，即边播放视频边翻译显示字幕 ？\n")
            .append("A：这似乎是播放器该干的事，暂时不会支持。\n\n")
            .append("Q：软件处理ASS格式字幕体验很差？\n")
            .append("A：ASS字幕格式比较复杂，现有解析器以及交互设计无法完全适配，需等待后续改进\n\n")
            .append("Q：快捷键有哪些？\n")
            .append("A：目前快捷键仅用于基础编辑中工具栏控制：Ctrl+T 显示（隐藏）工具栏、Ctrl+F 搜索、Ctrl+R 替换、Ctrl+J 行跳转、Ctrl+E 编码切换、Ctrl+Q 文字样式。\n\n")
            .append("Q：在工具栏中改变文本样式、显示模式切换页面后就还原了？\n")
            .append("A：设计如此，默认文本样式等只有到设置页更改并保存才会生效。\n\n")
            .append("Q：找不到接口设置位置？如何修改已经设置的服务接口？\n")
            .append("A：接口设置位于开始页，使用相关服务且未设置时会自动弹出；后续可于托盘菜单右键->设置 处进入。").toString();

}
