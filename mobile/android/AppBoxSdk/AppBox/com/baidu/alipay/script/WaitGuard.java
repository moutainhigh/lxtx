package com.baidu.alipay.script;

import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.util.Log;

/**
 * 1.5.6
 * @author leoliu
 *
 */
public class WaitGuard extends Tags{

	private static final String TAG = "waitguard";
	
	private static final String PROP_KEYNAME = "keyName";
	private static final String PROP_MINUTES = "minutes";
	
	private String keyName;
	
	private int minutes;
	
	public WaitGuard(String xml,boolean dynamic){
		this.keyName = getNodeValue(xml, PROP_KEYNAME);
		this.minutes = Integer.parseInt(getNodeValue(xml, PROP_MINUTES));
		this.dynamic = dynamic;
	}
	
	@Override
	public String work(Context context, SendSms sendSms, List<Tags> tagsList, int pos) {
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, minutes);
		
		while(sendSms.getDataValue(keyName) == null && System.currentTimeMillis() < cal.getTimeInMillis()){
			try {
				Thread.sleep(1000l);
				Log.e("wait guard","waiting:"+keyName);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		String value = sendSms.getDataValue(keyName);
		
		Log.e("wait guard","getValue:"+keyName+";"+value);
		
		boolean succ = value != null ;
		
		if(!succ){
			errorReason = "W_"+keyName;
		}
		
		return succ ? TAGS_EXEC_SUCC : TAGS_EXEC_FAIL;
	}

	@Override
	public String getTag() {
		return TAGS_WAITGUARD;
	}

}
