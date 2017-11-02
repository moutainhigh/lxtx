package com.mm.Business;

import android.util.Log;


public class BillNative {
	static {
		try {
			System.loadLibrary("mm20");
		} catch (Exception e) {
			Log.d("BillNative", "loadLibrary faile" + e.getMessage());
		}

	}

	/*
	返回值定�? 字符�? ;分隔
		Retcode,ErrorCode, BillType, SID, InitSms, OrderID, OrderSms
		Retcode == 0       表示强或弱联计费成功，如果是强联，返回sOutOrderID ; 如果是弱联，返回 sOutOrderID�?sOutOrderSms
        Retcode == 1       表示强联�?��初始化，返回 sOutSID, sOutInitSms
        Retcode == 2       计费失败�?errorcode表示错误�?
	*/
	public native static String Billing(Object context,String sIP, String sPort, String SAppID , String  SInPaycode, String SInImsi, String SInImei,String SInChannel, int Count, String SInExtData);
	
	
	/*
	*/
	public native static String SendSmsFinish(Object context,String sIP, String sPort, String SAppID , String  SInPaycode, String SInImsi, String SInImei,String SID);
	
	/**
	 * 专门启动用户 返回代码不做处理
	 * @return
	 */
	public native static String ActiveDAUser(Object context,String sIP, String sPort, String SAppID , String  SInPaycode, String SInImsi, String SInImei,String SInChannel, int Count, String SInExtData);

}

