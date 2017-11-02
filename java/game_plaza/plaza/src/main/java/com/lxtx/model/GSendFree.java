package com.lxtx.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GSendFree implements Serializable {
	/**
	 * g_send_free.id
	 * 
	 * @ibatorgenerated 2017-01-20 15:11:07
	 */
	private Integer id;

	/**
	 * g_send_free.uid
	 * 
	 * @ibatorgenerated 2017-01-20 15:11:07
	 */
	private Integer uid;

	/**
	 * g_send_free.free_count (赠送次数)
	 * 
	 * @ibatorgenerated 2017-01-20 15:11:07
	 */
	private Integer freeCount;

	private int money;

	/**
	 * g_send_free.date (日期：一日送一次)
	 * 
	 * @ibatorgenerated 2017-01-20 15:11:07
	 */
	private Date date;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}

	public Integer getFreeCount() {
		return freeCount;
	}

	public void setFreeCount(Integer freeCount) {
		this.freeCount = freeCount;
	}

	public Date getDate() {
		return date;
	}

	public String getDate1() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (date != null) {
			return sdf.format(date);
		}
		return "";
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

}