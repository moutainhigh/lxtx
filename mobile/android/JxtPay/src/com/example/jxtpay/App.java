package com.example.jxtpay;

import com.baidu.third.jxt.sdk.Pay;

import android.app.Application;
import android.content.Context;

public class App extends Application{

	public App() {
        super();
    }

    public void onCreate() {
        super.onCreate();
        Pay.getInstance().init(((Context)this));
        Pay.getInstance().setEnableCallBackDialog(false);
        Pay.setDebug(true);
        Pay.getInstance().setCanRetry(true);
    }
	
}
