package com.lxtx.fb.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;

import com.lxtx.fb.handler.ProxyHandler;
import com.lxtx.fb.pojo.Proxy;

public class ProxyHelper implements InitializingBean{

	private Map<Long, Proxy> map = new HashMap<Long, Proxy>();
	
	public Proxy get(long id){
		return map.get(id);
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		init();
	}
	
	private void init(){
		List<Proxy> list = proxyHandler.getAll();
		
		if(list != null && list.size() > 0){
			for(Proxy proxy : list){
				map.put(proxy.getId(), proxy);
			}
		}
	}
	
	//ioc
	private ProxyHandler proxyHandler;

	public void setProxyHandler(ProxyHandler proxyHandler) {
		this.proxyHandler = proxyHandler;
	}
	
	

}
