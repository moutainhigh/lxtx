package com.example.appboxsdk;

import com.baidu.serv1.PaySdk;
import com.baidu.third.WftActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		PaySdk.init(this);
		
		Intent intent = new Intent();
		intent.setClass(this, MainActivity.class);
		
		startActivity(intent);
		
		this.finish();
	}
	
}
