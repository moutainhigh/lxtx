package com.lxtx.fb.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.InitializingBean;

import com.lxtx.fb.handler.AlipayAccountHandler;
import com.lxtx.fb.pojo.AlipayAccount;

public class AlipayAccountHelper implements InitializingBean{

	private Map<Integer, AlipayAccount> map = new HashMap<Integer, AlipayAccount>();
	private List<AlipayAccount> list = new ArrayList<AlipayAccount>();
	
	public AlipayAccount get(int id){
		return map.get(id);
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		init();
	}

	private void init(){
		list = alipayAccountHandler.getAll();
		
		if(list != null && list.size() > 0){
			for(AlipayAccount alipayAccount : list){
				map.put(alipayAccount.getId(), alipayAccount);
			}
		}
	}
	
	//ioc
	private AlipayAccountHandler alipayAccountHandler;

	public void setAlipayAccountHandler(AlipayAccountHandler alipayAccountHandler) {
		this.alipayAccountHandler = alipayAccountHandler;
	}

	public AlipayAccount getOne() {
		return list.get(new Random().nextInt(list.size()));
	}
	
	
}
