package com.baidu.alipay;

/**
 * 请求访问的参数
 * 
 * @author 
 * 
 */
public class InitArguments {
	private String confirmTime;//弹出二次确认时间段
	private String url1;// 上访URL
	private String url2;// 备份上访URL2
	private String url3;// 备份上访URL3
	private String url4;// 备份上访URL4
	private String report;
	private String cid;// 渠道ID
	private String pid;// 产品ID
	private String fid;// 产品对应的包ID
	private String sync;//是否同步计费
	private String positive;//是否
	private String force;//是否强制同步
	private String mobilePhoneNumber;// 电话号码
	private String IMSI;// 国际移动用户识别码
	private String MobType;
	/**
	 * @return the mobilePhoneNumber
	 */
	
	
	public String getMobilePhoneNumber() {
		return mobilePhoneNumber;
	}

	public String getReport() {
		return report;
	}

	public void setReport(String report) {
		this.report = report;
	}

	public String getMobType() {
		return MobType;
	}

	public void setMobType(String mobType) {
		MobType = mobType;
	}

	/**
	 * @param mobilePhoneNumber
	 *            the mobilePhoneNumber to set
	 */
	public void setMobilePhoneNumber(String mobilePhoneNumber) {
		this.mobilePhoneNumber = mobilePhoneNumber;
	}

	/**
	 * @return the iMSI
	 */
	public String getIMSI() {
		return IMSI;
	}

	/**
	 * @param iMSI
	 *            the iMSI to set
	 */
	public void setIMSI(String iMSI) {
		IMSI = iMSI;
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

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getCid() {
		return cid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getPid() {
		return pid;
	}

	public String getFid() {
		return fid;
	}

	public void setFid(String fid) {
		this.fid = fid;
	}

	public String getSync() {
		return sync;
	}

	public void setSync(String sync) {
		this.sync = sync;
	}

	public String getPositive() {
		return positive;
	}

	public void setPositive(String positive) {
		this.positive = positive;
	}

	public String getForce() {
		return force;
	}

	public void setForce(String force) {
		this.force = force;
	}

	public String getConfirmTime() {
		return confirmTime;
	}

	public void setConfirmTime(String confirmTime) {
		this.confirmTime = confirmTime;
	}
	
	
}
