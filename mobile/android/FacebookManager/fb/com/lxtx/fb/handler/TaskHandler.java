package com.lxtx.fb.handler;

import com.lxtx.fb.pojo.Task;
import com.qlzf.commons.dao.Page;
import com.qlzf.commons.dao.PageCondition;
import com.qlzf.commons.handler.SimpleIbatisEntityHandler;

public class TaskHandler extends SimpleIbatisEntityHandler<Task>{

	public void updateStatus(Task task) {
		update("updateStatus", task);
	}
	
	public Page<Task> pagedByCsUser(PageCondition condition){
		return pagedQuery(condition, "cntByCsUser", "listByCsUser");
	}

}
