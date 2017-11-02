package com.lxtx.util;

import java.util.HashMap;

public class ErrorCode {

	public static final int SUCCESS = 0;
	public static final int ERROR = -1;
	public static final int USER_OR_PWD_ERROR = -100;
	public static final int PWD_ERROR = -10;
	public static final int OPER_DB_ERROR = -20;
	public static final int SEND_MESSAGE_ERROR = -11;
	public static final int FIRST_PWD_UPDATED = -99;
	public static final int SESSION_LOST = -9999;
	public static final int NO_POWER = -999;
	// 记录已存在
	public static final int DATA_EXISTS = -111;
	public static final int ADD_DATA_EXISTS = -112;

	private static HashMap<Integer, String> ErrorInfo = new HashMap<Integer, String>();

	static {
		ErrorInfo.put(SUCCESS, "成功");
		ErrorInfo.put(ERROR, "未知错误");
		ErrorInfo.put(USER_OR_PWD_ERROR, "用户名或密码错误");
		ErrorInfo.put(PWD_ERROR, "密码错误");
		ErrorInfo.put(OPER_DB_ERROR, "数据操作异常");
		ErrorInfo.put(SEND_MESSAGE_ERROR, "短信发送失败");
		ErrorInfo.put(FIRST_PWD_UPDATED, "初始密码已修改");
		ErrorInfo.put(SESSION_LOST, "您的登陆已经超时，需要重新登陆！");
		ErrorInfo.put(NO_POWER, "您没有该功能的访问权限！");
		ErrorInfo.put(DATA_EXISTS, "记录已存在");// update 用户时会用此
		ErrorInfo.put(ADD_DATA_EXISTS, "添加的记录已存在");// 添加用户时 如果存在会返回已存在的用户信息
	}

	public static String getErrorInfo(Integer err_code) {
		return ErrorInfo.get(err_code);
	}
}
