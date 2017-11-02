package com.baidu.third.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

//import com.lessen.paysdk.pay.PayTool;
import com.baidu.third.LessenNativePaySdk;
import com.baidu.third.ThirdConstants;
import com.baidu.third.ThirdPaySdk;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;



public class LessenWXPayEntryActivity extends Activity implements IWXAPIEventHandler {


	IWXAPI api;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		api = WXAPIFactory.createWXAPI(this, null, false);
		api.handleIntent(getIntent(), this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		Log.e("LessenWXPayEntryActivity", "onResp:"+resp.getType()+";"+resp.errCode);
		
//		if(ThirdPaySdk.APPID != null && ThirdPaySdk.APPID.length() > 0){
//			if (resp.errCode == 0) // 支付成功
//	        {
//	        	broadcast(true);
//	        } else {
//	        	broadcast(false);
//	        }
//		}else{
//			PayTool.ON_PAYCALLBACK(resp.errCode, resp.errStr);
//		}
		
		finish();
	}
	
	private void broadcast(boolean succ){
		Log.e("WXPayEntryActivity", "broadcast:"+succ);
		
		Intent intent = new Intent(ThirdConstants.ACTION_WXPAY);
		
		intent.putExtra("result", succ);
		
		this.sendBroadcast(intent);
	}
}