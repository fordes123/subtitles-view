package org.fordes.subview.entity;

import javafx.stage.Stage;

import java.io.File;

/**
 * 输入参数集合
 */
public class inputSet {

    private FileSet Subtitles = new FileSet();//字幕文件
    private FileSet Video = new FileSet();//视频文件
    private String code;//文件读写编码
    private String text;//生成或读取的文本
    private Boolean theme;//主题样式，false-浅色，true-深色
    private Stage stage;//目标窗体


    //文件参数对象
    class FileSet{
        public File file;
        public int type;
    }

    public void setSubtitles(File file,int type) {
        Subtitles.file = file;
        Subtitles.type = type;
    }

    public void setVideo(File file,int type) {
        this.Video.file = file;
        this.Video.type = type;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setText(String text) {
        this.text = text;
    }


    public void setTheme(Boolean theme) { this.theme = theme; }

    public void setStage(Stage stage) { this.stage = stage; }

    public FileSet getSubtitles() {
        return Subtitles;
    }

    public File getVideoFile(){ return Video.file;}

    public File getSubFile(){ return Subtitles.file;}

    public int getSubType(){ return Subtitles.type;}

    public int getVideoType(){ return Video.type;}

    public FileSet getVideo() {
        return Video;
    }

    public String getCode() {
        return code;
    }

    public String getText() {
        return text;
    }

    public Boolean getTheme() { return theme; }

    public Stage getStage() { return stage; }
}
