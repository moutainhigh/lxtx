package com.lxtx.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.lxtx.util.TimeUtil;

@SuppressWarnings("serial")
public class LotteryCqsscData implements Serializable {
	private Integer id;
	private Integer date;
	private Integer serialNumber;
	private String openCode;
	private Date openTime;
	private String openTimeStr;
	private List<String> codeList;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getDate() {
		return date;
	}

	public void setDate(Integer date) {
		this.date = date;
	}

	public Integer getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(Integer serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getOpenCode() {
		return openCode;
	}

	public void setOpenCode(String openCode) {
		this.openCode = openCode;
		
		this.codeList = Lists.newArrayList();
		for (String s: Splitter.on(",").split(this.openCode)) {
			this.codeList.add(s);
		}
	}

	public Date getOpenTime() {
		return openTime;
	}

	public void setOpenTime(Date openTime) {
		this.openTime = openTime;
		this.openTimeStr = TimeUtil.getTimeStr(this.openTime);
	}

	public List<String> getCodeList() {
		return codeList;
	}
	
	public String getOpenTimeStr() {
		return this.openTimeStr;
	}

}
