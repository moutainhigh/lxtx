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
            Toast.makeText(this.jxtPayActivity, "����֧��ʧ��:N001", 0).show();
            this.a("���󴴽������Ľ��HTResponseΪ��");
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
//                                Log.e("JxtPay", "*******����΢��ȥ֧��*******");
//                            }
//                            else {
//                            }
//                            return;
//                        }catch(Exception v0) {
//                            if(JxtPay.isDebug()) {
//                                Log.e("JxtPay", "δ��װ΢�ſͻ��˻�΢�Ű汾��֧��");
//                            }
//
//                            Toast.makeText(this.jxtPayActivity, "δ��װ΢�ſͻ��˻�΢�Ű汾��֧��", 0).show();
//                            this.a("9999���󴴽������ɹ�������΢��֧��ʧ�ܣ�δ��װ΢�ſͻ��˻�汾��֧��");
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
                            	Log.e("JxtPay", "*******����֧����ȥ֧��*******");
                            }
                            
                            return;
                        }

                        Toast.makeText(this.jxtPayActivity, "error:" + url, 0).show();
                        this.a("9999���󴴽������ɹ���message��Ϣ�޷�ʶ��message:" + url);
                        return;
                    }
                }
                else {
                	if(Pay.isDebug()) {
                        Log.e("JxtPay", "error ==> " + jo.getInt("errorCode") + " " + jo.getString("msg"));
                    }

                    Toast.makeText(this.jxtPayActivity, "error:" + jo.getInt("errorCode") + " " + jo.getString("msg"), 
                            0).show();
                    this.a("���󴴽�����ʧ�ܣ�errorCode:" + jo.getInt("errorCode") + " message:" + jo.getString(
                            "msg"));
                    return;
                }
            }

            if(Pay.isDebug()) {
                Log.e("JxtPay", "���봴������ʧ�� ==> " + requestResult.content);
            }

            this.a("���󴴽�����ʧ�ܣ����󷵻�����:" + requestResult.content);
        }
        catch(Exception v0) {
        	if(Pay.isDebug()) {
                Toast.makeText(this.jxtPayActivity, "���봴�������쳣", 0).show();
                Log.e("JxtPay", "���󴴽�����ʧ�ܣ�ʧ���쳣��Ϣ:" + v0.getMessage());
            }

            v0.printStackTrace();
            this.a("���󴴽�����ʧ�ܣ�ʧ���쳣��Ϣ:" + v0.getMessage());
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

