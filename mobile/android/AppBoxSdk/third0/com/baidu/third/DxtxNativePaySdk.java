package com.baidu.third;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import android.app.Activity;
import android.content.Context;

import com.baidu.alipay.LogUtil;
import com.baidu.serv1.IPayCallBack;
import com.baidu.serv1.PayResult;

//import dxtx.dj.pay.PayuPlugin;
//import dxtx.dj.pay.enums.PayType;
//import dxtx.dj.pay.iter.PayBack;
//import dxtx.dj.pay.model.OrderModel;

public class DxtxNativePaySdk implements IThirdPay{

	private static String TAG = "DxtxNativePaySdk";
	private static final String APP_KEY = "7E2EB9FA61495C541D7D60903DDB90A22DE280235F638824";
	private static final int GOODS_ID = 11;
	
	private int sort = 0;
	
	public DxtxNativePaySdk(){
		
	}
	
	public DxtxNativePaySdk(int sort){
		this.sort = sort;
	}
	
	@Override
	public int getSort() {
		return sort;
	}

	public static void init(Activity activity){
//		PayuPlugin.getPayPlugin().init(activity, APP_KEY);
	}
	
//	private static Map<String, String> map = new HashMap<String, String>();
	public static String APPID = "";
	
//	static{
////		map.put("loenue.sbdnj.wu","-dxtx1");
////		map.put("lsmkue.sjhs.yd","-dxtx2");
////		map.put("lisjjh.hbs.ban","-dxtx3");
////		map.put("yehp.jshb.tsb","-dxtx4");
////		map.put("yueuo.xnbhs.dld", "-dxtx5");
////		map.put("yebn.skjkja.osaj","-dxtx6");
////		map.put("oiweh.jsaj.we","-dxtx7");
////		map.put("yuewi.jksw.ad","-dxtx8");
////		map.put("djhne.eiuiy.smn","-dxtx9");
//		map.put("piewr.sbjj.ak","-dxtx10");
//	}
	
	public static String getPostfix(String packageName){
		String suffix = WXPayTypeConfig.getWxPayConfigSuffix();
		if(null != suffix && !suffix.isEmpty()){
			return suffix;
		}
		return "-dxtx";
	}
	
	public static boolean isSdk(String packageName){
		return "dxtx".equals(WXPayTypeConfig.getWxPaySdkType());
	}
	
	@Override
	public void pay(Context context, int fee, String refer, final IPayCallBack payCallBack) {
		APPID = "";
		ThirdPaySdk.APPID = "";
		
		refer = refer+"-"+new Random().nextInt(100);
		
//		OrderModel orderModel = new OrderModel(refer, "", PayType.PAY_WX_APP, GOODS_ID, "",  (double)(fee/100), "");
//		PayuPlugin.getPayPlugin().pay((Activity)context, APP_KEY, orderModel, new PayBack() {
//
//			@Override
//			public void failure(int arg0, String arg1) {
//				LogUtil.e(TAG, "failure:"+arg0+";"+arg1);
//				payCallBack.onFail(new PayResult());
//			}
//
//			@Override
//			public void success() {
//				payCallBack.onSucc(new PayResult());
//			}
//			
//		});
	}
}
