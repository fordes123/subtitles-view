package org.fordes.subview.utils.submerge.utils;

import cn.hutool.core.io.IoUtil;
import org.mozilla.universalchardet.UniversalDetector;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


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
		return guessEncoding(IoUtil.readBytes(is));
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
