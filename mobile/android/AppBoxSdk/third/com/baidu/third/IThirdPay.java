package com.baidu.third;

import android.content.Context;

import com.baidu.serv1.IPayCallBack;

public interface IThirdPay {
	public int getSort();
	
	public void pay(Context context,int fee,String refer,final IPayCallBack payCallBack);
	
}
