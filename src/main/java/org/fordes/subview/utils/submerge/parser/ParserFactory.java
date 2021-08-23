package org.fordes.subview.utils.submerge.parser;


public final class ParserFactory {

	/**
	 * Return the subtitle parser for the subtitle format matching the extension
	 * 
	 * @param extension the subtitle extention
	 * @return the subtitle parser, null if no matching parser
	 */
	public static SubtitleParser getParser(String extension) throws Exception {

		SubtitleParser parser = null;
		String lowerExt = extension.toLowerCase();
		
		if ("ass".equals(lowerExt) || "ssa".equals(lowerExt)) {
			parser = new ASSParser();
		} else if ("srt".equalsIgnoreCase(lowerExt)) {
			parser = new SRTParser();
		} else {
			throw new Exception(extension + " format not supported");
		}
		
		return parser;
	}

	/**
	 * Private constructor
	 */
	private ParserFactory() {
		
		throw new AssertionError();
	}

}
