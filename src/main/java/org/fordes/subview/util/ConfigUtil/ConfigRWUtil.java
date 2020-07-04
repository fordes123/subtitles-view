package org.fordes.subview.util.ConfigUtil;

import org.fordes.subview.entity.settingSet;
import org.fordes.subview.util.OrderedProperties;
import org.fordes.subview.util.SettingUtil.SettingUtil;

import java.io.*;
import java.rmi.server.ExportException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

public class ConfigRWUtil {


    public static HashMap<String, String> getTranslationSupportList(){
        HashMap<String, String> params = new HashMap<String, String>();
        Properties prop=new Properties();
        InputStreamReader inputFile= null;
        try {

            FileInputStream fis=new FileInputStream(System.getProperty("user.dir") + "\\date\\config\\Translation-LanguageSupportList.properties");
            inputFile = new InputStreamReader(fis,"UTF-8");
            prop.load(inputFile);
            Iterator<String> it=prop.stringPropertyNames().iterator();
            while(it.hasNext()){
                String key=it.next();
                params.put(key,prop.getProperty(key));
            }
            inputFile.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return params;
    }

    public static String GetInterfaceInformation(String target,String path){
        Properties prop=new Properties();
        try{
        FileInputStream inputFile=new FileInputStream(path);
        prop.load(inputFile);
        String res=prop.getProperty(target);
            inputFile.close();
            return res;
        }catch (Exception e){

        }
        return null;
    }

    public static Boolean SetInterfaceInformation(String key,String value,String path){
        Properties prop = new Properties();
        FileInputStream fis;
        try {
            fis = new FileInputStream(path);
            prop.load(fis);
            fis.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        prop.setProperty(key, value);

        try {
            FileOutputStream fos = new FileOutputStream(path);

            prop.store(fos, "");
            fos.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * 写入讯飞语音转写配置文件
     * @param id
     * @param key
     * @return
     */
    public boolean setLfasr(String id, String key){
        File file=new File(new ConfigPathUtil().getLfasrInfoPath());
        OrderedProperties prop=new OrderedProperties();
        try{
            FileOutputStream oFile=new FileOutputStream(file,false);
            prop.setProperty("app_id",id);
            prop.setProperty("secret_key",key);
            prop.store(oFile,"LfasrInfo");
            oFile.close();
            //SyncTargetFile(new ConfigPathUtil().getLfasrInfoPath(),new ConfigPathUtil().getLfasrInfoTargetPath());
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * 写入百度翻译接口配置文件
     * @param id
     * @param key
     * @return
     */
    public static boolean setBaiduTranInfo(String id, String key){
        File file=new File(new ConfigPathUtil().getBaiduTarnInfoPath());
        Properties prop=new Properties();
        try{
            FileOutputStream oFile=new FileOutputStream(file,false);
            prop.setProperty("app_id",id);
            prop.setProperty("secret_key",key);
            prop.store(oFile,"BaiduTranInfo");
            oFile.close();
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
        return true;
    }


    /**
     * 读取设置配置文件
     */
    public settingSet ReadSettingConfig(){
        settingSet settings=new settingSet();
        Properties prop=new Properties();
        InputStreamReader inputFile= null;
        try {
            FileInputStream fis=new FileInputStream(new ConfigPathUtil().getSettingConfigPath());
            inputFile = new InputStreamReader(fis,"UTF-8");
            prop.load(inputFile);
            Iterator<String> it=prop.stringPropertyNames().iterator();
            while(it.hasNext()){
                String key=it.next();
                settings.getBaseSettings().put(key,prop.getProperty(key));
            }
            inputFile.close();
        } catch (ExportException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
        return settings;
    }

    /**
     * 同步配置文件至编译路径下
     * @param sourceFile
     * @param targetFile
     */
    private static void SyncTargetFile(String sourceFile,String targetFile) throws IOException
    {
        FileReader fr=new FileReader(sourceFile);
        FileWriter fw=new FileWriter(targetFile);
        int len=0;
        while((len=fr.read())!=-1)
            fw.write((char)len);
        fr.close();
        fw.close();
    }

    /**
     * 在程序启动时读取配置主题
     * @return
     */
    public final Boolean ThemeInit(){
        Properties prop=new Properties();
        boolean res=false;
        try{
            FileInputStream inputFile=new FileInputStream(new ConfigPathUtil().getSettingConfigPath());
            prop.load(inputFile);
            String par=prop.getProperty("TimingState");
            if(par.equals("0"))
                res=prop.getProperty("ThemeMode").equals("0")?false:true;
            else {
                res=new SettingUtil().CheckTiming(Integer.valueOf(prop.getProperty("ThemeModeHigh")),Integer.valueOf(prop.getProperty("ThemeModeLow")));
                /*int date = Integer.valueOf(new SimpleDateFormat("HH").format(new Date()));
                int high = Integer.valueOf(prop.getProperty("ThemeModeHigh"));
                int low = Integer.valueOf(prop.getProperty("ThemeModeLow"));
                if((high>low&&date<=low&&date>=high)||(high<low&&date>=low&&date<=high))
                    res=true;*/
            }
            inputFile.close();
            return res;
        }catch (Exception e){
            return false;
        }
    }

    /**
     * 读取语音接口配置信息
     * @return
     */
    public HashMap<String,String> getVoiceConfig(){
        HashMap<String,String> res=new HashMap<>();
        String id= ConfigRWUtil.GetInterfaceInformation("app_id",new ConfigPathUtil().getLfasrInfoPath());
        String key= ConfigRWUtil.GetInterfaceInformation("secret_key",new ConfigPathUtil().getLfasrInfoPath());
        if(id==null||key==null||key.equals("")||id.equals("")||id.length()!=8||key.length()!=32)
            return null;
        res.put("id",id);
        res.put("key",key);
        return res;
    }

    /**
     * 读取在线翻译接口
     * @return
     */
    public HashMap<String,String> getTranConfig(){
        HashMap<String,String> res=new HashMap<>();
        String id =new ConfigRWUtil().GetInterfaceInformation("app_id",new ConfigPathUtil().getBaiduTarnInfoPath());
        String key =new ConfigRWUtil().GetInterfaceInformation("secret_key",new ConfigPathUtil().getBaiduTarnInfoPath());
        if(id==null||key==null||key.equals("")||id.equals("")){
            /**
             * 免费接口请勿滥用，自行注册一个效果会比这个好很多
             */
            id = "20200614000495109";
            key = "9PnFDnIdAW9NbxaGjpA5";
            System.out.println("翻译接口未配置或存在错误，使用内置免费接口");
            res.put("type","free");
        }else res.put("type","other");
        res.put("id",id);
        res.put("key",key);
        return res;
    }


}
