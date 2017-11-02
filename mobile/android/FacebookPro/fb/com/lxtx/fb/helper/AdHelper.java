package com.lxtx.fb.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;

import com.lxtx.fb.handler.AdHandler;
import com.lxtx.fb.pojo.Ad;

public class AdHelper implements InitializingBean{

	private Map<Long, List<Ad>> map = new HashMap<Long, List<Ad>>();
	
	public List<Ad> list(long adSetId){
		return map.get(adSetId);
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		init();
	}
	
	private void init(){
		List<Ad> list = adHandler.getAll();
		
		if(list != null && list.size() > 0){
			for(Ad ad : list){
				long adSetId = ad.getAdSetId();
				
				List<Ad> subList = map.get(adSetId);
				if(subList == null){
					subList = new ArrayList<Ad>();
					map.put(adSetId, subList);
				}
				
				subList.add(ad);
			}
		}
	}
	
	//ioc
	private AdHandler adHandler;
	
	public void setAdHandler(AdHandler adHandler){
		this.adHandler = adHandler;
	}

}
