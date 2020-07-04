package org.fordes.subview.util.VideoUtil;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * FFMPEG 的相关操作
 *
 * @author Administrator
 */
public class FFMpegUtil {

	
	private static String ffmpeg = System.getProperty("user.dir") + "\\ffmpeg\\windows\\ffmpeg.exe";
	private ArrayList<String> command;

	/**
	 * 执行ffmpeg命令
	 * @param com
	 */
	private static void Perform(String com){
		
		try {
			Runtime rt = Runtime.getRuntime();
			Process proc = rt.exec(com);
			InputStream stderr = proc.getErrorStream();
			InputStreamReader isr = new InputStreamReader(stderr);
			BufferedReader br = new BufferedReader(isr);
			String line = null;

			while ( (line = br.readLine()) != null) {
				
			}
			int exitVal = proc.waitFor();
			
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	/**
	 * 另一种执行方式
	 * @param command
	 * @throws IOException
	 */
	private static void Carried(ArrayList command) throws IOException {
		
		ProcessBuilder builder = new ProcessBuilder(command);
		Process process = builder.start();
		InputStream errorStream = process.getErrorStream();
		InputStreamReader isr = new InputStreamReader(errorStream);
		BufferedReader br = new BufferedReader(isr);
		String line = "";
		while ((line = br.readLine()) != null) {
		}
		if (br != null) {
			br.close();
		}
		if (isr != null) {
			isr.close();
		}
		if (errorStream != null) {
			errorStream.close();
		}
	}

	/**
	 * 分离音频
	 * @param videoInputPath 视频的输入路径
	 * @param audioOutPath   音频的输出路径
	 * @throws Exception
	 */
	public void SegmentedAudio (String videoInputPath, String audioOutPath) {
		command = new ArrayList<String>();
		command.add(ffmpeg);
		command.add(" -i ");
		command.add(videoInputPath);
		command.add(" -vn -acodec copy ");
		command.add(audioOutPath);
		String ssp = "";
		for (String c : command) {
			ssp+=c;
		}
		Perform(ssp);
	}

	/**
	 * 为视频添加字幕
	 * ffmpeg -i input.mp4 -vf subtitles=input.srt output.mp4
	 *
	 * @param input ,输入视频路径
	 * @param output ,输出视频路径
	 * @param subtitles ,字幕文件路径
	 * @param type ,合成模式 1、流式，2、嵌入式
	 */
	public static void CompositeSubtitles(String input,String output,String subtitles,int type){
		
		List<String> command = new ArrayList<String>();
		command.add(ffmpeg);
		command.add(" -i ");
		command.add("\""+input+"\" ");
		if(type==1){
			command.add("-i ");
			command.add("\""+subtitles+"\" ");
			command.add("-c:s mov_text ");
			command.add("-c:v copy ");
			command.add("-c:a copy ");
			command.add("-y ");
			command.add("\""+output+"\"");
		}else{
			command.add("-vf subtitles=");
			
			command.add("\\'\""+subtitles.replace("\\", "/")+"\"\\' ");
			command.add("-y ");
			command.add("\""+output+"\"");
		}
		String ssp = "";
		for (String c : command) {
			ssp+=c;
		}
		
		
		Perform(ssp);
	}

	/**
	 * 获取视频信息
	 * 命令：ffmpeg -i video.mp4
	 * @param videoPath
	 * @return
	 */
	public HashMap<String,String> getVideoInfo(String videoPath){
		HashMap<String,String> res=new HashMap<>();
		//生成命令
		StringBuffer command = new StringBuffer();
		command.append(ffmpeg);
		command.append(" -i ");
		command.append(videoPath);
		//执行命令语句并返回执行结果
		try {
			Process process = Runtime.getRuntime().exec(command.toString());
			InputStream in = process.getErrorStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String line ;
			String pattern1=".*Duration:\\s(\\d{2}:\\d{2}:\\d{2}.\\d{2}).*, bitrate:\\s([0-9]*)\\skb/s.*";
			String pattern2=".*Video:\\s(.*),\\s(.*),\\s(\\d*)x(\\d*).*,\\s(\\d*)\\skb/s,\\s(\\d*)\\sfps.*";
			while((line=br.readLine())!=null) {
				Matcher m1 = Pattern.compile(pattern1).matcher(line.trim());
				Matcher m2 = Pattern.compile(pattern2).matcher(line.trim());
				//文件信息
				if(m1.find()){
					//获取时长和比特率
					res.put("Duration",m1.group(1));
					res.put("Bitrate",m1.group(2));
					continue;
				}
				//视频流信息
				if(line.contains("fps")){
					res.put("Code",m1.group(1));//视频编码
					res.put("CompressionFormat",m1.group(2));//图像格式
					res.put("Width",m1.group(3));//宽度
					res.put("Height",m1.group(4));//高度
					res.put("videoBitrate",m1.group(5));//视频流比特率
					res.put("fps",m1.group(6));//帧数
					continue;
				}
			}
		}catch (Exception e){
			e.printStackTrace();
			return null;
		}
		return res;
	}

}
