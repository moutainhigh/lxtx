package com.neo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.neo.entity.Task;

public interface TaskRepository extends JpaRepository<Task, Long>{
	
}
