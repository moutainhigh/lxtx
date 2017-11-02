package com.lxtx.fb.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.InitializingBean;

import com.lxtx.fb.handler.FlagContactHandler;
import com.lxtx.fb.pojo.FlagContact;

public class FlagContactHelper implements InitializingBean{

	private List<FlagContact> list = new ArrayList<FlagContact>();
	
	public FlagContact getOne(){
		if(list != null && list.size() > 0){
			return list.get(new Random().nextInt(list.size()));
		}
		
		return null;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		init();
	}
	
	private void init(){
		list = flagContactHandler.getAll();
	}
	
	//ioc
	private FlagContactHandler flagContactHandler;

	public void setFlagContactHandler(FlagContactHandler flagContactHandler) {
		this.flagContactHandler = flagContactHandler;
	}

	
}
