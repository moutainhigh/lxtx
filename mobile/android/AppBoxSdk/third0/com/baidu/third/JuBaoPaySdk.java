package com.baidu.third;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

//import com.fanwei.sdk.activity.HandlerCallback;
//import com.fanwei.sdk.api.PaySdk;
//import com.fanwei.sdk.bean.PayParam;
import com.baidu.alipay.DeviceInfo;
import com.baidu.serv1.IPayCallBack;
import com.baidu.serv1.PayResult;

public class JuBaoPaySdk {

//	public static void init(Activity activity){
//		PaySdk.init(activity, PaySdk.PORTRAIT);
//	}
//	
	public ThirdResult pay(Context context,int fee,String refer,final IPayCallBack payCallBack){
//		
//		PayParam payParam = new PayParam();
//		// 金额(必填)
//		payParam.setAmount(String.valueOf(fee/100));
//		// 物品名称(必填)
//		payParam.setGoodsname("客服电话：4001558668");
//		// 商户那边生成的标识用户身份的Id(必填)
//		payParam.setPlayerid(DeviceInfo.getIMEI(context));
//		// 商户生成的订单号(必填)
//		payParam.setPayid(refer);
//		// 商户添加备注(可选)
//		payParam.setRemark("");
//		// 渠道(可选)
//		payParam.setChannelid("360");
//
//		// 调到多条支付通道界面
//		PaySdk.startPay((Activity)context, payParam, new HandlerCallback() {
//			@Override
//			public void processResult(int resultCode, String result) {
//				Log.i("result",  "[resultCode=" + resultCode + "][result=" + result + "]");
//						
//				if (resultCode == 0) {
//					// do something
//					payCallBack.onSucc(new PayResult());
//				} else {
//					payCallBack.onFail(new PayResult());
//				}
//			}
//		});
//		
//		
		return new ThirdResult();
	}
	
}