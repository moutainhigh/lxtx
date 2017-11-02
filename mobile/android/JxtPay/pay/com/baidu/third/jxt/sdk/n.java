package com.baidu.third.jxt.sdk;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.third.jxt.sdk.interfaces.RequestCallBack;
import com.baidu.third.jxt.sdk.interfaces.QueryOrderCallBack;
import com.baidu.third.jxt.sdk.model.JxtError;

class n implements RequestCallBack {
	private Pay jxtPay;
	private long tradeId;
	private QueryOrderCallBack queryOrderCallBack;
	
    n(Pay jxtPay, long tradeId, QueryOrderCallBack queryOrderCallBack) {
        super();
        this.jxtPay = jxtPay;
        this.tradeId = tradeId;
        this.queryOrderCallBack = queryOrderCallBack;
    }

    public void onError(com.baidu.third.jxt.sdk.aa.RequestResult requestResult) {
        if(Pay.isDebug()) {
            Log.d("JxtPay", "查询订单结果 ==> orderId:" + this.tradeId + " content:" + requestResult.content);
        }

        if(this.queryOrderCallBack != null) {
            this.queryOrderCallBack.onError(new JxtError(requestResult.resonseCode.intValue(), requestResult.content));
        }
    }

    public void onSuccess(com.baidu.third.jxt.sdk.aa.RequestResult requestResult) {
        if(Pay.isDebug()) {
            Log.d("JxtPay", "查询订单结果 ==> orderId:" + this.tradeId + " content:" + requestResult.content);
        }

        if(this.queryOrderCallBack != null) {
            try {
                JSONObject jo = new JSONObject(requestResult.content);
                if((jo.has("status")) && ("1".equals(jo.getString("status")))) {
                    this.queryOrderCallBack.onSuccess(true);
                    return;
                }

                this.queryOrderCallBack.onSuccess(false);
            }
            catch(JSONException v0) {
                v0.printStackTrace();
                this.queryOrderCallBack.onError(new JxtError(requestResult.resonseCode.intValue(), requestResult.content));
            }
        }
    }
}

