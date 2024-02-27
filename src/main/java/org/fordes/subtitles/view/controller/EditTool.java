package org.fordes.subtitles.view.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.lang.Singleton;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.jfoenix.controls.JFXComboBox;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fordes.subtitles.view.config.ApplicationConfig;
import org.fordes.subtitles.view.constant.CommonConstant;
import org.fordes.subtitles.view.constant.StyleClassConstant;
import org.fordes.subtitles.view.enums.EditToolEventEnum;
import org.fordes.subtitles.view.enums.FileEnum;
import org.fordes.subtitles.view.enums.ServiceType;
import org.fordes.subtitles.view.event.EditToolEvent;
import org.fordes.subtitles.view.event.LoadingEvent;
import org.fordes.subtitles.view.event.ToastChooseEvent;
import org.fordes.subtitles.view.event.ToastConfirmEvent;
import org.fordes.subtitles.view.factory.TranslateServiceFactory;
import org.fordes.subtitles.view.model.DTO.AvailableServiceInfo;
import org.fordes.subtitles.view.model.DTO.Subtitle;
import org.fordes.subtitles.view.model.PO.Language;
import org.fordes.subtitles.view.service.InterfaceService;
import org.fordes.subtitles.view.service.translate.TranslateService;
import org.fordes.subtitles.view.utils.CacheUtil;
import org.fordes.subtitles.view.utils.SubtitleUtil;
import org.fordes.subtitles.view.utils.submerge.subtitle.ass.ASSTime;
import org.fordes.subtitles.view.utils.submerge.subtitle.common.TimedLine;
import org.fordes.subtitles.view.utils.submerge.subtitle.common.TimedTextFile;
import org.fordes.subtitles.view.utils.submerge.subtitle.srt.SRTTime;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.fxmisc.richtext.model.TwoDimensional;
import org.mozilla.universalchardet.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 编辑工具 控制器
 *
 * @author fordes on 2022/7/15
 */
@Slf4j
@Component
public class EditTool extends DelayInitController {

    private static Subtitle subtitle;

    private static StyleClassedTextArea area;

    private static ToggleButton editMode;

    private static int max;

    private static final Map<EditToolEventEnum, GridPane> bindMap = MapUtil.newHashMap();

    @FXML
    private CheckMenuItem search_case, search_regex, replace_case, replace_regex;

    @FXML
    private JFXComboBox<String> code_choice, font_family;

    @FXML
    private ChoiceBox<AvailableServiceInfo> translate_source;

    @FXML
    private ChoiceBox<String> translate_mode;

    @FXML
    private JFXComboBox<Language> translate_original, translate_target;

    @FXML
    private JFXComboBox<Integer> font_size;

    @FXML
    private ChoiceBox<TimelineType> timeline_option;

    @FXML
    private TextField timeline_input, jump_input, search_input, replace_input, replace_find_input;

    private final InterfaceService interfaceService;

    private final SidebarBottom sidebarBottom;

    private final ApplicationConfig config;

    @Autowired
    public EditTool(InterfaceService interfaceService,
                    SidebarBottom sidebarBottom, ApplicationConfig config) {
        this.interfaceService = interfaceService;
        this.sidebarBottom = sidebarBottom;
        this.config = config;
    }

    @Override
    public void delay() {

        //编码选择框
        code_choice.getItems().addAll(Arrays.stream(ReflectUtil.getFieldsValue(Constants.class))
                .map(Object::toString).toArray(String[]::new));
        //初始化字体大小
        font_size.getItems().addAll(CollUtil.newArrayList(12, 14, 16, 18, 20, 22, 24, 26, 28, 36, 48, 72));
        //初始化字体列表
        font_family.getItems().addAll(Font.getFontNames());
        //时间轴校正选项
        timeline_option.getItems().addAll(TimelineType.values());
        timeline_option.getSelectionModel().selectedItemProperty().addListener((observableValue, strings, t1)
                -> timeline_input.setPromptText(t1.desc));
        timeline_option.getSelectionModel().select(0);
        timeline_input.textProperty().addListener((observableValue, s, t1)
                -> timeline_input.getStyleClass().remove("error"));
        //翻译相关
        translate_original.getSelectionModel().selectedItemProperty().addListener((observableValue, strings, t1) -> {
            if (t1 != null) {
                Collection<Language> gap = CollUtil.subtract(config.getLanguageListMode() ?
                        t1.getTarget().stream().filter(e -> e.isGeneral() == config.getLanguageListMode()).collect(Collectors.toList()) :
                        t1.getTarget(), translate_target.getItems());
                Collection<Language> neg = CollUtil.subtract(translate_target.getItems(), t1.getTarget());
                if (!gap.isEmpty()) {
                    translate_target.getItems().addAll(gap);
                }
                if (!neg.isEmpty()) {
                    translate_target.getItems().removeAll(neg);
                }
            } else translate_target.getItems().clear();
        });
        translate_source.getSelectionModel().selectedItemProperty()
                .addListener((observableValue, availableServiceInfo, t1) -> {
                    if (t1 != null) {
                        translate_original.getItems().clear();
                        translate_original.getItems().addAll(CacheUtil.getLanguageDict(ServiceType.TRANSLATE, t1.getProvider(), config.getLanguageListMode()));
                        translate_original.getSelectionModel().selectFirst();
                    }
                });
        translate_source.getItems().clear();
        translate_source.getItems().addAll(interfaceService.getAvailableService(ServiceType.TRANSLATE));
        translate_source.getSelectionModel().selectFirst();
        translate_mode.getItems().addAll(CommonConstant.TRANSLATE_REPLACE, CommonConstant.TRANSLATE_BILINGUAL);
        translate_mode.getSelectionModel().selectFirst();
        //回车提交操作
        timeline_input.setOnAction(this::applyTimeline);
        jump_input.setOnAction(this::applyJump);
        search_input.setOnAction(this::applySearch);
        replace_find_input.setOnAction(this::applyReplaceFind);
        replace_input.setOnAction(this::applyReplaceNext);
        //错误输入
        jump_input.textProperty().addListener((observableValue, s, t1)
                -> jump_input.getStyleClass().remove(StyleClassConstant.ERROR));
        search_input.textProperty().addListener((observableValue, s, t1)
                -> search_input.getStyleClass().remove(StyleClassConstant.ERROR));
        timeline_input.textProperty().addListener((observableValue, s, t1)
                -> timeline_input.getStyleClass().remove(StyleClassConstant.ERROR));

    }

    @Override
    public void async() {
        Stage stage = Singleton.get(Stage.class);
        //各工具面板互斥
        root.getChildren().forEach(node -> {
                    if (node instanceof GridPane) {
                        EditToolEventEnum type = EditToolEventEnum.valueOf((String) node.getUserData());
                        bindMap.put(type, (GridPane) node);
                        node.visibleProperty().addListener((observableValue, aBoolean, t1) -> {
                            if (t1) {
                                bindMap.values().forEach(e -> e.setVisible(node.equals(e)));
                                root.setVisible(true);
                            }
                        });
                    }
                }
        );

        root.visibleProperty().addListener((observableValue, aBoolean, t1) -> {
            if (!t1) {
                bindMap.values().forEach(e -> e.setVisible(false));
            }
        });
        //监听编辑工具事件 唤起对应功能面板
        stage.addEventHandler(EditToolEvent.EVENT_TYPE, event -> {

            subtitle = event.getSubtitle();
            area = event.getSource();
            editMode = event.getEditMode();
            Parent parent = bindMap.get(event.getType());

            switch (event.getType()) {
                case SEARCH: //搜索
                    search_input.requestFocus();
                    parent.setVisible(true);
                    break;

                case REPLACE://替换
                    replace_find_input.requestFocus();
                    parent.setVisible(true);
                    break;

                case JUMP://跳转
                    jump_input.requestFocus();
                    max = 0;
                    for (TimedLine timedLine : subtitle.getTimedTextFile().getTimedLines()) {
                        max += timedLine.getTextLines().size();
                    }
                    parent.setVisible(true);
                    break;

                case FONT: //字体（样式）
//                    font_family.getSelectionModel().select(config.getFontFace());
                    font_size.getSelectionModel().select(config.getFontSize());
                    parent.setVisible(true);
                    break;

                case TIMELINE: //时间轴
                    TimedLine start = CollUtil.getFirst(subtitle.getTimedTextFile().getTimedLines());
                    timeline_input.setPromptText(start.getTime().getStart().toString());
                    timeline_input.requestFocus();
                    parent.setVisible(true);
                    break;

                case CODE://编码
                    code_choice.getSelectionModel().select(subtitle.getCharset());
                    parent.setVisible(true);
                    break;

                case TRANSLATE:
                    List<AvailableServiceInfo> list = interfaceService.getAvailableService(ServiceType.TRANSLATE);
                    if (list.isEmpty()) {
                        stage.fireEvent(new ToastChooseEvent("未配置翻译服务", "是否立即转到设置?",
                                "确定", () -> sidebarBottom.getSetting().getOnAction().handle(null)));
                        parent.setVisible(false);
                    } else {
                        Collection<AvailableServiceInfo> gap = CollUtil.subtract(list, translate_source.getItems());
                        Collection<AvailableServiceInfo> neg = CollUtil.subtract(translate_source.getItems(), list);
                        if (!gap.isEmpty()) {
                            translate_source.getItems().addAll(gap);
                        }
                        if (!neg.isEmpty()) {
                            translate_source.getItems().removeAll(neg);
                        }
                        parent.setVisible(true);
                    }
                    break;

                case REF: //刷新
                    try {
                        SubtitleUtil.parse(subtitle);
                        area.clear();
                        area.append(SubtitleUtil.toStr(subtitle.getTimedTextFile(), editMode.isSelected()), "styled-text-area");
                        area.setStyle(StrUtil.format(StyleClassConstant.FONT_STYLE_TEMPLATE,config.getFontSize(), config.getFontFace()));
                    } catch (Exception e) {
                        log.error(ExceptionUtil.stacktraceToString(e));
                        stage.fireEvent(new ToastConfirmEvent("编码更改出错", "已切换回原编码~"));
                    }
                    break;
            }
        });

    }

    @FXML
    private void onClose(ActionEvent actionEvent) {
        actionEvent.consume();
        area = null;
        subtitle = null;
        editMode = null;
        root.setVisible(false);
    }

    @FXML
    private void applyCode(ActionEvent actionEvent) {
        String original = subtitle.getCharset();
        try {
            subtitle.setCharset(code_choice.getSelectionModel().getSelectedItem());
            SubtitleUtil.parse(subtitle);
            area.clear();
            area.append(SubtitleUtil.toStr(subtitle.getTimedTextFile(), editMode.isSelected()), StrUtil.EMPTY);
        } catch (Exception e) {
            log.error(ExceptionUtil.stacktraceToString(e));
            Singleton.get(Stage.class).fireEvent(new ToastConfirmEvent("编码更改出错", "已切换回原编码~"));
            subtitle.setCharset(original);
            code_choice.getSelectionModel().select(original);
        }
        actionEvent.consume();
    }

    @FXML
    private void applyFont(ActionEvent actionEvent) {
        Stage stage = Singleton.get(Stage.class);

        String originalFontFamily = config.getFontFace();
        Integer originalFontSize = config.getFontSize();
        try {
            config.setFontSize(Convert.toInt(font_size.getValue()));
            config.setFontFace(font_family.getValue());
            area.setStyle(StrUtil.format(StyleClassConstant.FONT_STYLE_TEMPLATE,
                    config.getFontSize(), config.getFontFace()));
            area.requestFocus();
        } catch (Exception e) {
            log.error(ExceptionUtil.stacktraceToString(e));
            config.setFontSize(originalFontSize);
            config.setFontFace(originalFontFamily);
            font_family.setValue(originalFontFamily);
            font_size.setValue(originalFontSize);
            stage.fireEvent(new ToastConfirmEvent("字体更改出错", "已切换回原字体~"));
        }
        actionEvent.consume();
    }

    @FXML
    private void applyTimeline(ActionEvent actionEvent) {
        LocalTime newTime = null;
        String timeLine = timeline_input.getText();
        TimelineType option = timeline_option.getValue();
        if (TimelineType.TIMELINE.equals(option)) {
            try {
                newTime = FileEnum.SRT.equals(subtitle.getFormat()) ?
                        SRTTime.fromString(timeLine) : ASSTime.fromString(timeLine);
            } catch (Exception ignored) {
            }

        } else {
            if (NumberUtil.isInteger(timeLine)) {
                int offset = Convert.toInt(timeLine);
                LocalTime date = CollUtil.getFirst(subtitle.getTimedTextFile().getTimedLines()).getTime().getStart();
                newTime = date.plus(offset, option.rate);
            }
        }
        if (newTime != null) {
            //TODO 按选中范围处理 待支持
            TimedTextFile original = subtitle.getTimedTextFile();
            try {
                TimedTextFile target = SubtitleUtil
                        .revise(subtitle.getTimedTextFile(), newTime, null, editMode.isSelected());
                subtitle.setTimedTextFile(target);
                SubtitleUtil.write(subtitle, success -> {
                    Singleton.get(Stage.class).fireEvent(new LoadingEvent(!success));
                    if (success) {
                        area.clear();
                        area.append(SubtitleUtil.toStr(subtitle.getTimedTextFile(),
                                editMode.isSelected()), StrUtil.EMPTY);
                    } else throw new RuntimeException("写入失败");
                });
            } catch (Exception e) {
                log.error(ExceptionUtil.stacktraceToString(e));
                subtitle.setTimedTextFile(original);
                Singleton.get(Stage.class).fireEvent(new ToastConfirmEvent("时间轴更改出错", "已切换回原时间轴~"));
            }
        } else timeline_input.getStyleClass().add(StyleClassConstant.ERROR);
        actionEvent.consume();
    }

    @FXML
    private void applyJump(ActionEvent actionEvent) {
        String text = jump_input.getText();
        int value = NumberUtil.isInteger(text) ? NumberUtil.parseInt(text) : 0;

        if (value > 0 && value <= max) {
            TwoDimensional.Position position = area.position(value, 1);
            area.moveTo(position.toOffset());
            area.requestFollowCaret();
        } else {
            jump_input.getStyleClass().add(StyleClassConstant.ERROR);
        }
        actionEvent.consume();
    }

    @FXML
    private void applySearch(ActionEvent actionEvent) {
        String str = search_input.getText();
        if (StrUtil.isNotBlank(str)) {
            SubtitleUtil.search(area, str, search_case.isSelected(), search_regex.isSelected());
        } else search_input.getStyleClass().add(StyleClassConstant.ERROR);
        actionEvent.consume();
    }

    @FXML
    private void applyReplaceNext(ActionEvent actionEvent) {
        applyReplace(false);
        actionEvent.consume();
    }

    @FXML
    private void applyReplaceAll(ActionEvent actionEvent) {
        applyReplace(true);
        actionEvent.consume();
    }

    @FXML
    private void applyReplaceFind(ActionEvent actionEvent) {
        String str = replace_find_input.getText();
        if (StrUtil.isNotBlank(str)) {
            SubtitleUtil.find(area, str, replace_case.isSelected(), replace_regex.isSelected());
        }
        actionEvent.consume();
    }

    private void applyReplace(boolean isAll) {
        if (editMode.isSelected()) {
            String replaceText = replace_input.getText();
            String searchText = replace_find_input.getText();
            if (StrUtil.isAllNotBlank(replaceText, searchText)) {
                try {
                    SubtitleUtil.replace(area, subtitle, searchText, replaceText, isAll,
                            replace_case.isSelected(), replace_regex.isSelected());
                } catch (Exception e) {
                    log.error(ExceptionUtil.stacktraceToString(e));
                    Singleton.get(Stage.class).fireEvent(new ToastConfirmEvent("替换出错", "已切换回原文本~"));
                }
            }
        } else Singleton.get(Stage.class).fireEvent(new ToastChooseEvent("操作受限", "是否切换至完整模式?",
                "切换", () -> editMode.setSelected(true)));
    }

    @FXML
    private void applyTranslate(ActionEvent actionEvent) {
        AvailableServiceInfo source = translate_source.getValue();
        boolean mode = StrUtil.equals(CommonConstant.TRANSLATE_BILINGUAL, translate_mode.getValue());
        Language origin = translate_original.getValue();
        Language target = translate_target.getValue();
        if (source != null && origin != null && target != null) {

            TranslateService service = TranslateServiceFactory.getService(source.getProvider().getValue());
            Singleton.get(Stage.class).fireEvent(new LoadingEvent(true));
            globalExecutor.execute(() -> service.translate(subtitle, target.getCode(), origin.getCode(),
                    source.getVersionInfo(), mode, JSONUtil.parseObj(source.getAuth())));
        }
        actionEvent.consume();
    }


    /**
     * 时间轴校正 操作类型枚举
     */
    @AllArgsConstructor
    enum TimelineType {

        TIMELINE("时间轴", null, "形如: xx:xx:xx:xx"),
        SECOND("秒", ChronoUnit.SECONDS, "整数，时间偏移量"),
        MILLISECOND("毫秒", ChronoUnit.MILLIS, "整数，时间偏移量"),
        MINUTE("分钟", ChronoUnit.MINUTES, "整数，时间偏移量"),
        HOUR("小时", ChronoUnit.HOURS, "整数，的时间偏移量");

        public final String name;
        public final ChronoUnit rate;
        public final String desc;

        @Override
        public String toString() {
            return this.name;
        }
    }
}
