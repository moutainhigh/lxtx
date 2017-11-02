package com.baidu.third;

import java.net.URLDecoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

//import com.iapppay.h5.interfaces.callback.IPayResultCallback;
//import com.iapppay.h5.sdk.main.IAppPay;
//import com.iapppay.h5.utils.RSAHelper;
import com.baidu.alipay.DeviceInfo;
import com.baidu.serv1.IPayCallBack;
import com.baidu.serv1.PayResult;
import com.baidu.third.iapppayh5.IAppPaySDKConfig;
import com.baidu.third.iapppayh5.Order;
import com.baidu.third.iapppayh5.SignHelper;

public class IAppPayH5Sdk {
	
	private static final String TAG = "IAppPayH5Sdk";

	public static void init(Activity activity){
//		IAppPay.init(activity,IAppPay.PORTRAIT, IAppPaySDKConfig.APP_ID);
	}
	
	public ThirdResult pay(final Context context,final int fee,final String refer,final IPayCallBack payCallBack){
		ThirdResult thirdResult = new ThirdResult();
		
		new Thread(){
			public void run(){
				
				try {
					boolean succ = false;
					
					String transid = Order.CheckSign(IAppPaySDKConfig.APP_ID, 1, "购买充值卡", refer, fee/100, DeviceInfo.getIMEI(context), "cpprivateinfo123456", null);
					
					if(transid != null && transid.length() > 0){
						JSONObject jsonObject=new JSONObject();
						jsonObject.put("transid",transid);
						jsonObject.put("redirecturl", "http://ipay.test.happyapk.com:9002/test/index.jsp");
						jsonObject.put("cpurl", "http://ipay.test.happyapk.com:9002/test/index.jsp");
						String payData=jsonObject.toString();
						String payDataSign = SignHelper.sign(payData, IAppPaySDKConfig.APPV_KEY);
						
						startPay((Activity)context, payData, payDataSign, payCallBack);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				
			}
		}.start();
		
		return thirdResult;
	}
	
	public void startPay( Activity activity , String payData , String payDataSign,final IPayCallBack payCallBack) {
//		Log.d(TAG, "payData:" + payData );
//		Log.d(TAG, "payDataSign:" + payDataSign );
//    	IAppPay.startPay(activity , payData , payDataSign , new IPayResultCallback() {
//			
//			@Override
//			public void onPayResult(int resultCode, String signvalue, String resultInfo) {
//				// TODO Auto-generated method stub
//				
//				if(resultCode == IAppPay.PAY_SUCCESS && dealPaySuccess(signvalue)){
//					payCallBack.onSucc(new PayResult());
//				}else{
//					payCallBack.onFail(new PayResult());
//				}
//			}
//			
//			
//			/**
//			 * 4.支付成功。
//			 *  需要对应答返回的签名做验证，只有在签名验证通过的情况下，才是真正的支付成功
//			 */
//			private boolean dealPaySuccess(String signValue) {
//				Log.i(TAG, "sign = " + signValue);
//				if (TextUtils.isEmpty(signValue)) {
//					return false;
//				}
//
//				boolean isvalid = false;
//				try {
//					isvalid = signCpPaySuccessInfo(signValue);
//				} catch (Exception e) {
//				
//					isvalid = false;
//				}
//				return isvalid;
//			}
//
//			/**
//			 * valid cp callback sign
//			 * @param signValue
//			 * @return
//			 * @throws Exception
//			 * 
//			 * transdata={"cporderid":"1","transid":"2","appid":"3","waresid":31,
//			 * "feetype":4,"money":5,"count":6,"result":0,"transtype":0,
//			 * "transtime":"2012-12-12 12:11:10","cpprivate":"7",
//			 * "paytype":1}&sign=xxxxxx&signtype=RSA
//			 */
//			private boolean signCpPaySuccessInfo(String signValue) throws Exception {
//				int transdataLast = signValue.indexOf("&sign=");
//				String transdata = URLDecoder.decode(signValue.substring("transdata=".length(), transdataLast));
//				
//				int signLast = signValue.indexOf("&signtype=");
//				String sign = URLDecoder.decode(signValue.substring(transdataLast+"&sign=".length(),signLast));
//				
//				String signtype = signValue.substring(signLast+"&signtype=".length());
//				
//				if (signtype.equals("RSA") && RSAHelper.verify(transdata, IAppPaySDKConfig.PLATP_KEY, sign)) {//publicKey
//					return true;
//				}else{
//					Log.e(TAG, "wrong type ");
//				}
//				return false;
//			}
//		});
	}
}
