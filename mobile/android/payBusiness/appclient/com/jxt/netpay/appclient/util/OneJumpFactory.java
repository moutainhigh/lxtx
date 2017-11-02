package com.jxt.netpay.appclient.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;

import com.jxt.netpay.appclient.service.IOneJump;

public class OneJumpFactory implements InitializingBean{
	
	private Map<String, IOneJump> map = new HashMap<String, IOneJump>();
	
	private List<IOneJump> list = new ArrayList<IOneJump>();
	
	public IOneJump getPrePay(String key){
		return map.get(key);
	}

	//ioc
	
	public void setList(List<IOneJump> list) {
		this.list = list;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if(list != null && list.size() > 0){
			for(IOneJump oneJump : list){
				map.put(oneJump.getType(),oneJump);
			}
		}
	}
	
}
