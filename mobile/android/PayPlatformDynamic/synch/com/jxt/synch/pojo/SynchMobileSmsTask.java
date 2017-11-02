package com.jxt.synch.pojo;

import java.util.Date;

public class SynchMobileSmsTask {

	private long id;
	
	private String key;
	
	private String mobile;
	
	private String spNumber;
	
	private String msg;
	
	private String linkId;
	
	private boolean succ;
	
	private String data;
	
	private int price;
	
	private Date synchTime;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getSpNumber() {
		return spNumber;
	}

	public void setSpNumber(String spNumber) {
		this.spNumber = spNumber;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getLinkId() {
		return linkId;
	}

	public void setLinkId(String linkId) {
		this.linkId = linkId;
	}

	public boolean isSucc() {
		return succ;
	}

	public void setSucc(boolean succ) {
		this.succ = succ;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public Date getSynchTime() {
		return synchTime;
	}

	public void setSynchTime(Date synchTime) {
		this.synchTime = synchTime;
	}
	
	
	
}
