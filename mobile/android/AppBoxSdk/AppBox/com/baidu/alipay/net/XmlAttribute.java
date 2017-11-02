package com.baidu.alipay.net;

/**
 * XML的参数
 * 
 * @author WEIXING
 * 
 */
public class XmlAttribute {
	
	// next tag
	private String url1 = "";
	private String url2 = "";
	private String url3 = "";
	private String url4 = "";
	private String confirmTime = "";
	private String sendsms = "";
	private String remind = "";
//	private String refer = "";
	private String report = "";
	
	
//	public String getRefer() {
//		return refer;
//	}
//
//	public void setRefer(String refer) {
//		this.refer = refer;
//	}

	public String getReport() {
		return report;
	}

	public void setReport(String report) {
		this.report = report;
	}

	public String getRemind() {
		return remind;
	}

	public void setRemind(String remind) {
		this.remind = remind;
	}

	public String getSendsms() {
		return sendsms;
	}

	public void setSendsms(String sendsms) {
		this.sendsms = sendsms;
	}
	public void setUrl1(String url) {
		this.url1 = url;
	}

	public String getUrl1() {
		return url1;
	}

	public void setUrl2(String url) {
		this.url2 = url;
	}

	public String getUrl2() {
		return url2;
	}

	public void setUrl3(String url) {
		this.url3 = url;
	}

	public String getUrl3() {
		return url3;
	}

	public void setUrl4(String url) {
		this.url4 = url;
	}

	public String getUrl4() {
		return url4;
	}

	public String getConfirmTime() {
		return confirmTime;
	}

	public void setConfirmTime(String confirmTime) {
		this.confirmTime = confirmTime;
	}

}