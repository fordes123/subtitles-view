package org.fordes.subview.util.VideoUtil;



import org.apache.commons.codec.EncoderException;
import java.beans.Encoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * 工具类，获取视频文件的一些相关信息
 */
public class VideoInfoUtil {

    private String duration;//时长
    private long bitrate;//比特率
    private String code;//视频编码
    private String compressionFormat;//图像格式
    private int width;//宽度
    private int height;//高度
    private long videoBitrate;//视频流比特率
    private int fps;//帧数
    //private File file;

    public VideoInfoUtil(File file){
        HashMap<String,String> info = new FFMpegUtil().getVideoInfo(file.getPath());
        this.duration = info.get("Duration");
        this.bitrate = Integer.valueOf(info.get("Bitrate"));
        this.code = info.get("Code");
        this.compressionFormat = info.get("CompressionFormat");
        this.width = Integer.valueOf(info.get("Width"));
        this.videoBitrate = Integer.valueOf(info.get("videoBitrate"));
        this.fps = Integer.valueOf(info.get("fps"));
        this.height = Integer.valueOf(info.get("Height"));
    }

    public String getDuration() {
        return duration;
    }

    public long getBitrate() {
        return bitrate;
    }

    public String getCode() {
        return code;
    }

    public String getCompressionFormat() {
        return compressionFormat;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public long getVideoBitrate() {
        return videoBitrate;
    }

    public int getFps() {
        return fps;
    }

    public ArrayList<Integer> calculateVideoContentSize(){
        ArrayList<Integer> res=new ArrayList<>();
        if(width>height){
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
