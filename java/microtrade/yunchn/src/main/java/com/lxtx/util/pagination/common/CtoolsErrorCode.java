/*
 * 
 * @Auth:  定义TOOLS内部所使用的错误代码及文字描述
 * 
 * @Date: Wed Jul 23 14:30:22 CST 2014
 */
package com.lxtx.util.pagination.common;

import java.util.HashMap;

public class CtoolsErrorCode {

	/** 成功 */
	public static final int SUCCESS = 0;
	/** 内部异常错误 **/
	public static final int E9998 = 9998;
	/** 其他错误 */
	public static final int E9999 = 9999;

	private static HashMap<Integer, String> ErrorInfo = new HashMap<Integer, String>();

	static {
		ErrorInfo.put(SUCCESS, "成功");
		ErrorInfo.put(E9998, "内部异常错误");
		ErrorInfo.put(E9999, "其他错误");
	}

	public static String getErrInfo(Integer err_code) {
		return ErrorInfo.get(err_code);
	}

	public static void setErrInfo(Integer err_code, String err_msg) {
		ErrorInfo.put(err_code, err_msg);
	}
}
