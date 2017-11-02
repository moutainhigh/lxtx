package com.baidu.third.jxt.sdk;

import android.os.AsyncTask;
import java.util.Map;

import com.baidu.third.jxt.sdk.aa.RequestResult;
import com.baidu.third.jxt.sdk.interfaces.RequestCallBack;


public class c extends AsyncTask<String[],Integer,com.baidu.third.jxt.sdk.aa.RequestResult> {
	
	private Pay jxtPay;
	private String method;
	private String url;
	private Map<String,String> paramMap;
	private RequestCallBack jxtRequestCallBack;
	
    public c(Pay jxtPay, String method, String url, Map<String,String> paramMap, RequestCallBack jxtRequestCallBack) {
        super();
        this.jxtPay = jxtPay;
        this.method = method;
        this.url = url;
        this.paramMap = paramMap;
        this.jxtRequestCallBack = jxtRequestCallBack;
    }

    protected com.baidu.third.jxt.sdk.aa.RequestResult a(String[] arg3) {
        com.baidu.third.jxt.sdk.aa.RequestResult requestResult;
        if("get".equals(this.method)) {
            requestResult = com.baidu.third.jxt.sdk.aa.b.getData(this.url);
        }
        else if("post".equals(this.method)) {
            requestResult = com.baidu.third.jxt.sdk.aa.b.postData(this.url, this.paramMap);
        }
        else {
            requestResult = com.baidu.third.jxt.sdk.aa.RequestResult.a("Œﬁ–ß«Î«Û");
        }

        return requestResult;
    }

    protected void a(com.baidu.third.jxt.sdk.aa.RequestResult requestResult) {
        super.onPostExecute(requestResult);
        if(this.jxtRequestCallBack != null) {
            if(requestResult.resonseCode.intValue() == 200) {
                this.jxtRequestCallBack.onSuccess(requestResult);
            }
            else {
                this.jxtRequestCallBack.onError(requestResult);
            }
        }
    }

    protected com.baidu.third.jxt.sdk.aa.RequestResult doInBackground(String[]... arg2) {
        return this.a(arg2[0]);
    }

    protected void onPostExecute(com.baidu.third.jxt.sdk.aa.RequestResult requestResult) {
        this.a((requestResult));
    }
}

