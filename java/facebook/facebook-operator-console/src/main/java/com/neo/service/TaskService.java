package com.neo.service;

import java.util.List;
import java.util.Map;

import com.neo.entity.FilterWords;
import com.neo.entity.Task;

public interface TaskService {
	public List<Task> getTaskList(long operatorId);
	
	public Task getTaskById(long taskId);

	public void saveTask(Task task);
	
	public void updateTaskStatus(long taskId, int status);
	
	public void updateTaskStatusAndReason(long taskId, int status, String reason);
	
	public Map<String, FilterWords> fetchAllFilterWords();
}
