package org.fordes.subview.util.TransUtil;

import org.fordes.subview.controller.StartController;
import org.fordes.subview.main.Launcher;
import org.fordes.subview.util.ConfigUtil.ConfigRWUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;

public class Translation {

    private static String APP_ID = "";
    private static String SECURITY_KEY = "";
    private static int limit=2000;
    private static boolean free;
    private static HashMap<String, String> LanguageSupportList = new HashMap<String, String>();
    private StartController startController = (StartController) Launcher.controllers.get(StartController.class.getSimpleName());

    static {
        HashMap<String,String> info = new ConfigRWUtil().getTranConfig();
        APP_ID = info.get("id");
        SECURITY_KEY =info.get("key");
        free = info.get("type").equals("free")?true:false;
    }

    static {
        LanguageSupportList.put("自动检测","auto");
        LanguageSupportList.put("简体中文","zh");
        LanguageSupportList.put("英语","en");
        LanguageSupportList.put("粤语","yue");
        LanguageSupportList.put("文言文","wyw");
        LanguageSupportList.put("日语","jp");
        LanguageSupportList.put("韩语","kor");
        LanguageSupportList.put("法语","fra");
        LanguageSupportList.put("西班牙语","spa");
        LanguageSupportList.put("泰语","th");
        LanguageSupportList.put("阿拉伯语","ara");
        LanguageSupportList.put("俄语","ru");
        LanguageSupportList.put("葡萄牙语","pt");
        LanguageSupportList.put("德语","de");
        LanguageSupportList.put("意大利语","it");
        LanguageSupportList.put("希腊语","el");
        LanguageSupportList.put("荷兰语","nl");
        LanguageSupportList.put("波兰语","pl");
        LanguageSupportList.put("保加利亚语","bul");
        LanguageSupportList.put("爱沙尼亚语","est");
        LanguageSupportList.put("丹麦语","dan");
        LanguageSupportList.put("芬兰语","fin");
        LanguageSupportList.put("捷克语","cs");
        LanguageSupportList.put("罗马尼亚语","rom");
        LanguageSupportList.put("斯洛文尼亚语","slo");
        LanguageSupportList.put("瑞典语","swe");
        LanguageSupportList.put("匈牙利语","hu");
        LanguageSupportList.put("繁体中文","cht");
        LanguageSupportList.put("越南语","vie");
    }


    public ArrayList getLanguageList(){
        ArrayList<String> ll=new ArrayList<>();
        ll.add("自动检测");
        ll.add("简体中文");
        ll.add("英语");
        ll.add("粤语");
        ll.add("文言文");
        ll.add("日语");
        ll.add("韩语");
        ll.add("法语");
        ll.add("西班牙语");
        ll.add("泰语");
        ll.add("阿拉伯语");
        ll.add("俄语");
        ll.add("葡萄牙语");
        ll.add("德语");
        ll.add("意大利语");
        ll.add("希腊语");
        ll.add("荷兰语");
        ll.add("波兰语");
        ll.add("保加利亚语");
        ll.add("爱沙尼亚语");
        ll.add("丹麦语");
        ll.add("芬兰语");
        ll.add("捷克语");
        ll.add("罗马尼亚语");
        ll.add("斯洛文尼亚语");
        ll.add("瑞典语");
        ll.add("匈牙利语");
        ll.add("繁体中文");
        ll.add("越南语");
        return ll;
    }


    public String TransText_Baidu(String subtitles, String orig, String target, int type) throws ExecutionException, InterruptedException {
        String TranText="";
        String[] list = subtitles.split("\n");
        ArrayList<String> SubtitlesList = new ArrayList<>();
        ArrayList<String> ResList=new ArrayList<>();
        ArrayList<String> EffectList=new ArrayList<>();
        String Orig=LanguageSupportList.get(orig);
        String Target=LanguageSupportList.get(target);

        /*过滤无需翻译内容*/
        for(int i=0;i<list.length;i++) {
            if (startController.timelineUtil.TimelineCheck(list[i]) || list[i].length() == 0 || startController.timelineUtil.isNumericzidai(list[i]))
                SubtitlesList.add(i,list[i]);
            else {
                SubtitlesList.add(i, null);
                EffectList.add(list[i]);
            }
        }
        /*根据长度限制对有效进行分段并初始化线程池*/
        ExecutorService executor = Executors.newFixedThreadPool(10);
        final List<Future> FutureList = Collections.synchronizedList(new ArrayList<Future>());
        long start = System.currentTimeMillis();
        String temp="";
        int part=0;
        for(String line:EffectList){
            if(line.length()+temp.length()<limit)
                temp+=line+"\n";
            else{
                FutureList.add(part,executor.submit(new BaiduTransUtil(temp,Orig,Target,APP_ID,SECURITY_KEY,false)));
                if(free)
                    Thread.sleep(1000);/*免费接口每秒仅允许一次请求(服务器限制)*/
                temp=line+"\n";
                ++part;
            }
        }
        /*补全剩下部分/不足一个分段*/
        FutureList.add(part,executor.submit(new BaiduTransUtil(temp,Orig,Target,APP_ID,SECURITY_KEY,false)));
        executor.shutdown();
        try {
            while (!executor.awaitTermination(10, TimeUnit.SECONDS));
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.print("线程数："+FutureList.size()+"，处理时长：" + (end - start)+"毫秒，");
        /*解析结果*/
        for(int i=0;i<FutureList.size();i++)
            for(String line:FutureList.get(i).get().toString().split("\n"))
                ResList.add(line);
        System.out.println("总行数："+SubtitlesList.size()+"，有效行："+EffectList.size()+",已处理:"+EffectList.size()+"行，获得结果："+ResList.size()+"行");
        /*还原结构*/
        if(EffectList.size()==ResList.size()){
            int i=0,j=0;
            String line="";
            for(i=0;i<SubtitlesList.size();i++){
                line=SubtitlesList.get(i)==null?ResList.get(j++):SubtitlesList.get(i);
                TranText+=line+"\n";
            }
            return TranText;
        }
        return null;
    }



}
