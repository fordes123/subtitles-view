package org.fordes.subview.util;

import javafx.scene.control.TextArea;
import org.fordes.subview.controller.startControl;
import org.fordes.subview.main.Launcher;
import org.fordes.subview.util.SubtitlesUtil.TimelineUtil;
import org.fordes.subview.util.SubtitlesUtil.editUtil;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FindReplaceUtil {
    private static String target;
    private static String ReplaceTarget;

    private static Boolean caseState,regState,filterState;
    private static String[] list;
    private static String pattern;
    private static Pattern r;
    private static Matcher m;

    private static String Pending="";//待处理内容
    private static TextArea edit;
    private static int counter=0;//计数器

    private int posRow=0;//记录关键字所在行
    private int posCol=0;//记录关键字所在列

    private startControl controller= (startControl) Launcher.controllers.get(startControl.class.getSimpleName());

    /**
     * 初始化构造搜索，每次更改关键字需重新构造
     * @param ，搜索文本
     * @param target，目标文本
     * @param caseState，区分大小写
     * @param regState，正则表达式
     * @param filterState，过滤时间轴
     */
    public FindReplaceUtil(TextArea edit,String target,boolean caseState,boolean regState,boolean filterState){
        //搜索选项
        this.caseState=caseState;
        this.regState=regState;
        this.filterState=filterState;
        this.edit=edit;
        //分行存取
        list=edit.getText().split("\n");
        //初始化
        /*如开启了正则表达式，则输入为正则，否则输入为目标文本*/
        if(regState)
            pattern=target;
        else
            this.target=target;//目标字符串
        counter=0;
//        System.out.println("区分大小写："+(caseState?"开":"关"));
//        System.out.println("正则表达式："+(regState?"开":"关"));
//        System.out.println("过滤时间轴："+(filterState?"开":"关")+"\n");
    }


    /**
     * 检索关键字
     * @return
     */
    private Boolean find(){
        boolean state=false;
        //获取光标位置（搜索起始位置）
        ArrayList<Integer> res=new editUtil().changPos(edit.getText(),edit.getCaretPosition());
        int line=res.get(0)-1; int pos=res.get(1)-1;
        //System.out.println("第"+line+"行，第"+pos+"列");

        /*从起始位置向下搜索*/
        for(int i=line;i<list.length;i++){
            if(i==line){
                Pending=list[i].substring(pos,list[i].length());
               // System.out.println("从["+Pending+"]开始搜索");
            }else
                Pending=list[i];
            //执行搜索
            if(get()){
                state=true;
                posRow=i;
                break;
            }
        }
        /*从开始向起始位置搜索*/
        if(!state)/*从起始位置向下搜索未找到，此时计数器归零*/
            counter=0;
        for(int i=0;i<=line;i++){
            if(state){//已经搜索到，则此处计数
                if(i==line)
                    counter+=list[i].substring(0,pos).length();
                else
                    counter+=list[i].length()+1;
            }else {
//                if(i==line)
//                    counter--;
                Pending=list[i];
               // System.out.println("计数："+counter+",搜索："+Pending);
                if(get()){
                    state=true;
                    posRow=i;
                    return state;
                }

            }
        }
        //System.out.println("计数器："+counter);
        return state;
    }


    /**
     * 从一串文本中校验关键字
     * @return
     */
    private boolean get(){
        /*如开启了过滤时间轴，校验并跳过时间轴行*/
        if(filterState&&new TimelineUtil(controller.inputset.getSubType()).TimelineCheck(Pending)){
            counter+=Pending.length()+1;
            return false;
        }
        /*如未开启区分大小写，统一转换为小写*/
        if(!caseState){
            Pending=Pending.toLowerCase();
            if(!regState)//在未使用正则的情况下，搜素目标转小写
                target=target.toLowerCase();
        }
        /*如开启了正则表达式，则搜索使用该正则*/
        if(!regState)
            pattern="(.*?)("+target+")";//构建正则
        /*使用正则进行搜索*/
        r = Pattern.compile(pattern);//创建 Pattern 对象
        m = r.matcher(Pending);// 创建 matcher 对象
        if(!m.find()){//如该行无关键字，跳过
            counter+=Pending.length()+1;
            return false;
        }
        else{
            //int point;
            if(regState){
                //System.out.println("使用自定义正则:"+m.group());
                target = m.group();
                posCol=m.end();
            }
            else
                posCol=m.end(2);

            counter+=posCol;
            posCol-=target.length();
            return true;
        }
    }

    public Boolean search(){
        boolean state=find();
        if(state)
            edit.selectRange(counter-target.length(),counter);
        //System.out.println("搜索目标："+target+"位置："+(counter-target.length())+","+counter);
        return state;
    }

    public Boolean replace(String ReplaceTarget){
        this.ReplaceTarget=ReplaceTarget;
        String str="";
        boolean state=find();
        if(state){
            //System.out.println("关键字位置");
            for(int i=0;i<list.length;i++){
                if(i==posRow){
                    String before,after="";
                    before=list[i].substring(0,posCol);
                    after=list[i].substring(posCol,list[i].length());
                    String temp=before+r.matcher(after).replaceFirst(ReplaceTarget)+"\n";
                    str+=temp;
                    System.out.println("替换后的行："+temp);
                }else
                    str+=list[i]+"\n";
        }
            edit.setText(str);
            edit.selectRange(counter-target.length(),counter-target.length()+ReplaceTarget.length());
        }
        return state;
    }


    public boolean replaceAll(String ReplaceTarget){
        this.ReplaceTarget=ReplaceTarget;
        String str="";
        boolean state=find();
        if(state){
            str=edit.getText().replaceAll(target,ReplaceTarget);
            edit.setText(str);
            return true;
        }
        return false;
    }

}
