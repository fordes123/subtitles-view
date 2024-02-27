package org.fordes.subtitles.view.utils.submerge.parser;


import org.fordes.subtitles.view.utils.submerge.parser.exception.InvalidFileException;
import org.fordes.subtitles.view.utils.submerge.parser.exception.InvalidSubException;
import org.fordes.subtitles.view.utils.submerge.subtitle.common.TimedTextFile;

import java.io.File;
import java.io.InputStream;



public interface SubtitleParser {

	/**
	 * Parse a subtitle file and return the corresponding subtitle object
	 * 
	 * @param file the subtitle file
	 * @return the subtitle object
	 * @throws InvalidSubException if the subtitle is not valid
	 * @throws InvalidFileException if the file is not valid
	 */
	TimedTextFile parse(File file);

	/**
	 * Parse a subtitle file from an inputstream and return the corresponding subtitle
	 * object
	 * 
	 * @param is the input stream
	 * @param fileName the fileName
	 * @return the subtitle object
	 * @throws InvalidSubException if the subtitle is not valid
	 * @throws InvalidFileException if the file is not valid
	 */
	TimedTextFile parse(InputStream is, String fileName);

	/**
	 * Parse a subtitle file and return the corresponding subtitle object
	 *
	 * @param file the file
	 * @param charset the file charset
	 * @return the subtitle object
	 * @throws InvalidSubException if the subtitle is not valid
	 * @throws InvalidFileException if the file is not valid
	 */
	TimedTextFile parse(File file, String charset);

	/**
	 * Parse a subtitle file from an string and return the corresponding subtitle
	 * object
	 *
	 * @param is the input stream
	 * @param fileName the fileName
	 * @parse charset the file charset
	 * @return the subtitle object
	 * @throws InvalidSubException if the subtitle is not valid
	 * @throws InvalidFileException if the file is not valid
	 */
	TimedTextFile parse(InputStream is, String fileName, String charset);

	/**
	 * Parse a subtitle file from an string and return the corresponding subtitle object
	 *
	 * @param str the subtitle string
	 * @param fileName the fileName
	 * @return the subtitle object
	 */
	TimedTextFile parse(String str, String fileName);
}
