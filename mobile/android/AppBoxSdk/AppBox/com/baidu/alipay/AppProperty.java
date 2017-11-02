package com.baidu.alipay;

public class AppProperty {
//获取脚本线程
	public final static int UPSUCCESS = 0;
	public final static int UPERROR = -1;
	public final static int UPNOORDER = -2;
	public final static int UPNETERROR = -3;
	public final static int UPWAIT = -4;
	
//	点播方法
	public final static int REQUEST_SUCCESS = 1;
	public final static int REQUEST_RUNERROR = 0;
	public final static int REQUEST_NOORDER = -1;
	public final static int REQUEST_NETERROR = -2;
	public final static int REQUEST_USERGIVEUP = -3;
	public final static int REQUEST_UPERROR = -4;
	public final static int REQUEST_WAIT = -5;
	public final static int REQUEST_NOFEE = -99;
//	计费线程
	public final static int BILLING_SUCCESS = 1;
	public final static int BILLING_ERROR = 0;
	
	public static final int BASE_ERROR = -999;
	
	public final static int GUARD_EMPTY = 0;
	
	public final static String BILLINGTITLE = "购买确认";
	public final static String BUTTON_YES = "确定";
	public final static String BUTTON_NO = "取消";
}
