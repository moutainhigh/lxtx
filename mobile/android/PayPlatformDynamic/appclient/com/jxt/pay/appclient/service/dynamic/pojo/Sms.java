package com.jxt.pay.appclient.service.dynamic.pojo;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("sms")
public class Sms {

	public static final String SENDTYPE_DATA = "2";//二进制发送
	
	@XStreamAlias("smsdest")
	private String smsDest;
	@XStreamAlias("smscontent")
	private String smsContent;
	@XStreamAlias("successtimeout")
	private Integer successTimeOut;
	@XStreamImplicit(itemFieldName="guard")	
	private List<Guard> guardList;
	@XStreamAlias("sendtype")
	private String sendType;
	@XStreamAlias("waitguard")
	private String waitGuard; 
	
	public String getSmsDest() {
		return smsDest;
	}
	public void setSmsDest(String smsDest) {
		this.smsDest = smsDest;
	}
	public String getSmsContent() {
		return smsContent;
	}
	public void setSmsContent(String smsContent) {
		this.smsContent = smsContent;
	}
	public Integer getSuccessTimeOut() {
		return successTimeOut;
	}
	public void setSuccessTimeOut(Integer successTimeOut) {
		this.successTimeOut = successTimeOut;
	}
	public List<Guard> getGuardList() {
		return guardList;
	}
	public void setGuardList(List<Guard> guardList) {
		this.guardList = guardList;
	}
	public String getSendType() {
		return sendType;
	}
	public void setSendType(String sendType) {
		this.sendType = sendType;
	}
	public String getWaitGuard() {
		return waitGuard;
	}
	public void setWaitGuard(String waitGuard) {
		this.waitGuard = waitGuard;
	}
	
}
