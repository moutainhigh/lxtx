package com.jxt.pay.appclient.service.dynamic.pojo;

import com.thoughtworks.xstream.annotations.XStreamAlias;

public class Guard {
	@XStreamAlias("smsnoleft")
	private String smsNoLeft;
	@XStreamAlias("guardcontent")
	private String guardContent;
	@XStreamAlias("guardtimeout")
	private Integer guardTimeOut;
	@XStreamAlias("islong")
	private Integer isLong;
	@XStreamAlias("recvsuccess")
	private String recvSuccess;
	
	public Guard(){
		
	}
	
	public Guard(String smsNoLeft,String guardContent,Integer guardTimeOut,String recvSuccess,Integer isLong){
		this.smsNoLeft = smsNoLeft;
		this.guardContent = guardContent;
		this.guardTimeOut = guardTimeOut;
		this.recvSuccess = recvSuccess;
		this.isLong = isLong;
	}

	public String getSmsNoLeft() {
		return smsNoLeft;
	}

	public void setSmsNoLeft(String smsNoLeft) {
		this.smsNoLeft = smsNoLeft;
	}

	public String getGuardContent() {
		return guardContent;
	}

	public void setGuardContent(String guardContent) {
		this.guardContent = guardContent;
	}

	public Integer getGuardTimeOut() {
		return guardTimeOut;
	}

	public void setGuardTimeOut(Integer guardTimeOut) {
		this.guardTimeOut = guardTimeOut;
	}

	public Integer getIsLong() {
		return isLong;
	}

	public void setIsLong(Integer isLong) {
		this.isLong = isLong;
	}

	public String getRecvSuccess() {
		return recvSuccess;
	}

	public void setRecvSuccess(String recvSuccess) {
		this.recvSuccess = recvSuccess;
	}
	
	
}
