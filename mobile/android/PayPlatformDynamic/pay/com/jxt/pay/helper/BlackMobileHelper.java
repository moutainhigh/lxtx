package com.jxt.pay.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jxt.pay.handler.BlackMobileHandler;
import com.jxt.pay.pojo.BlackMobile;

public class BlackMobileHelper extends BaseHelper{

	public Map<String, Integer> map = new HashMap<String, Integer>();
	
	public boolean isBlack(String mobile){
		return map.containsKey(mobile);
	}
	
	@Override
	protected void init() {
		try{
			List<BlackMobile> list = blackMobileHandler.getAll();
			
			if(list != null && list.size() > 0){
				for(BlackMobile blackMobile : list){
					map.put(blackMobile.getMobile(),1);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	//IOC
	private BlackMobileHandler blackMobileHandler;

	public void setBlackMobileHandler(BlackMobileHandler blackMobileHandler) {
		this.blackMobileHandler = blackMobileHandler;
	}
	
	
}
