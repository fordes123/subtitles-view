package org.fordes.subview.entity;

import javafx.stage.Stage;

import java.io.File;

/**
 * 输入参数集合
 */
public class inputSet {

    private FileSet Subtitles = null;
    private FileSet Video = null;
    private String code;
    private String text;
    private Boolean theme;
    private Stage stage;
    private VoiceService voiceService;

    public FileSet getSubtitles() {
        return Subtitles;
    }

    public FileSet getVideo() {
        return Video;
    }

    public String getCode() {
        return code;
    }

    public String getText() {
        return text;
    }

    public Boolean getTheme() {
        return theme;
    }

    public Stage getStage() {
        return stage;
    }

    public VoiceService getVoiceService() {
        return voiceService;
    }

    public void setSubtitles(FileSet subtitles) {
        Subtitles = subtitles;
    }

    public void setVideo(FileSet video) {
        Video = video;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setTheme(Boolean theme) {
        this.theme = theme;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setVoiceService(VoiceService voiceService) {
        this.voiceService = voiceService;
    }
}
