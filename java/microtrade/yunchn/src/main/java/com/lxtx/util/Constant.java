package com.lxtx.util;

public class Constant {
	/** 初始密码及管理员用户前缀，后期可修改为其它方式初始 */
	/** 初始密码-未加密 */
	public static final String FIRST_PWD_DECODE = "1";
	/** 初始密码-加密 */
	public static final String FIRST_PWD_ENCODE = "$2a$05$04pIIbCpKAl2N6pZMgBxQOvM5Jxjp0clSZF5rJml4lhOj5b9xpMZm";
	public static final String C_FIRST_PWD_DECODE = "123456";
	public static final String C_FIRST_PWD_ENCODE = "$2a$05$LgRPDTzN7tSuzKLUrFkSgeHahnBn4D9Y6jWy24LSUzfuijd2Byc0K";

	/** 管理员用户前缀 */
	public static final String FIRST_ADMIN_PREF = "admin";

	public static final String SESSION_USER = "session_user";
	public static final String SESSION_PWD = "session_pwd";
	public static final String SESSION_MENU_LIST = "session_menu_list";
	public static final String SESSION_USER_ROLE = "session_user_role";
	/* jqgrid */
	public static final String JQGRID_OPER_ADD = "add";
	public static final String JQGRID_OPER_DELETE = "del";
	public static final String JQGRID_OPER_UPDATE = "edit";

	/** 模板状态 0:待生效 1:已生效 2:已失效 */
	public static final Integer C_TEMP_STATUS_PRE = 0;
	public static final Integer C_TEMP_STATUS_AGREE = 1;
	public static final Integer C_TEMP_STATUS_REFUSE = 2;

	/**
	 * 启用
	 */
	public static final Integer IN_USE = 1;
	/**
	 * 停用
	 */
	public static final Integer IN_STOP = 0;
	
	/**
	 * 账户充值 0：充入 1：指出
	 */
	public static final Integer CHANGENUM_TYPE_IN=0;
	public static final Integer CHANGENUM_TYPE_OUT=1;

}
