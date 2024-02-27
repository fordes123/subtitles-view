package org.fordes.subtitles.view.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.sevenzipjbinding.ExtractOperationResult;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.simple.ISimpleInArchive;
import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem;
import org.fordes.subtitles.view.enums.FileEnum;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;

/**
 * 文件解压工具类
 *
 * @author fordes on 2022/4/4
 */
@Slf4j
public class ArchiveUtil {

    /**
     * 解压文件至当前路径下uuid命名路径 并删除原文件
     * 将排除不受支持的文件
     *
     * @param file 压缩文件
     * @return 文件路径
     */
    public static Collection<File> unArchiveToCurrentPath(File file) {
        Collection<File> result = Collections.emptyList();
        if (FileUtil.exist(file)) {
            String outPath = StrUtil.concat(false, file.getParent(), File.separator, UUID.fastUUID().toString());
            //创建目标文件夹
            if (!FileUtil.exist(outPath)) {
                FileUtil.mkdir(outPath);
            }
            String suffix = FileUtil.getSuffix(file);
            if (StrUtil.equalsAnyIgnoreCase(suffix, FileEnum.SUPPORT_SUBTITLE)) {
                File newFile = FileUtil.file(StrUtil.concat(false, outPath, File.separator, URLUtil.decode(file.getName(), Charset.defaultCharset())));
                FileUtil.move(file, newFile, true);
                result = CollUtil.newArrayList(newFile);
            } else {
                result = unArchiveFile(file, outPath, FileEnum.SUPPORT_SUBTITLE);
            }
            FileUtil.del(file);
        }
        return result;
    }


    /**
     * 解压文件，不保留内部结构
     *
     * @param in      压缩文件路径
     * @param outPath 输出路径
     * @param filter  指定需要提取的文件后缀 如 ass
     */
    public static Collection<File> unArchiveFile(File in, String outPath, String... filter) {
        Collection<File> result = CollUtil.newArrayList();
        TimeInterval interval = DateUtil.timer();

        RandomAccessFile randomAccessFile = null;
        IInArchive inArchive = null;
        try {
            randomAccessFile = new RandomAccessFile(in.getPath(), "r");
            inArchive = SevenZip.openInArchive(null, new RandomAccessFileInStream(randomAccessFile));
            ISimpleInArchive archive = inArchive.getSimpleInterface();
            for (ISimpleInArchiveItem item : archive.getArchiveItems()) {
                if (!item.isFolder() && StrUtil.equalsAnyIgnoreCase(FileUtil.getSuffix(item.getPath()), filter)) {
                    File file = FileUtil.file(StrUtil.concat(false, outPath, File.separator, item.getPath()));
                    ExtractOperationResult operationResult = item.extractSlow(data -> {
                        FileUtil.writeBytes(data, file);
                        return data.length;
                    });
                    if (operationResult == ExtractOperationResult.OK) {
                        result.add(file);
                        log.debug("提取成功 => {}", item.getPath());
                    } else {
                        log.error("提取失败 => {}\n{}", item.getPath(), operationResult);
                    }
                }
            }
        } catch (FileNotFoundException | SevenZipException e) {
            log.error("解压文件出错！{} => {}", in.getPath(), outPath);
            log.error(ExceptionUtil.stacktraceToString(e));
        } finally {
            if (inArchive != null) {
                try {
                    inArchive.close();
                } catch (SevenZipException e) {
                    log.error(ExceptionUtil.stacktraceToString(e));
                }
            }
            if (randomAccessFile != null) {
                try {
                    randomAccessFile.close();
                } catch (IOException e) {
                    log.error(ExceptionUtil.stacktraceToString(e));
                }
            }
            if (result.isEmpty()) {
                FileUtil.del(outPath);
            }

        }
        log.debug("解压文件：{} => {}，耗时：{} ms", in.getPath(), outPath, interval.intervalMs());
        return result;
    }
}
