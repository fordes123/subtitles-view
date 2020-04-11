package org.fordes.subview.util.TransUtil;

import org.fordes.subview.controller.startControl;
import org.fordes.subview.main.Launcher;
import org.fordes.subview.util.ConfigUtil.ConfigPathUtil;
import org.fordes.subview.util.ConfigUtil.ConfigRWUtil;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TranslationUtil {

    private static String APP_ID = "";
    private static String SECURITY_KEY = "";
    private static int limit=2000;//每次翻译内容长度不超过2k字节
    //语言支持列表
    private static HashMap<String, String> LanguageSupportList = new HashMap<String, String>();
    private startControl controller= (startControl) Launcher.controllers.get(startControl.class.getSimpleName());

    static {
        APP_ID=new ConfigRWUtil().GetInterfaceInformation("app_id",new ConfigPathUtil().getBaiduTarnInfoPath());
        SECURITY_KEY=new ConfigRWUtil().GetInterfaceInformation("secret_key",new ConfigPathUtil().getBaiduTarnInfoPath());
    }

    //构造语言支持列表
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

    //从语言支持列表中取得语言列表，用于构造选项
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

    //百度在线翻译调用
    public String TransText_Baidu(String subtitles, String orig, String target, int type) throws ExecutionException, InterruptedException {
        String TranText="";
        ArrayList<String> EffectList=new ArrayList<>();
        ArrayList<String> SubtitlesList = new ArrayList<>();
        ArrayList<String> ResList=new ArrayList<>();
        String[] list = subtitles.split("\n");//逐行分割内容
        for (int i=0;i<list.length;i++) {
            String text=list[i];
            //分割有效行和无效行
            if(controller.timelineUtil.TimelineCheck(text)||text.length()==0||isNumericzidai(text)){
                SubtitlesList.add(i,text);
            }else {
                EffectList.add(text);
                SubtitlesList.add(i,null);
            }
        }
        //处理待翻译文本
        int EffLength=0;
        for (String test:EffectList)
            EffLength+=test.getBytes().length;
        //计算分段数
        int part=EffLength/limit+1;//设置每段不超过5k字节
        int partSize=EffectList.size()/part;//每段的容量

        // 创建一个并发执行的线程池
        ExecutorService executor = Executors.newFixedThreadPool(10);
        ArrayList<Future> FutureList=new ArrayList<>();
        int count=0;

        long start = System.currentTimeMillis();

            //分段翻译并组合结果
            for (int i = 0; i < part; i++) {
                String Text = "";
                for (int j = i * partSize; j < (i + 1) * partSize; j++) {
                    Text += EffectList.get(j) + "\n";
                }
                if(++count==10) {
                    Thread.sleep(2000);
                    count=0;
                }
                Callable tranTask=new TranThread(Text,LanguageSupportList.get(orig), LanguageSupportList.get(target),APP_ID,SECURITY_KEY);
                FutureList.add(i,executor.submit(tranTask));
            }
            if (part > 1) {
                String Text = "";
                for (int j = part * partSize; j < EffectList.size(); j++) {
                    Text += EffectList.get(j) + "\n";
                }
                Callable tranTask=new TranThread(Text,LanguageSupportList.get(orig), LanguageSupportList.get(target),APP_ID,SECURITY_KEY);
                FutureList.add(part,executor.submit(tranTask));
            }

        executor.shutdown();

        try
        {
            // awaitTermination返回false即超时会继续循环，返回true即线程池中的线程执行完成主线程跳出循环往下执行，每隔10秒循环一次
            while (!executor.awaitTermination(10, TimeUnit.SECONDS));
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        long end = System.currentTimeMillis();
        System.out.println("\n处理时长：" + (end - start));


        //组合结果集
        for(Future temp:FutureList){
            String res=temp.get().toString();
            for(String str:res.split("\n"))
                ResList.add(str);
        }
        //构造翻译后的字幕文本
        ResList.add("\n");
        int j=0;
        try{for(int i=0;i<SubtitlesList.size();i++){
            String text="";
            if(SubtitlesList.get(i)==null){
                text=ResList.get(j);
                j++;
            }else
                text=SubtitlesList.get(i);
            TranText+=text+"\n";
        }}catch (Exception e){
            return null;
        }
        return TranText;
    }


    public static void main(String[] args) {
    }

    //解析百度翻译的结果集
    static String AnalyticalBaiduTransResults(String line){
        String res="";
        //正则表达式
        String pattern = "(\"dst\":\")(.*?)(\")"; //第一个括号表示以""dst":""开头，第三个括号表示以"""(反引号)结尾，中间括号为目标值，
        // 创建 Pattern 对象
        Pattern r = Pattern.compile(pattern);
        // 创建 matcher 对象
        Matcher m = r.matcher(line);
        while (m.find())/*
             自动遍历打印所有结果   group方法打印捕获的组内容，以正则的括号角标从1开始计算，我们这里要第2个括号里的
             值， 所以取 m.group(2)， m.group(0)取整个表达式的值，如果越界取m.group(4),则抛出异常
           */
            res+=unicodeToChina(m.group(2))+"\n";
            //System.out.println(res);
        return res;
    }

    //Unicode转换为中文
    public static String unicodeToChina(String str) {
        Charset set = Charset.forName("UTF-16");
        Pattern p = Pattern.compile("\\\\u([0-9a-fA-F]{4})");
        Matcher m = p.matcher( str );
        int start = 0 ;
        int start2 = 0 ;
        StringBuffer sb = new StringBuffer();
        while( m.find( start ) ) {
            start2 = m.start() ;
            if( start2 > start ){
                String seg = str.substring(start, start2) ;
                sb.append( seg );
            }
            String code = m.group( 1 );
            int i = Integer.valueOf( code , 16 );
            byte[] bb = new byte[ 4 ] ;
            bb[ 0 ] = (byte) ((i >> 8) & 0xFF );
            bb[ 1 ] = (byte) ( i & 0xFF ) ;
            ByteBuffer b = ByteBuffer.wrap(bb);
            sb.append( String.valueOf( set.decode(b) ).trim() );
            start = m.end() ;
        }
        start2 = str.length() ;
        if( start2 > start ){
            String seg = str.substring(start, start2) ;
            sb.append( seg );
        }
        return sb.toString() ;
    }

    //判断字符串是否为数字
    public static boolean isNumericzidai(String str) {
        Pattern pattern = Pattern.compile("-?[0-9]+\\.?[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if (!isNum.matches()) {
            return false;
        }
        return true;
    }

}
