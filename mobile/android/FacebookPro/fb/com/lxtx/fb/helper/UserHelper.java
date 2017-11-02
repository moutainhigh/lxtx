package com.lxtx.fb.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;

import com.lxtx.fb.handler.UserHandler;
import com.lxtx.fb.pojo.User;

public class UserHelper implements InitializingBean{

	private Map<Long, User> map = new HashMap<Long, User>();
	
	public User get(long userId){
		return map.get(userId);
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		init();
	}
	
	private void init(){
		List<User> list = userHandler.getAll();
				
		if(list != null && list.size() > 0){
			for(User user : list){
				map.put(user.getId(), user);
			}
		}
	}

	//ioc
	private UserHandler userHandler;

	public void setUserHandler(UserHandler userHandler) {
		this.userHandler = userHandler;
	}
	
	
	
}
