package org.fordes.subview.util.AudioUtil;

import java.util.HashMap;

import org.apache.log4j.PropertyConfigurator;

import com.alibaba.fastjson.JSON;
import com.iflytek.msp.cpdb.lfasr.client.LfasrClientImp;
import com.iflytek.msp.cpdb.lfasr.exception.LfasrException;
import com.iflytek.msp.cpdb.lfasr.model.LfasrType;
import com.iflytek.msp.cpdb.lfasr.model.Message;
import com.iflytek.msp.cpdb.lfasr.model.ProgressStatus;

public class LaugDemo {
private static final LfasrType type = LfasrType.LFASR_TELEPHONY_RECORDED_AUDIO;
public static void main(String[] args) {

    
    PropertyConfigurator.configure("data\\config\\log4j.properties");
    String path ="F:\\Home\\Test\\1.m4a";
    LfasrClientImp lc =null;
    String task_id = "";
    try {
        lc= LfasrClientImp.initLfasrClient();
        HashMap<String, String> map = new HashMap<>();
        map.put("has_participle", "true");
        Message uploadMsg = lc.lfasrUpload(path, type, map);
        int msg = uploadMsg.getOk();
        if(msg == 0){
            task_id = uploadMsg.getData();
            System.out.println("task_id======="+task_id);
        }else{
            System.out.println("转换语音发生异常==========="+uploadMsg.getErr_no());
        }
        
    } catch (Exception e) {
        Message uploadMsg = JSON.parseObject(e.getMessage(), Message.class);
        System.out.println("ecode=" + uploadMsg.getErr_no());
        System.out.println("failed=" + uploadMsg.getFailed());
}

    while(true){
        try {
            Thread.sleep(5 * 1000);
            System.out.println("waiting ...");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            
            Message msg = lc.lfasrGetProgress(task_id);
            if(msg.getOk() !=0){ 
                System.out.println("任务失败"+task_id);
                System.out.println("ecode==========="+msg.getErr_no());
                System.out.println("failed====="+msg.getFailed());
            }else{
                
                ProgressStatus ps = JSON.parseObject(msg.getData(),ProgressStatus.class);
                if(ps.getStatus()==9){
                    System.out.println("任务完成===="+task_id+",status===="+ps.getDesc());
                    break;
                }else{
                    System.out.println("任务没有完成====="+task_id+",status===="+ps.getDesc());
                    continue;
                }
            }
        } catch (LfasrException e) {
            e.printStackTrace();
            }
    }

}


}