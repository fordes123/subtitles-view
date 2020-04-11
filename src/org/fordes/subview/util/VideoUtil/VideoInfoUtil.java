package org.fordes.subview.util.VideoUtil;

import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.InputFormatException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

/**
 * 工具类，获取视频文件的一些相关信息
 */
public class VideoInfoUtil {

    private Encoder encoder = new Encoder();
    private it.sauronsoftware.jave.MultimediaInfo m;
    private File file;

    public VideoInfoUtil(File file){
        this.file=file;
        try {
            m = encoder.getInfo(file);
        } catch (InputFormatException e) {
            e.printStackTrace();
        } catch (EncoderException e) {
            e.printStackTrace();
        }

    }

    /**
     *视频高度
     */
    public int getVideoHeight(){
        return m.getVideo().getSize().getHeight();
    }

    /**
     *视频宽度
     */
    public int getVideoWidth(){
       return m.getVideo().getSize().getWidth();
    }

    /**
     * 视频格式
     * （仅供参考）
     */
    public String getVideoType(){
        return m.getFormat();
    }

    /**
     * 视频时长(ms)
     */
    public long getVideoLength(){
        return m.getDuration();
    }

    /**
     * 视频大小()
     */
    public int getVideoSize(){
        String size="";
        try {
            FileInputStream fis = new FileInputStream(file);
            FileChannel fc = fis.getChannel();
            BigDecimal fileSize = new BigDecimal(fc.size());
            size=fileSize.divide(new BigDecimal(1048576), 2, RoundingMode.HALF_UP)+"";
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Integer.parseInt(size);
    }

    //动态计算视频窗格初始尺寸
    public ArrayList<Integer> calculateVideoContentSize(){
        ArrayList<Integer> res=new ArrayList<>();
        int width=getVideoWidth();
        int height=getVideoHeight();
        if(width>height){//视频宽度大于高度，取宽度
            if(width<=728){
                res.add(width);
                res.add(height);
                return res;
            }else{

            }
        }else {

        }
        return res;
    }
}
