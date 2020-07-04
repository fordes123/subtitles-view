package org.fordes.subview.util.SubtitlesUtil;



import org.fordes.subview.controller.StartController;
import org.fordes.subview.main.Launcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
工具类，时间轴相关
 */
public class TimelineUtil {

    private StartController startController = (StartController) Launcher.controllers.get(StartController.class.getSimpleName());
    private int type;
    public TimelineUtil(int type){
        this.type=type;
    }
    /**
     * 时间轴转换为毫秒
     * @param time ,时间轴字符串
     */
    public long TimelineToMillisecond(String time){

        switch (type){
            case 1:
                return (Integer.parseInt(time.substring(0, 2)) * 3600 + Integer.parseInt(time.substring(3, 5)) * 60 + Integer.parseInt(time.substring(6, 8))) * 1000 + Integer.parseInt(time.substring(9, 12));
            case 2:
                return Integer.parseInt(time.substring(20, 22)) + Integer.parseInt(time.substring(17, 19)) * 100 + Integer.parseInt(time.substring(14, 16)) * 6000 + Integer.parseInt(time.substring(12, 13)) * 360000;
            case 3:
                return Integer.parseInt(time.substring(7, 9)) + Integer.parseInt(time.substring(4, 6)) * 100 + Integer.parseInt(time.substring(1, 3)) * 6000;
        }
    return 0;
    }

    /**
     * 根据时间轴类型，返回校验正则
     * @return
     */
    public String getRegex(int Types){
        String regex="";
        switch (Types){
            case 1:
                regex = "(\\d{2})(:)(\\d{2})(:)(\\d{2})(,)(\\d{3})(.*?)";
                break;
            case 2:
                regex="(Dialogue:)(.*?)";
                break;
            case 3:
                regex = "\\d\\d:\\d\\d.\\d\\d";
                break;
        }
        return regex;
    }

    public String getRegex(){
        return getRegex(type);
    }

    /**
     * 将时间轴解析为具体int型时间
     * @param line，必须为严格正确的时间轴
     * @return
     */
    public ArrayList<Integer> TimeLineToInt(String line){
        ArrayList<Integer> res=new ArrayList<>();
        String regex="";
        Matcher m;
        switch (type){
            case 1:
                System.out.println(line);
                regex="(\\d{2})(:)(\\d{2})(:)(\\d{2})(,)(\\d{3})";
                m = Pattern.compile(regex).matcher(line);
                if(m.find()) {
                    res.add(Integer.valueOf(m.group(1)));
                    res.add(Integer.valueOf(m.group(3)));
                    res.add(Integer.valueOf(m.group(5)));
                    res.add(Integer.valueOf(m.group(7)));
                }else
                    return null;
                break;
            case 2:
               
                regex="(\\d{1})(:)(\\d{2})(:)(\\d{2})(.)(\\d{2})";
                m = Pattern.compile(regex).matcher(line);
                if(m.find()) {
                    res.add(Integer.valueOf(m.group(1)));
                    res.add(Integer.valueOf(m.group(3)));
                    res.add(Integer.valueOf(m.group(5)));
                    res.add(Integer.valueOf(m.group(7)));
                }else
                    return null;
                break;
            case 3:
                regex="(\\d{2})(:)(\\d{2})(.)(\\d{2})";
                m = Pattern.compile(regex).matcher(line);
                if(m.find()){
                    res.add(Integer.valueOf(m.group(1)));
                    res.add(Integer.valueOf(m.group(3)));
                    res.add(Integer.valueOf(m.group(5)));
                }else
                    return null;
                break;
        }
        return res;
    }

    /**
     * 校验字符串是否为时间轴
     * @param str ,字符串
     */
    public Boolean TimelineCheck(String str){
        return Pattern.matches(getRegex(), str);
    }

    public Boolean TimelineCheck(String str,int Type){
        return Pattern.matches(getRegex(Type), str);
    }

    /**
     * 将数值型时间转换为时间轴可以使用的形如01；02格式
     * @return
     */
    public ArrayList<String> TimeToString(ArrayList<Integer> in){
        ArrayList<String> res=null;
        String h,m,s,ms="";
        int hour=in.get(0),min=in.get(1),sec=in.get(2),mse=in.get(3);
        
        switch (type){
            case 1:
                
                if(hour<10)
                    h = "0" + hour;
                else
                    h = "" + hour;
                if (min<10)
                    m = "0" + min;
                else
                    m = "" + min;
                if (sec<10)
                    s = "0" + sec;
                else
                    s = "" + sec;
                if (mse<10)
                    ms = "00" + mse;
                else if(mse<100)
                    ms = "0" + mse;
                else
                    ms = "" + mse;
                res=new ArrayList<String>(Arrays.asList(h,m,s,ms));
                break;
            case 2:
                break;
            case 3:
                break;

        }
        return res;
    }

    /**
     * 毫秒转换为时间轴
     * @param time ,时间数值
     */
    private String MillisecondToTimeline(long time){

        switch (type){
            case 1:
                String h,m,s,ms;
                
                int hour = (int)time/3600000;
                int min = (int)(time%3600000)/60000;
                int sec = (int)((time%3600000)%60000)/1000;
                int mse =  (int)((time%3600000)%60000)%1000;
                
                ArrayList<String> res=TimeToString(new ArrayList<Integer>(Arrays.asList(hour,min,sec,mse)));
                
                return res.get(0)+":"+res.get(1)+":"+res.get(2)+","+res.get(3);
            case 2:
                return null;
            case 3:
                return null;
        }
        System.out.println("未知错误");
        return null;
    }


    /*获取时间轴起点*/
    public String getTimeStart(){
        return ParsingTimeline(startController.inputset.getText()).get(0);
    }

    /*获取时间轴终点*/
    public String getTimeEnd(){
        return ParsingTimeline(startController.inputset.getText()).get(1);
    }

    /*获取时间轴起止信息*/
    public String getTimeStart_Stop(){
        ArrayList<String> res=ParsingTimeline(startController.inputset.getText());
        return res.get(0)+" - "+res.get(1);
    }
    /**
     * 解析时间轴起点、终点
     * @param subtitles ,字幕文本
     */
    private ArrayList<String> ParsingTimeline(String subtitles) {
        String[] list=subtitles.split("\n");
        String regex="";
        Pattern r;
        String start="",end="";
        boolean state=false;
        switch (type) {
            case 1:
                regex = "\\d\\d:\\d\\d:\\d\\d,\\d\\d\\d";
                r = Pattern.compile(regex);
                for(String line:list){
                    Matcher m = r.matcher(line);
                    while (m.find()){
                        if(state){
                            end=m.group();}
                        else{
                            start=m.group();
                            state=true;
                        }
                    }
                }
                break;
            case 2:
                regex="(Dialogue: 0,)(.*?)(,)(.*?)(,)";
                
                r = Pattern.compile(regex);
                for(String line:list){
                    Matcher m = r.matcher(line);
                    while (m.find()){
                        if(state){
                            end=m.group(4);}
                        else{
                            start=m.group(2);
                            state=true;
                        }
                    }
                }
                break;
            case 3: 
                regex="\\d\\d:\\d\\d.\\d\\d";
                
                r = Pattern.compile(regex);
                for(String line:list){
                    Matcher m = r.matcher(line);
                    while (m.find()){
                        if(state){
                            end=m.group();}
                        else{
                            start=m.group();
                            state=true;
                        }
                    }
                }
                break;

        }
        
        ArrayList<String> res=new ArrayList<String>();
        res.add(start);res.add(end);
        return res;


    }


    /**
     * 时间轴对齐
     * @param temp ,字幕文本
     * @param time ,时间
     */
    public String Revise(String temp, long time) {
        
        long StartTime = 0;
        String[] list;
        String subtitles = "";
        long timeError;
        switch (type) {
            case 1:

                list = temp.split("\n");
                String regex1 = "\\d\\d:\\d\\d:\\d\\d,\\d\\d\\d --> \\d\\d:\\d\\d:\\d\\d,\\d\\d\\d";
                
                timeError=time-TimelineToMillisecond(ParsingTimeline(temp).get(0));
                for (int j = 0; j < list.length; j++) {
                    if (!Pattern.matches(regex1, list[j])) {
                        subtitles += list[j] + "\n";
                        continue;
                    } else {
                        
                        long first=TimelineToMillisecond(list[j].substring(0, 12));
                        
                        long last=TimelineToMillisecond(list[j].substring(17, 29));
                        
                        String newFirst=MillisecondToTimeline(first+timeError);
                        
                        String newLast=MillisecondToTimeline(last+timeError);
                        
                        subtitles+=newFirst+" --> "+newLast+"\n";
                       }
                }
                return subtitles;

            case 2:

               /* newsub = "";
                list = temp.split("\n");
                for (int i = 0; i < list.length; i++) {
                    if (list[i].length() < 33 || !(list[i].substring(0, 8).equals("Dialogue")))
                        continue;
                    else {
                        int millisecond = Integer.parseInt(list[i].substring(20, 22));
                        int second = Integer.parseInt(list[i].substring(17, 19));
                        int minute = Integer.parseInt(list[i].substring(14, 16));
                        int hour = Integer.parseInt(list[i].substring(12, 13));
                        StartTime = millisecond + second * 100 + minute * 6000 + hour * 360000; 

                        break;
                    }
                }




                timeError=time-StartTime;

                for (int j = 0; j < list.length; j++) {
                    if (list[j].length() < 33 || !(list[j].substring(0, 8).equals("Dialogue"))) {
                        newsub += list[j] + "\n";
                        continue;
                    } else {

                        int ms = Integer.parseInt(list[j].substring(20, 22));
                        int s = Integer.parseInt(list[j].substring(17, 19));
                        int m = Integer.parseInt(list[j].substring(14, 16));
                        int h = Integer.parseInt(list[j].substring(12, 13));

                        String h1 = "" + ((timeError + ms + s * 100 + m * 6000 + h * 360000) / 360000);
                        String m1 = "" + (((timeError + ms + s * 100 + m * 6000 + h * 360000) % 360000) / 6000);
                        String s1 = "" + ((timeError + ms + s * 100 + m * 6000 + h * 360000) % 6000 / 100);
                        String ms1 = "" + ((timeError + ms + s * 100 + m * 6000 + h * 360000) % 100);

                        int ms2 = Integer.parseInt(list[j].substring(31, 33));
                        int s2 = Integer.parseInt(list[j].substring(28, 30));
                        int m2 = Integer.parseInt(list[j].substring(25, 27));
                        int h2 = Integer.parseInt(list[j].substring(23, 24));

                        String h3 = "" + ((timeError + ms2 + s2 * 100 + m2 * 6000 + h2 * 360000) / 360000);
                        String m3 = "" + (((timeError + ms2 + s2 * 100 + m2 * 6000 + h2 * 360000) % 360000) / 6000);
                        String s3 = "" + ((timeError + ms2 + s2 * 100 + m2 * 6000 + h2 * 360000) % 6000 / 100);
                        String ms3 = "" + ((timeError + ms2 + s2 * 100 + m2 * 6000 + h2 * 360000) % 100);

                        newsub += list[j].substring(0, 12) + h1 + ":";
                        if (m1.length() == 1)
                            m1 = "0" + m1;
                        newsub += m1 + ":";
                        if (s1.length() == 1)
                            s1 = "0" + s1;
                        newsub += s1 + ".";
                        if (ms1.length() == 1)
                            ms1 = "0" + s1;
                        newsub += ms1 + ",";

                        newsub += h3 + ":";
                        if (m3.length() == 1)
                            m3 = "0" + m3;
                        newsub += m3 + ":";
                        if (s3.length() == 1)
                            s3 = "0" + s3;
                        newsub += s3 + ".";

                        if (ms3.length() == 1)
                            ms3 = "0" + s3;

                        newsub += ms3 + list[j].substring(33, list[j].length()) + "\n";
                    }
                }
                return newsub;
*/

            case 3: 
               /* String regex3 = "\\d\\d:\\d\\d.\\d\\d";
                newsub = "";
                list = temp.split("\n");
                for (int i = 0; i < list.length; i++) {
                    
                    if (!Pattern.matches(regex3, list[i].substring(0, 9)))
                        continue;
                    else {
                        int ms = Integer.parseInt(list[i].substring(7, 9));
                        int s = Integer.parseInt(list[i].substring(4, 6));
                        int m = Integer.parseInt(list[i].substring(1, 3));
                        StartTime = ms + s * 100 + m * 6000; 

                        break;
                    }
                }



                timeError=time-StartTime;

                for (int j = 0; j < list.length; j++) {
                    if (list[j].length() < 9) {
                        newsub += list[j] + "\n";
                        continue;
                    } else if (!Pattern.matches(regex3, list[j].substring(1, 9))) {
                        newsub += list[j] + "\n";
                        continue;

                    } else {

                        int ms = Integer.parseInt(list[j].substring(7, 9));
                        int s = Integer.parseInt(list[j].substring(4, 6));
                        int m = Integer.parseInt(list[j].substring(1, 3));

                        String m1 = "" + ((timeError + ms + s * 100 + m * 6000) / 6000);
                        String s1 = "" + (((timeError + ms + s * 100 + m * 6000) % 6000) / 100);
                        String ms1 = "" + ((timeError + ms + s * 100 + m * 6000) % 100);

                        if (m1.length() == 1)
                            m1 = "[" + "0" + m1;
                        newsub += m1 + ":";
                        if (s1.length() == 1)
                            s1 = "0" + s1;
                        newsub += s1 + ".";
                        if (ms1.length() == 1)
                            ms1 = "0" + ms1;
                        newsub += ms1 + "]";

                        newsub += list[j].substring(10, list[j].length()) + "\n";
                    }
                }
                return newsub;*/

        }
    return null;
    }


    /**
     * 文字预览，排除时间轴，只显示纯文本
     * @param subtitles ,字幕文本
     */
    public String Preview(String subtitles){
        String[] list = subtitles.split("\n");
        String result="";
        for (int i = 0; i < list.length; i++) {
            if(TimelineCheck(list[i]))
                continue;
            else
                result+=list[i]+"\n";
        }
        return result;
    }

    /**
     * 语音转换出的原始数据解析出时间轴
     * @param data ,一行原始数据
     */
    public String exTimeLine(String data) {
        String reg = "\\\"bg\\\":\\\"(\\d*)\\\",\\\"ed\\\"\\:\\\"(\\d*)\\\",\\\"onebest\\\"\\:\\\"(.*)\\\"";
        Matcher matcher = Pattern.compile(reg).matcher(data);
        if(matcher.find()){
            String bg=matcher.group(1);
            String ed=matcher.group(2);
            String dialogue=matcher.group(3).trim();
            return MillisecondToTimeline(Integer.valueOf(bg))+" --> "+MillisecondToTimeline(Integer.valueOf(ed))+"\n"+dialogue;
        }
        return null;
    }


    /**
     * 处理语音转换结果生成初始字幕
     */
    public StringBuffer parsing(String date) {
        int counter=0;
        StringBuffer res=new StringBuffer();
        String reg = "(\\{)(.*?)(\\})";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(date);
        while (matcher.find())
            res.append(counter+++"\n"+exTimeLine(matcher.group(2))+"\n\n");
        res.delete(res.length()-2,res.length());//去除多的换行符
        return res;
    }

    public static boolean isNumericzidai(String str) {
        Pattern pattern = Pattern.compile("-?[0-9]+\\.?[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches())
            return false;
        return true;
    }

}
