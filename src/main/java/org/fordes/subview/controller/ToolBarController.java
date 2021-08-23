package org.fordes.subview.controller;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import de.felixroske.jfxsupport.FXMLController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;
import lombok.SneakyThrows;
import org.fordes.subview.entity.DTO.SearchDTO;
import org.fordes.subview.entity.DTO.data.Subtitles;
import org.fordes.subview.enums.ToastEnum;
import org.fordes.subview.utils.SubtitlesUtil;
import org.fordes.subview.utils.common.ApplicationCommon;
import org.fordes.subview.utils.constants.StyleConstants;
import org.mozilla.universalchardet.Constants;

import javax.annotation.Resource;
import java.awt.*;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

/**
 * 编辑器工具 控制器
 *
 * @author fordes on 2021/2/25
 */
@FXMLController
public class ToolBarController extends BasicController implements Initializable {


    @FXML
    private CheckMenuItem edit_tool_search_case, edit_tool_search_regular,
            edit_tool_find_case, edit_tool_find_regular;

    @FXML
    private ChoiceBox<Integer> edit_tool_style_font_size;

    @FXML
    private ChoiceBox<String> edit_tool_code_box, edit_tool_style_font;

    @FXML
    private GridPane editToolbar, toolPanel, findPanel, replacePanel, jumpPanel, codePanel, stylePanel;

    @FXML
    private TextField edit_tool_jump_input, edit_tool_find_input, edit_tool_search_input, edit_tool_replace_input;

    @FXML
    private Button edit_tool_find;

    @Resource
    private ToastController toastController;

    @Resource
    private HomeController homeController;


    @SneakyThrows
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Subtitles fileInfo = ApplicationCommon.getInstance().getSubtitles();
        //初始化编码选项
        Class<?> clazz = Class.forName(Constants.class.getName());
        for (Field e : clazz.getFields()) {
            edit_tool_code_box.getItems().add(e.get(clazz).toString());
        }
        edit_tool_code_box.getSelectionModel().select(fileInfo.getCharset());
        //初始化字体列表
        edit_tool_style_font.getItems().addAll(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        edit_tool_style_font.getSelectionModel().select(StyleConstants.DEFAULT_FONT);
        //初始化字体大小
        edit_tool_style_font_size.getItems().addAll(Arrays.stream(NumberUtil.range(10, 48)).boxed().toArray(Integer[]::new));
        edit_tool_style_font_size.getSelectionModel().select(StyleConstants.DEFAULT_FONT_SIZE);

        //添加事件
        edit_tool_style_font.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            homeController.getFocusEditor().setFont(new Font(newValue, edit_tool_style_font_size.getValue()));
        });
        edit_tool_style_font_size.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            homeController.getFocusEditor().setFont(new Font(edit_tool_style_font.getValue(), newValue));
        });
        setHotKey(homeController.getHomePanel());
        toolPanel.setMouseTransparent(true);
        toolPanel.visibleProperty().addListener((observable, oldValue, newValue) -> toolPanel.setMouseTransparent(!newValue));
    }


    public void onFind(ActionEvent actionEvent) {
        toolPanel.setVisible(true);
        this.focus(findPanel);
    }

    public void onReplace(ActionEvent actionEvent) {
        toolPanel.setVisible(true);
        this.focus(replacePanel);
    }

    public void onJumpLine(ActionEvent actionEvent) {
        toolPanel.setVisible(true);
        this.focus(jumpPanel);
    }

    public void onChangeCode(ActionEvent actionEvent) {
        toolPanel.setVisible(true);
        this.focus(codePanel);
    }

    public void onChangeStyle(ActionEvent actionEvent) {
        toolPanel.setVisible(true);
        this.focus(stylePanel);
    }

    public void onRegularLine(ActionEvent actionEvent) {
        int pos = homeController.getFocusEditor().getCaretPosition();
        homeController.getFocusEditor().insertText(pos, StrUtil.concat(false, StrUtil.LF, "请输入..."));
        homeController.getFocusEditor().selectRange(pos, pos + 7);
    }

    public void onHidden(ActionEvent actionEvent) {
        RowConstraints parentSpace = homeController.getToolBarSpace();
        parentSpace.setMaxHeight(editToolbar.isVisible() ? 0 : 60);
        parentSpace.setMinHeight(editToolbar.isVisible() ? 0 : 60);
        parentSpace.setPrefHeight(editToolbar.isVisible() ? 0 : 60);
        toastController.pushMessage(ToastEnum.INFO, editToolbar.isVisible() ? "按Ctrl+T 即可呼出" : "按Ctrl+T 即可隐藏");
        editToolbar.setVisible(!editToolbar.isVisible());
    }

    public void onFindClose(ActionEvent actionEvent) {
        toolPanel.setVisible(false);
    }

    public void onReplaceSearchClose(ActionEvent actionEvent) {
        toolPanel.setVisible(false);
    }

    public void onReplaceNext(ActionEvent actionEvent) {
        String searchStr = edit_tool_search_input.getText();
        String replace = edit_tool_replace_input.getText();
        if (StrUtil.isAllNotBlank(searchStr, replace)) {
            textReplace(searchStr, replace, edit_tool_search_case.isSelected(), edit_tool_search_regular.isSelected(), false);
        }
    }

    public void onReplaceAll(ActionEvent actionEvent) {
        String searchStr = edit_tool_search_input.getText();
        String replace = edit_tool_replace_input.getText();
        if (StrUtil.isAllNotBlank(searchStr, replace)) {
            textReplace(searchStr, replace, edit_tool_search_case.isSelected(), edit_tool_search_regular.isSelected(), true);
        }
    }

    public void onRefresh(ActionEvent actionEvent) throws Exception {
        homeController.rereadFile(edit_tool_code_box.getValue());
    }

    public void onRefreshClose(ActionEvent actionEvent) {
        toolPanel.setVisible(false);
    }

    public void onStyleClose(ActionEvent actionEvent) {
        toolPanel.setVisible(false);
    }

    public void onStyleDefault(ActionEvent actionEvent) {
        edit_tool_style_font_size.getSelectionModel().select(StyleConstants.DEFAULT_FONT_SIZE);
        edit_tool_style_font.getSelectionModel().select(StyleConstants.DEFAULT_FONT);
        homeController.getFocusEditor().setFont(new Font(edit_tool_style_font.getValue(), edit_tool_style_font_size.getValue()));
    }

    public void onReplaceSearch(ActionEvent actionEvent) {
        String keyword = edit_tool_search_input.getText();
        if (StrUtil.isNotBlank(keyword)) {
            textSearch(keyword, edit_tool_search_case.isSelected(), edit_tool_search_regular.isSelected());
        }
    }

    public void onJumpClose(ActionEvent actionEvent) {
        toolPanel.setVisible(false);
    }

    public void onJump(ActionEvent actionEvent) {
        String lineStr = edit_tool_jump_input.getText();
        if (StrUtil.isNotBlank(lineStr)) {
            if (ReUtil.isMatch("^[1-9]\\d*$", lineStr)) {
                int lineNum = Integer.parseInt(lineStr);
                int cursor = 0;
                String[] array = StrUtil.splitToArray(homeController.getFocusEditor().getText(), StrUtil.C_LF, lineNum);
                if (lineNum > array.length) {
                    lineNum = array.length;
                    toastController.pushMessage(ToastEnum.INFO, "行号超出最大范围啦~");
                    edit_tool_jump_input.setText(String.valueOf(lineNum));
                }
                for (String e : ArrayUtil.remove(array, lineNum - 1)) {
                    cursor += e.length() + 1;
                }
                homeController.getFocusEditor().positionCaret(cursor);
            } else {
                edit_tool_jump_input.setText(StrUtil.EMPTY);
                toastController.pushMessage(ToastEnum.FAIL, "别闹, 这个行号不对~");
            }
        }
    }


    public void onFindNext(ActionEvent actionEvent) {
        String keyword = edit_tool_find_input.getText();
        if (StrUtil.isNotBlank(keyword)) {
            textSearch(keyword, edit_tool_find_case.isSelected(), edit_tool_find_regular.isSelected());
        }
    }

    public void onTimeLine(ActionEvent actionEvent) {

    }


    private void textSearch(String filter, boolean isIgnoreCase, boolean isRegular) {
        TextArea temp = homeController.getFocusEditor();
        int oldPos = temp.getCaretPosition();
        SearchDTO result;
        if (oldPos > 0) {
            result = SubtitlesUtil.search(temp.getText(oldPos, temp.getText().length()), filter, isIgnoreCase, isRegular);
            if (!result.isSuccess()) {
                toastController.pushMessage(ToastEnum.INFO, "已回到文档开头");
                result = SubtitlesUtil.search(temp.getText(0, oldPos), filter, isIgnoreCase, isRegular);
                oldPos = 0;
            }
        } else {
            result = SubtitlesUtil.search(temp.getText(), filter, isIgnoreCase, isRegular);
        }

        if (result.isSuccess()) {
            homeController.getFocusEditor().selectRange(oldPos + result.getCursor_start(), oldPos + result.getCursor_end());
        } else {
            toastController.pushMessage(ToastEnum.INFO, "未找到~");
            homeController.getFocusEditor().selectRange(0, 0);
        }

    }

    private void textReplace(String searchStr, String replaceStr, boolean isIgnoreCase, boolean isRegular, boolean isAll) {

        TextArea temp = homeController.getFocusEditor();
        int oldPos = temp.getCaretPosition();
        SearchDTO result;
        if (isAll) {
            //替换全部
            result = SubtitlesUtil.replace(temp.getText(oldPos, temp.getText().length()), searchStr, replaceStr, true, isIgnoreCase, isRegular);
            if (result.isSuccess()) {
                homeController.getFocusEditor().setText(result.getContent());
                homeController.getFocusEditor().selectRange(oldPos + result.getCursor_start(), oldPos + result.getCursor_end());
                toastController.pushMessage(ToastEnum.INFO, "已全部替换~");
            } else {
                toastController.pushMessage(ToastEnum.INFO, "未找到~");
                homeController.getFocusEditor().selectRange(0, 0);
            }
        } else {
            //单步替换
            String before = temp.getText(0, oldPos);
            String after = temp.getText(oldPos, temp.getText().length());
            if (oldPos > 0) {
                result = SubtitlesUtil.replace(after, searchStr, replaceStr, false, isIgnoreCase, isRegular);
                if (result.isSuccess()) {
                    homeController.getFocusEditor().setText(before + result.getContent());
                    homeController.getFocusEditor().selectRange(oldPos + result.getCursor_start(), oldPos + result.getCursor_end());
                    return;
                } else {
                    toastController.pushMessage(ToastEnum.INFO, "已回到文档开头");
                    result = SubtitlesUtil.replace(temp.getText(0, oldPos), searchStr, replaceStr, false, isIgnoreCase, isRegular);
                    if (result.isSuccess()) {
                        homeController.getFocusEditor().setText(result.getContent() + after);
                        homeController.getFocusEditor().selectRange(result.getCursor_start(), result.getCursor_end());
                        return;
                    }
                }
            } else {
                result = SubtitlesUtil.replace(temp.getText(), searchStr, replaceStr, false, isIgnoreCase, isRegular);
                if (result.isSuccess()) {
                    homeController.getFocusEditor().setText(result.getContent());
                    homeController.getFocusEditor().selectRange(result.getCursor_start(), result.getCursor_end());
                    return;
                }
            }
            toastController.pushMessage(ToastEnum.INFO, "未找到~");
            homeController.getFocusEditor().selectRange(0, 0);
        }
    }

    private void setHotKey(Node node) {
        node.setOnKeyPressed(event -> {
            if (event.isControlDown()) {
                switch (event.getCode()) {
                    case F:
                        onFind(null);
                        break;
                    case R:
                        onReplace(null);
                        break;
                    case J:
                        onJump(null);
                        break;
                    case E:
                        onChangeCode(null);
                        break;
                    case Q:
                        onChangeStyle(null);
                        break;
                    case T:
                        onHidden(null);
                        break;
                    default:
                        break;
                }
//                homeController.getFocusEditor().requestFocus();
            }
        });
    }


}
