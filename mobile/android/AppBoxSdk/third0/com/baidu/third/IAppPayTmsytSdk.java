package com.baidu.third;

//import com.iapppay.interfaces.callback.IPayResultCallback;
//import com.iapppay.sdk.main.IAppPay;
//import com.iapppay.sdk.main.IAppPayOrderUtils;
import com.baidu.alipay.DeviceInfo;
import com.baidu.serv1.IPayCallBack;
import com.baidu.serv1.PayResult;

import android.app.Activity;
import android.content.Context;

public class IAppPayTmsytSdk implements IThirdPay{

	private static final String appid = "3006560256";
	private final static String privateKey = "MIICWwIBAAKBgQCkTVz72XYxmjo/VrcYkE+mDyHqFuW269xTPungE1ImivaodRUfcelQ9Nq9Qi5BntB0OCj6u+2hlzNrB+xVTf8aJ2HRsB3QgBwLmpzHv/XzIBy/nrjBOKH41d2+hM80dQFe5wIKTmfaxc8xgQ9ZKFgyHS199kNKVCu4Apt/VELXUQIDAQABAoGASnxkLi0dH0mrJHHcmf86jK8JkgWFaNMu1HKONq6rrJuNTTufMt1CiBgpXmm1bLxiBn47Xch656oTfm9eHiI5BtaK1nn01kAVUhDsBTFDfnIWDUSjtfXXFgL0PcNSmffM33h+/jjtpKkHxLvOkZdJfx+sFE4wWHJcOOLOpZO9rgECQQD0TDm9Q3aZrnMqNODZ/u1TU+OI4P5ychUOsj6WmyGW4IwLre/YoyQxyzFujq9Koxyd0038q5AcEWVzNoXXDmHhAkEArCwrDqEaOd9WpIRIfjxM7nHBDMdTBi4iIB9wdZSFwSTsNiHSIprMWr9sxWI+k5+S9WCL5EmY/Qye0rWTvEQDcQJAW9lM3BvBO4abRgMbhtqR4AJqScxN3jmGMUBSx80Lk+5YuQtsYcb0lMQQkcCTpzwsRipr59OZGvRvcCHPsEt4AQJAMjAt3PXZT0VrXEmzWBj71AYU4ES6TvLjv7lqbtv8oXDB6oWxW6UELO/gJSmeVCoQDLc4EDPkBN/I7GNBzX+GAQJAE5QF9CSLPqrst2rkGIVDkoXUswizjngUSvMveyRMRbZNe5Dq1QlIGdGB7GkOoR8sMYJZSavfnam5iKVWcsbGpA==";
	private final static String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCC/YV/o4j1hqboMtZEnfQbrLydcbMWsnF5EA7gv/sHFGz6aiBF6ltmri59c8kxiJDYnlvYu/LmqkYxtRufNuYUJJAwjI2oZvTpUdQbw34O/fQK3JhxmvwinvoaYt0Lt2HWrOcxdwS99maYqe2e4Iy/7mmzM/ydKs1tdt8R6+cgwQIDAQAB";
	
	private int sort = 0;
	
	public IAppPayTmsytSdk(){
		
	}
	
	public IAppPayTmsytSdk(int sort){
		this.sort = sort;
	}
	
	public int getSort(){
		return sort;
	}
	
	public static void init(Activity activity){
//		IAppPay.init(activity,IAppPay.PORTRAIT, appid);
	}
	
	public void pay(Context context,int fee,String refer,final IPayCallBack payCallBack){
		
//		String param = getTransdata(DeviceInfo.getIMEI(context),"cpprivateinfo123456",1,fee/100,refer);
//		
//		IAppPay.startPay((Activity)context, param, new IPayResultCallback() {
//			
//			@Override
//			public void onPayResult(int resultCode, String signvalue, String resultInfo) {
//
//				if(resultCode == IAppPay.PAY_SUCCESS && IAppPayOrderUtils.checkPayResult(signvalue, publicKey)){
//					payCallBack.onSucc(new PayResult());
//				}else{
//					payCallBack.onFail(new PayResult());
//				}
//			}
//		},403);
		
	}
	
	/** 获取收银台参数 */
//	private String getTransdata( String appuserid, String cpprivateinfo, int waresid, float price, String cporderid) {
//		//调用 IAppPayOrderUtils getTransdata() 获取支付参数
//		IAppPayOrderUtils orderUtils = new IAppPayOrderUtils();
//		orderUtils.setAppid(appid);
//		orderUtils.setWaresid(waresid);
//		orderUtils.setCporderid(cporderid);
//		orderUtils.setAppuserid(appuserid);
//		orderUtils.setPrice(price);//单位 元
//		orderUtils.setWaresname("自定义名称");//开放价格名称(用户可自定义，如果不传以后台配置为准)
//		orderUtils.setCpprivateinfo(cpprivateinfo);
//		
//		return orderUtils.getTransdata(privateKey);
//	}
}
