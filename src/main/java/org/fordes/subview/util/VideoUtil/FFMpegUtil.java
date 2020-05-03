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
	private static String ffmpeg = System.getProperty("user.dir") + "\\ffmpeg\\windows\\ffmpeg.exe";
	private ArrayList<String> command;//命令集

/*	 Linux与mac下 ffmpeg的路径
	 private static String ffmpeg = "/developer/ffmpeg-4.0/bin/ffmpeg";*/

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
	 * @param videoInputPath 视频的输入路径
	 * @param audioOutPath   视频的输出路径
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

}
