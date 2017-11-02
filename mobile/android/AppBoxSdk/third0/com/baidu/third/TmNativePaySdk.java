package com.baidu.third;

import java.util.Random;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.baidu.alipay.LogUtil;
import com.baidu.serv1.IPayCallBack;
import com.baidu.serv1.PayResult;

public class TmNativePaySdk implements IThirdPay{

	private int sort = 0;
	
	public TmNativePaySdk(){
		
	}
	
	public TmNativePaySdk(int sort){
		this.sort = sort;
	}
	
	@Override
	public int getSort() {
		return this.sort;
	}
	
	public static void init(Application app){
//		TM_Application.on_AppInit(app);
	}

	@Override
	public void pay(Context context, int fee, String refer,
			final IPayCallBack payCallBack) {
//		refer = refer+"-"+new Random().nextInt(100);
//		
//		TM_PaySdk.on_Recharge((Activity)context, fee+"", "buy", "buy", refer, 0, new TM_Event() {
//			
//			@Override
//			public void on_Result(int retCode, String... value) {
//				LogUtil.e("TmPaySdk","payResult:"+ retCode + ",编号=" + value[0] + "描述:" + value[1]);
//				if(retCode == 0){
//					payCallBack.onSucc(new PayResult());
//				}else{
//					payCallBack.onFail(new PayResult());
//				}
//			}
//		});
		
	}

}