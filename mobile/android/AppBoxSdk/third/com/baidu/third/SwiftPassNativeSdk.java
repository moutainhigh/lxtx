package com.baidu.third;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import com.alipay.a.a.c;
import com.baidu.alipay.LogUtil;
import com.baidu.serv1.IPayCallBack;
import com.baidu.serv1.PayResult;
import com.switfpass.pay.MainApplication;
import com.switfpass.pay.activity.PayPlugin;
import com.switfpass.pay.bean.RequestMsg;
import com.switfpass.pay.utils.MD5;
import com.switfpass.pay.utils.Util;
import com.switfpass.pay.utils.XmlUtils;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class SwiftPassNativeSdk implements IThirdPay{

	private int sort = 0;

	private Handler mhandler;
	
	private BroadcastReceiver receiver = null;

//	public static IWXAPI wxApi = null;
	
	public SwiftPassNativeSdk(){
		
	}
	
	public SwiftPassNativeSdk(int sort){
		this.sort = sort;
	}
	
	@Override
	public int getSort() {
		return this.sort;
	}

	public static String appid = "";
	private static String payType = "wxsdk3";
	
	public static String getPostfix(String packageName){
		if(isSdk(packageName)){
			payType = "wxsdk"+WXPayTypeConfig.getWxPayConfigSuffix();
			return WXPayTypeConfig.getWxPayConfigSuffix(); //map.get(packageName);
//			payType = "wxsdk"+map.get(packageName);
//			return map.get(packageName);
		}else if(packageName.startsWith("com.baidu.")){
			String pkName = packageName.replace("com.baidu.", "");
			payType = "wxsdk"+pkName;
			return "-"+pkName;
		}else{
			String pkName = packageName.replace(".", "");
			payType = "wxsdk"+pkName;
			return "-"+pkName;
		}
	}
	
//	public static final Map<String, String> map = new HashMap<String, String>();
//	
//	static{
//		map.put("com.vcoveo.beidber.kkou","-wimi7");
//		map.put("com.dymes.osrdn.de1593","-wimi8");
//		
//		//map.put("com.yymy.dcrw","-rm1");
//		map.put("com.zxsy.wdmj", "-rm2");
//		map.put("com.zxsy.zyhl", "-rm3");
//	}
	
	public static boolean isSdk(String packageName){
		return "rm".equals(WXPayTypeConfig.getWxPaySdkType()) || "wimi".equals(WXPayTypeConfig.getWxPaySdkType()) || "dx".equals(WXPayTypeConfig.getWxPaySdkType());
//		return map.containsKey(packageName);
	}
	
	public static void init(Activity activity){
//		if(wxApi == null){
//			wxApi = WXAPIFactory.createWXAPI(activity, ThirdConstants.APPID_WX1, false);
//			
//			wxApi.registerApp(ThirdConstants.APPID_WX1);
//		}
	}
	
	@Override
	public void pay(final Context context, int fee, String refer,
			final IPayCallBack payCallBack) {
		
		mhandler = new Handler(Looper.getMainLooper()){
			public void handleMessage(android.os.Message msg) {
				super.handleMessage(msg);
				
				context.unregisterReceiver(receiver);
				
				if(msg.what == 1){
					payCallBack.onSucc(new PayResult());
				}else{
					payCallBack.onFail(new PayResult());
				}
			}
		};
		
		receiver = new BroadcastReceiver(){

			@Override
			public void onReceive(Context context, Intent intent) {
				boolean result = intent.getBooleanExtra("result", false);
				 mhandler.obtainMessage(result?1:0).sendToTarget();
			}
			
		};

		context.registerReceiver(receiver, new IntentFilter(ThirdConstants.ACTION_WXPAY));

		new GetPrepayIdTask(context, mhandler, fee, refer).execute();
		
	}

	private class GetPrepayIdTask extends AsyncTask<Void, Void, Map<String, String>>{

		private Handler handler;
		private int fee;
		private String refer;
		private Context context;
		
        public GetPrepayIdTask(Context context,Handler handler, int fee, String refer){
        	this.context = context;
        	this.fee = fee;
        	this.refer = refer;
        	this.handler = handler;
        }
        
        @Override
        protected void onPreExecute(){
            
        }
        
        @Override
        protected void onPostExecute(Map<String, String> result){

        	if (result == null){
        		handler.sendEmptyMessage(0);
            }else{
            	if (result.get("status").equalsIgnoreCase("0")){
                    
            		appid = result.get("appid");
//            		if(ThirdPaySdk.isHaiTunNativeSdk()){
//            			HaiTunNativePaySdk.APPID = appid;
//            		}else if(ThirdPaySdk.isLessenNativeSdk()){
//            			LessenNativePaySdk.APPID = appid;
//            		}else if(ThirdPaySdk.isRongMengNativeSdk()){
//            			RongMengNativePaySdk.APPID = appid;
//            		}
            		ThirdPaySdk.APPID = appid;
            		
                    RequestMsg msg = new RequestMsg();
                    msg.setTokenId(result.get("token_id"));
                    // 微信wap支付
                    msg.setTradeType(MainApplication.WX_APP_TYPE);
                    msg.setAppId(appid);
                    
                    PayPlugin.unifiedAppPay((Activity)context, msg);
                    
                }else{
                	handler.sendEmptyMessage(0);
                }
	                
            }
        }
        
        @Override
        protected void onCancelled(){
            super.onCancelled();
            handler.sendEmptyMessage(0);
        }
        
        @Override
        protected Map<String, String> doInBackground(Void... params){
        	byte[] buf = null;
        	
            // 统一预下单接口
        	double price = ((double)fee - new Random().nextInt(5)*10)/100;
        	
        	if(!ThirdPaySdk.isWftNativeSdk()){
//	        	if(ThirdPaySdk.isJshyNativeSdk()){
//	        		payType = "wxsdk2";
//	        	}else if(ThirdPaySdk.isHaiTunNativeSdk()){
//	        		payType = "wxsdk"+HaiTunNativePaySdk.getPostfix(ThirdPaySdk.packageName);
//	        	}else if(ThirdPaySdk.isLessenNativeSdk()){
//	        		payType = "wxsdk"+LessenNativePaySdk.getPostfix(ThirdPaySdk.packageName);
//	        	}else if(ThirdPaySdk.isRongMengNativeSdk()){
//	        		payType = "wxsdk"+RongMengNativePaySdk.getPostfix(ThirdPaySdk.packageName);
//	        	}else if(isSdk(ThirdPaySdk.packageName)){
//	        		payType = "wxsdk"+map.get(ThirdPaySdk.packageName);
//	        	}else{
//	        		payType = "wxsdk"+ThirdPaySdk.packageName.replace(".","");
//	        	}
        		payType = "wxsdk"+ThirdPaySdk._postfix;
        	}
        	
    		String url = "http://pay.grzyyy.com/paycenter/appclient/getUrl.do";
    		url += "?appId=100001&orderId="+refer+"&payType="+payType+"&price="+price+"&desc=buy&callBackUrl=http://www.baidu.com&notifyUrl=http://www.cyjd1300.com:9020/pay/synch/netpay/jxtPayNotify.do";
    		
    		String md5 = "100001"+refer+payType+price+"9d85e686031b1c1ae4f13ae22b3cb36f";//MD5签名格式
    		String sign = MD5.md5s(md5).toLowerCase();//计算MD5值 
    		
    		url += "&sign="+sign;
    		
    		LogUtil.e("SwiftPassNativeSdk", "url:"+url);
    		
    		buf = Util.httpGet(url);
        	
            if (buf == null || buf.length == 0){
            	return null;
            }
            String content = new String(buf);
            LogUtil.e("SwiftPassNativeSdk","content:"+content);
            
            try{
                return XmlUtils.parse(content);
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        	
        }
    }
	
}
