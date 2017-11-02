package com.lxtx.util;

import com.lxtx.util.pagination.common.PageParameter;

/**
 * ajax请求的返回格式
 * 
 * @author ningx
 * 
 */
public class AjaxJson {

	private int code;
	private Object data;
	private Object obj;// 数据对象

	private PageParameter page = new PageParameter();// 分页对象

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

	/**
	 * @return the page
	 */
	public PageParameter getPage() {
		return page;
	}

	/**
	 * @param page
	 *            the page to set
	 */
	public void setPage(PageParameter page) {
		this.page = page;
	}

	/**
	 * @return the obj
	 */
	public Object getObj() {
		return obj;
	}

	/**
	 * @param obj
	 *            the obj to set
	 */
	public void setObj(Object obj) {
		this.obj = obj;
	}

}
