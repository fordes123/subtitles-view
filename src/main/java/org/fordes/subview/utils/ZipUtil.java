package org.fordes.subview.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.fordes.subview.enums.SevenZipEnum;
import org.fordes.subview.enums.SubtitlesTypeEnum;
import org.fordes.subview.utils.constants.CommonConstants;

import java.io.File;
import java.io.IOException;
import java.util.Set;

/**
 * 文件解压工具类
 * 调用7zip解压，仅适用于windows操作系统
 */
@Slf4j
public class ZipUtil {

    public static Set<String> formats = Sets.newHashSet("rar", "zip", "7z", "gz", "xz");

    /**
     * 解压至文件命名路径并删除文件
     *
     * @param file 压缩文件
     * @return 文件路径
     */
    public static String unZipToCurrentPath(File file) {
        String outPath = StrUtil.concat(false, file.getParent(), StrUtil.BACKSLASH, FileUtil.getPrefix(file));
        try {
            //判断是否为单字幕文件
            if (SubtitlesTypeEnum.isSubtitles(FileUtil.getSuffix(file))) {
                String path = StrUtil.concat(false, file.getParent(), String.valueOf(System.currentTimeMillis()));
                FileUtil.move(file, FileUtil.mkdir(path), true);
                return path;
            }
            //创建目标文件夹
            if (FileUtil.exist(outPath)){
                FileUtil.del(outPath);
            }
            FileUtil.mkdir(outPath);
            //解压文件
            unZipFile(file, outPath);
            //删除原文件
            FileUtil.del(file);
            //整合并删除非字幕文件
            movAllFile(outPath, outPath);
            return outPath;
        } catch (Exception e) {
            FileUtil.del(outPath);
            return StrUtil.EMPTY;
        }
    }

    public static String unZipToCurrentPath(String path) {
        return unZipToCurrentPath(new File(path));
    }

    /**
     * 递归移动所有字幕文件至指定路径, 并删除多余
     * @param path 源路径
     * @param target 目标路径
     */
    public static void movAllFile(String path, String target) {
        if (FileUtil.isDirectory(path)){
            for (File e : FileUtil.ls(path)) {
                movAllFile(e.getPath(), target);
            }if (!path.equals(target)) {
                FileUtil.del(path);
            }
        }else {
            if (SubtitlesTypeEnum.isSubtitles(FileUtil.getSuffix(path))) {
                FileUtil.move(new File(path), new File(target), true);
            } else {
                FileUtil.del(path);
            }
        }
    }

    public static void unZipFile(File zipFile, String outPath) throws IOException, InterruptedException {
        unZipFile(zipFile, outPath, null);
    }


    public static void unZipFile(File zipFile, String outPath, String password) throws IOException, InterruptedException {
        long start = System.currentTimeMillis();
        StrBuilder command = new StrBuilder();
        command.append("\"")
                .append(CommonConstants.SEVEN_ZIP_PATH)
                .append("\" x -aoa \"")
                .append(zipFile.getPath())
                .append(StrUtil.isNotBlank(password)?"\" -p"+ password:"\"")
                .append(" -o\"")
                .append(outPath)
                .append("\"");
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(command.toString());
            process.waitFor();
            if (process.exitValue()>1){
                throw new RuntimeException();
            }
            log.debug("命令：{}, 退出值：{}, 状态：{}", command, process.exitValue(), SevenZipEnum.getStatus(process.exitValue()));
        }catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        }finally {
            if (ObjectUtil.isNotNull(process)) {
                process.destroy();
            }
            log.debug("解压文件：{} => {}，耗时：{} ms", zipFile.getPath(), outPath, System.currentTimeMillis()- start);
        }
    }
}
