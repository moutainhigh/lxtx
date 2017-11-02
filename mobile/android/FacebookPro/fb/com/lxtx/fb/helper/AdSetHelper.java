package com.lxtx.fb.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;

import com.lxtx.fb.handler.AdSetHandler;
import com.lxtx.fb.pojo.AdSet;

public class AdSetHelper implements InitializingBean{

	private Map<Long, AdSet> map = new HashMap<Long, AdSet>();
	
	public AdSet get(long adSetId){
		return map.get(adSetId);
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		init();
	}
	
	private void init(){
		List<AdSet> list = adSetHandler.getAll();
		
		if(list != null && list.size() > 0){
			for(AdSet adSet : list){
				map.put(adSet.getId(), adSet);
			}
		}
	}
	
	//ioc
	private AdSetHandler adSetHandler;

	public void setAdSetHandler(AdSetHandler adSetHandler) {
		this.adSetHandler = adSetHandler;
	}
	
	

}
