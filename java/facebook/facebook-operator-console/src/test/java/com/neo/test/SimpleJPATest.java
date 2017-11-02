package com.neo.test;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import com.neo.entity.FbUser;
import com.neo.entity.Machine;
import com.neo.entity.MachineUser;
import com.neo.entity.Task;
import com.neo.repository.TaskRepository;

@RunWith(SpringRunner.class)
@DataJpaTest
public class SimpleJPATest {

	@Autowired
	private TestEntityManager entityManager;
	
	@Autowired
	private TaskRepository taskRepository;
	
	@Test
	public void testListTask() {
//		List<Task> taskList = this.taskService.getTaskList(1);
//		System.out.println(taskList.size());
		Machine machine = new Machine();
		machine.setIp("127.0.0.1");
		
		MachineUser user = new MachineUser();
		user.setMachineId(1l);
		user.setName("admin1");
		user.setStatus(1);
		
		FbUser fbUser = new FbUser();
		fbUser.setMachine(machine);
		fbUser.setMachineUser(user);
		
		Task task = new Task();
		task.setFbUser(fbUser);
		this.taskRepository.save(task);
		long taskId = task.getId();
		System.out.println(task.getId());
		System.out.println(this.taskRepository.count());
		Task foundTask = this.taskRepository.getOne(taskId);
		foundTask = this.taskRepository.findOne(taskId);
		System.out.println("hello");
	} 
	
}
