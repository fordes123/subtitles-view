package org.fordes.subtitles.view.constant;

/**
 * @author fordes on 2022/1/24
 */
public class CommonConstant {

    public static final double SCENE_MIN_WIDTH = 1050.0;

    public static final double SCENE_MIN_HEIGHT = 700.0;

    public static final double SIDE_BAR_WIDTH = 250.0;

    public static final String PREFIX = "*.";

    public static final String TITLE_ALL_FILE = "选择文件以开始";

    public static final String TITLE_PATH = "选择文件路径";

    public static final String PATH_HOME = System.getProperty("user.home");

    public static final String ROOT_PATH = System.getProperty("user.dir");

    public static final String TEMP_PATH = ROOT_PATH+ "\\temp\\";

    public static final String DOWNLOAD_PATH = TEMP_PATH+ "download\\";

//    public static final String FILE_PATH = TEMP_PATH+ "file\\";

//    public static final String LIB_PATH = ROOT_PATH+  "\\lib\\";
//
//    public static final String SEVEN_ZIP_PATH = LIB_PATH+ "7z";

    /**
     * 7z解压命令 递归子目录、全部解压到指定文件夹、只解压指定格式文件
     */
//    public static final String UN_ARCHIVE_COMMAND_FORMAT = SEVEN_ZIP_PATH+ " e -aoa -bse0 -r {} -o{} {} -y";

    public static final String CONCISE_MODE = "简洁模式";

    public static final String FULL_MODE = "完整模式";

    public static final String TRANSLATE_REPLACE =  "替换模式";

    public static final String TRANSLATE_BILINGUAL =  "双语模式";

    public static final String URL_HOME = "https://github.com/fordes123/subtitles-view";

    public static final String URL_ISSUES = URL_HOME + "/issues";
}
