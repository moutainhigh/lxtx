package com.baidu.alipay.script;

import java.util.List;

import com.baidu.alipay.LogUtil;

import android.content.Context;

public class Sets extends Tags{

	private static final String TAG = "Sets";
	
	private static final String PROP_KEY = "key";
	private static final String PROP_VALUE = "value";
	
	private String key;
	
	private String value;
	
	public Sets(String xml,boolean dynamic){
		this.key = getNodeValue(xml, PROP_KEY);
		this.value = getNodeValue(xml, PROP_VALUE);
	
		this.dynamic = dynamic;
	}
	
	@Override
	public String work(Context context, SendSms sendSms, List<Tags> tagsList, int pos) {
		
//		LogUtil.e(TAG,"sets works");
		
		sendSms.setDataValue(key, value);
		
		return TAGS_EXEC_SUCC;
	}

	@Override
	public String getTag() {
		return TAGS_SETS;
	}

}
