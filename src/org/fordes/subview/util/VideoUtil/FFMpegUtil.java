package org.fordes.subview.util.VideoUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * FFMPEG 的相关操作
 *
 * @author Administrator
 */
public class FFMpegUtil {

	// Windows下 ffmpeg.exe的路径
	private static String ffmpegEXE = System.getProperty("user.dir") + "\\ffmpeg\\bin\\ffmpeg.exe";
	private ArrayList<String> command;//命令集
	//private List<String> command;

	// Linux与mac下 ffmpeg的路径
	// private static String ffmpegEXE = "/developer/ffmpeg-4.0/bin/ffmpeg";

	/**
	 * 执行ffmpeg命令
	 * @param com
	 */
	private static void Perform(String com){
		//System.out.println(com);
		try {
			Runtime rt = Runtime.getRuntime();
			Process proc = rt.exec(com);
			InputStream stderr = proc.getErrorStream();
			InputStreamReader isr = new InputStreamReader(stderr);
			BufferedReader br = new BufferedReader(isr);
			String line = null;

			while ( (line = br.readLine()) != null) {
				//System.out.println(line);
			}
			int exitVal = proc.waitFor();
			//System.out.println("流程退出值: " + exitVal);
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
		// 执行操作
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
	 * @param videoInputPath 视频的输入路径
	 * @param audioOutPath   视频的输出路径
	 * @throws Exception
	 */

	// 分离音频
	// ffmpeg.exe -i input.mp4 -vn -acodec copy output.mp3
	public void SegmentedAudio (String videoInputPath, String audioOutPath) {
		//构造命令
		command = new ArrayList<String>();
		command.add(ffmpegEXE);
		command.add(" -i ");
		command.add(videoInputPath);
		command.add(" -vn -acodec copy ");
		command.add(audioOutPath);
		String ssp = "";
		for (String c : command) {
			ssp+=c;
		}
		//执行命令
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
		//构造命令
		List<String> command = new ArrayList<String>();
		command.add(ffmpegEXE);
		command.add(" -i ");
		command.add("\""+input+"\" ");
		if(type==1){//流式 ffmpeg.exe -i input.mp4 -i subtitles.srt -c:s mov_text -c:v copy -c:a copy output.mp4
			command.add("-i ");
			command.add("\""+subtitles+"\" ");
			command.add("-c:s mov_text ");
			command.add("-c:v copy ");
			command.add("-c:a copy ");
			command.add("-y ");
			command.add("\""+output+"\"");
		}else{
			command.add("-vf subtitles=");
			//由于ffmpeg限制，因此字幕路径需要以 \'"xxx"\' 格式，且路径需要以"/"替换"\"
			command.add("\\'\""+subtitles.replace("\\", "/")+"\"\\' ");
			command.add("-y ");
			command.add("\""+output+"\"");
		}
		String ssp = "";
		for (String c : command) {
			ssp+=c;
		}
		//执行命令
		Perform(ssp);
	}

	public static void main(String[] args) {
		CompositeSubtitles("F:\\HOME\\1.mp4","F:\\HOME\\out.mp4","F:\\HOME\\1.srt",2);
	}
}
