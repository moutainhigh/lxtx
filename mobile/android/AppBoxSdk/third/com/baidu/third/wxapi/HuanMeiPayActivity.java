package com.baidu.third.wxapi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

//import com.fast.pay.FastParams;
//import com.fast.pay.FastPay;
import com.baidu.third.ThirdConstants;

public class HuanMeiPayActivity extends Activity {

	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;

//		try {
//			Bundle extras = getIntent().getExtras();
//			if (extras == null) {
//				finish();
//				return;
//			}
//			FastParams params = new FastParams();
//			params.setAppKey(extras.getString("appkey"));// app key
//			params.setBusKey(extras.getString("buskey"));// 商户号
//
//			params.setOrderno(extras.getString("oederno"));// 订单号
//			params.setFee(extras.getInt("fee"));// 支付金额 - 单位为分
//			params.setBody(extras.getString("body"));// 商品名称
//			params.setAttach(extras.getString("attach"));// 商品描述
//			params.setCallbackUrl(extras.getString("callbackUrl"));// 支付成功后进入页面
//			params.setNotifyUrl(extras.getString("notifyUrl"));// 数据同步地址
//
//			FastPay.getInstance().pay(this, params, handler, FastPay.WEICHAT);
//		} catch (Exception e) {
//			finish();
//			return;
//		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//		FastPay.getInstance().callResult(requestCode, resultCode, data);
	}

	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			Intent intent = new Intent(ThirdConstants.ACTION_WXPAY);
			intent.putExtra("result", msg.what == 1);
			// intent.putExtra("errCode", msg.obj);
			HuanMeiPayActivity.this.sendBroadcast(intent);
			finish();
		}
	};
}
