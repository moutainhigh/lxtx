package com.lxtx.fb.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.InitializingBean;

import com.lxtx.fb.handler.ActiveHandler;
import com.lxtx.fb.pojo.Active;

public class ActiveHelper implements InitializingBean{

	private List<Active> list = new ArrayList<Active>();
	
	public Active getOne(){
		return list.get(new Random().nextInt(list.size()));
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		init();
	}
	
	private void init(){
		list = activeHandler.getAll();
	}

	
	//ioc
	private ActiveHandler activeHandler;

	public void setActiveHandler(ActiveHandler activeHandler) {
		this.activeHandler = activeHandler;
	}
	
	
}
