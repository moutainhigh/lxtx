package com.jxt.netpay.appclient.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;

import com.jxt.netpay.appclient.service.IPrePay;

public class PrePayFactory implements InitializingBean{
	
	private Map<String, IPrePay> map = new HashMap<String, IPrePay>();
	
	private List<IPrePay> list = new ArrayList<IPrePay>();
	
	public IPrePay getPrePay(String key){
		return map.get(key);
	}

	//ioc
	
	public void setList(List<IPrePay> list) {
		this.list = list;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if(list != null && list.size() > 0){
			for(IPrePay prePay : list){
				map.put(prePay.getType(),prePay);
			}
		}
	}
	
}
