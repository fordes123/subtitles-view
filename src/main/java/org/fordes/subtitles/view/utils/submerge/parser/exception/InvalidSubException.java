package org.fordes.subtitles.view.utils.submerge.parser.exception;

public class InvalidSubException extends RuntimeException {

	private static final long serialVersionUID = -8431409375872882596L;

	public InvalidSubException() {
	}

	public InvalidSubException(String arg0) {
		super(arg0);
	}

	public InvalidSubException(Throwable arg0) {
		super(arg0);
	}

	public InvalidSubException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public InvalidSubException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}
}
