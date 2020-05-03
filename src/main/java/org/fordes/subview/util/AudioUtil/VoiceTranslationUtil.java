package org.fordes.subview.util.AudioUtil;

import com.alibaba.fastjson.JSON;
import com.iflytek.msp.cpdb.lfasr.client.LfasrClientImp;
import com.iflytek.msp.cpdb.lfasr.exception.LfasrException;
import com.iflytek.msp.cpdb.lfasr.model.LfasrType;
import com.iflytek.msp.cpdb.lfasr.model.Message;
import com.iflytek.msp.cpdb.lfasr.model.ProgressStatus;

import java.util.HashMap;

public class VoiceTranslationUtil {

    private String filepath;
    private String re;
    private static final LfasrType type = LfasrType.LFASR_STANDARD_RECORDED_AUDIO;
    private static int sleepSecond = 5;

    public VoiceTranslationUtil(String filepath) {
        
        this.filepath=filepath;

    }

    public String start(String language,String speaker_number) {
        Do(language,speaker_number);
        if(re.equals("")&&re==null) {
            
            System.out.println("转换失败");
            System.exit(0);
        }
        return re;
    }

    @SuppressWarnings("unused")
    private void Do(String language,String speaker_number) {
        
        LfasrClientImp lc = null;
        try {
            lc = LfasrClientImp.initLfasrClient();
        } catch (LfasrException e) {
            
            Message initMsg = JSON.parseObject(e.getMessage(), Message.class);
            System.out.println("ecode=" + initMsg.getErr_no());
            System.out.println("failed=" + initMsg.getFailed());
            System.out.println("初始化异常，解析异常描述信息");
        }

        
        String task_id = "";
        HashMap<String, String> params = new HashMap<String, String>();
        
        params.put("has_participle", "false");
        params.put("speaker_number", speaker_number);
        params.put("language", language);
        
        
        try {
            
            Message uploadMsg = lc.lfasrUpload(filepath, type, params);

            
            int ok = uploadMsg.getOk();
            if (ok == 0) {
                
                task_id = uploadMsg.getData();
                System.out.println("task_id=" + task_id);
            } else {
                
                System.out.println("ecode=" + uploadMsg.getErr_no());
                System.out.println("failed=" + uploadMsg.getFailed());
            }
        } catch (LfasrException e) {
            
            Message uploadMsg = JSON.parseObject(e.getMessage(), Message.class);
            System.out.println("ecode=" + uploadMsg.getErr_no());
            System.out.println("failed=" + uploadMsg.getFailed());
        }

        
        while (true) {
            try {
                
                Thread.sleep(sleepSecond * 1000);
                System.out.println("waiting ...");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                
                Message progressMsg = lc.lfasrGetProgress(task_id);

                
                if (progressMsg.getOk() != 0) {
                    System.out.println("task was fail. task_id:" + task_id);
                    System.out.println("ecode=" + progressMsg.getErr_no());
                    System.out.println("failed=" + progressMsg.getFailed());

                    return;
                } else {
                    ProgressStatus progressStatus = JSON.parseObject(progressMsg.getData(), ProgressStatus.class);
                    if (progressStatus.getStatus() == 9) {
                        
                        System.out.println("task was completed. task_id:" + task_id);
                        break;
                    } else {
                        
                        System.out.println("task is incomplete. task_id:" + task_id + ", status:" + progressStatus.getDesc());
                        continue;
                    }
                }
            } catch (LfasrException e) {
                
                Message progressMsg = JSON.parseObject(e.getMessage(), Message.class);
                System.out.println("ecode=" + progressMsg.getErr_no());
                System.out.println("failed=" + progressMsg.getFailed());
            }
        }

        
        try {
            Message resultMsg = lc.lfasrGetResult(task_id);
            
            if (resultMsg.getOk() == 0) {
                
                System.out.println(resultMsg.getData());
                re=resultMsg.getData();
            } else {
                
                System.out.println("ecode=" + resultMsg.getErr_no());
                System.out.println("failed=" + resultMsg.getFailed());
            }
        } catch (LfasrException e) {
            
            Message resultMsg = JSON.parseObject(e.getMessage(), Message.class);
            System.out.println("ecode=" + resultMsg.getErr_no());
            System.out.println("failed=" + resultMsg.getFailed());
        }
    }


}
