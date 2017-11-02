package com.lxtx.fb.handler;

import com.lxtx.fb.pojo.MachineUser;
import com.qlzf.commons.dao.Page;
import com.qlzf.commons.dao.PageCondition;
import com.qlzf.commons.handler.SimpleIbatisEntityHandler;

public class MachineUserHandler extends SimpleIbatisEntityHandler<MachineUser>{

	public void updateStatus(MachineUser machineUser){
		update("updateStatus", machineUser);
	}
	
	
	public Page<MachineUser> pagedByMachineId(PageCondition condition){
		return pagedQuery(condition, "cntByMachineId", "listByMachineId");
	}
	
}
