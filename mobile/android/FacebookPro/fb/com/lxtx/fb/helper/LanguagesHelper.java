package com.lxtx.fb.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;

import com.lxtx.fb.handler.LanguagesHandler;
import com.lxtx.fb.pojo.Languages;

public class LanguagesHelper implements InitializingBean{

	private Map<String, Languages> map = new HashMap<String, Languages>();
	
	public Languages get(String country){
		return map.get(country);
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		init();
	}
	
	private void init(){
		List<Languages> list = languagesHandler.getAll();
		
		if(list != null && list.size() > 0){
			for(Languages languages : list){
				map.put(languages.getCountry(), languages);
			}
		}
	}
	
	//ioc
	private LanguagesHandler languagesHandler;

	public void setLanguagesHandler(LanguagesHandler languagesHandler) {
		this.languagesHandler = languagesHandler;
	}
	
	

}
