package com.baidu.third.wxapi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

//import com.szrm.pay.RMAPIFactory;
import com.baidu.alipay.LogUtil;
import com.baidu.third.RongMengNativePaySdk;
import com.baidu.third.ThirdConstants;
import com.baidu.third.ThirdPaySdk;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class RongMengWXPayEntryActivity extends Activity implements IWXAPIEventHandler {
	private IWXAPI api;

	public RongMengWXPayEntryActivity() {
		super();
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		if (ThirdPaySdk.APPID != null && ThirdPaySdk.APPID.length() > 0) {
//			this.api = WXAPIFactory.createWXAPI(((Context) this), RongMengNativePaySdk.APPID);
//			this.api.handleIntent(this.getIntent(), ((IWXAPIEventHandler) this));
//		} else {
//			api = WXAPIFactory.createWXAPI(this, RMAPIFactory.wxAppId);
//			api.handleIntent(getIntent(), this);
//		}
	}

	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		this.setIntent(intent);
	}

	public void onReq(BaseReq req) {
//		if (RMAPIFactory.handler != null) {
//			RMAPIFactory.handler.onReq(req);
//		}
	}

	public void onResp(BaseResp resp) {
		LogUtil.e("RongMengWXPayEntryActivity", "onResp:" + resp.getType() + ";" + resp.errCode);

//		if (ThirdPaySdk.APPID != null && ThirdPaySdk.APPID.length() > 0) {
//			if (resp.errCode == 0) // 支付成功
//			{
//				broadcast(true);
//			} else {
//				broadcast(false);
//			}
//
//			this.finish();
//		} else {
//			if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
//				finish();
//				if (RMAPIFactory.handler != null) {
//					RMAPIFactory.handler.onResp(resp);
//				}
//			}
//		}
	}

	private void broadcast(boolean succ) {
		Log.e("WXPayEntryActivity", "broadcast:" + succ);

		Intent intent = new Intent(ThirdConstants.ACTION_WXPAY);

		intent.putExtra("result", succ);

		this.sendBroadcast(intent);
	}
}
