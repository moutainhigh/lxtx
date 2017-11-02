package com.lxtx.fb.task.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.lxtx.fb.handler.ProxyHandler;
import com.lxtx.fb.handler.UserHandler;
import com.lxtx.fb.pojo.Proxy;
import com.lxtx.fb.pojo.User;

/**
 * 给用户自动分配代理
 * @author thinkpad
 *
 */
public class UserProxyService {

	public boolean exec(){
		
		List<User> userList = userHandler.listNoProxy();
		
		if(userList != null && userList.size() > 0){
			
			Map<String,List<Proxy>> proxyMap = getProxyMap();
			
			for(User user : userList){ 
				
				String country = user.getCountry();
				
				Proxy proxy = getOne(proxyMap, country);
				
				if(proxy != null){
					user.setProxyId(proxy.getId());
					
					userHandler.updateProxy(user);
					
					if(proxy.getStatus() == 1){
						proxy.setStatus(2);
						
						proxyHandler.updateStatus(proxy);
					}
				}
			}
			
			return userList.size() == 100;
		}
		
		return false;
	}
	
	private Map<String,List<Proxy>> getProxyMap(){
		Map<String, List<Proxy>> map = new HashMap<String, List<Proxy>>();
		
		List<Proxy> proxyList = proxyHandler.getAll();
		
		if(proxyList != null && proxyList.size() > 0){
			for(Proxy proxy : proxyList){
				String country = proxy.getCountry();
				
				List<Proxy> list = map.get(country);
				if(list == null){
					list = new ArrayList<>();
					map.put(country, list);
				}
				
				list.add(proxy);
			}
		}
		
		return map;
	}
	
	private Proxy getOne(Map<String,List<Proxy>> proxyMap, String country){
		
		if(proxyMap.size() > 0){
			
			List<Proxy> list = proxyMap.get(country);
			
			if(list != null && list.size() > 0){
				
				for(Proxy proxy : list){
					if(proxy.getStatus() == 1){
						return proxy;
					}
				}
				
				return list.get(new Random().nextInt(list.size()));
			}
		}
		
		return null;
	}
	
	//ioc
	private UserHandler userHandler;
	
	private ProxyHandler proxyHandler;

	public void setUserHandler(UserHandler userHandler) {
		this.userHandler = userHandler;
	}

	public void setProxyHandler(ProxyHandler proxyHandler) {
		this.proxyHandler = proxyHandler;
	}
	
	
}
