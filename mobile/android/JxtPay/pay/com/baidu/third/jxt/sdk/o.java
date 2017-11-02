package com.baidu.third.jxt.sdk;

import com.baidu.third.jxt.sdk.interfaces.QueryOrderCallBack;
import com.baidu.third.jxt.sdk.model.JxtError;

import android.widget.Toast;

class o implements QueryOrderCallBack {
	
	private PayActivity jxtPayActivity;
	
    public o(PayActivity jxtPayActivity) {
        super();
        this.jxtPayActivity = jxtPayActivity;
    }

    public void onError(JxtError jxtError) {
        Toast.makeText(this.jxtPayActivity, jxtError.message, 0).show();
        PayActivity.getProgressDialog(this.jxtPayActivity).dismiss();
        this.jxtPayActivity.finish();
        if(Pay.getInstance().getPayCallBack() != null) {
            Pay.getInstance().getPayCallBack().onPayResult(false, Pay.getInstance().getPaymentBean()
                    .getOrderId());
        }
    }

    public void onSuccess(boolean succ) {
        PayActivity.getProgressDialog(this.jxtPayActivity).dismiss();
        this.jxtPayActivity.finish();
        if(Pay.getInstance().getPayCallBack() != null) {
            if(succ) {
                Pay.getInstance().getPayCallBack().onPayResult(true, Pay.getInstance().getPaymentBean()
                        .getOrderId());
            }
            else {
                Pay.getInstance().getPayCallBack().onPayResult(false, Pay.getInstance().
                        getPaymentBean().getOrderId());
            }
        }
    }
}

