package com.lxtx.fb.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.InitializingBean;

import com.lxtx.fb.handler.UaHandler;
import com.lxtx.fb.pojo.Ua;

public class UaHelper implements InitializingBean{

	private List<Ua> list = new ArrayList<Ua>();
	
	private Map<Integer, Ua> map = new HashMap<Integer, Ua>();
	
	public Ua getOne(){
		if(this.list != null && this.list.size() > 0){
			return this.list.get(new Random().nextInt(this.list.size()));
		}
		
		return null;
	}
	
	public Ua get(int uaId){
		return map.get(uaId);
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		init();
	}

	private void init(){
		this.list = uaHandler.getAll();
		
		if(this.list != null && this.list.size() > 0){
			for(Ua ua : list){
				map.put(ua.getId(), ua);
			}
		}
	}
	
	//ioc
	private UaHandler uaHandler;

	public void setUaHandler(UaHandler uaHandler) {
		this.uaHandler = uaHandler;
	}
	
}
