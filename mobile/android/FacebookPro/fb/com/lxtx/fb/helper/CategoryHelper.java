package com.lxtx.fb.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;

import com.lxtx.fb.handler.CategoryHandler;
import com.lxtx.fb.pojo.Category;

public class CategoryHelper implements InitializingBean{

	private List<Category> list = new ArrayList<Category>();
	private Map<Integer, Category> map = new HashMap<Integer, Category>();
	
	public List<Category> list(){
		return this.list;
	}
	
	public Category get(Integer categoryId){
		return this.map.get(categoryId);
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		init();	
	}
	
	private void init(){
		
		this.list = categoryHandler.getAll();
	
		if(this.list != null && this.list.size() > 0){
			for(Category category : this.list){
				map.put(category.getId(), category);
			}
		}
	}
	
	//ioc
	private CategoryHandler categoryHandler;

	public void setCategoryHandler(CategoryHandler categoryHandler) {
		this.categoryHandler = categoryHandler;
	}
	
	

}
