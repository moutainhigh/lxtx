package com.lxtx.fb.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;

import com.lxtx.fb.handler.FilterWordsHandler;
import com.lxtx.fb.pojo.FilterWords;

public class FilterWordsHelper implements InitializingBean{

	private Map<String, FilterWords> map = new HashMap<String, FilterWords>();
	
	public FilterWords get(String country){
		return map.get(country);
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		init();
	}
	
	private void init(){
		List<FilterWords> list = filterWordsHandler.getAll();
		
		if(list != null && list.size() > 0){
			for(FilterWords filterWords : list){
				map.put(filterWords.getCountry(), filterWords);
			}
		}
	}

	//ioc
	private FilterWordsHandler filterWordsHandler;

	public void setFilterWordsHandler(FilterWordsHandler filterWordsHandler) {
		this.filterWordsHandler = filterWordsHandler;
	}
	
	
	
}
