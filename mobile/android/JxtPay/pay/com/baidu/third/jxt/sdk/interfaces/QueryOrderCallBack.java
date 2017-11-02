package com.baidu.third.jxt.sdk.interfaces;

import com.baidu.third.jxt.sdk.model.JxtError;

public interface QueryOrderCallBack {

	void onError(JxtError jxtError);

	void onSuccess(boolean succ);
	
}
