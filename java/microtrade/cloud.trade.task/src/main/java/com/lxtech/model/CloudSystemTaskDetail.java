package com.lxtech.model;

import java.io.Serializable;
import java.util.Date;

public class CloudSystemTaskDetail implements Serializable {
	/**
	 * cloud_system_task_detail.id
	 * 
	 * @ibatorgenerated 2016-11-14 16:05:03
	 */
	private Integer id;

	/**
	 * cloud_system_task_detail.exec_day (执行日期)
	 * 
	 * @ibatorgenerated 2016-11-14 16:05:03
	 */
	private String execDay;

	/**
	 * cloud_system_task_detail.exec_time (执行时间)
	 * 
	 * @ibatorgenerated 2016-11-14 16:05:03
	 */
	private String execTime;

	/**
	 * cloud_system_task_detail.detail (详情)
	 * 
	 * @ibatorgenerated 2016-11-14 16:05:03
	 */
	private String detail;

	/**
	 * cloud_system_task_detail.task_nm (任务名)
	 * 
	 * @ibatorgenerated 2016-11-14 16:05:03
	 */
	private String taskNm;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getExecDay() {
		return execDay;
	}

	public void setExecDay(String execDay) {
		this.execDay = execDay;
	}

	public String getExecTime() {
		return execTime;
	}

	public void setExecTime(String execTime) {
		this.execTime = execTime;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getTaskNm() {
		return taskNm;
	}

	public void setTaskNm(String taskNm) {
		this.taskNm = taskNm;
	}
}