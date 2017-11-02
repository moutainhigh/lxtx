package com.baidu.third;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import com.baidu.alipay.LogUtil;
import com.baidu.serv1.IPayCallBack;
import com.baidu.serv1.PayResult;
import com.switfpass.pay.utils.MD5;
import com.switfpass.pay.utils.Util;
import com.switfpass.pay.utils.XmlUtils;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WxPaySdk implements IThirdPay{

	private int sort;

	private Handler mhandler;
	
	private BroadcastReceiver receiver = null;

	public static IWXAPI wxApi = null;
	
	public WxPaySdk(){
		
	}
	
	public WxPaySdk(int sort){
		this.sort = sort;
	}

	@Override
	public int getSort() {
		return this.sort;
	}

	public static void init(Activity activity){
//		if(wxApi == null){
//			wxApi = WXAPIFactory.createWXAPI(activity, ThirdConstants.APPID_WX, false);
//			
//			wxApi.registerApp(ThirdConstants.APPID_WX);
//		}
	}
	
	private static String payType = "wxsdkvip";
	
	public static String getPostfix(String packageName){
		if(packageName.startsWith("com.sina.")){
			String pkName = packageName.replace("com.sina.", "");
			payType = "wxsdk"+pkName;
			return "-"+pkName+"1";
		}
		
		return "-vip";
	}
	
	
	public static String appId = null;
	private static Activity activity = null;
	
	@Override
	public void pay(final Context context, int fee, String refer,
			final IPayCallBack payCallBack) {
		
		activity = (Activity)context;
		
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
		
		new GetPrepayIdTask(mhandler, fee, refer).execute();
		
	}

	private class GetPrepayIdTask extends AsyncTask<Void, Void, Map<String, String>>{
        
		private Handler handler;
		private int fee;
		private String refer;
		
        public GetPrepayIdTask(Handler handler, int fee, String refer){
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
        		if(result.get("return_code").equalsIgnoreCase("SUCCESS")){
        			Map<String, String> map = new HashMap<String, String>();
        			
        			map = result;
                	
        			PayReq req = new PayReq();
					
					req.appId			= map.get("appid");
					req.partnerId		= map.get("partnerid");
					req.prepayId		= map.get("prepayid");
					req.nonceStr		= map.get("noncestr");
					req.timeStamp		= map.get("timestamp");
					req.packageValue	= map.get("package");
					req.sign			= map.get("sign");
					
					WxPaySdk.appId = req.appId;
//					if(ThirdPaySdk.isHaiTunNativeSdk()){
//            			HaiTunNativePaySdk.APPID = appId;
//            		}else if(ThirdPaySdk.isLessenNativeSdk()){
//            			LessenNativePaySdk.APPID = appId;
//            		}else if(SwiftPassNativeSdk.isSdk(ThirdPaySdk.packageName)){
//            			SwiftPassNativeSdk.appid = appId;
//            		}
					ThirdPaySdk.APPID = appId;
					// 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
					
					getWxApi().sendReq(req);
					
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
        	double price = fee/100;
        
        	if(!ThirdPaySdk.isWxPaySdk()){
//	        	if(ThirdPaySdk.isHaiTunNativeSdk()){
//	        		payType = "wxsdkvip"+HaiTunNativePaySdk.getPostfix(ThirdPaySdk.packageName);
//	        	}else if(ThirdPaySdk.isLessenNativeSdk()){
//	        		payType = "wxsdkvip"+LessenNativePaySdk.getPostfix(ThirdPaySdk.packageName);
//	        	}else if(SwiftPassNativeSdk.isSdk(ThirdPaySdk.packageName)){
//	        		payType = "wxsdkvip"+SwiftPassNativeSdk.getPostfix(ThirdPaySdk.packageName);
//	        	}else if(ThirdPaySdk.isRongMengNativeSdk()){
//	        		payType = "wxsdkvip"+RongMengNativePaySdk.getPostfix(ThirdPaySdk.packageName);
//	        	}else{
//	        		payType = "wxsdkvip"+ThirdPaySdk.packageName.replace(".", "");
//	        	}
        		
        		payType = "wxsdkvip"+ThirdPaySdk._postfix;
        	}
        	
        	
    		String url = "http://pay.grzyyy.com/paycenter/appclient/getUrl.do";
    		url += "?appId=100001&orderId="+refer+"&payType="+payType+"&price="+price+"&desc=buy&callBackUrl=http://www.baidu.com&notifyUrl=http://www.cyjd1300.com:9020/pay/synch/netpay/jxtPayNotify.do";
    		
    		String md5 = "100001"+refer+payType+price+"9d85e686031b1c1ae4f13ae22b3cb36f";//MD5签名格式
    		String sign = MD5.md5s(md5).toLowerCase();//计算MD5值 
    		
    		url += "&sign="+sign;
    		
    		LogUtil.e("WxPaySdk", "url:"+url);
    		
    		buf = Util.httpGet(url);
        	
            if (buf == null || buf.length == 0){
            	return null;
            }
            String content = new String(buf);
            LogUtil.e("WxPaySdk","content:"+content);
            try{

                Map<String,String> ret = new HashMap<String, String>();
                
//                JSONObject jo = new JSONObject(content);
//            	
//                String[] arr = new String[]{"sign","timestamp","noncestr","partnerid","prepayid","package","appid"};
//                
//                for(String key : arr){
//                	ret.put(key,jo.getString(key));
//                }
                
                ret = XmlUtils.parse(content);
                
                ret.put("return_code", "SUCCESS");
                
                return ret;
            }catch (Exception e){
                e.printStackTrace();
            }
        	
        	
            return null;
        }
    }
	
	public static IWXAPI getWxApi(){
		if(wxApi == null && appId != null && appId.length() > 0){
			wxApi = WXAPIFactory.createWXAPI(activity, appId, false);
			
			wxApi.registerApp(appId);
		}
		
		return wxApi;
	}
}