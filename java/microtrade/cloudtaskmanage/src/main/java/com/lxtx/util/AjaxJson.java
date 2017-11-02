package com.lxtx.util;

/**
 * ajax请求的返回格式
 * 
 * @author hecm
 *
 */
public class AjaxJson {

	private int code;
	private Object data;
	private Object Message;// 数据对象

	public AjaxJson(String data) {
		this.code = ErrorCode.ERROR;
		this.data = data;
	}

	public AjaxJson() {
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
		this.data = ErrorCode.getErrorInfo(code);
	}

	public void setCode(int code, String data) {
		this.code = code;
		this.data = data;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public Object getMessage() {
		return Message;
	}

	public void setMessage(Object message) {
		Message = message;
	}

}
