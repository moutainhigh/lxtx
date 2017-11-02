package com.lxtx.util.pagination.common;

public class CtoolsException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private int err_code = 0;

	public static final int UNKNOWN_ERROR = 9999;

	public CtoolsException(String msg) {
		super(msg);
		this.err_code = UNKNOWN_ERROR;
	}

	public CtoolsException(Exception ex) {
		super(ex);
		err_code = UNKNOWN_ERROR;
	}

	public CtoolsException(int err_code) {
		super(CtoolsErrorCode.getErrInfo(err_code));
		this.err_code = err_code;
	}

	public CtoolsException(int err_code, String err_msg) {
		super(err_msg);
		this.err_code = err_code;
	}

	public int getErrcode() {
		return err_code;
	}
}
