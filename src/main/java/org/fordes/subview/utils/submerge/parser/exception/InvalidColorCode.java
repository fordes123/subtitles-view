package org.fordes.subview.utils.submerge.parser.exception;

public class InvalidColorCode extends RuntimeException {

	private static final long serialVersionUID = -4904697807940273825L;

	public InvalidColorCode() {
	}

	public InvalidColorCode(String message) {
		super(message);
	}

	public InvalidColorCode(Throwable cause) {
		super(cause);
	}

	public InvalidColorCode(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidColorCode(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
