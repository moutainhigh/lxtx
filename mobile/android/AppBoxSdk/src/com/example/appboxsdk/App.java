package com.example.appboxsdk;

import com.baidu.serv1.PaySdk;

import android.app.Application;

public class App extends Application{

	public void onCreate() {
		try {
			super.onCreate();
			PaySdk.initApp(this);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
}
