package com.jxt.pay.pojo;

/**
 * 手机黑名单
 * @author leoliu
 *
 */
public class BlackMobile {

	private long id;
	
	private String mobile;
	
	private String prefix;
	
	private String version;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
}
