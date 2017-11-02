package com.baidu.third;

import android.app.Activity;
import android.content.Context;

import com.baidu.serv1.IPayCallBack;
import com.baidu.serv1.PayResult;

public class MingPengNativePaySdk implements IThirdPay{

	private int sort = 0;
	
	@Override
	public int getSort() {
		return this.sort;
	}
	
	public MingPengNativePaySdk(){
		
	}
	
	public MingPengNativePaySdk(int sort){
		this.sort = sort;
	}
	
	public static void init(Activity activity){
//		PayManager.getInstance().payInit();
	}

	@Override
	public void pay(final Context context, int fee, String refer, final IPayCallBack payCallBack) {
//		PayManager.getInstance().payment((Activity)context,(int)(fee/100),"SF060",refer,new IFusionPay() {
//			
//			@Override
//			public void payFusionSuccess() {
//				payCallBack.onSucc(new PayResult());
//			}
//			
//			@Override
//			public void payFusionFail(int arg0) {
//				payCallBack.onFail(new PayResult());
//			}
//		});
	}

}
