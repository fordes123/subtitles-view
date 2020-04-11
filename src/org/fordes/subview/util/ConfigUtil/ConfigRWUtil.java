package org.fordes.subview.util.ConfigUtil;

import javafx.scene.control.TextInputDialog;
import org.fordes.subview.util.OrderedProperties;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Optional;
import java.util.Properties;

public class ConfigRWUtil {

    //读取在线翻译支持列表
    public static HashMap<String, String> getTranslationSupportList(){
        HashMap<String, String> params = new HashMap<String, String>();
        Properties prop=new Properties();
        InputStreamReader inputFile= null;
        try {
            //得到字节流
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
        Properties prop = new Properties();// 属性集合对象
        FileInputStream fis;
        try {
            fis = new FileInputStream(path);
            prop.load(fis);// 将属性文件流装载到Properties对象中
            fis.close();// 关闭流
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
        // 文件输出流
        try {
            FileOutputStream fos = new FileOutputStream(path);
            // 将Properties集合保存到流中
            prop.store(fos, "");
            fos.close();// 关闭流
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        //System.out.println("获取修改后的属性值：password=" + prop.getProperty("password"));
        return true;
    }

    public static void setLfasrInfo(String id, String key, String path){
        File file=new File(path);
        //if(file.exists())
        OrderedProperties prop=new OrderedProperties();
        try{
            FileOutputStream oFile=new FileOutputStream(file,false);
            prop.setProperty("app_id",id);
            prop.setProperty("secret_key",key);
            prop.setProperty("lfasr_host","http://raasr.xfyun.cn/api");
            prop.setProperty("file_piece_size","10485760");
            prop.setProperty("store_path",new ConfigPathUtil().getTempPath());
            prop.store(oFile,"LfasrInfo");
            oFile.close();
        } catch (FileNotFoundException e) {
            System.out.println(e);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public static boolean setBaiduTranInfo(String id, String key, String path){
        File file=new File(path);
        //if(file.exists())
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

    public static void main(String[] args) {
        setBaiduTranInfo("","",new ConfigPathUtil().getBaiduTarnInfoPath());
        System.out.println(GetInterfaceInformation("app_id",new ConfigPathUtil().getBaiduTarnInfoPath()));
    }
}
