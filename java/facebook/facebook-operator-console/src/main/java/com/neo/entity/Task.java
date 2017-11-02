package com.neo.entity;

import java.math.BigInteger;
import java.sql.Timestamp;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Task")
public class Task {
	@javax.persistence.Id
	@GeneratedValue
	private long Id;
//	@Column(name = "userid")
//	private Long UserId;
	@Column(name = "type")
	private Integer Type;
	@Column(name = "day")
	private Long Day;
	@Column(name = "data")
	private String Data;
	@Column(name = "status")
	private Integer Status;
	@Column(name = "Country")
	private String Country;
	@Column(name = "updatetime")
	private Timestamp UpdateTime;
	@Column(name = "starttime")
	private BigInteger StartTime;
	@Column(name = "machineid")
	private Long MachineId;
	@Column(name = "csuserid")
	private Long CsUserId;
	@Column(name = "errorreason")
	private String ErrorReason;
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "userid")
	private FbUser fbUser;

	private String taskType;
	
	public long getId() {
		return Id;
	}

	public void setId(long id) {
		Id = id;
	}

//	public Long getUserId() {
//		return UserId;
//	}
//
//	public void setUserId(Long userId) {
//		UserId = userId;
//	}

	public Integer getType() {
		return Type;
	}

	public void setType(Integer type) {
		Type = type;
	}

	public Long getDay() {
		return Day;
	}

	public void setDay(Long day) {
		Day = day;
	}

	public String getData() {
		return Data;
	}

	public void setData(String data) {
		Data = data;
	}

	public Integer getStatus() {
		return Status;
	}

	public void setStatus(Integer status) {
		Status = status;
	}

	public String getCountry() {
		return Country;
	}

	public void setCountry(String country) {
		Country = country;
	}

	public Timestamp getUpdateTime() {
		return UpdateTime;
	}

	public void setUpdateTime(Timestamp updateTime) {
		UpdateTime = updateTime;
	}

	public BigInteger getStartTime() {
		return StartTime;
	}

	public void setStartTime(BigInteger startTime) {
		StartTime = startTime;
	}

	public Long getMachineId() {
		return MachineId;
	}

	public void setMachineId(Long machineId) {
		MachineId = machineId;
	}

	public Long getCsUserId() {
		return CsUserId;
	}

	public void setCsUserId(Long csUserId) {
		CsUserId = csUserId;
	}

	public String getErrorReason() {
		return ErrorReason;
	}

	public void setErrorReason(String errorReason) {
		ErrorReason = errorReason;
	}

	public FbUser getFbUser() {
		return fbUser;
	}

	public void setFbUser(FbUser fbUser) {
		this.fbUser = fbUser;
	}

	public String getTaskType() {
		return taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
	}
}
