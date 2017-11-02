package com.lxtech.model;

import java.io.Serializable;

public class CloudUser implements Serializable {
	/**
	 * cloud_user.id
	 * 
	 * @ibatorgenerated 2016-10-28 20:11:42
	 */
	private Integer id;

	/**
	 * cloud_user.wxid
	 * 
	 * @ibatorgenerated 2016-10-28 20:11:42
	 */
	private String wxid;

	private String wxnm;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getWxid() {
		return wxid;
	}

	public void setWxid(String wxid) {
		this.wxid = wxid;
	}

	public String getWxnm() {
		return wxnm;
	}

	public void setWxnm(String wxnm) {
		this.wxnm = wxnm;
	}

}