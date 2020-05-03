package org.fordes.subview.util.SubtitlesUtil;

import javafx.scene.control.TextArea;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

public class editUtil {

    /**
     * 转换文本区光标位置为行、列
     * @param text
     * @param pos
     * @return
     */
    public ArrayList<Integer> changPos( String text, int pos){
        ArrayList<Integer> res= new ArrayList<>();
        
        String line=null;
        int counter=0;
        int position=pos+1;
        BufferedReader br = new BufferedReader(new StringReader(text));
        try {
        while((line = br.readLine())!=null)
        {
            counter++;
            if(position-(line.length()+1)<=0){
                res.add(counter);
                res.add(position);
                return res;
            }else{
                position-=(line.length()+1);
            }
        }
        }catch (Exception e){
            System.out.println(e);
        }
        
        res.add(++counter);
        res.add(position);
        return res;
    }


    public int ChangLine(TextArea edit,int line){
        int counter=0;
        int pos=0;
        String[] list=edit.getText().split("\n");
        for(String temp:list){
            counter++;
            pos+=temp.length()+1;
            if(counter==line-1)
                return pos;
        }
        if(line-1==counter)
            return pos;
        if(line-1==counter+1)
            return pos+1;
        else
            return -1;
    }
}
