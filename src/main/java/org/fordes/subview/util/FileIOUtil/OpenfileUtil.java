package org.fordes.subview.util.FileIOUtil;

import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;

/**
 * 返回FileChooser，选择指定文件
 * -拟加入配置文件中
 * @author Fordes
 */

public class OpenfileUtil{

    private FileChooser fileChooser=new FileChooser();
    private DirectoryChooser PathChooser=new DirectoryChooser();


    public FileChooser Load(){
        fileChooser.setTitle("选择字幕或视频文件以开始");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home"))
        );
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Files", "*.*"),
                new FileChooser.ExtensionFilter("SRT", "*.srt"),
                new FileChooser.ExtensionFilter("ASS", "*.ass"),
                new FileChooser.ExtensionFilter("LRC", "*.lrc"),
                new FileChooser.ExtensionFilter("MP4", "*.mp4")
        );
        return fileChooser;
    }

    public FileChooser LoadVideo(){
        fileChooser.setTitle("选择视频文件以开始");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("MP4", "*.mp4")
        );
        return fileChooser;
    }

    public DirectoryChooser LoadPath(){
        fileChooser.setTitle("选择文件存放路径");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        return PathChooser;
    }
}
