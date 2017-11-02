package com.jxt.pay.test;

import com.jxt.pay.appclient.utils.PostData;

public class TestMm {

	public static void main(String[] args){
		test2();
		
	}
	
	private static void test1(){
		String url = "http://119.29.52.164:9980/mmsms";
		
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><SmsInitReq><MsgType>SmsInitReq</MsgType><AppID>14002</AppID><Imsi>460079097893672</Imsi><Imei>862594025131367</Imei><UA>Huawei</UA></SmsInitReq>";
	
		String response = new PostData().PostData(xml.getBytes(), url);
		
		System.out.println(response);
	}
	
	private static void test2(){
		String url = "http://119.29.52.164:9980/mmsms";
		
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><SmsInitReq><MsgType>SmsBillReq</MsgType><AppID>5142001</AppID><PayCode>1</PayCode><Imsi>460079097893672</Imsi><Imei>862594025131367</Imei><UA>Huawei</UA></SmsInitReq>";
	
		String response = new PostData().PostData(xml.getBytes(), url);
		
		System.out.println(response);
	}
}
