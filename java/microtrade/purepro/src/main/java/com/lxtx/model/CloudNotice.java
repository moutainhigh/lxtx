package com.lxtx.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CloudNotice implements Serializable {
	/**
	 * cloud_notice.id
	 * 
	 * @ibatorgenerated 2016-11-23 11:14:44
	 */
	private Integer id;

	/**
	 * cloud_notice.title
	 * 
	 * @ibatorgenerated 2016-11-23 11:14:44
	 */
	private String title;

	/**
	 * cloud_notice.content
	 * 
	 * @ibatorgenerated 2016-11-23 11:14:44
	 */
	private String content;

	/**
	 * cloud_notice.day
	 * 
	 * @ibatorgenerated 2016-11-23 11:14:44
	 */
	private Date day;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDay() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (day != null) {
			return sdf.format(day);
		}
		return "";
	}

	public void setDay(Date day) {
		this.day = day;
	}
}