package com.lxtx.fb.task.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.Cookie;

import com.lxtx.fb.handler.UserHandler;
import com.lxtx.fb.pojo.User;

public class LoginService {


	public void updateLanguage(User user) {
		
		userHandler.updateLanguage(user);
	}
	
	
	public void saveCookies(User user, Set<Cookie> cookies){
		
		try{

			Map<String, Cookie> map = new HashMap<String, Cookie>();
			
			for(Cookie cookie : cookies){
				map.put(cookie.getName(), cookie);
			}
			
			JSONArray ja = new JSONArray();
			
			if(user.getCookies() != null && user.getCookies().length() > 0){
				ja = new JSONArray(user.getCookies());
			}
			
			JSONArray ja1 = new JSONArray();
			
			if(ja.length() > 0){
				for(int i = 0; i < ja.length(); i ++){
					JSONObject jo = ja.getJSONObject(i);
					
					String name = jo.getString("name");
					
					Cookie cookie = map.get(name);
					
					if(cookie != null){
						jo.put("value", cookie.getValue());
						jo.put("expirationDate", ((double)cookie.getExpiry().getTime())/1000);
					}
					
					ja1.put(jo);
				}
			}
			
			user.setCookies(ja1.toString());
			user.setCookieUpdate(true);
			
			userHandler.updateCookies(user);
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	
	//ioc
	private UserHandler userHandler;

	public void setUserHandler(UserHandler userHandler) {
		this.userHandler = userHandler;
	}

	
	
}
