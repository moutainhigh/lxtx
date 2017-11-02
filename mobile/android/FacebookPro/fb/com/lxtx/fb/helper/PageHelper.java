package com.lxtx.fb.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;

import com.lxtx.fb.handler.PageHandler;
import com.lxtx.fb.pojo.Page;

public class PageHelper implements InitializingBean{

	private Map<Long, Page> map = new HashMap<Long, Page>();
	
	public Page get(long id){
		return map.get(id);
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		init();
	}
	
	private void init(){
		List<Page> list = pageHandler.getAll();
		
		if(list != null && list.size() > 0){
			for(Page page : list){
				map.put(page.getId(), page);
			}
		}
	}

	//ioc
	private PageHandler pageHandler;

	public void setPageHandler(PageHandler pageHandler) {
		this.pageHandler = pageHandler;
	}
	
	
	
}
