package org.fordes.subview.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;
import org.fordes.subview.main.Launcher;
import org.fordes.subview.util.PopUpWindowUtil.ToastUtil;
import org.fordes.subview.util.SubtitlesUtil.TimelineUtil;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class HomePanelController extends RootController implements Initializable {

    @FXML
    private GridPane HomePanel,TimeAdj_left,ToolPanel;
    @FXML
    private GridPane EditToolBar;
    @FXML
    private SplitPane TimeAdjPanel;
    @FXML
    public TextArea editor,TimeAdj_Area, PreviewArea,TimeAdj_oldTime_Area;
    @FXML
    private ToggleGroup ListGroup;
    @FXML
    private ToggleButton BasicEdit,TimelineAdj,FindR,Preview;
    @FXML
    private AnchorPane TimeAdjPanel_right,TimeAdjPanel_left;
    @FXML
    private RadioButton Processing_all,Processing_selected;
    @FXML
    private ToggleGroup TimeAdj_mode;
    @FXML
    private ComboBox TimeAdj_hour,TimeAdj_min,TimeAdj_sec,TimeAdj_ms;
    @FXML
    private RowConstraints ToolBarSpace;
    @FXML
    private TextField TimeAdj_input;
    //全局主题
    private Object LightTheme=getClass().getClassLoader().getResource("css/mainStyle_Light.css").toString();
    private Object DarkTheme=getClass().getClassLoader().getResource("css/mainStyle_Dark.css").toString();
    //加载控制器
    private StartController startController = (StartController) Launcher.controllers.get(StartController.class.getSimpleName());
   // private EditToolBarController editToolBarController = (EditToolBarController) Launcher.controllers.get(EditToolBarController.class.getSimpleName());//工具栏控制器
    //private ToolPanelController toolPanelController = (ToolPanelController) Launcher.controllers.get(ToolPanelController.class.getSimpleName());//工具面板控制器

    private TimelineUtil tu= startController.timelineUtil;
    public double DividerPositions;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        HomePanel.getStylesheets().add(LightTheme.toString());
        editor.setFont(new Font(startController.getSettings().getBaseSettings().get("Editor_Font"),Integer.valueOf(startController.getSettings().getBaseSettings().get("Editor_Size"))));
        TimeAdj_Area.setFont(new Font(startController.getSettings().getBaseSettings().get("Editor_Font"),Integer.valueOf(startController.getSettings().getBaseSettings().get("Editor_Size"))));
        PreviewArea.setFont(new Font(startController.getSettings().getBaseSettings().get("Editor_Font"),Integer.valueOf(startController.getSettings().getBaseSettings().get("Editor_Size"))));
       // setEditor(startController.inputset.getText());
       /* ListGroup.selectToggle(BasicEdit);
        BasicEdit.setSelected(true);*/
        TimeAdj_mode.selectToggle(Processing_all);
        /*ToolPanel.setVisible(false);*/
        /*TimeAdj_left.prefWidthProperty().bind(TimeAdjPanel_left.widthProperty());*/
        TimeAdj_left.prefHeightProperty().bind(TimeAdjPanel_left.heightProperty());
        TimeAdj_Area.prefWidthProperty().bind(TimeAdjPanel_right.widthProperty());
        TimeAdj_Area.prefHeightProperty().bind(TimeAdjPanel_right.heightProperty());
        HomePanel.setFocusTraversable(true);
        setComboBox();
    }

    public ToggleGroup getListGroup() {
        return ListGroup;
    }

    public TextArea getPreviewArea() {
        return PreviewArea;
    }

    public TextArea getTimeAdj_Area() {
        return TimeAdj_Area;
    }

    /**
     * 切换颜色模式
     * @param ModeState
     */
    public void ThemeApply(Boolean ModeState){
        /*移除所有样式表*/
        HomePanel.getStylesheets().remove(LightTheme);
        HomePanel.getStylesheets().remove(DarkTheme);
        if(!ModeState)
            HomePanel.getStylesheets().add(LightTheme.toString());
        else
            HomePanel.getStylesheets().add(DarkTheme.toString());
    }

    public void setEditor(String text){
        editor.setText(text);
    }

    private void hide(){
        editor.setVisible(false);
        TimeAdjPanel.setVisible(false);
        PreviewArea.setVisible(false);
        ToolPanel.setVisible(false);
    }

//    public SplitPane getTimeAdjPanel(){
//        return TimeAdjPanel;
//    }

    public GridPane getHomePanel() {
        return HomePanel;
    }

    public RowConstraints getToolBarSpace() {
        return ToolBarSpace;
    }

    public TextArea getEditor() {
        return editor;
    }


    /*基础编辑*/
    public void DoBasicEdit(ActionEvent actionEvent) {
        ListGroup.selectToggle(BasicEdit);
        BasicEdit.setSelected(true);
        if(startController.focus_indicator.equals(BasicEdit.getId()))
            return;
        startController.focus_indicator=BasicEdit.getId();
        hide();
        editor.setVisible(true);
        editor.setText(startController.inputset.getText());
        editor.setFont(new Font(startController.getSettings().getBaseSettings().get("Editor_Font"),Integer.valueOf(startController.getSettings().getBaseSettings().get("Editor_Size"))));
        editor.positionCaret(0);
        /*自动保存修改*/
        editor.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
                startController.inputset.setText("");
                startController.inputset.setText(editor.getText());
            }
        });
    }

    /*时间轴对齐*/
    public void DoTimelineAdj(ActionEvent actionEvent) {
        ListGroup.selectToggle(TimelineAdj);
        TimelineAdj.setSelected(true);
        if(startController.focus_indicator.equals(TimelineAdj.getId()))
            return;
        startController.focus_indicator=TimelineAdj.getId();
        hide();
        TimeAdjPanel.setVisible(true);
        TimeAdj_Area.setText(startController.inputset.getText());
        TimeAdj_Area.setFont(new Font(startController.getSettings().getBaseSettings().get("Editor_Font"),Integer.valueOf(startController.getSettings().getBaseSettings().get("Editor_Size"))));
        TimeAdj_Area.textProperty().addListener((observable, oldValue, newValue) -> {
            startController.inputset.setText("");
            startController.inputset.setText(TimeAdj_Area.getText());
            System.out.println("时间轴对齐自动保存"+"\n\n");
        });

        TimeAdj_oldTime_Area.setText(new TimelineUtil(startController.inputset.getSubtitles().getType()).getTimeStart_Stop());

        TimeAdj_hour.getSelectionModel().select(null);
        TimeAdj_min.getSelectionModel().select(null);
        TimeAdj_ms.getSelectionModel().select(null);
        TimeAdj_sec.getSelectionModel().select(null);
        TimeAdj_input.setText("");

        TimeAdj_hour.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                ParsingSelection();
            }
        });
        TimeAdj_min.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                ParsingSelection();
            }
        });
        TimeAdj_sec.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                ParsingSelection();
            }
        });
        TimeAdj_ms.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                ParsingSelection();
            }
        });

        TimeAdj_input.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                ParsingInput();
            }
        });

    }


    /*文字预览*/
    public void DoPreview(ActionEvent actionEvent) {
        ListGroup.selectToggle(Preview);
        Preview.setSelected(true);

        if(startController.focus_indicator.equals(Preview.getId()))
            return;
        startController.focus_indicator=Preview.getId();
        hide();
        PreviewArea.setVisible(true);
        PreviewArea.setText(startController.inputset.getText());
        PreviewArea.setFont(new Font(startController.getSettings().getBaseSettings().get("Editor_Font"),Integer.valueOf(startController.getSettings().getBaseSettings().get("Editor_Size"))));
        PreviewArea.setText(clean());
    }



    private void setComboBox(){
        for (int i=0;i<=99;i++){
            String temp="";
            if(i<10)
                temp="0"+i;
            else if(i>9)
                temp=""+i;
            if(i<60){
                TimeAdj_min.getItems().add(temp);
                TimeAdj_sec.getItems().add(temp);
            }
            TimeAdj_hour.getItems().add(temp);
        }

        for (int i=0;i<=999;i++){
            String temp="";
            if(i<10)
                temp="00"+i;
            else if(i>9&&i<100)
                temp="0"+i;
            else
                temp=""+i;
            TimeAdj_ms.getItems().add(temp);
        }
    }


    /*时间轴对齐->解析选项到输入框*/
    private void ParsingSelection(){
        if(TimeAdj_hour.getSelectionModel().getSelectedItem()!=null&&TimeAdj_min.getSelectionModel().getSelectedItem()!=null&&TimeAdj_sec.getSelectionModel().getSelectedItem()!=null&&TimeAdj_ms.getSelectionModel().getSelectedItem()!=null){
            switch (startController.inputset.getSubtitles().getType()){
                case 1://srt
                    TimeAdj_input.setText(TimeAdj_hour.getSelectionModel().getSelectedItem().toString()+":"+TimeAdj_min.getSelectionModel().getSelectedItem().toString()+":"+TimeAdj_sec.getSelectionModel().getSelectedItem().toString()+","+TimeAdj_ms.getSelectionModel().getSelectedItem().toString());
                    break;
                case 2://ass
                    break;
                case 3://lrc
                    break;
            }
        }
    }
    /*时间轴对齐->解析输入到框选项*/
    private void ParsingInput(){
        TimelineUtil tu=new TimelineUtil(startController.inputset.getSubtitles().getType());
        String text=TimeAdj_input.getText().trim();
        String regex=new TimelineUtil(startController.inputset.getSubtitles().getType()).getRegex();
        if(text.length()<12||!Pattern.matches(regex, text))
            return;
        ArrayList<String> res;
        switch (startController.inputset.getSubtitles().getType()){
            case 1://srt
                res=tu.TimeToString(tu.TimeLineToInt(text));
                TimeAdj_hour.getSelectionModel().select(res.get(0));
                TimeAdj_min.getSelectionModel().select(res.get(1));
                TimeAdj_sec.getSelectionModel().select(res.get(2));
                TimeAdj_ms.getSelectionModel().select(res.get(3));
                break;
            case 2://ass
                break;
            case 3://lrc
                break;
        }

    }

    /**
     * 文字预览主方法
     * @return
     */
    private String clean(){
        String res="";

        String[] list= startController.inputset.getText().split("\n");
        for (String temp:list){
            if(temp==null||temp.equals("")||Pattern.compile("[0-9]*").matcher(temp).matches()||tu.TimelineCheck(temp))
                continue;
            else
                res+=temp+"\n";
        }
        return res;
    }


    public void onHandleTimeline(ActionEvent actionEvent) {
        String line=TimeAdj_input.getText().trim();
        TimelineUtil tu=new TimelineUtil((startController.inputset.getSubtitles().getType()));
        if(line==null||line.equals("")||line.length()<12||!Pattern.matches(tu.getRegex(), line))
            return;
        String res=tu.Revise(TimeAdj_Area.getText(),tu.TimelineToMillisecond(line));
        if(res!=null){
            System.out.println(res);
            TimeAdj_Area.setText(res);
            new ToastUtil().toast("对齐完成",1000);
        }
        else
            new ToastUtil().toast("未知错误",1000);
    }
}
