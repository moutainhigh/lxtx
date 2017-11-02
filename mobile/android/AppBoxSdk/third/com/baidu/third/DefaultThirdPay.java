package com.baidu.third;

import android.content.Context;

import com.baidu.serv1.IPayCallBack;
import com.baidu.serv1.PayResult;

public class DefaultThirdPay implements IThirdPay{

	private int sort = 0;
	
	public DefaultThirdPay(){
		
	}
	
	public DefaultThirdPay(int sort){
		this.sort = sort;
	}
	
	@Override
	public int getSort() {
		return sort;
	}

	@Override
	public void pay(Context context, int fee, String refer,
			IPayCallBack payCallBack) {
		payCallBack.onFail(new PayResult());
	}

}
