package org.fordes.subview.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;
import org.fordes.subview.main.Launcher;
import org.fordes.subview.util.SubtitlesUtil.TimelineUtil;
import org.fordes.subview.util.SubtitlesUtil.editUtil;
import org.fordes.subview.util.ToastUtil;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class homePanelControl implements Initializable {

    @FXML
    private GridPane Home,TimeAdj_left,Toolbar,ToolPanel;
    @FXML
    private SplitPane TimeAdjPanel;
    @FXML
    public TextArea editor,TimeAdj_Area,PreviewPanel,TimeAdj_oldTime_Area;
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
    private EditToolBarControl ToolbarController;//工具栏控制器
    @FXML
    private ToolPanelControl ToolPanelController;//工具面板控制器
    @FXML
    private RowConstraints ToolBarSpace;
    @FXML
    private TextField TimeAdj_input;
    //全局主题
    private Object LightTheme=getClass().getClassLoader().getResource("resources/css/mainStyle_Light.css").toString();
    private Object DarkTheme=getClass().getClassLoader().getResource("resources/css/mainStyle_Dark.css").toString();
    private startControl controller= (startControl) Launcher.controllers.get(startControl.class.getSimpleName());

    private int line=1,pos=1;
    private TimelineUtil tu=controller.timelineUtil;
    public double DividerPositions;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //默认主题
        Home.getStylesheets().add(LightTheme.toString());
        editor.setFont(new Font("System",18));
        TimeAdj_Area.setFont(new Font("System",18));
        PreviewPanel.setFont(new Font("System",18));
        //读入文本
        setEditor(controller.inputset.getText());
        //为文本区添加监听器，保证其修改同步
        editor.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
                // this will run whenever text is changed
                controller.inputset.setText("");
                controller.inputset.setText(editor.getText());
                System.out.println("自动保存"+"\n\n");
            }
        });
        //文本区光标监测
        editor.caretPositionProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                ArrayList<Integer> res=new editUtil().changPos(editor.getText(),newValue.intValue());
                line=res.get(0);
                pos=res.get(1);
                setPos_Indicator();
            }
        });

        //默认启动项
        ListGroup.selectToggle(BasicEdit);
        BasicEdit.setSelected(true);
        TimeAdj_mode.selectToggle(Processing_all);
        ToolPanel.setVisible(false);

        //绑定组件
        /*TimeAdj_left.prefWidthProperty().bind(TimeAdjPanel_left.widthProperty());*/
        TimeAdj_left.prefHeightProperty().bind(TimeAdjPanel_left.heightProperty());
        TimeAdj_Area.prefWidthProperty().bind(TimeAdjPanel_right.widthProperty());
        TimeAdj_Area.prefHeightProperty().bind(TimeAdjPanel_right.heightProperty());
        //
        setComboBox();
        //设置热键
        setHotKey();
    }

    /**
     * 切换颜色模式
     * @param ModeState
     */
    public void modeChange(Boolean ModeState){
        /*移除所有样式表*/
        Home.getStylesheets().remove(LightTheme);
        Home.getStylesheets().remove(DarkTheme);
        if(ModeState)//深色->浅色模式
            Home.getStylesheets().add(LightTheme.toString());
        else//浅色->深色模式
            Home.getStylesheets().add(DarkTheme.toString());
            //ModeState=!ModeState;
        //重设快捷键
       // setHotKey();
    }

    public void setEditor(String text){
        editor.setText(text);
    }

    private void setPos_Indicator(){
        ToolbarController.pos_Indicator.setText("第"+line+"行，"+"第"+pos+"列");
    }

    private void hide(){
        editor.setVisible(false);
        TimeAdjPanel.setVisible(false);
        PreviewPanel.setVisible(false);
        ToolPanel.setVisible(false);//切换编辑器时，隐藏工具面板
    }

//    public SplitPane getTimeAdjPanel(){
//        return TimeAdjPanel;
//    }

    /**
     * 当窗体缩放时不改变时间对齐面板左侧宽度
     */
//    public void zoom(){
//        double d=350/(TimeAdjPanel.getWidth());
//        TimeAdjPanel.setDividerPositions(d);
//    }

    /*基础编辑*/
    public void DoBasicEdit(ActionEvent actionEvent) {
        ListGroup.selectToggle(BasicEdit);
        BasicEdit.setSelected(true);
        //询问焦点，如已在此选项上，则操作无效化，避免重复加载
        if(controller.focus_indicator.equals(BasicEdit.getId()))
            return;
        controller.focus_indicator=BasicEdit.getId();//设置焦点
        //刷新文本
        hide();
        editor.setVisible(true);
        editor.setText(controller.inputset.getText());
        /*setEditor(controller.inputset.getText());*/
        ToolbarController.setToolBarControl(ToolPanelController,Home,ToolBarSpace,editor);
        editor.positionCaret(0);
        line=pos=1;
        setPos_Indicator();

    }

    /*时间轴对齐*/
    public void DoTimelineAdj(ActionEvent actionEvent) {

        ListGroup.selectToggle(TimelineAdj);
        TimelineAdj.setSelected(true);
        //询问焦点，如已在此选项上，则操作无效化，避免重复加载
        if(controller.focus_indicator.equals(TimelineAdj.getId()))
            return;
        controller.focus_indicator=TimelineAdj.getId();//设置焦点
        hide();
        TimeAdjPanel.setVisible(true);
        TimeAdj_Area.setText(controller.inputset.getText());
        //为文本区添加监听器，保证其修改同步
        TimeAdj_Area.textProperty().addListener((observable, oldValue, newValue) -> {
            controller.inputset.setText("");
            controller.inputset.setText(TimeAdj_Area.getText());
            System.out.println("时间轴对齐自动保存"+"\n\n");
        });
        //文本区光标监测
        TimeAdj_Area.caretPositionProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                ArrayList<Integer> res=new editUtil().changPos(editor.getText(),newValue.intValue());
                line=res.get(0);
                pos=res.get(1);
                setPos_Indicator();
            }
        });
        TimeAdj_Area.positionCaret(0);
        line=pos=1;
        setPos_Indicator();
        ToolbarController.setToolBarControl(ToolPanelController,Home,ToolBarSpace,TimeAdj_Area);
        //载入时间轴信息
        TimeAdj_oldTime_Area.setText(new TimelineUtil(controller.inputset.getSubType()).getTimeStart_Stop());
        //开关复位
        TimeAdj_hour.getSelectionModel().select(null);
        TimeAdj_min.getSelectionModel().select(null);
        TimeAdj_ms.getSelectionModel().select(null);
        TimeAdj_sec.getSelectionModel().select(null);
        TimeAdj_input.setText("");
        //选择框监听
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
        //输入框监听
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
        //询问焦点，如已在此选项上，则操作无效化，避免重复加载
        if(controller.focus_indicator.equals(Preview.getId()))
            return;
        controller.focus_indicator=Preview.getId();//设置焦点
        hide();
        PreviewPanel.setVisible(true);
        PreviewPanel.setText(controller.inputset.getText());
        //为文本区添加监听器，保证其修改同步
/*        PreviewPanel.textProperty().addListener((observable, oldValue, newValue) -> {
            controller.inputset.setText("");
            controller.inputset.setText(PreviewPanel.getText());
            System.out.println("文字预览自动保存"+"\n\n");
        });*/
        //文本区光标监测
        PreviewPanel.caretPositionProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                ArrayList<Integer> res=new editUtil().changPos(editor.getText(),newValue.intValue());
                line=res.get(0);
                pos=res.get(1);
                setPos_Indicator();
            }
        });
        //editorController.setEdit(PreviewPanel);
        PreviewPanel.positionCaret(0);
        line=pos=1;
        setPos_Indicator();
        ToolbarController.setToolBarControl(ToolPanelController,Home,ToolBarSpace,PreviewPanel);
        //设置内容
        PreviewPanel.setText(clean());
    }


    //设置时间轴选择器列表数据
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

    private void setHotKey(){
        Home.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
                //工具栏显隐Ctrl+T
                if (ke.isControlDown()&&ke.getCode() == KeyCode.T) {
                        ToolbarController.hideToolBar();
                    ke.consume(); // 释放
                }
                //搜索事件Ctrl+F
                if (ke.isControlDown()&&ke.getCode() == KeyCode.F) {
                    if(!ToolPanel.isVisible())
                        ToolbarController.onFind(null);
                    else
                        ToolbarController.onClose();
                    ke.consume(); // 释放
                }
                //搜索事件Ctrl+H
                if (ke.isControlDown()&&ke.getCode() == KeyCode.H) {
                    if(!ToolPanel.isVisible())
                        ToolbarController.onReplace(null);
                    else
                        ToolbarController.onClose();
                    ke.consume(); // 释放
                }
                //搜索事件Ctrl+H
                if (ke.isControlDown()&&ke.getCode() == KeyCode.J) {
                    if(!ToolPanel.isVisible())
                        ToolbarController.onJumpLine(null);
                    else
                        ToolbarController.onClose();
                    ke.consume(); // 释放
                }
            }
        });

    }

    /*时间轴对齐->解析选项到输入框*/
    private void ParsingSelection(){
        if(TimeAdj_hour.getSelectionModel().getSelectedItem()!=null&&TimeAdj_min.getSelectionModel().getSelectedItem()!=null&&TimeAdj_sec.getSelectionModel().getSelectedItem()!=null&&TimeAdj_ms.getSelectionModel().getSelectedItem()!=null){
            switch (controller.inputset.getSubType()){
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
        TimelineUtil tu=new TimelineUtil(controller.inputset.getSubType());
        String text=TimeAdj_input.getText().trim();
        String regex=new TimelineUtil(controller.inputset.getSubType()).getRegex();
        if(text.length()<12||!Pattern.matches(regex, text))
            return;
        ArrayList<String> res;
        switch (controller.inputset.getSubType()){
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

    //清除字幕中除对话外所有内容
    private String clean(){
        String res="";
        //int Type=controller.inputset.getType()<10?controller.inputset.getType():1;
        String[] list=controller.inputset.getText().split("\n");
        for (String temp:list){
            if(temp==null||temp.equals("")||Pattern.compile("[0-9]*").matcher(temp).matches()||tu.TimelineCheck(temp))
                continue;
            else
                res+=temp+"\n";
        }
        return res;
    }

    //处理时间轴
    public void onHandleTimeline(ActionEvent actionEvent) {
        String line=TimeAdj_input.getText().trim();
        TimelineUtil tu=new TimelineUtil((controller.inputset.getSubType()));
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
