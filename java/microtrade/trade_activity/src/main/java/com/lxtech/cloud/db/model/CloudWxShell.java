package com.lxtech.cloud.db.model;

public class CloudWxShell {

	private long id;
	
	private String app_id;
	
	private String app_secret;
	
	private String app_name;
	
	private String chnno;
	
	private String domain;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getApp_id() {
		return app_id;
	}

	public void setApp_id(String app_id) {
		this.app_id = app_id;
	}

	public String getApp_secret() {
		return app_secret;
	}

	public void setApp_secret(String app_secret) {
		this.app_secret = app_secret;
	}

	public String getApp_name() {
		return app_name;
	}

	public void setApp_name(String app_name) {
		this.app_name = app_name;
	}

	public String getChnno() {
		return chnno;
	}

	public void setChnno(String chnno) {
		this.chnno = chnno;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	
	
}
