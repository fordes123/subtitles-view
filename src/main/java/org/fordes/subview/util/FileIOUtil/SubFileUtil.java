package org.fordes.subview.util.FileIOUtil;

import org.fordes.subview.controller.startControl;
import org.fordes.subview.main.Launcher;
import org.fordes.subview.util.SubtitlesUtil.TimelineUtil;

import java.io.*;

public class SubFileUtil {

    private String code="UTF-8";
    private startControl controller= (startControl) Launcher.controllers.get(startControl.class.getSimpleName());

    public void setCode(String code) {
        this.code = code;
    }

    public String Read(File file,String code){
        setCode(code);
        return Read(file);
    }

    
    public String Read(File file){
        String srt = "";
        try {
            BufferedReader re = new BufferedReader(new InputStreamReader(new FileInputStream(file), code));
            BufferedReader r = new BufferedReader(re);
            String temp;
            while ((temp = r.readLine()) != null) {
                srt += (temp + "\n");
            }

            re.close();
        } catch (IOException e) {
            
            e.printStackTrace();
        }
        
        return srt.replace("\uFEFF", "");
    }

    /**
     * 将文本写入至指定文件
     * @param file
     * @param temp
     * @param coding,写入编码
     */
    public void WriteFile(File file, String temp, String coding) {
        if(coding!=null)
            this.code=coding;
        try {
            System.out.println("开始写入文件，编码为："+code);
            OutputStreamWriter outStream = new OutputStreamWriter(new FileOutputStream(file), code);
            
            BufferedWriter wr = new BufferedWriter(outStream);
            wr.write(temp);
            wr.flush();
            wr.close();
            
        } catch (IOException e1) {
            
            e1.printStackTrace();
        }
    }

    /**
     * 从文本生成字幕文件
     * @param SubText，写入文本
     * @param file，写入文件
     * @param type，字幕类型
     * @param coding，写入编码
     * @param Option，是否为无结构字幕，如是，则创建结构
     */
    public void GenerateSubtitles(String SubText,File file,int type,String coding,boolean Option){
        if(Option) {
            String[] list = SubText.split("\n");
            String str = "";
            int num = 0;
            for (String temp : list) {
                if (new TimelineUtil(type).TimelineCheck(temp))
                    str+=num+++""+"\n";
                str += temp + "\n";
            }
            
            WriteFile(file,str,coding);
        }else
            WriteFile(file,SubText,coding);

    }

    public static void ReplaceOldFile(String old,String news){
        new File(old).delete();
        System.out.println("删除原文件"+old+"  "+news);
        File newFile=new File(news);
        newFile.renameTo(new File(old));
        System.out.println("结束");
    }

    public static void main(String[] args) {
        ReplaceOldFile("C:\\Users\\以谶\\Desktop\\666.srt","C:\\Users\\以谶\\Desktop\\777.srt");
    }
}
