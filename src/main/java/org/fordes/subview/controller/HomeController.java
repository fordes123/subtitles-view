package org.fordes.subview.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import de.felixroske.jfxsupport.FXMLController;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.fordes.subview.entity.DTO.data.Subtitles;
import org.fordes.subview.enums.SubtitlesTypeEnum;
import org.fordes.subview.enums.ToastEnum;
import org.fordes.subview.utils.SubtitlesUtil;
import org.fordes.subview.utils.common.ApplicationCommon;
import org.fordes.subview.utils.common.ApplicationConfig;
import org.fordes.subview.utils.constants.CommonConstants;
import org.fordes.subview.utils.constants.MessageConstants;
import org.fordes.subview.utils.constants.StyleConstants;
import org.fordes.subview.utils.submerge.subtitle.ass.ASSTime;
import org.fordes.subview.utils.submerge.subtitle.common.TimedTextFile;
import org.fordes.subview.utils.submerge.subtitle.srt.SRTTime;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.URL;
import java.time.LocalTime;
import java.util.List;
import java.util.ResourceBundle;

@Component
@FXMLController
@Slf4j
public class HomeController extends BasicController implements Initializable {

    @FXML
    @Getter
    private GridPane homePanel;

    @FXML
    private Label timeAlignment_file_name, timeAlignment_time_start, timeAlignment_time_size, timeAlignment_encode,
            timeAlignment_file_size, edit_mode_label, editIndicator;

    @FXML
    @Getter
    private ToggleButton edit_mode;

    @FXML
    private Button perform_time_alignment;

    @FXML
    private GridPane timeAlignmentPanel, editorPanel;

    @FXML
    private TextArea editor, timeAlignment_area;

    @FXML
    private ToggleGroup listGroup, timeAlignment_mode;

    @FXML
    private ToggleButton basicEdit, timeAlignment;

    @FXML
    private RadioButton timeAlignment_processing_all;

    @FXML
    private ComboBox<String> timeAlignment_hour, timeAlignment_min, timeAlignment_sec, timeAlignment_ms;

    @FXML
    @Getter
    private RowConstraints toolBarSpace, contentSpace;

    @FXML
    private TextField timeAlignment_input;

    @Resource
    private ToastController toastController;

    private static int row = 1, column = 0;

    private final ChangeListener<String> conciseEditListener = (observable, oldValue, newValue) -> {
        log.error("触发简洁模式修改");
        Subtitles subtitles = SubtitlesUtil.editorChange(ApplicationCommon.getInstance().getSubtitles(),
                StrUtil.split(newValue, StrUtil.C_LF), row);
        ApplicationCommon.getInstance().setSubtitles(subtitles);
        refreshEditor(false);

    };

    private final ChangeListener<String> completeEditListener = (observable, oldValue, newValue) -> {
        Subtitles subtitles = ApplicationCommon.getInstance().getSubtitles();
        try {
            TimedTextFile timedTextFile = SubtitlesUtil.readSubtitleStr(newValue, (SubtitlesTypeEnum) subtitles.getFormat());
            subtitles.setTimedTextFile(timedTextFile);
            refreshEditor(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    };

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //初始化
        basicEdit.setOnAction(event -> doBasicEdit());
        timeAlignment.setOnAction(event -> doTimeAlignment());
        perform_time_alignment.setOnAction(event -> onHandleTimeline());
        homePanel.visibleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                Font font = new Font(ApplicationConfig.getInstance().getApplicationSettings().getFont_face(),
                        ApplicationConfig.getInstance().getApplicationSettings().getFont_size());
                editor.setFont(font);
                timeAlignment_area.setFont(font);
                edit_mode.setSelected(ApplicationConfig.getInstance().getApplicationSettings().isEdit_mode());
            }

        });

        //编辑器光标事件
        editorPositionListener(editor);
        editorPositionListener(timeAlignment_area);
        //编辑器模式
        edit_mode.selectedProperty().addListener((observable, oldValue, newValue) -> editModeChange(newValue));


        //默认功能项
        this.focus(editorPanel);
//        editor.textProperty().addListener(edit_mode.isSelected() ? completeEditListener : conciseEditListener);
    }

    /*基础编辑*/
    public void doBasicEdit() {
        listGroup.selectToggle(basicEdit);
        if (!this.focus(editorPanel)) {
            refreshEditor(true);
        }
    }

    /*时间轴对齐*/
    public void doTimeAlignment() {
        listGroup.selectToggle(timeAlignment);
        if (!this.focus(timeAlignmentPanel)) {
            refreshEditor(false);
        }
        timeAlignment_hour.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> parsingSelection());
        timeAlignment_min.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> parsingSelection());
        timeAlignment_sec.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> parsingSelection());
        timeAlignment_ms.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> parsingSelection());
        timeAlignment_input.textProperty().addListener((observable, oldValue, newValue) -> parsingInput());

    }

    public void onHandleTimeline() {
        String line = timeAlignment_input.getText();
        if (StrUtil.isNotEmpty(line) && !timeAlignment_input.getStyleClass().contains(StyleConstants.INPUT_ERROR_STYLE_CLASS)) {
            try {
                LocalTime time = LocalTime.of(Integer.parseInt(timeAlignment_hour.getValue()),
                        Integer.parseInt(timeAlignment_min.getValue()),
                        Integer.parseInt(timeAlignment_sec.getValue()),
                        Integer.parseInt(timeAlignment_ms.getValue()) * 1000000);
                Subtitles subtitles = ApplicationCommon.getInstance().getSubtitles();
                subtitles.setTimedTextFile(timeAlignment_processing_all.isSelected() ?
                        SubtitlesUtil.reviseTimeLine(subtitles.getTimedTextFile(), time, edit_mode.isSelected()) :
                        SubtitlesUtil.reviseTimeLine(subtitles.getTimedTextFile(), time, timeAlignment_area.getSelection(), edit_mode.isSelected()));
                ApplicationCommon.getInstance().setSubtitles(subtitles);
                toastController.pushMessage(ToastEnum.SUCCESS, MessageConstants.SUCCESS_MESSAGE);
            } catch (Exception e) {
                log.error(e.getMessage());
                toastController.pushMessage(ToastEnum.ERROR, MessageConstants.TIMELINE_FORMAT_ERROR);
            }
        } else {
            toastController.pushMessage(ToastEnum.ERROR, MessageConstants.TIMELINE_FORMAT_ERROR);
        }
    }

    public void setComboBox(SubtitlesTypeEnum typeEnum) {
        timeAlignment_hour.getItems().clear();
        timeAlignment_min.getItems().clear();
        timeAlignment_sec.getItems().clear();
        for (int i = 0; i <= 99; i++) {
            String temp = "" + i;
            if (i < 10) {
                temp = "0" + i;
            }
            if (i < 20) {
                timeAlignment_hour.getItems().add(temp);
            }
            if (i < 60) {
                timeAlignment_min.getItems().add(temp);
                timeAlignment_sec.getItems().add(temp);
            }
        }
        int flag = 0;
        switch (typeEnum) {
            case ASS:
                flag = 99;
                break;
            case LRC:
                break;
            case SRT:
                flag = 999;
                break;
        }

        timeAlignment_ms.getItems().clear();
        for (int i = 0; i <= flag; i++) {
            String temp = StrUtil.EMPTY;
            if (flag <= 99) {
                if (i < 10) temp = "0" + i;
                else temp += i;
            } else {
                if (i < 10) temp = "00" + i;
                else if (i < 100) temp = "0" + i;
                else temp += i;
            }
            timeAlignment_ms.getItems().add(temp);
        }
    }

    /*时间轴对齐->解析选项到输入框*/
    private void parsingSelection() {
        try {
            if (ObjectUtil.isAllNotEmpty(timeAlignment_hour.getValue(), timeAlignment_min.getValue(),
                    timeAlignment_ms.getValue(), timeAlignment_sec.getValue())) {
                Subtitles subtitles = ApplicationCommon.getInstance().getSubtitles();
                SubtitlesTypeEnum typeEnum = (SubtitlesTypeEnum) subtitles.getFormat();
                String timeStr = StrUtil.format(typeEnum.getFormat(), timeAlignment_hour.getValue(),
                        timeAlignment_min.getValue(), timeAlignment_sec.getValue(), timeAlignment_ms.getValue());
                timeAlignment_input.setText(timeStr);
//                switch ((SubtitlesTypeEnum) subtitles.getFormat()) {
//                    case ASS:
//                        timeAlignment_input.setText(ASSTime.format(time));
//                        break;
//                    case SRT:
//                        timeAlignment_input.setText(SRTTime.format(time));
//                        break;
//                    case LRC:
//                        break;
//                    default:
//                        throw new Exception(MessageConstants.TIMELINE_FORMAT_ERROR);
//                }
            }
        } catch (Exception e) {
            toastController.pushMessage(ToastEnum.ERROR, e.getMessage());
        }
    }


    /*时间轴对齐->解析输入到框选项*/
    private void parsingInput() {
        try {
            Subtitles subtitles = ApplicationCommon.getInstance().getSubtitles();
            String text = timeAlignment_input.getText();
            LocalTime time = null;
            if (NumberUtil.isNumber(text)) {
                time = LocalTime.ofNanoOfDay(Long.parseLong(text) * 1000000);
            } else {
                switch ((SubtitlesTypeEnum) subtitles.getFormat()) {
                    case ASS:
                        time = ASSTime.fromString(text);
                        break;
                    case SRT:
                        time = SRTTime.fromString(text);
                        break;
                    case LRC:
                        break;
                    default:
                        throw new Exception(MessageConstants.TIMELINE_FORMAT_ERROR);
                }
            }
            timeAlignment_hour.getSelectionModel().select(time.getHour());
            timeAlignment_min.getSelectionModel().select(time.getMinute());
            timeAlignment_sec.getSelectionModel().select(time.getSecond());
            timeAlignment_ms.getSelectionModel().select((time.getNano() / 1000000));
            timeAlignment_input.getStyleClass().remove(StyleConstants.INPUT_ERROR_STYLE_CLASS);
        } catch (Exception e) {
            timeAlignment_input.getStyleClass().add(0, StyleConstants.INPUT_ERROR_STYLE_CLASS);
        }

    }

    /**
     * 编辑模式改变
     *
     * @param intact false-简洁，ture-完整
     */
    private void editModeChange(boolean intact) {
        log.info("切换至 {}", intact ? "完整" : "简洁");
        edit_mode.getStyleClass().clear();
        edit_mode.getStyleClass().add(intact ?
                StyleConstants.EDIT_MODE_NO : StyleConstants.EDIT_MODE_OFF);
        TextArea area = features.equals(homePanel) ? editor : timeAlignment_area;
        area.textProperty().removeListener(conciseEditListener);
        area.textProperty().removeListener(completeEditListener);
        //更新文本区
        ApplicationConfig.getInstance().getApplicationSettings().setEdit_mode(intact);
        String text = SubtitlesUtil.subtitleDisplay();
        editor.setText(text);
        timeAlignment_area.setText(text);
        edit_mode_label.setText(intact ? CommonConstants.EDIT_SENIOR_MODE : CommonConstants.EDIT_SIMPLE_MODE);
        area.textProperty().addListener(intact ? completeEditListener : conciseEditListener);
    }

    /**
     * 指定编码重新读取字幕文件
     *
     * @param charset 编码
     */
    public void rereadFile(String charset) {
        Subtitles subtitles = ApplicationCommon.getInstance().getSubtitles();
        try {
            if (StrUtil.equals(subtitles.getCharset(), charset)) {
                subtitles.setCharset(charset);
                String text = SubtitlesUtil.subtitleDisplay();
                editor.setText(text);
                timeAlignment_area.setText(text);
                ApplicationCommon.getInstance().setSubtitles(SubtitlesUtil.readSubtitleFile(subtitles));
            }
        } catch (Exception e) {
            e.printStackTrace();
            toastController.pushMessage(ToastEnum.ERROR, e.getMessage());
        }
    }

    public TextArea getFocusEditor() {
        return basicEdit.isSelected() ? editor : timeAlignment_area;
    }

    private void editorPositionListener(TextArea area) {
        area.caretPositionProperty().addListener((observable, oldValue, newValue) -> {
            int count = 0;
            row = 1;
            column = 0;
            List<String> lines = Lists.newArrayList();
            IoUtil.readLines(StrUtil.getReader(area.getText()), lines);
            for (String line : lines) {
                if (count + line.length() > newValue.intValue()) {
                    column = newValue.intValue() - count;
                    break;
                } else if (count + line.length() < newValue.intValue()) {
                    row++;
                    count += line.length() + 1;
                } else if (count + line.length() == newValue.intValue()) {
                    column = line.length();
                    break;
                }
            }
            editIndicator.setText(StrUtil.format(CommonConstants.EDIT_INDICATOR_FORMAT, row, column));
        });
    }

    /**
     * 刷新编辑器
     *
     * @param isMain 是否为主编辑器
     */
    public void refreshEditor(boolean isMain) {
        Subtitles subtitles = ApplicationCommon.getInstance().getSubtitles();
        TextArea area;
        if (isMain) {
            area = editor;
        } else {
            LocalTime startTime = CollUtil.getFirst(subtitles.getTimedTextFile().getTimedLines()).getTime().getStart();
            LocalTime endTime = CollUtil.getLast(subtitles.getTimedTextFile().getTimedLines()).getTime().getStart();
            timeAlignment_file_name.setText(StrUtil.brief(subtitles.getFile_name(), 20));
            timeAlignment_time_start.setText(startTime.toString());
            timeAlignment_time_size.setText(DateUtil.formatBetween(endTime.toSecondOfDay() * 1000L -
                    startTime.toSecondOfDay() * 1000L, BetweenFormatter.Level.MINUTE));
            timeAlignment_encode.setText(subtitles.getCharset());
            timeAlignment_file_size.setText(subtitles.getSize());
            timeAlignment_area.setFont(new Font(ApplicationConfig.getInstance().getApplicationSettings().getFont_face(),
                    ApplicationConfig.getInstance().getApplicationSettings().getFont_size()));
            area = timeAlignment_area;
        }
        area.textProperty().removeListener(completeEditListener);
        area.textProperty().removeListener(conciseEditListener);
        area.setText(SubtitlesUtil.subtitleDisplay(subtitles.getTimedTextFile(), edit_mode.isSelected()));
        area.textProperty().addListener(edit_mode.isSelected() ? completeEditListener : conciseEditListener);
    }
}
