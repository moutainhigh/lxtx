package com.lxtx.fb.task.service;

import org.springframework.beans.factory.InitializingBean;

import com.lxtx.fb.Config;
import com.lxtx.fb.helper.AdminHelper;
import com.lxtx.fb.task.util.CommonUtil;

public class ConfigService implements InitializingBean{

	@Override
	public void afterPropertiesSet() throws Exception {
		CommonUtil.initAdmin(adminHelper.get(Config.getOpMachineId()));
	}
	

	//ioc
	private AdminHelper adminHelper;

	public void setAdminHelper(AdminHelper adminHelper) {
		this.adminHelper = adminHelper;
	}
	
	
}
