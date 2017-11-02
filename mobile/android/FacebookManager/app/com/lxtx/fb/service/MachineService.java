package com.lxtx.fb.service;

import com.lxtx.fb.handler.MachineHandler;
import com.lxtx.fb.handler.MachineUserHandler;
import com.lxtx.fb.pojo.Machine;
import com.qlzf.commons.dao.Page;
import com.qlzf.commons.dao.PageCondition;

public class MachineService {

	/**
	 * 根据国家查找
	 * @param country
	 * @param condition
	 * @return
	 */
	public Page<Machine> pageMachine(String country, int status, PageCondition condition){
		if(country != null && country.length() > 0){
			condition.put("country", country);
		}
		if(status != 0){
			condition.put("status", status);
		}
		
		return machineHandler.pagedByCountry(condition);
	}
	
	
	//ioc
	private MachineHandler machineHandler;
	
	private MachineUserHandler machineUserHandler;

	public void setMachineHandler(MachineHandler machineHandler) {
		this.machineHandler = machineHandler;
	}

	public void setMachineUserHandler(MachineUserHandler machineUserHandler) {
		this.machineUserHandler = machineUserHandler;
	}
	
	
}
