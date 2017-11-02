package com.baidu.third.jxt.sdk;

import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import java.util.HashMap;
import java.util.Map;


import org.json.JSONObject;

import com.baidu.third.jxt.sdk.model.PaymentBean;

public class g extends AsyncTask<PaymentBean[],Integer,com.baidu.third.jxt.sdk.aa.RequestResult> {
	PayActivity jxtPayActivity;
	
    public g(PayActivity jxtPayActivity) {
        super();
        this.jxtPayActivity = jxtPayActivity;
    }

    private void callBack() {
        if(Pay.getInstance().getPayCallBack() != null) {
            Pay.getInstance().getPayCallBack().onPayResult(false, Pay.getInstance().getPaymentBean()
                    .getOrderId());
        }
    }

    private void a(String arg2) {
        this.b();
        Pay.getInstance().submitErrorMsgRequest(arg2);
    }

    protected com.baidu.third.jxt.sdk.aa.RequestResult a(PaymentBean[] paymentBeans) {
    	com.baidu.third.jxt.sdk.aa.RequestResult ret;
        if(paymentBeans == null || paymentBeans.length == 0 || paymentBeans[0] == null) {
            ret = null;
        }
        else {
            if(Pay.isDebug()) {
                Log.d("JxtPay", paymentBeans[0].toString());
            }

            String url = (TextUtils.isEmpty(Pay.getInstance().getCreateOrderUrl())) || !Pay
                    .getInstance().getCreateOrderUrl().startsWith("http") ? com.baidu.third.jxt.sdk.aa.UrlGenerator.getDefaultOrderUrl() : Pay.getInstance()
                    .getCreateOrderUrl();
            ret = com.baidu.third.jxt.sdk.aa.b.postData(url, paymentBeans[0].getParamsMap());
        }

        return ret;
    }

    protected void a(com.baidu.third.jxt.sdk.aa.RequestResult requestResult) {
        String url;
        JSONObject jo;
        super.onPostExecute(requestResult);
        
        if(PayActivity.getProgressDialog(this.jxtPayActivity) != null) {
            PayActivity.getProgressDialog(this.jxtPayActivity).dismiss();
        }

        if(requestResult == null) {
            Toast.makeText(this.jxtPayActivity, "调用支付失败:N001", 0).show();
            this.a("请求创建订单的结果HTResponse为空");
            return;
        }

        try {
            jo = new JSONObject(requestResult.content);
            
            if((jo.has("errorCode")) && (jo.has("msg"))) {
                if(jo.getInt("errorCode") == 1) {
                	
                	long tradeId = jo.getLong("tradeId");
                	Pay.getInstance().getPaymentBean().setTradeId(tradeId);
                	
                	try{
                		if(jo.has("validTimes")){
                			int validTimes = jo.getInt("validTimes");
                			Pay.setValidTimes(Pay.getInstance(),validTimes);
                		}
                	}catch(Exception e){
                	}
                	
                    url = jo.getString("msg");
                    //url = url.replaceAll("http://pay.grzyyy.com/paycenter/qrcode.jsp", "http://192.168.0.103:8080/TsPayCenter/qrcode.jsp");
//                    if(url.toLowerCase().startsWith("weixin://")) {
//                        try{
//                        	this.jxtPayActivity.startActivity(Intent.parseUri(url, 1));
//                            JxtPayActivity.a(this.jxtPayActivity, true);
//                            this.jxtPayActivity.getWindow().getDecorView().postDelayed(new h(this), 2000);
//                            if(JxtPay.isDebug()) {
//                                Log.e("JxtPay", "*******调起微信去支付*******");
//                            }
//                            else {
//                            }
//                            return;
//                        }catch(Exception v0) {
//                            if(JxtPay.isDebug()) {
//                                Log.e("JxtPay", "未安装微信客户端或微信版本不支持");
//                            }
//
//                            Toast.makeText(this.jxtPayActivity, "未安装微信客户端或微信版本不支持", 0).show();
//                            this.a("9999请求创建订单成功，调起微信支付失败，未安装微信客户端或版本不支持");
//                            return;
//                        }   
//                    }
//                    else 
                    {
                    	if(url.toLowerCase().startsWith("http") || url.toLowerCase().startsWith("weixin://")) {
                    		if(url.toLowerCase().startsWith("weixin://")){
                    			url = "url|"+url;
                    		}
                    		
                            if(TextUtils.isEmpty(Pay.getInstance().getPaymentBean().getOrderId())) {
                                Map<String,String> map = PayActivity.getMapFromRequest(this.jxtPayActivity, url);
                                if(map.containsKey("orderId")) {
                                    Pay.getInstance().getPaymentBean().setOrderId(map.get("orderId"));
                                }
                            }

                            Intent intent = new Intent(this.jxtPayActivity, WebPayActivity.class);
                            intent.putExtra("PAY_URL", url);
                            this.jxtPayActivity.startActivity(intent);
                            PayActivity.a(this.jxtPayActivity, true);
                            this.jxtPayActivity.getWindow().getDecorView().postDelayed(new i(this), 2000);
                            if(Pay.isDebug()) {
                            	Log.e("JxtPay", "*******调起支付宝去支付*******");
                            }
                            
                            return;
                        }

                        Toast.makeText(this.jxtPayActivity, "error:" + url, 0).show();
                        this.a("9999请求创建订单成功，message信息无法识别，message:" + url);
                        return;
                    }
                }
                else {
                	if(Pay.isDebug()) {
                        Log.e("JxtPay", "error ==> " + jo.getInt("errorCode") + " " + jo.getString("msg"));
                    }

                    Toast.makeText(this.jxtPayActivity, "error:" + jo.getInt("errorCode") + " " + jo.getString("msg"), 
                            0).show();
                    this.a("请求创建订单失败，errorCode:" + jo.getInt("errorCode") + " message:" + jo.getString(
                            "msg"));
                    return;
                }
            }

            if(Pay.isDebug()) {
                Log.e("JxtPay", "申请创建订单失败 ==> " + requestResult.content);
            }

            this.a("请求创建订单失败，请求返回内容:" + requestResult.content);
        }
        catch(Exception v0) {
        	if(Pay.isDebug()) {
                Toast.makeText(this.jxtPayActivity, "申请创建订单异常", 0).show();
                Log.e("JxtPay", "请求创建订单失败，失败异常信息:" + v0.getMessage());
            }

            v0.printStackTrace();
            this.a("请求创建订单失败，失败异常信息:" + v0.getMessage());
        }
    }

    private void b() {
        this.callBack();
        this.jxtPayActivity.finish();
    }

    protected void b(com.baidu.third.jxt.sdk.aa.RequestResult requestResult) {
        super.onCancelled(requestResult);
        this.jxtPayActivity.finish();
    }

    protected com.baidu.third.jxt.sdk.aa.RequestResult doInBackground(PaymentBean[]... args) {
        return this.a(args[0]);
    }

    protected void onCancelled() {
        super.onCancelled();
        this.jxtPayActivity.finish();
    }

    protected void onCancelled(com.baidu.third.jxt.sdk.aa.RequestResult requestResult) {
        this.b(requestResult);
    }

    protected void onPostExecute(com.baidu.third.jxt.sdk.aa.RequestResult requestResult) {
        this.a(requestResult);
    }

    protected void onPreExecute() {
        super.onPreExecute();
        if(PayActivity.getProgressDialog(this.jxtPayActivity) != null && (PayActivity.getProgressDialog(this.jxtPayActivity).isShowing())) {
            PayActivity.getProgressDialog(this.jxtPayActivity).dismiss();
        }

        if(PayActivity.getProgressDialog(this.jxtPayActivity) != null) {
            PayActivity.getProgressDialog(this.jxtPayActivity).show();
        }
    }
    
    
    
}

