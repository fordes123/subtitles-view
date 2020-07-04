import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegTest {
    public static void main(String[] args) {
        String text="Duration: 00:01:36.33, start: 0.000000, bitrate: 688 kb/s               ".trim();
        String text2="Stream #0:0(und): Video: h264 (High) (avc1 / 0x31637661), yuv420p(tv, bt470bg/bt709/bt709), 1280x720 [SAR 1:1 DAR 16:9], 566 kb/s, 25 fps, 25 tbr, 12800 tbn, 50 tbc (default)                   ".trim();
        //String text2 = " Stream #0:0: Video: tscc (tscc / 0x63637374), bgr24, 1366x740, 321 kb/s, 15 fps, 15 tbr, 15 tbn, 15 tbc  ".trim();
        String pattern=".*Duration:\\s(\\d{2}:\\d{2}:\\d{2}.\\d{2}).*, bitrate:\\s([0-9]*)\\skb/s.*";
        String pattern2=".*Video:\\s(.*),\\s(.*),\\s(\\d*)x(\\d*).*,\\s(\\d*)\\skb/s,\\s(\\d*)\\sfps.*";
        //Pattern r = Pattern.compile(pattern);
        Matcher m = Pattern.compile(pattern).matcher(text);
        if(m.find()){
            System.out.println("总匹配串"+m.group());
            System.out.println("视频时长："+m.group(1));
            System.out.println("比特率："+m.group(2));
        }else System.out.println("未匹配");
        Matcher m2 = Pattern.compile(pattern2).matcher(text2);
        if(m2.find()){
            System.out.println("\n总匹配串"+m2.group());
            System.out.println("编码格式："+m2.group(1));
            System.out.println("存储格式："+m2.group(2));
            System.out.println("视频宽度："+m2.group(3)+",视频高度："+m2.group(4));
            System.out.println("视频流比特率："+m2.group(5));
            System.out.println("帧数："+m2.group(6));
        }else System.out.println("未匹配");
    }
}
