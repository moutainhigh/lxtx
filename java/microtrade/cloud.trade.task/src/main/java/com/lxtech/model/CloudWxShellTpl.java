package com.lxtech.model;

public class CloudWxShellTpl {
	private Integer id;
	private Integer shell_id;
	private String tpl_id;
	private Integer send_hour;
	private Integer status;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getShell_id() {
		return shell_id;
	}

	public void setShell_id(Integer shell_id) {
		this.shell_id = shell_id;
	}

	public String getTpl_id() {
		return tpl_id;
	}

	public void setTpl_id(String tpl_id) {
		this.tpl_id = tpl_id;
	}

	public Integer getSend_hour() {
		return send_hour;
	}

	public void setSend_hour(Integer send_hour) {
		this.send_hour = send_hour;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}
