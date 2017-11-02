package com.example.jxtpay;


import com.baidu.third.jxt.sdk.Pay;
import com.baidu.third.jxt.sdk.PayType;
import com.baidu.third.jxt.sdk.aa.UrlGenerator;
import com.baidu.third.jxt.sdk.interfaces.PayCallBack;
import com.baidu.third.jxt.sdk.model.PaymentBean;

import android.app.Activity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity{
    
	private Activity activity = this;
	
	double fee = 1.00;
	int feeI = 100;
	String desc = "测试";
	String notifyUrl = "";
	
	private String getOrderId(){
		String orderId = "101000-"+feeI+"-"+System.currentTimeMillis();
		return orderId;
	}
	
    public MainActivity() {
        super();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);
        
        ((Button)(findViewById(R.id.button1))).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				PaymentBean paymentBean = new PaymentBean(getOrderId(), fee, desc, notifyUrl,PayType.wxqp);
				
				Pay.getInstance().pay(activity, paymentBean, new PayCallBack() {
                    public void onPayResult(boolean isSuccess, String orderId) {
                        if(isSuccess) {
                            Toast.makeText(MainActivity.this, "支付成功", 0).show();
                        }
                        else {
                            Toast.makeText(MainActivity.this, "支付失败", 0).show();
                        }
                    }
                });
			}
		});
        
        ((Button)(findViewById(R.id.button2))).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				test();
				
//				PaymentBean paymentBean = new PaymentBean(getOrderId(), fee, desc, notifyUrl,PayType.zfb);
//				
//				JxtPay.getInstance().pay(activity, paymentBean, new JxtPayCallBack() {
//                    public void onPayResult(boolean isSuccess, String orderId) {
//                        if(isSuccess) {
//                            Toast.makeText(MainActivity.this, "支付成功", 0).show();
//                        }
//                        else {
//                            Toast.makeText(MainActivity.this, "支付失败", 0).show();
//                        }
//                    }
//                });

			}
		});
        

    }

    private void test(){
//      Log.e("MainActivity",new String(Base64.decode("aHR0cDovL3BheS5mc3BlbnR1LmNvbS9wYXljZW50ZXIvYXBwY2xpZW50L3F1ZXJ5LmRv", 1)));
      	Log.e("MainActivity",UrlGenerator.getDefaultOrderUrl());
     Log.e("MainActivity",UrlGenerator.getDefaultIniturl());
     Log.e("MainActivity",UrlGenerator.getDefaultQueryUrl());
    }
}