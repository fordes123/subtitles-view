package org.fordes.subtitles.view.utils.submerge.utils;

import cn.hutool.core.io.CharsetDetector;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.mozilla.universalchardet.UniversalDetector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;


@Slf4j
public class EncodeUtils {

	/**
	 * Detect charset encoding of a file
	 * 
	 * @param file: the file to detect encoding from
	 * @return the charset encoding
	 * @throws IOException
	 */
	public static String guessEncoding(File file) throws IOException {
		try (FileInputStream is = new FileInputStream(file)) {
			return guessEncoding(is);
		}
	}

	/**
	 * Detect charset encoding of an input stream
	 * 
	 * @param is: the InputStream to detect encoding from
	 * @return the charset encoding
	 * @throws IOException
	 */
	public static String guessEncoding(InputStream is) throws IOException {
		//先使用juniversalchardet检测
		String code =  guessEncoding(IoUtil.readBytes(is));
		if (code != null) {
			return code;
		}
		//使用hutool的charset检测
		Charset charset = CharsetDetector.detect(is);
		if (charset != null) {
			return charset.name();
		}
		//默认使用UTF-8
		log.debug("文件编码检测失败，使用默认编码UTF-8");
		return CharsetUtil.UTF_8;
	}

	/**
	 * Detect charset encoding of a byte array
	 * 
	 * @param bytes: the byte array to detect encoding from
	 * @return the charset encoding
	 */
	public static String guessEncoding(byte[] bytes) {
		UniversalDetector detector = new UniversalDetector(null);
		detector.handleData(bytes, 0, bytes.length);
		detector.dataEnd();
		String encoding = detector.getDetectedCharset();
		detector.reset();
		return encoding;
	}

}
