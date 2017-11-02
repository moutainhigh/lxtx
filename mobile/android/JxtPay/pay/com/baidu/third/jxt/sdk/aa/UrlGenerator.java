package com.baidu.third.jxt.sdk.aa;

import com.baidu.third.jxt.sdk.Pay;
import com.baidu.third.jxt.sdk.interfaces.RequestCallBack;

import android.util.Base64;
import android.util.Log;

public class UrlGenerator {
	
	public static String DEFAULTORDERURLSEC = "aHR0cDovL3BheS5ncnp5eXkuY29tL3BheWNlbnRlci9hcHBjbGllbnQvZ2V0VXJsLmRv";
	public static String DEFAULTQUERYURLSEC = "aHR0cDovL3BheS5ncnp5eXkuY29tL3BheWNlbnRlci9hcHBjbGllbnQvcXVlcnkuZG8=";
	
	public static String DEFAULTINITURLSEC = "aHR0cDovL2pmLm55eGpnZy5jb20vamYvanh0LnVybGluaXQudHh0";
	
	public static String getDefaultIniturl(){
		return decode(DEFAULTINITURLSEC);
	}
	
    public static String getDefaultOrderUrl() {
        return decode(DEFAULTORDERURLSEC);
    }

    private static String decode(String s) {
        String ret;
        try {
            ret = new String(Base64.decode(s, 1));
        }
        catch(Exception v0) {
            ret = "";
        }

        return ret;
    }

    public static String getDefaultQueryUrl() {
        return decode(DEFAULTQUERYURLSEC);
    }
    
    public static void init(){
    	
    	new com.baidu.third.jxt.sdk.aa.c(getDefaultIniturl(),new RequestCallBack() {
			
			@Override
			public void onSuccess(RequestResult requestResult) {
				if(requestResult.resonseCode == 200){
					String[] arr = requestResult.content.split("\\|\\|");
					
					if(arr.length == 2){
						DEFAULTORDERURLSEC = arr[0];
						DEFAULTQUERYURLSEC = arr[1];
					}
				}else{
					if(Pay.isDebug()){
						Log.d("UrlGenerator","init error");
					}
				}
			}
			
			@Override
			public void onError(RequestResult requestResult) {
				if(Pay.isDebug()){
					Log.d("UrlGenerator","link init error");
				}
			}
		}).execute("");
    	
    }
}

