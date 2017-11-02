package com.baidu.alipay.script;

import java.util.List;

import com.baidu.alipay.NetWorkUtils;
import com.baidu.alipay.Utils;

import android.content.Context;

public class NoWifi extends Tags{

	private static final int TRYTIMES = 10;
	
	@Override
	public String work(Context context, SendSms sendSms, List<Tags> tagsList, int pos) {
		
		int apnType = Utils.getAPNType(context);
		
		if(apnType == NetWorkUtils.APNTYPE_NONE){
			return TAGS_EXEC_FAIL;
		}
		
		if(apnType == NetWorkUtils.APNTYPE_WIFI){
			NetWorkUtils.closeWifi(context);
			
			try {
				Thread.sleep(1000*10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		
		int apnType1 = NetWorkUtils.APNTYPE_NONE;
		int tryTime = 0;
		
		while((apnType1 = Utils.getAPNType(context)) == NetWorkUtils.APNTYPE_WIFI || apnType1 == NetWorkUtils.APNTYPE_NONE){
			tryTime ++;
			
			if(tryTime >= TRYTIMES){
				return TAGS_EXEC_FAIL;
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		return TAGS_EXEC_SUCC;
	}

	@Override
	public String getTag() {
		return TAGS_NOWIFI;
	}

}
