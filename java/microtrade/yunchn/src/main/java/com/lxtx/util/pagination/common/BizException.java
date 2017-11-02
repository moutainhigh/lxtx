package com.lxtx.util.pagination.common;

public class BizException extends RuntimeException {
	/** 
	* 
	*/
	private static final long serialVersionUID = 1L;
	private int err_code = 0;

	public static final int UNKNOWN_ERROR = 9999;

	public BizException(String msg) {
		super(msg);
		this.err_code = UNKNOWN_ERROR;
	}

	public BizException(Exception ex) {
		super(ex);
		err_code = UNKNOWN_ERROR;
	}

	public BizException(int err_code, String err_msg) {
		super(err_msg);
		this.err_code = err_code;
	}

	public int getErrcode() {
		return err_code;
	}
}
