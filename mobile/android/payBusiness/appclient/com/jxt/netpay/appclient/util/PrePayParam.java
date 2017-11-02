package com.jxt.netpay.appclient.util;

public class PrePayParam{
	private String type;
	
	private String params;
	
	public PrePayParam(String xml){
		type = XmlUtils.getNodeValue(xml, "type");
		params = XmlUtils.getNodeValue(xml, "params");
	}
	
	public String getType(){
		return type;
	}
	
	public String getParams(){
		return params;
	}
}