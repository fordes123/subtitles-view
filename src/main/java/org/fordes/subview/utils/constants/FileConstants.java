package org.fordes.subview.utils.constants;

import cn.hutool.core.util.ArrayUtil;
import org.fordes.subview.enums.EnumBasic;
import org.fordes.subview.enums.SubtitlesTypeEnum;
import org.fordes.subview.enums.VideoTypeEnum;

/**
 * 文件相关常量
 *
 * @author Fordes on 2020/11/8
 */
public class FileConstants {

    public static final String PREFIX = "*.";

    public static final String RESULTS_NOT_SUPPORT = "暂不支持该类型文件";

    public static final String RESULTS_OPEN_ERROR = "读取文件失败";

    public static final String TITLE_ALL_FILE = "选择字幕或视频文件以开始";

    public static final String TITLE_SUBTITLE_FILE = "选择字幕文件以开始";

    public static final String TITLE_VIDEO_FILE = "选择视频文件以开始";

    public static final String TITLE_PATH = "选择文件路径";

    public static final String PATH_HOME = System.getProperty("user.home");

    public static EnumBasic[] subtitlesFile = SubtitlesTypeEnum.values();

    public static EnumBasic[] videoFile = VideoTypeEnum.values();

    public static EnumBasic[] allFile = ArrayUtil.addAll(subtitlesFile, videoFile);
}
