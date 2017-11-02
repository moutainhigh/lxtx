package com.baidu.alipay.script;

import java.util.List;

import android.content.Context;

public class Asynch extends Tags{
	
	@Override
	public String work(Context context, SendSms sendSms, List<Tags> tagsList, int pos) {
		return TAGS_EXEC_SUCC;
	}

	@Override
	public String getTag() {
		return Tags.TAGS_ASYNCH;
	}

}
