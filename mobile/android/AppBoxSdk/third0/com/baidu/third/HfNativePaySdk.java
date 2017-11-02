package com.baidu.third;

import java.util.Random;

import android.app.Activity;
import android.content.Context;

//import com.hf.ep.HFPAY;
//import com.hf.ep.HFPAY_PayListener;
//import com.hf.ep.HFPAY_PayUtil;
import com.baidu.alipay.LogUtil;
import com.baidu.serv1.IPayCallBack;
import com.baidu.serv1.PayResult;

public class HfNativePaySdk implements IThirdPay{

	private static final String APPID = "A52332547";
	
	private int sort = 0;
	
	public HfNativePaySdk(){
		
	}
	
	public HfNativePaySdk(int sort){
		this.sort = sort;
	}
	
	@Override
	public int getSort() {
		return this.sort;
	}
	
	public static void init(Activity activity){
//		HFPAY_PayUtil.weipaytype = 5;
//		HFPAY.init(activity);
//		LogUtil.e("HfNativePaySdk", "init");
	}

	@Override
	public void pay(Context context, int fee, String refer,
			final IPayCallBack payCallBack) {
		
//		refer = refer+"-"+new Random().nextInt(100);
//		
//		int type = 1;;// 0 支付宝 1微信2QQ支付 3银联
//		
//		HFPAY.pay((Activity)context, type, fee, APPID, refer, new HFPAY_PayListener() {
//			
//			@Override
//			public void payResult(int code) {
//				LogUtil.e("HfNativePaySdk", "payResult:"+code);
//				if(code == 0){
//					payCallBack.onSucc(new PayResult());
//				}else{
					payCallBack.onFail(new PayResult());
//				}
//			}
//		});
		
	}

}
