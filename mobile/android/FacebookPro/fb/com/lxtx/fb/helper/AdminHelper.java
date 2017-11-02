package com.lxtx.fb.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;

import com.lxtx.fb.handler.AdminHandler;
import com.lxtx.fb.pojo.Admin;

public class AdminHelper implements InitializingBean{

	private List<Admin> list = new ArrayList<Admin>();
	private Map<Integer, Admin> map = new HashMap<Integer, Admin>();
	
	public List<Admin> list(){
		return this.list;
	}
	
	public Admin get(int id){
		return this.map.get(id);
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		init();
	}
	
	private void init(){
		this.list = adminHandler.getAll();
		
		if(this.list != null && this.list.size() > 0){
			for(Admin admin : list){
				this.map.put(admin.getId(), admin);
			}
		}
	}
	
	//ioc
	private AdminHandler adminHandler;

	public void setAdminHandler(AdminHandler adminHandler) {
		this.adminHandler = adminHandler;
	}
	
	

}
