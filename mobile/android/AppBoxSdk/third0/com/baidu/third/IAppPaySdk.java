package com.baidu.third;

//import com.iapppay.interfaces.callback.IPayResultCallback;
//import com.iapppay.sdk.main.IAppPay;
//import com.iapppay.sdk.main.IAppPayOrderUtils;
import com.baidu.alipay.DeviceInfo;
import com.baidu.serv1.IPayCallBack;
import com.baidu.serv1.PayResult;

import android.app.Activity;
import android.content.Context;
import android.os.SystemClock;

public class IAppPaySdk {

	public static void init(Activity activity){
//		IAppPay.init(activity, IAppPayConfig.appid);
	}
	
	public ThirdResult pay(Context context,int fee,String refer,final IPayCallBack payCallBack){
		ThirdResult thirdResult = new ThirdResult();
		
//		final IAppPaySdkResult ret = new IAppPaySdkResult();
//		
//		String param = getTransdata(DeviceInfo.getIMEI(context),"cpprivateinfo123456",1,fee/100,refer);
//		
//		IAppPay.startPay((Activity)context, param, new IPayResultCallback() {
//			
//			@Override
//			public void onPayResult(int resultCode, String signvalue, String resultInfo) {
//
//				switch (resultCode) {
//				case IAppPay.PAY_SUCCESS:
//					//调用 IAppPayOrderUtils 的验签方法进行支付结果验证
//					boolean payState = IAppPayOrderUtils.checkPayResult(signvalue, IAppPayConfig.publicKey);
//					if(payState){
//						payCallBack.onSucc(new PayResult());
//					}
//					break;
//				default:
//						payCallBack.onFail(new PayResult());
//					break;
//				}
//			}
//		});
		
		return thirdResult;
	}
	
//	 /** 获取收银台参数 */
//	private String getTransdata( String appuserid, String cpprivateinfo, int waresid, float price, String cporderid) {
//		//调用 IAppPayOrderUtils getTransdata() 获取支付参数
//		IAppPayOrderUtils orderUtils = new IAppPayOrderUtils();
//		orderUtils.setAppid(IAppPayConfig.appid);
//		orderUtils.setWaresid(waresid);
//		orderUtils.setCporderid(cporderid);
//		orderUtils.setAppuserid(appuserid);
//		orderUtils.setPrice(price);//单位 元
//		orderUtils.setWaresname("购买充值卡");//开放价格名称(用户可自定义，如果不传以后台配置为准)
//		orderUtils.setCpprivateinfo(cpprivateinfo);
//		orderUtils.setNotifyurl(IAppPayConfig.notifyurl);
//		return orderUtils.getTransdata(IAppPayConfig.privateKey);
//	}
	
	class IAppPaySdkResult{
		Boolean result = null;
	}
}