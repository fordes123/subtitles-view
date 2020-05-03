package org.fordes.subview.entity;

import javafx.stage.Stage;

import java.io.File;

/**
 * 输入参数集合
 */
public class inputSet {

    private FileSet Subtitles = new FileSet();
    private FileSet Video = new FileSet();
    private String code;
    private String text;
    private Boolean theme;
    private Stage stage;


    
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
