package com.lxtx.fb.task.service;

import java.util.HashMap;
import java.util.Map;

import com.lxtx.fb.handler.IpTimeHandler;
import com.lxtx.fb.pojo.IpTime;
import com.lxtx.fb.task.util.CommonUtil;

public class IpTimeService {

	private int offSet = 1200000;
	
	private Map<String, Long> map = new HashMap<String, Long>();
	
	public boolean checkIp(String ip){
		
		String section = CommonUtil.getIpSection(ip);
		
		if(map.containsKey(section)){
			if(System.currentTimeMillis() - map.get(section) < offSet){
				return false;
			}
		}
		
		IpTime ipTime = ipTimeHandler.getOne(section);
		
		if(ipTime == null){
			ipTime = new IpTime(section);
			ipTimeHandler.insert(ipTime);
			
			map.put(section, ipTime.getLastTime());
			
			return true;
		}else{
			long lastTime = ipTime.getLastTime();
			long now = System.currentTimeMillis();
			
			boolean valid = (now - lastTime) >= offSet;
			
			if(valid){
				ipTime.setLastTime1(System.currentTimeMillis());
				
				if(ipTimeHandler.updateOne(ipTime) == 1){
					map.put(section, ipTime.getLastTime());
				}else{
					valid = false;
				}
			}
			
			return valid;
		}
	}
	
	//ioc
	private IpTimeHandler ipTimeHandler;

	public void setIpTimeHandler(IpTimeHandler ipTimeHandler) {
		this.ipTimeHandler = ipTimeHandler;
	}

	public void setOffSet(int offSet) {
		this.offSet = offSet;
	}
	
	
}
