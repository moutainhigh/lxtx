package com.baidu.alipay.script;

import java.util.List;

import com.baidu.alipay.LogUtil;

import android.content.Context;

public class Error extends Tags{

	private static final String TAG = "Error";
	
	private String errorCode;
	
	public Error(String xml,boolean dynamic){
		this.errorCode = xml;
		
		this.dynamic = dynamic;
	}
	
	@Override
	public String work(Context context, SendSms sendSms, List<Tags> tagsList, int pos) {
		
//		LogUtil.e(TAG, "work : errorCode : "+errorCode);
		errorReason = errorCode;
		return TAGS_EXEC_FAIL;
	}

	@Override
	public String getTag() {
		return Tags.TAGS_ERROR;
	}

}
