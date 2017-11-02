package com.baidu.serv1;

public interface IPayCallBack {
	
	public void onSucc(PayResult payResult);
	
	public void onFail(PayResult payResult);
	
	public void onCancel(PayResult payResult);
	
}
