package com.lxtx.util;

/**
 * 
 * @author ningx
 * 
 */
public class MyException extends Exception {

	private static final long serialVersionUID = 1L;
	private int err_code = 0;

	public MyException(String msg) {
		super(msg);
		this.err_code = ErrorCode.ERROR;
	}

	public MyException(Exception ex) {
		super(ex);
		err_code = ErrorCode.ERROR;
	}

	public MyException(int err_code) {
		super(ErrorCode.getErrorInfo(err_code));
		this.err_code = err_code;
	}

	public MyException(int err_code, String err_msg) {
		super(err_msg);
		this.err_code = err_code;
	}

	public int getErrcode() {
		return err_code;
	}

}
