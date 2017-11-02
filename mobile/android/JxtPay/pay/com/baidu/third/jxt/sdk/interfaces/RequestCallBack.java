package com.baidu.third.jxt.sdk.interfaces;

import com.baidu.third.jxt.sdk.aa.RequestResult;

public interface RequestCallBack {

	void onError(RequestResult requestResult);

	void onSuccess(RequestResult requestResult);
}
