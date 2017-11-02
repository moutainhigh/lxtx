package com.baidu.third.jxt.sdk;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.baidu.third.jxt.sdk.aa.UrlGenerator;
import com.baidu.third.jxt.sdk.interfaces.PayCallBack;
import com.baidu.third.jxt.sdk.interfaces.RequestCallBack;
import com.baidu.third.jxt.sdk.interfaces.QueryOrderCallBack;
import com.baidu.third.jxt.sdk.model.PaymentBean;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;

public class Pay {
	
	private String appKey;
	private String appId;
    private String orderUrl;
    private String queryUrl;
    private boolean enableCallBackDialog;
    
    private PayCallBack mPayCallBack;
    private PaymentBean mPaymentBean;
    private long delayQueryMillis ;
    
    private int validTimes = 1;
    private int tryTimes = 1;
    
    private boolean canRetry = false;
    
    private static boolean isDebug = false;
    
	private static Pay instance = new Pay(); 
	
	public static Pay getInstance(){
		return instance;
	}
	
	private Pay() {
        super();
        this.delayQueryMillis = 2000;
        this.enableCallBackDialog = true;
    }
	
	public void init(String appId,String appKey){
		this.appId = appId;
		this.appKey = appKey;
		
		UrlGenerator.init();
	}
	
	public void init(Context context){
		try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), 
                    128);
            this.appKey = applicationInfo.metaData.getString("JXT_APP_KEY", "");
            this.appId = applicationInfo.metaData.getInt("JXT_APP_ID") + "";
            this.orderUrl = applicationInfo.metaData.getString("JXT_ORDER_URL", "");
            this.queryUrl = applicationInfo.metaData.getString("JXT_QUERY_URL", "");
            this.canRetry = applicationInfo.metaData.getBoolean("JXT_CANRETRY", false);
        }catch(Exception e) {
            e.printStackTrace();
        }
		
		UrlGenerator.init();
	}
	
	public void setCanRetry(boolean canRetry){
		this.canRetry = canRetry;
	}
	
	public void setEnableCallBackDialog(boolean enableCallBackDialog) {
        this.enableCallBackDialog = enableCallBackDialog;
    }
	
	public static void setDebug(boolean debug) {
        Pay.isDebug = debug;
    }
	
	public void pay(final Context context,PaymentBean paymentBean,final PayCallBack jxtPayCallBack){
		this.mPaymentBean = paymentBean;
		this.validTimes = 1;
		this.tryTimes = 1;
		
		this.mPayCallBack = new PayCallBack() {
			
			@Override
			public void onPayResult(boolean isSuccess, String orderId) {
				
				if(isSuccess || tryTimes >= validTimes || !canRetry){
					jxtPayCallBack.onPayResult(isSuccess, orderId);
				}else{
					tryTimes = tryTimes + 1;
					
					mPaymentBean.setPaySort(tryTimes - 1);
					
					SystemClock.sleep(1000l);
					
					realPay(context);
				}
				
			}
		};
		
		realPay(context);
	}
	
	private void realPay(Context context){
		
		if(("0".equals(this.appId)) || (TextUtils.isEmpty(this.appId)) || (TextUtils.isEmpty(
                this.appKey))) {
            this.init(context);
        }
		if(!TextUtils.isEmpty(this.appId) && !TextUtils.isEmpty(this.appKey)) {
			this.openPayAct(context, this.appId, this.appKey);
            return;
        }

        throw new RuntimeException("缺少微信key和merid，请确认配置正确");
	}
		
	private void openPayAct(Context context, String appId, String appKey) {
        if(context != null && mPaymentBean != null) {
        	if(mPaymentBean.getFee() <= 0) {
                throw new RuntimeException("支付金额必须大于0");
            }else {
                this.mPaymentBean.setKey(appKey);
                this.mPaymentBean.setAppId(appId);
                
                Intent intent = new Intent(context, PayActivity.class);
                
                if(!(context instanceof Activity)){
                	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                
                context.startActivity(intent);
                if((context instanceof Activity)) {
                    ((Activity)context).overridePendingTransition(0, 0);
                }

                return;
            }        
        }else{
        	throw new RuntimeException("context为空或者计费参数为空");
        }
    }

	public boolean isEnableCallBackDialog() {
		return enableCallBackDialog;
	}

	public PaymentBean getPaymentBean() {
		return mPaymentBean;
	}

	public static boolean isDebug() {
		return isDebug;
	}

	public long getDelayQueryMillis() {
		return delayQueryMillis;
	}

	public PayCallBack getPayCallBack() {
		return mPayCallBack;
	}

	public void submitErrorMsgRequest(String msg) {
//        Map<String,String> map = new HashMap<String,String>();
//        String v0 = JxtPay.getInstance().getPaymentBean() != null ? JxtPay.getInstance().getPaymentBean()
//                .getOrderId() : "";
//        ((Map)v1).put("error", String.format(Locale.getDefault(), "{\"orderid\":\"%s\",\"wechatmerid\":\"%s\",\"alipaymerid\":\"%s\",\"errormsg\":\"%s\"}", 
//                v0, this.merId_wechat, this.merId_wechat, arg7));
//        this.request("post", "http://msg.fspentu.com", ((Map)v1), null);
    }
	
	public void queryOrderRequest(long tradeId, long delay, QueryOrderCallBack queryOrderCallBack) {
        if(delay < 0) {
            delay = 0;
        }

        new Handler().postDelayed(new m(this, tradeId, queryOrderCallBack), delay);
    }
	
	public void queryOrderRequest(long tradeId, QueryOrderCallBack queryOrderCallBack) {
        String url = (TextUtils.isEmpty(this.queryUrl)) || !this.queryUrl.startsWith("http")
                 ? UrlGenerator.getDefaultQueryUrl() : this.queryUrl;
        Map<String,String> map = new HashMap<String,String>();
        map.put("id", tradeId+"");
        this.request("post", url, map, new com.baidu.third.jxt.sdk.n(this, tradeId, queryOrderCallBack));
    }

	private void request(String method, String url, Map<String,String> paramMap, RequestCallBack jxtRequestCallBack) {
        if(method != null && !TextUtils.isEmpty((url))) {
            new com.baidu.third.jxt.sdk.c(this, method, url, paramMap, jxtRequestCallBack).execute(new String[0]);
            return;
        }

        throw new RuntimeException("缺少必要的请求参数");
    }
	
	public String getCreateOrderUrl() {
		return orderUrl;
	}
	
	public static void setValidTimes(Pay jxtPay,int validTimes){
		jxtPay.setValidTimes(validTimes);
	}
	
	private void setValidTimes(int validTimes){
		this.validTimes = validTimes;
	}
}
