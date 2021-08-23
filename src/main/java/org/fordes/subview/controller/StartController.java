package org.fordes.subview.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import de.felixroske.jfxsupport.FXMLController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.fordes.subview.SubtitlesViewApplication;
import org.fordes.subview.entity.DO.FileAbstract;
import org.fordes.subview.entity.DTO.data.Subtitles;
import org.fordes.subview.entity.DTO.data.Video;
import org.fordes.subview.entity.DTO.search.SearchItem;
import org.fordes.subview.entity.DTO.search.SearchResult;
import org.fordes.subview.enums.*;
import org.fordes.subview.service.SearchSearch;
import org.fordes.subview.service.data.InterfaceInfoService;
import org.fordes.subview.utils.FileUtils;
import org.fordes.subview.utils.SystemUtils;
import org.fordes.subview.utils.common.ApplicationCommon;
import org.fordes.subview.utils.constants.CommonConstants;
import org.fordes.subview.utils.constants.FileConstants;
import org.fordes.subview.utils.constants.MessageConstants;
import org.fordes.subview.utils.constants.StyleConstants;
import org.fordes.subview.view.ApplicationView;
import org.fordes.subview.view.SystemTrayView;

import javax.annotation.Resource;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * 启动页面 控制器
 *
 * @author fordes on 2020/10/8
 */
@Slf4j
@FXMLController
public class StartController extends BasicController implements Initializable {

    @FXML
    private Button news, open, search_execute;
    @FXML
    private Label folder, tips;

    @FXML
    private ListView<TextFlow> search_content;

    @FXML
    private TextField search_input;

    @FXML
    private GridPane root, interfacePanel, fileDragPanel, searchPanel, historyRecordPanel;

    @FXML
    private ToggleGroup position;

    @FXML
    private ToggleButton history, net, homePage;

    @FXML
    private ChoiceBox<SubtitlesSearchEnum> interface_choose;

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Resource
    private ToastController toastController;

    @Resource
    private SystemTrayView systemTrayView;

    @Resource
    private SystemTrayMenuController systemTrayMenuController;

    @Resource
    private SearchSearch searchService;

    private static File dragFile;

    private static boolean isClear = false;

    @SneakyThrows
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //初始化界面
        ApplicationCommon.getInstance().initialize(ApplicationModeEnum.START, root);
        //注册事件
        searchService.setOnSucceeded(event -> searchSucceeded());
        search_content.setOnMouseClicked(this::searchItemClick);
        //界面交互事件
        net.setOnAction(event -> netSearch());
        homePage.setOnAction(event -> localChoose());
        history.setOnAction(event -> historyChoose());
        news.setOnAction(event -> newFile());
        open.setOnAction(event -> openFile());
        search_execute.setOnAction(event -> onSearchSubtitles());
        search_input.setOnAction(event -> submit_search());
        folder.setOnMouseClicked(event -> openFileAction());
        tips.setOnMouseClicked(event -> openFileAction());
        fileDragPanel.setOnDragOver(this::onDragOver);
        fileDragPanel.setOnDragExited(this::onDragExited);
        fileDragPanel.setOnDragDropped(this::onDragDropped);
        //初始化，存在则显示历史记录
        int historyCount = 0;//fileInfoService.count(new QueryWrapper<FileInfo>().lambda().isNotNull(FileInfo::getId));
        if (historyCount > 0) {
            //显示历史记录业务

        } else {
            this.focus(fileDragPanel);
        }


        interface_choose.getItems().addAll(SubtitlesSearchEnum.values());
        interface_choose.getSelectionModel().selectFirst();
        interface_choose.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            search_content.getItems().clear();
        });
        //初始化托盘
        ApplicationCommon.getInstance().initSysTray(systemTrayView, systemTrayMenuController);
        //托盘设置事件
        systemTrayMenuController.getSetting().setOnMouseClicked(event -> {
            ApplicationCommon.getInstance().showStage();
            position.getToggles().forEach(e -> e.setSelected(false));
            this.focus(interfacePanel);
        });
    }


    //字幕搜索结束事件
    private void searchSucceeded() {
        SearchResult result = searchService.getValue();
        if (CollUtil.isNotEmpty(result.getData())) {
            if (isClear) search_content.getItems().clear();
            searchService.archive(result.getData());
            search_content.getItems().addAll(
                    result.getData().stream().map(e -> {
                        TextFlow item = new TextFlow();
                        Text title = new Text(e.getTitle());
                        Text remark = new Text();
                        if (result.isFile()) {
                            remark.setText(StrUtil.LF + e.getFile().getPath());
                        } else {
                            remark.setText(StrUtil.concat(true, StrUtil.LF, (CollUtil.isEmpty(e.getTags())) ?
                                    CollUtil.join(e.getTags(), StrUtil.COMMA): e.getSecondary()));
                        }
                        //设置样式
                        item.getStyleClass().add(StyleConstants.SEARCH_ITEM);
                        title.getStyleClass().addAll(StyleConstants.SEARCH_ITEM_CONTENT, StyleConstants.SEARCH_ITEM_TEXT);
                        remark.getStyleClass().addAll(StyleConstants.SEARCH_ITEM_CONTENT, StyleConstants.SEARCH_ITEM_REMARK);
                        //添加组件
                        item.getChildren().addAll(title, remark);
                        return item;

                    }).collect(Collectors.toList())
            );

            if (result.hasNext()) {
                ScrollBar bar = (ScrollBar) search_content.lookup(".scroll-bar");
                if (bar != null) {
                    bar.valueProperty().addListener((src, ov, nv) -> {
                        if (nv.intValue() == 1) {
                            if (searchService.getValue() != null ) {
                                if (searchService.getValue().hasNext()) {
                                    searchService.page();
                                    toastController.pushMessage(ToastEnum.INFO, MessageConstants.AUTO_NEXT_PAGE);
                                }
                            }
                        }
                    });
                } else {
                    searchService.page();
                }
            }
        } else {
            //...居中显示未搜索到
            toastController.pushMessage(ToastEnum.INFO, "暂未找到~");
        }
    }


    //字幕搜索结果列表双击事件
    private void searchItemClick(MouseEvent event) {
        if (event.getButton().equals(MouseButton.PRIMARY)) {
            if (event.getClickCount() == 2) {
                if (!searchService.isRunning()) {
                    int sort = search_content.getSelectionModel().getSelectedIndices().get(0);
                    if (!searchService.get().isEmpty()) {
                        SearchItem item = searchService.get().get(sort);
                        if (item != null) {
                            if (searchService.getValue().isFile()) {
                                //文件打开
                                showApplication(item.getFile());
                                isClear = false;
                            } else {
                                if (item.getIs_down()) {
                                    //下载调用
                                    searchService.download(item.getRequest(), item.getFile_name());
                                }else {
                                    //下一级
                                    searchService.next(item.getRequest());
                                }
                                isClear = true;
                            }
                            return;
                        }
                    }
                    toastController.pushMessage(ToastEnum.FAIL, MessageConstants.FIELD_DATA_ERROR);
                    return;
                }
                toastController.pushMessage(ToastEnum.FAIL, MessageConstants.PROCESSING);
            }
        }
    }

    //字幕搜索
    private void netSearch() {
        this.focus(searchPanel);
    }

    //拖放或选择文件
    public void localChoose() {
        this.focus(fileDragPanel);
    }

    //历史记录
    public void historyChoose() {
        this.focus(historyRecordPanel);
    }

    //新建文件以开始
    public void newFile() {
        SystemUtils.pushTrayNotice("这是一个标题", "我们终于可以推送系统通知啦~~~");
//        systemTrayView.showView(Modality.NONE);
    }


    //打开文件以开始
    public void openFile() {
        try {
            File file = FileUtils.chooseFile(FileConstants.TITLE_ALL_FILE, FileConstants.allFile)
                    .showOpenDialog(ApplicationCommon.getInstance().getStage());
            //读取文件信息
            if (file != null && file.exists()) {
                showApplication(file);
            }
        } catch (Exception e) {
            toastController.pushMessage(ToastEnum.ERROR, e.getMessage());
        }

    }

    //发起字幕搜索
    private void onSearchSubtitles() {
        if (!searchService.isRunning()) {
            String target = search_input.getText().trim();
            if (StrUtil.isNotBlank(target)) {
                isClear = true;
                searchService.search(interface_choose.getValue(), target);
                return;
            }
            toastController.pushMessage(ToastEnum.FAIL, MessageConstants.INPUT_INVALID);
            search_input.clear();
            return;
        }
        toastController.pushMessage(ToastEnum.INFO, MessageConstants.PROCESSING);

    }

    //字幕搜索回车提交
    private void submit_search() {
        onSearchSubtitles();
    }

    private void openFileAction() {
        openFile();
    }

    private void onDragOver(DragEvent dragEvent) {
        Dragboard db = dragEvent.getDragboard();
        if (db.hasFiles()) {
            dragFile = db.getFiles().get(0);
            String suffix = FileUtil.getSuffix(dragFile);
            if (SubtitlesTypeEnum.isSubtitles(suffix) || VideoTypeEnum.isVideo(suffix)) {
                dragEvent.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                tips.setText(CommonConstants.TIPS_DRAG_OPEN);
                folder.getStyleClass().add(StyleConstants.START_FOLDER_SUCCESS);
            } else {
                tips.setText(CommonConstants.TIPS_DRAG_NOT_SUPPORT);
                folder.getStyleClass().add(StyleConstants.START_FOLDER_NOT_SUPPORT);
                dragFile = null;
            }
        }
        dragEvent.consume();
    }

    private void onDragExited(DragEvent dragEvent) {
        tips.setText(CommonConstants.TIPS_DRAG_DEFAULT);
        folder.getStyleClass().removeAll(StyleConstants.START_FOLDER_SUCCESS,
                StyleConstants.START_FOLDER_NOT_SUPPORT);
    }

    private void onDragDropped(DragEvent dragEvent) {
        try {
            if (dragFile != null && dragFile.exists()) {
               showApplication(dragFile);
            }
        } catch (Exception e) {
            toastController.pushMessage(ToastEnum.ERROR, e.getMessage());
        }
    }

    //打开主界面
    private void showApplication(File file) {
        try {
            FileAbstract info = FileUtils.readFileInfo(file);
            if (info.getFormat().getClass().equals(SubtitlesTypeEnum.class)) {
                ApplicationCommon.getInstance().setSubtitles((Subtitles) info);
                SubtitlesViewApplication.showInitialView(ApplicationView.class);
            } else {
                ApplicationCommon.getInstance().setVideo((Video) info);
                if (!interfaceInfoService.isVoiceInterface()) {
                    toastController.pushMessage(ToastEnum.INFO, MessageConstants.VOICES_INTERFACE_NOT_READY);
                    position.getToggles().forEach(e -> e.setSelected(false));
                    this.focus(interfacePanel);
                } else {
                    //TODO 语音转换调用
                }
            }
        }catch (Exception e) {
            log.error(e.getMessage());
            toastController.pushMessage(ToastEnum.ERROR, e.getMessage());
        }
    }



}