package com.baidu.third.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
//import com.zf.config.PayConfig;
//import com.zf.util.LogUtil;
//import com.zf.wx.PayZXWXLoad;

public class MingPengWXPayEntryActivity extends Activity implements IWXAPIEventHandler {

	private IWXAPI api;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		String appid = PayConfig.zxAppId;
//		api = WXAPIFactory.createWXAPI(this,appid);// appid需换成商户自己开放平台appid
//		api.handleIntent(getIntent(), this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
//		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
//			LogUtil.i("WXPayEntryActivity", "返回的code"+resp.errCode);
//			if (resp.errCode == 0){
//				paySuccess();
//			} else {
//				payFail(resp.errCode);
//			}
//			finish();
//		}
	}

	private void payFail(final int code) {
//		runOnUiThread(new Runnable() {
//			@Override
//			public void run() {
//				PayZXWXLoad.payCallBack.payFusionFail(code);
//			}
//		});
	}

	private void paySuccess() {
//		runOnUiThread(new Runnable() {
//			@Override
//			public void run() {
//				PayZXWXLoad.payCallBack.payFusionSuccess();
//			}
//		});
	}
}