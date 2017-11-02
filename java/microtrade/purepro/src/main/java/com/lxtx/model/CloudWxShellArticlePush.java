package com.lxtx.model;

public class CloudWxShellArticlePush {

	private long id;

	private String wxid;

	private long last_send_time;

	private int current_index;

	private String chnno;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getWxid() {
		return wxid;
	}

	public void setWxid(String wxid) {
		this.wxid = wxid;
	}

	public long getLast_send_time() {
		return last_send_time;
	}

	public void setLast_send_time(long last_send_time) {
		this.last_send_time = last_send_time;
	}

	public int getCurrent_index() {
		return current_index;
	}

	public void setCurrent_index(int current_index) {
		this.current_index = current_index;
	}

	public String getChnno() {
		return chnno;
	}

	public void setChnno(String chnno) {
		this.chnno = chnno;
	}

}
