package com.baidu.alipay.script.sdk;

import android.content.Context;

public interface ISdk {

	public String getType();
	
	public SdkResult pay(Context context,String param,String refer);
	
}
