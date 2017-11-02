package com.jxt.pay.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;

import com.jxt.pay.handler.ShuaConfigHandler;
import com.jxt.pay.pojo.ShuaConfig;

public class ShuaConfigHelper implements InitializingBean{

	private Map<String, String> map = new HashMap<String, String>();
	
	public String getModel(String payCode){
		return map.get(payCode);
	}
	
	private void init(){
		new Thread(){
			public void run(){
				while(true){
					try{
						initMap();
						
						Thread.sleep(1000 * 60 * 10);
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		}.start();
	}
	
	private void initMap(){
		List<ShuaConfig> list = shuaConfigHandler.getAll();
		
		if(list != null && list.size() > 0){
			Map<String, String> _map = new HashMap<String, String>();
			
			for(ShuaConfig config : list){
				_map.put(config.getPayCode(), config.getModel());
			}
			
			this.map.clear();
			this.map = _map;
		}
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		init();
	}

	//ioc
	private ShuaConfigHandler shuaConfigHandler;

	public void setShuaConfigHandler(ShuaConfigHandler shuaConfigHandler) {
		this.shuaConfigHandler = shuaConfigHandler;
	}
	
	
}
