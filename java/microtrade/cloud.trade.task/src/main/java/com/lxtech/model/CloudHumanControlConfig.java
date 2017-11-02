package com.lxtech.model;

import java.util.Date;

public class CloudHumanControlConfig {
	private Integer id;
	private Integer status;
	private String chnno;
	private Integer order_money;
	private Integer order_num;
	private Integer need_run_count;
	private Integer runed_count;
	private Integer uid;
	private Date run_time;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getChnno() {
		return chnno;
	}

	public void setChnno(String chnno) {
		this.chnno = chnno;
	}

	public Integer getOrder_money() {
		return order_money;
	}

	public void setOrder_money(Integer order_money) {
		this.order_money = order_money;
	}

	public Integer getOrder_num() {
		return order_num;
	}

	public void setOrder_num(Integer order_num) {
		this.order_num = order_num;
	}

	public Date getRun_time() {
		return run_time;
	}

	public void setRun_time(Date run_time) {
		this.run_time = run_time;
	}

	public Integer getNeed_run_count() {
		return need_run_count;
	}

	public void setNeed_run_count(Integer need_run_count) {
		this.need_run_count = need_run_count;
	}

	public Integer getRuned_count() {
		return runed_count;
	}

	public void setRuned_count(Integer runed_count) {
		this.runed_count = runed_count;
	}

	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}

}
