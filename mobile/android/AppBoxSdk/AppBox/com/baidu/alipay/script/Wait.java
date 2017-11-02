package com.baidu.alipay.script;

import java.util.List;

import com.baidu.alipay.LogUtil;

import android.content.Context;

public class Wait extends Tags{

	private static final String TAG = "wait";
	
	private String sleepSeconds;
	
	public Wait(String sleepSeconds,boolean dynamic){
		this.sleepSeconds = sleepSeconds;
		
		this.dynamic = dynamic;
	}
	
	@Override
	public String work(Context context, SendSms scriptHelper, List<Tags> tagsList, int pos) {
		
		try{
			LogUtil.e(TAG, " work ");
			
			Integer seconds = Integer.parseInt(sleepSeconds);
			
			Thread.sleep(seconds * 1000);
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return TAGS_EXEC_SUCC;
	}

	@Override
	public String getTag() {
		return TAGS_WAIT;
	}

}
