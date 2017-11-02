package com.lxtx.fb.handler;

import com.lxtx.fb.pojo.Machine;
import com.qlzf.commons.dao.Page;
import com.qlzf.commons.dao.PageCondition;
import com.qlzf.commons.handler.SimpleIbatisEntityHandler;

public class MachineHandler extends SimpleIbatisEntityHandler<Machine>{

	
	
	public void updateNum(Machine machine){
		update("updateNum", machine);
	}
	
	public Page<Machine> pagedByCountry(PageCondition condition) {
		return pagedQuery(condition, "cntByCountry", "listByCountry");
	}

}
