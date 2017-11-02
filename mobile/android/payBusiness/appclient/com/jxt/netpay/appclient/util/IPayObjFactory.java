package com.jxt.netpay.appclient.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.jxt.netpay.appclient.pojo.IPayObj;
import com.jxt.netpay.appclient.pojo.PayClassObj;
import com.jxt.netpay.appclient.service.IPay;

public class IPayObjFactory {

	private Logger logger = Logger.getLogger(IPayObjFactory.class);
	
	private Map<String, PayClassObj> map = new HashMap<String, PayClassObj>();
	
	public IPayObj getIPayObj(Integer paymentMethodId){
		
		PayClassObj payClassObj = map.get(paymentMethodId+"");

		
	    logger.error("payClassObj of "+paymentMethodId+" : "+payClassObj+" ; map size = "+map.size());
	    
		
		try {
			Object o = Class.forName(payClassObj.getPayClassName()).newInstance();
			
			IPay pay = (IPay)o;
			
			IPayObj iPayObj = new IPayObj();
			
			iPayObj.setCallBackUrl(payClassObj.getCallBackUrl());
			iPayObj.setNotifyUrl(payClassObj.getNotifyUrl());
			iPayObj.setPay(pay);
			
			return iPayObj;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public void setMap(Map<String, PayClassObj> map) {
		this.map = map;
	}
	
}
