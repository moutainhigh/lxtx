package com.lxtx.util;

import java.util.HashMap;

public class ErrorCode {

	public static final int SUCCESS = 0;
	public static final int ERROR = -1;
	public static final int SESSION_LOST=-9999;
	
	private static HashMap<Integer,String> ErrorInfo = new HashMap<Integer,String>();

	static
	{
		ErrorInfo.put(SUCCESS,"成功");
		ErrorInfo.put(ERROR,"失败");
		ErrorInfo.put(SESSION_LOST,"您的登陆已经超时，需要重新登陆！");
	}

	public static String getErrorInfo(Integer err_code)
	{
		return ErrorInfo.get(err_code);
	}
}
