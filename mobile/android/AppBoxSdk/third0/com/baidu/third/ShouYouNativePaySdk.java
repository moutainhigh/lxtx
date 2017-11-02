package com.baidu.third;

import java.util.Random;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

//import com.shouyoupay.ZX_PayCallBack;
//import com.shouyoupay.ZX_PaySdk;
import com.baidu.alipay.LogUtil;
import com.baidu.serv1.IPayCallBack;
import com.baidu.serv1.PayResult;

public class ShouYouNativePaySdk implements IThirdPay{

	private int sort = 0;
	
	public ShouYouNativePaySdk(){
		
	}
	
	public ShouYouNativePaySdk(int sort){
		this.sort = sort;
	}
	
	@Override
	public int getSort() {
		 return this.sort;
	}

	public static void init(Application app){
		
//		ZX_PaySdk.on_AppInit(app.getApplicationContext());
//		LogUtil.e("ShouYouNativePaySdk", "init");
	}
	
	@Override
	public void pay(Context context, int fee, String refer,
			final IPayCallBack payCallBack) {
		
//		refer += "-"+new Random().nextInt(100);
//		
//		LogUtil.e("ShouYouNativePaySdk", "pay:"+refer+";"+fee);
//		
//		ZX_PaySdk.Pay((Activity)context, fee+"", "buy", "buy", refer, 0, new ZX_PayCallBack() {
//			
//			@Override
//			public void on_Result(int retCode, String... value) {
//				LogUtil.e("ShouYouNativePaySdk","payResult:"+ retCode + ",编号=" + value[0] + "描述:" + value[1]);
//				if(retCode == 0){
//					payCallBack.onSucc(new PayResult());
//				}else{
//					payCallBack.onFail(new PayResult());
//				}
//			}
//		});
		
	}

}
