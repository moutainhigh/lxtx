package com.jxt.pay.test;

import com.jxt.pay.appclient.utils.GetData;

public class TestDmby {

	public static void main(String[] args){
		
		String url = "http://wap.dm.10086.cn/waph5/auth/orderPage.html?free=1&ts=&chann_id=2&watch_type=comicOnlinePlay&opus_id=43840&content_id=1623183&quality=1&ts=1448711188908";
		
		String content = GetData.getData(url);
		
		System.out.println(content);
	}
	
}
