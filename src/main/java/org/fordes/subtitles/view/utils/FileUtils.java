package org.fordes.subtitles.view.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import lombok.extern.slf4j.Slf4j;
import org.fordes.subtitles.view.constant.CommonConstant;
import org.fordes.subtitles.view.enums.FileEnum;
import org.fordes.subtitles.view.model.DTO.Subtitle;
import org.fordes.subtitles.view.model.DTO.Video;
import org.fordes.subtitles.view.model.PO.FileRecord;
import org.fordes.subtitles.view.utils.submerge.utils.EncodeUtils;
import org.springframework.lang.NonNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

import static cn.hutool.core.thread.ThreadUtil.sleep;

/**
 * 文件工具类
 *
 * @author fordes on 2022/1/23
 */
@Slf4j
public class FileUtils {

    /**
     * 根据路径获取文件流，支持http和resource
     * @param path
     * @return
     */
    public static InputStream getStream(@NonNull String path) {
        if (ReUtil.isMatch("^http[s]?://.*", path)) {
            HttpResponse response = HttpUtil.createGet(path, true).execute();
            if (response.isOk()) {
                return response.bodyStream();
            }
        }else {
            ClassPathResource resource = new ClassPathResource(path);
            return resource.getStream();
        }

        throw new RuntimeException(StrUtil.format("resource: {} not found", path));
    }

    /**
     * 选择文件
     * @param title 选择框标题内容
     * @param items 选项
     * @return 返回指定文件选择器
     */
    public static FileChooser chooseFile(String title, FileEnum... items) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.setInitialDirectory(new File(CommonConstant.PATH_HOME));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("全部文件", "*.*"));
        if (ArrayUtil.isNotEmpty(items)) {
            fileChooser.getExtensionFilters().addAll(Arrays.stream(items)
                    .filter(e -> e.support)
                    .map(e -> new FileChooser.ExtensionFilter(e.suffix, CommonConstant.PREFIX + e.suffix))
                    .collect(Collectors.toList()));
        }
        return fileChooser;
    }


    /**
     * 选择路径
     * @return 文件夹选择器
     */
    public static DirectoryChooser choosePath(String path) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(CommonConstant.TITLE_PATH);
        directoryChooser.setInitialDirectory(FileUtil.file(StrUtil.isNotEmpty(path)? path: CommonConstant.PATH_HOME));
        return directoryChooser;
    }

    /**
     * 读取文件信息
     * @param file 文件
     * @return 文件信息实例
     */
    public static <T> FileRecord readFileInfo(File file) throws IOException {
        String suffix = FileUtil.extName(file);
        FileRecord info;
        FileEnum type = FileEnum.of(FileUtil.getSuffix(file));

        assert type != null;
        if (type.media) {
            info =  new Video().setFormat(type);
        }else {
            info = new Subtitle().setCharset(EncodeUtils.guessEncoding(file)).setFormat(type);
        }

        return info.setFile(file)
                .setFile_name(file.getName())
                .setPath(file.getPath())
                .setSize(FileUtil.readableFileSize(file))
                .setFile_modify_time(FileUtil.lastModifiedTime(file));
    }

    /**
     * 加锁将集合按行写入文件
     *
     * @param file    目标文件
     * @param content 内容集合
     */
    public static void write(File file, Collection<String> content, String charset) {
        write(file,CollUtil.join(content, StrUtil.CRLF), charset);
    }

    public static void write(File file, String content, String charset) {
        if (StrUtil.isNotEmpty(content)) {
            try (RandomAccessFile accessFile = new RandomAccessFile(file, "rw");
                 FileChannel channel = accessFile.getChannel()) {
                //加锁写入文件，如获取不到锁则休眠
                FileLock fileLock = null;
                while (true) {
                    try {
                        fileLock = channel.tryLock();
                        break;
                    } catch (Exception e) {
                        sleep(1000);
                    }
                }
                accessFile.seek(accessFile.length());
                accessFile.write(content.getBytes(charset));
                accessFile.write(StrUtil.CRLF.getBytes(charset));
            } catch (IOException ioException) {
                log.error("写入文件出错，{} => {}", file.getPath(), ioException.getMessage());
                throw new RuntimeException("写入文件出错");
            }
        }
    }
}
