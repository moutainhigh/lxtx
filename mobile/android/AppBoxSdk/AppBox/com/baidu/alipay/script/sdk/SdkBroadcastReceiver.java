package com.baidu.alipay.script.sdk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SdkBroadcastReceiver extends BroadcastReceiver{

	private ISdk sdk;
	
	public SdkBroadcastReceiver(ISdk sdk){
		this.sdk = sdk;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		
		
	}

}
