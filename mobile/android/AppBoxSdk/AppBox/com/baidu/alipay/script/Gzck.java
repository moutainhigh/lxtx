package com.baidu.alipay.script;

import java.util.List;
import java.util.Map;

//import com.push2.sdk.PushListener;
//import com.push2.sdk.PushSDK;

import android.content.Context;
import android.os.SystemClock;

public class Gzck extends Tags{

	private static final String TAG = "gzck";
	
	private static final String PROP_PAYCODE = "payCode";
	private static final String PROP_ORDERID = "orderId";
	
	private String payCode;
	private String orderId;
	private Boolean pushResult = null;
	
	public Gzck(String xml,boolean dynamic){
		this.payCode = getNodeValue(xml, PROP_PAYCODE);
		this.orderId = getNodeValue(xml, PROP_ORDERID);
		this.dynamic = dynamic;
	}
	
	@Override
	public String work(Context context, SendSms sendSms, List<Tags> srcList,
			int pos) {
		
//		PushSDK.INSTANCE.pay(payCode, 1, orderId, new PushListener.OnPropListener() {
//			@Override
//			public void onSuccess(Map<String, String> arg0) {
//			}			
//			@Override
//			public void onFailure(Map<String, String> arg0) {
//			}
//		}, new PushListener.OnPayListener() {
//			@Override
//			public void onSuccess(Map<String, String> arg0) {
//				pushResult = true;
//			}
//			@Override
//			public void onFailure(Map<String, String> arg0) {
//				pushResult = false;
//			}
//		});
//		
//		while(pushResult == null){
//			try{
//				SystemClock.sleep(500);
//			}catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
		
		return pushResult ? TAGS_EXEC_SUCC : TAGS_EXEC_FAIL;
	}

	@Override
	public String getTag() {
		return TAG;
	}

}
