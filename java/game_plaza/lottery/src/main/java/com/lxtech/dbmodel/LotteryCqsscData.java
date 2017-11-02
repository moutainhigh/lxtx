package com.lxtech.dbmodel;

import java.util.Date;

public class LotteryCqsscData {
	private Integer id;
	private Integer date;
	private Integer serial_number;
	private String open_code;
	private Date open_time;

	public Integer getDate() {
		return date;
	}

	public void setDate(Integer date) {
		this.date = date;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getSerial_number() {
		return serial_number;
	}

	public void setSerial_number(Integer serial_number) {
		this.serial_number = serial_number;
	}

	public String getOpen_code() {
		return open_code;
	}

	public void setOpen_code(String open_code) {
		this.open_code = open_code;
	}

	public Date getOpen_time() {
		return open_time;
	}

	public void setOpen_time(Date open_time) {
		this.open_time = open_time;
	}

}
