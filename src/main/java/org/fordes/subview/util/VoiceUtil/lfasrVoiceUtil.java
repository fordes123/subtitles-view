package org.fordes.subview.util.VoiceUtil;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.iflytek.msp.lfasr.LfasrClient;
import com.iflytek.msp.lfasr.model.Message;
import org.fordes.subview.controller.StartController;
import org.fordes.subview.main.Launcher;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class lfasrVoiceUtil {
    private static String APP_ID = "";
    private static String SECRET_KEY = "";
    private static String AUDIO_FILE_PATH = "";
    private static String result;
    private StartController startController = (StartController) Launcher.controllers.get(StartController.class.getSimpleName());

    public lfasrVoiceUtil(File file) {
        this.APP_ID = startController.inputset.getVoiceService().getId();
        this.SECRET_KEY = startController.inputset.getVoiceService().getKey();
        this.AUDIO_FILE_PATH = file.getPath();
    }

    public String start(String language,String speaker_number) {
        try {
            performance(language, speaker_number);
        } catch (Exception e) {
            return null;
        }
        System.out.println(result);
        return result;
    }


    /**
     * 标准调用
     *
     * @throws InterruptedException e
     */
    private static void standard() throws InterruptedException {
        //创建客户端实例
        LfasrClient lfasrClient = LfasrClient.getInstance(APP_ID, SECRET_KEY);

        //上传
        Message task = lfasrClient.upload(AUDIO_FILE_PATH);
        String taskId = task.getData();
        System.out.println("转写任务 taskId：" + taskId);

        //查看转写进度
        int status = 0;
        while (status != 9) {
            Message message = lfasrClient.getProgress(taskId);
            JSONObject object = JSON.parseObject(message.getData());
            status = object.getInteger("status");
            System.out.println(message.getData());
            TimeUnit.SECONDS.sleep(2);
        }
        //获取结果
        result = lfasrClient.getResult(taskId).getData();
    }

    /**
     * 带有业务参数调用
     *
     * @throws InterruptedException e
     */
    private static void businessExtraParams() throws InterruptedException {
        //创建客户端实例
        LfasrClient lfasrClient = LfasrClient.getInstance(APP_ID, SECRET_KEY);

        //上传
        //设置业务参数
        Map<String, String> param = new HashMap<>(16);
        //是否开启分词：默认 false
        //param.put("has_participle","true");
        //转写结果中最大的候选词个数：默认：0，最大不超过5
        //param.put("max_alternatives","2");

        //是否开启角色分离：默认为false
        //param.put("has_seperate","true");
        //发音人个数，可选值：0-10，0表示盲分：默认 2
        //param.put("speaker_number","3");
        //角色分离类型 1-通用角色分离；2-电话信道角色分离：默认 1
        //param.put("role_type","1");

        //是否开启敏感词检测：默认 false
        //param.put("has_sensitive","true");
        //敏感词检测类型： 0-默认词库；1-自定义敏感词
        //param.put("sensitive_type","1");
        //自定义的敏感词：每个词用英文逗号分割，整个字符串长度不超过256
        //param.put("keywords","你好");

        //语种： cn-中文（默认）;en-英文（英文不支持热词）
        param.put("language", "cn");
        //垂直领域个性化：法院-court；教育-edu；金融-finance；医疗-medical；科技-tech
        //param.put("pd","finance");

        Message task = lfasrClient.upload(
                AUDIO_FILE_PATH
                , param);
        String taskId = task.getData();
        System.out.println("转写任务 taskId：" + taskId);

        //查看转写进度
        int status = 0;
        while (status != 9) {
            Message message = lfasrClient.getProgress(taskId);
            JSONObject object = JSON.parseObject(message.getData());
            status = object.getInteger("status");
            System.out.println(message.getData());
            TimeUnit.SECONDS.sleep(2);
        }
        //获取结果
        result = lfasrClient.getResult(taskId).getData();
    }

    /**
     * 设置网络代理，调用样例
     *
     * @throws InterruptedException e
     */
    private static void netProxy() throws InterruptedException {
        //1、创建客户端实例, 设置网络代理
        LfasrClient lfasrClient = LfasrClient.getInstance(APP_ID, SECRET_KEY, "http://x.y.z/");
        //LfasrClient lfasrClient = LfasrClient.getInstance(APP_ID, SECRET_KEY);


        //2、上传
        //2.1、设置业务参数
        Map<String, String> param = new HashMap<>(16);
        //语种： cn-中文（默认）;en-英文（英文不支持热词）
        param.put("language", "cn");
        //垂直领域个性化：法院-court；教育-edu；金融-finance；医疗-medical；科技-tech
        //param.put("pd","finance");

        Message task = lfasrClient.upload(
                AUDIO_FILE_PATH
                , param);
        String taskId = task.getData();
        System.out.println("转写任务 taskId：" + taskId);


        //3、查看转写进度
        int status = 0;
        while (status != 9) {
            Message message = lfasrClient.getProgress(taskId);
            JSONObject object = JSON.parseObject(message.getData());
            status = object.getInteger("status");
            System.out.println(message.getData());
            TimeUnit.SECONDS.sleep(2);
        }
        //4、获取结果
        result = lfasrClient.getResult(taskId).getData();
    }

    /**
     * 性能调优参数调用
     *
     * @throws InterruptedException e
     */
    private static void performance(String language,String speaker_number) throws InterruptedException {
        //1、创建客户端实例, 设置性能参数
        LfasrClient lfasrClient =
                LfasrClient.getInstance(
                        APP_ID,
                        SECRET_KEY,
                        20, //线程池：核心线程数
                        50, //线程池：最大线程数
                        50, //网络：最大连接数
                        10000, //连接超时时间
                        30000, //响应超时时间
                        null);
        //上传
        //设置业务参数
        Map<String, String> param = new HashMap<>(16);
        //是否开启分词：默认 false
        //param.put("has_participle","true");
        //转写结果中最大的候选词个数：默认：0，最大不超过5
        //param.put("max_alternatives","2");

        //是否开启角色分离：默认为false
        param.put("has_seperate","true");
        //发音人个数，可选值：0-10，0表示盲分：默认 2
        param.put("speaker_number",speaker_number);
        //角色分离类型 1-通用角色分离；2-电话信道角色分离：默认 1
        //param.put("role_type","1");

        //是否开启敏感词检测：默认 false
        //param.put("has_sensitive","true");
        //敏感词检测类型： 0-默认词库；1-自定义敏感词
        //param.put("sensitive_type","1");
        //自定义的敏感词：每个词用英文逗号分割，整个字符串长度不超过256
        //param.put("keywords","你好");

        //语种： cn-中文（默认）;en-英文（英文不支持热词）
        param.put("language", language);
        //垂直领域个性化：法院-court；教育-edu；金融-finance；医疗-medical；科技-tech
        //param.put("pd","finance");

        Message task = lfasrClient.upload(
                AUDIO_FILE_PATH
                , param);
        String taskId = task.getData();
        System.out.println("转写任务 taskId：" + taskId);

        //查看转写进度
        int status = 0;
        while (status != 9) {
            Message message = lfasrClient.getProgress(taskId);
            JSONObject object = JSON.parseObject(message.getData());
            status = object.getInteger("status");
            System.out.println(message.getData());
            TimeUnit.SECONDS.sleep(2);
        }
        //获取结果
        result = lfasrClient.getResult(taskId).getData();
    }
}
