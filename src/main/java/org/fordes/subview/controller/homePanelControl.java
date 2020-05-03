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
    private EditToolBarControl ToolbarController;
    @FXML
    private ToolPanelControl ToolPanelController;
    @FXML
    private RowConstraints ToolBarSpace;
    @FXML
    private TextField TimeAdj_input;

    private Object LightTheme=getClass().getClassLoader().getResource("css/mainStyle_Light.css").toString();
    private Object DarkTheme=getClass().getClassLoader().getResource("css/mainStyle_Dark.css").toString();
    private startControl controller= (startControl) Launcher.controllers.get(startControl.class.getSimpleName());

    private int line=1,pos=1;
    private TimelineUtil tu=controller.timelineUtil;
    public double DividerPositions;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        Home.getStylesheets().add(LightTheme.toString());
        editor.setFont(new Font("System",18));
        TimeAdj_Area.setFont(new Font("System",18));
        PreviewPanel.setFont(new Font("System",18));

        setEditor(controller.inputset.getText());

        editor.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {

                controller.inputset.setText("");
                controller.inputset.setText(editor.getText());
                System.out.println("自动保存"+"\n\n");
            }
        });

        editor.caretPositionProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                ArrayList<Integer> res=new editUtil().changPos(editor.getText(),newValue.intValue());
                line=res.get(0);
                pos=res.get(1);
                setPos_Indicator();
            }
        });


        ListGroup.selectToggle(BasicEdit);
        BasicEdit.setSelected(true);
        TimeAdj_mode.selectToggle(Processing_all);
        ToolPanel.setVisible(false);


        /*TimeAdj_left.prefWidthProperty().bind(TimeAdjPanel_left.widthProperty());*/
        TimeAdj_left.prefHeightProperty().bind(TimeAdjPanel_left.heightProperty());
        TimeAdj_Area.prefWidthProperty().bind(TimeAdjPanel_right.widthProperty());
        TimeAdj_Area.prefHeightProperty().bind(TimeAdjPanel_right.heightProperty());

        setComboBox();

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
        if(ModeState)
            Home.getStylesheets().add(LightTheme.toString());
        else
            Home.getStylesheets().add(DarkTheme.toString());



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
        ToolPanel.setVisible(false);
    }

//    public SplitPane getTimeAdjPanel(){
//        return TimeAdjPanel;
//    }

    /**
     * 当窗体缩放时不改变时间对齐面板左侧宽度
     */
//    public void zoom(){

//        TimeAdjPanel.setDividerPositions(d);
//    }

    /*基础编辑*/
    public void DoBasicEdit(ActionEvent actionEvent) {
        ListGroup.selectToggle(BasicEdit);
        BasicEdit.setSelected(true);

        if(controller.focus_indicator.equals(BasicEdit.getId()))
            return;
        controller.focus_indicator=BasicEdit.getId();

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

        if(controller.focus_indicator.equals(TimelineAdj.getId()))
            return;
        controller.focus_indicator=TimelineAdj.getId();
        hide();
        TimeAdjPanel.setVisible(true);
        TimeAdj_Area.setText(controller.inputset.getText());

        TimeAdj_Area.textProperty().addListener((observable, oldValue, newValue) -> {
            controller.inputset.setText("");
            controller.inputset.setText(TimeAdj_Area.getText());
            System.out.println("时间轴对齐自动保存"+"\n\n");
        });

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

        TimeAdj_oldTime_Area.setText(new TimelineUtil(controller.inputset.getSubType()).getTimeStart_Stop());

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

        if(controller.focus_indicator.equals(Preview.getId()))
            return;
        controller.focus_indicator=Preview.getId();
        hide();
        PreviewPanel.setVisible(true);
        PreviewPanel.setText(controller.inputset.getText());

/*        PreviewPanel.textProperty().addListener((observable, oldValue, newValue) -> {
            controller.inputset.setText("");
            controller.inputset.setText(PreviewPanel.getText());
            System.out.println("文字预览自动保存"+"\n\n");
        });*/

        PreviewPanel.caretPositionProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                ArrayList<Integer> res=new editUtil().changPos(editor.getText(),newValue.intValue());
                line=res.get(0);
                pos=res.get(1);
                setPos_Indicator();
            }
        });

        PreviewPanel.positionCaret(0);
        line=pos=1;
        setPos_Indicator();
        ToolbarController.setToolBarControl(ToolPanelController,Home,ToolBarSpace,PreviewPanel);

        PreviewPanel.setText(clean());
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

    private void setHotKey(){
        Home.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {

                if (ke.isControlDown()&&ke.getCode() == KeyCode.T) {
                        ToolbarController.hideToolBar();
                    ke.consume();
                }

                if (ke.isControlDown()&&ke.getCode() == KeyCode.F) {
                    if(!ToolPanel.isVisible())
                        ToolbarController.onFind(null);
                    else
                        ToolbarController.onClose();
                    ke.consume();
                }

                if (ke.isControlDown()&&ke.getCode() == KeyCode.H) {
                    if(!ToolPanel.isVisible())
                        ToolbarController.onReplace(null);
                    else
                        ToolbarController.onClose();
                    ke.consume();
                }

                if (ke.isControlDown()&&ke.getCode() == KeyCode.J) {
                    if(!ToolPanel.isVisible())
                        ToolbarController.onJumpLine(null);
                    else
                        ToolbarController.onClose();
                    ke.consume();
                }
            }
        });

    }

    /*时间轴对齐->解析选项到输入框*/
    private void ParsingSelection(){
        if(TimeAdj_hour.getSelectionModel().getSelectedItem()!=null&&TimeAdj_min.getSelectionModel().getSelectedItem()!=null&&TimeAdj_sec.getSelectionModel().getSelectedItem()!=null&&TimeAdj_ms.getSelectionModel().getSelectedItem()!=null){
            switch (controller.inputset.getSubType()){
                case 1:
                    TimeAdj_input.setText(TimeAdj_hour.getSelectionModel().getSelectedItem().toString()+":"+TimeAdj_min.getSelectionModel().getSelectedItem().toString()+":"+TimeAdj_sec.getSelectionModel().getSelectedItem().toString()+","+TimeAdj_ms.getSelectionModel().getSelectedItem().toString());
                    break;
                case 2:
                    break;
                case 3:
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


    private String clean(){
        String res="";

        String[] list=controller.inputset.getText().split("\n");
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
