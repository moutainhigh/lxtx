package com.jxt.pay.appclient.utils;

import java.util.ArrayList;
import java.util.List;

public class XmlUtils {

	public static final List<String> getNodeValues(String xml,String name){
		List<String> nodeValues = new ArrayList<String>();
		
		String prefix = "<"+name+">";
		String postfix = "</"+name+">";
		
		int start = -1;
		int end = -1;
		
		while((start = xml.indexOf(prefix,end)) > 0 && (end = xml.indexOf(postfix,start)) > 0){
			nodeValues.add(xml.substring(start,end+postfix.length()));
		}
		
		return nodeValues;
	}
	
	public static final String getNodeValue(String xml,String name){
		
		String prefix = "<"+name+">";
		String postfix = "</"+name+">";
		
		int start = xml.indexOf(prefix);
		int end = xml.indexOf(postfix);
	
		if(start >= 0 && end > start){
			return xml.substring(start+prefix.length(),end);
		}
		
		return null;
	}
	
	public static void main(String[] args){
//		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
//				+"<moRequest>"
//				+"<amount>200</amount>"
//				+"<content>02Xt0111</content>"
//				+"<cpId>2002</cpId>"
//				+"<moTime>2013-04-23 02:22:51</moTime>"
//				+"<phone>18250864327</phone>"
//				+"<serviceId>5959595959</serviceId>"
//				+"<serviceNo>106901334042</serviceNo>"
//				+"<transactionId>20130423142333820324</transactionId>"
//				+"</moRequest>";
//		
//		System.out.println(getNodeValue(xml, "transactionId"));
//		System.out.println(getNodeValue(xml, "cpOrderNo"));
		
		String xml1 = "<mos><mo><id>1</id></mo><mo><id>2</id></mo><mo><id>3</id></mo></mos>";
		
		List<String> nodeValues = getNodeValues(xml1, "mo");
		
		System.out.println(nodeValues.size());
		
		for(String nodeValue : nodeValues){
			System.out.println(nodeValue);
		}
	}
}
