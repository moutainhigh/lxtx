package com.baidu.third.jxt.sdk.aa;

import com.baidu.third.jxt.sdk.interfaces.RequestCallBack;

import android.os.AsyncTask;

public class c extends AsyncTask<String,Integer,com.baidu.third.jxt.sdk.aa.RequestResult> {
	
	private String url = "";
	private RequestCallBack jxtRequestCallBack = null;
	
	public c(String url,RequestCallBack jxtRequestCallBack){
		this.url = url;
		this.jxtRequestCallBack = jxtRequestCallBack;
	}
	
	protected void onPostExecute(com.baidu.third.jxt.sdk.aa.RequestResult requestResult){ 
		if(this.jxtRequestCallBack != null) { 
            if(requestResult != null && requestResult.resonseCode != null && requestResult.resonseCode.intValue() == 200) {
                this.jxtRequestCallBack.onSuccess(requestResult);
            }
            else {
                this.jxtRequestCallBack.onError(requestResult);
            }
        }
	}
	
	@Override
	protected RequestResult doInBackground(String... arg0) {
		
		com.baidu.third.jxt.sdk.aa.RequestResult requestResult = com.baidu.third.jxt.sdk.aa.b.getData(url);
		
		return requestResult;
	}

}
