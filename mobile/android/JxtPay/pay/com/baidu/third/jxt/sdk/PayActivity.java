package com.baidu.third.jxt.sdk;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import java.util.HashMap;
import java.util.Map;

import com.baidu.third.jxt.sdk.model.PaymentBean;


public class PayActivity extends Activity {
    private ProgressDialog progressDialog;
    private boolean b;

    public PayActivity() {
        super();
    }

    public static ProgressDialog getProgressDialog(PayActivity jxtWebPayActivity) {
        return jxtWebPayActivity.progressDialog;
    }

    public static Map<String,String> getMapFromRequest(PayActivity jxtPayActivity, String url) {
        return jxtPayActivity.getMapFromRequest(url);
    }

    private Map<String,String> getMapFromRequest(String url) {
        Map<String,String> map = new HashMap<String,String>();
        String v1 = url.substring(url.indexOf("?") + 1, url.length());
        if(!TextUtils.isEmpty(((CharSequence)v1))) {
            String[] arr = v1.split("&");
            int len = arr.length;

            for(int i = 0; i < len; ++i) {
                String[] subArr = arr[i].split("=");
                if(subArr.length > 1) {
                    map.put(subArr[0], subArr[1]);
                }
            }
        }

        return map;
    }

    private void showCallBackDialog() {
        if(Pay.getInstance().isEnableCallBackDialog()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(((Context)this));
            builder.setMessage("是否支付完成？");
            builder.setNegativeButton("否", new d(this));
            builder.setPositiveButton("支付完成", new e(this));
            AlertDialog dialog = builder.create();
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
        }
    }

    public static boolean a(PayActivity jxtWebPayActivity, boolean arg1) {
    	jxtWebPayActivity.b = arg1;
        return arg1;
    }

    private void queryOrder() {
        if(Pay.getInstance().getPaymentBean() == null) {
            this.finish();
        }
        else {
            if(Pay.isDebug()) {
                Log.d("JxtPay", "check orderId:" + Pay.getInstance().getPaymentBean().getOrderId());
            }

            this.progressDialog.show();
            Pay.getInstance().queryOrderRequest(Pay.getInstance().getPaymentBean().getTradeId(), 
                    Pay.getInstance().getDelayQueryMillis(), new o(this));
        }
    }

    public static void showCallBackDialog(PayActivity jxtWebPayActivity) {
    	jxtWebPayActivity.showCallBackDialog();
    }

    public static void queryOrder(PayActivity jxtWebPayActivity) {
    	jxtWebPayActivity.queryOrder();
    }

    public void finish() {
        super.finish();
        this.overridePendingTransition(0, 0);
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if(Pay.getInstance().getPaymentBean() == null) {
            throw new RuntimeException("缺少支付构建对象");
        }

        this.progressDialog = new ProgressDialog(((Context)this));
        this.progressDialog.setMessage("正在请求...");
        new g(this).execute(new PaymentBean[]{Pay.getInstance().getPaymentBean()});
    }

    protected void onResume() {
        super.onResume();
        if(!Pay.getInstance().isEnableCallBackDialog() && (this.b)) {
            this.queryOrder();
        }
    }
}

