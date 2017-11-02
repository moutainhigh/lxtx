package com.lxtx.fb.handler;

import java.util.List;

import com.lxtx.fb.Config;
import com.lxtx.fb.pojo.Task;
import com.qlzf.commons.handler.SimpleIbatisEntityHandler;

public class TaskHandler extends SimpleIbatisEntityHandler<Task>{

	public List<Task> listUnDeal(){
		
		Task task = new Task();
		task.setStartTime(System.currentTimeMillis());
		task.setMachineId(Config.getOpMachineId());
		
		return queryForList("listUnDeal", task);
	}
	
	public void updateStatus(Task task){
		update("updateStatus", task);
	}
}
