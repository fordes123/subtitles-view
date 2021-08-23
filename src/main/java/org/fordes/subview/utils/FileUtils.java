package org.fordes.subview.utils;

import cn.hutool.core.io.FileUtil;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import lombok.extern.slf4j.Slf4j;
import org.fordes.subview.entity.DO.FileAbstract;
import org.fordes.subview.entity.DTO.data.Subtitles;
import org.fordes.subview.entity.DTO.data.Video;
import org.fordes.subview.enums.DataTypeEnum;
import org.fordes.subview.enums.EnumBasic;
import org.fordes.subview.enums.SubtitlesTypeEnum;
import org.fordes.subview.enums.VideoTypeEnum;
import org.fordes.subview.utils.constants.FileConstants;
import org.fordes.subview.utils.submerge.utils.EncodeUtils;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 文件相关
 *
 * @author Fordes on 2020/11/8
 */
@Slf4j
public class FileUtils {

    /**
     * 选择文件
     * @param title 选择框标题内容
     * @param items 选项
     * @return 返回指定文件选择器
     */
    public static FileChooser chooseFile(String title, EnumBasic... items) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.setInitialDirectory(new File(FileConstants.PATH_HOME));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("全部文件", "*.*"));
        fileChooser.getExtensionFilters().addAll(Arrays.stream(items)
                .map(e->new FileChooser.ExtensionFilter(e.getRemark(), FileConstants.PREFIX+ e.getSuffix())).collect(Collectors.toList()));
        return fileChooser;
    }


    /**
     * 选择路径
     * @return 文件夹选择器
     */
    public static DirectoryChooser choosePath() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(FileConstants.TITLE_PATH);
        directoryChooser.setInitialDirectory(new File(FileConstants.PATH_HOME));
        return directoryChooser;
    }

    /**
     * 读取文件信息
     * @param file 文件
     * @return 文件信息实例
     */
    public static FileAbstract readFileInfo(File file) throws IOException {
        String suffix = FileUtil.extName(file);
        FileAbstract info;
        if (SubtitlesTypeEnum.isSubtitles(suffix)) {
            info = new Subtitles().setCharset(EncodeUtils.guessEncoding(file))
                    .setFormat(SubtitlesTypeEnum.getType(suffix))
                    .setType(DataTypeEnum.VIDEO);
        }else {
            info = new Video().setFormat(VideoTypeEnum.getType(suffix)).setType(DataTypeEnum.SUBTITLES);
        }
        info.setFile(file)
                .setFile_name(file.getName())
                .setPath(file.getPath())
                .setSize(FileUtil.readableFileSize(file))
                .setFile_modify_time(FileUtil.lastModifiedTime(file));
        return info;
    }

}
