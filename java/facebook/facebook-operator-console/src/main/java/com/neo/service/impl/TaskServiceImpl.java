package com.neo.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers;
import org.springframework.stereotype.Service;

import com.neo.entity.FbUser;
import com.neo.entity.FilterWords;
import com.neo.entity.Task;
import com.neo.repository.FilterWordsRepository;
import com.neo.repository.TaskRepository;
import com.neo.service.TaskService;

@Service
public class TaskServiceImpl implements TaskService{

    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private FilterWordsRepository wordsRepository;
	
	@Override
	public List<Task> getTaskList(long operatorId) {
		Task task = new Task();
		task.setCsUserId(operatorId);
		task.setStatus(0);
		
		ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("CsUserId", GenericPropertyMatchers.exact()).withMatcher("Status", GenericPropertyMatchers.exact()).withIgnorePaths("Id");
		Example<Task> example = Example.of(task, matcher);
		return taskRepository.findAll(example);
	}
	
	@Override
	public void updateTaskStatus(long taskId, int status) {
		Task task = this.taskRepository.findOne(taskId);
		task.setStatus(status);
		taskRepository.saveAndFlush(task);
	}
	
	@Override
	public void updateTaskStatusAndReason(long taskId, int status, String reason) {
		Task task = this.taskRepository.findOne(taskId);
		task.setStatus(status);
		if (reason != null && !reason.equals("")) {
			task.setErrorReason(reason);
		}
		taskRepository.saveAndFlush(task);

		if(status == -1 || status == -2) { //update all related tasks
			Task matchTask = new Task();
			FbUser fUser = new FbUser();
			fUser.setId(task.getFbUser().getId());
			matchTask.setFbUser(fUser);
			matchTask.setStatus(0);
			ExampleMatcher matcher = ExampleMatcher.matching().withIgnorePaths("Id");
			Example<Task> example = Example.of(matchTask, matcher);
			
			List<Task> matchedTasks = taskRepository.findAll(example);
			if (matchedTasks.size() > 0) {
				for (Task t : matchedTasks) {
					task = this.taskRepository.findOne(t.getId());
					task.setStatus(status);
					taskRepository.save(task);
				}
				taskRepository.flush();
			}
		}
	}
	

	@Override
	public Task getTaskById(long taskId) {
		return this.taskRepository.findOne(taskId);
	}

	@Override
	public void saveTask(Task task) {
		this.taskRepository.saveAndFlush(task);
	}

	private void fillMap(Map map, String key, Object obj) {
		map.put(key, obj);
	}
	
	@Override
	public Map<String, FilterWords> fetchAllFilterWords() {
		List<FilterWords> list = wordsRepository.findAll();
		Map<String, FilterWords> wordsMap = new HashMap<String, FilterWords>();
		list.stream().forEach(words -> this.fillMap(wordsMap, words.getCountry(), words));
		return wordsMap;
	}
}
