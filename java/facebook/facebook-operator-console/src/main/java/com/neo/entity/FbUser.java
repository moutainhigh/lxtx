package com.neo.entity;

import java.math.BigInteger;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "user")
public class FbUser {
	@javax.persistence.Id
	@GeneratedValue
	private BigInteger Id;
	@Column(name = "username")
	private String userName;
	@Column(name = "userpass")
	private String userPass;
	@Column(name = "country")
	private String country;
	@Column(name = "birthday")
	private String birthDay;
	@Column(name = "friends")
	private Long friends;
	@Column(name = "createday")
	private Long createDay;
	@Column(name = "loginday")
	private Long LoginDay;
	@Column(name = "settingday")
	private Long SettingDay;
	@Column(name = "pageday")
	private Long PageDay;
	@Column(name = "lastactiveday")
	private Long LastActiveDay;
	@Column(name = "adday")
	private Long AdDay;
	@Column(name = "adflagday")
	private Long AdFlagDay;
	@Column(name = "adflagcontactday")
	private Long AdFlagContactDay;
	@Column(name = "adpassday")
	private Long AdPassDay;
	@Column(name = "addeliveryday")
	private Long AdDeliveryDay;
	@Column(name = "invalidday")
	private Long InvalidDay;
	@Column(name = "status")
	private Long status;
	@Column(name = "cookies")
	private String cookies;
	@Column(name = "pageurl")
	private String pageUrl;
	@Column(name = "pixel")
	private String pixel;
	@Column(name = "language")
	private String language;
	@Column(name = "categoryid")
	private Long CategoryId;
	@Column(name = "uaid")
	private Long UaId;
	@Column(name = "cookieupdate")
	private Boolean CookieUpdate;
	// @Column(name = "machineid")
	// private Long machineId;
	// @Column(name = "machineuserid")
	// private BigInteger machineUserId;
	@Column(name = "lastoptime")
	private BigInteger lastOpTime;
	@Column(name = "tasknum")
	private Long taskNum;
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "machineid")
	private Machine machine;
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "machineuserid")
	private MachineUser machineUser;
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "proxyid")
	private Proxy proxy;

	public BigInteger getId() {
		return Id;
	}

	public void setId(BigInteger id) {
		Id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserPass() {
		return userPass;
	}

	public void setUserPass(String userPass) {
		this.userPass = userPass;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getBirthDay() {
		return birthDay;
	}

	public void setBirthDay(String birthDay) {
		this.birthDay = birthDay;
	}

	public Long getFriends() {
		return friends;
	}

	public void setFriends(Long friends) {
		this.friends = friends;
	}

	public Long getCreateDay() {
		return createDay;
	}

	public void setCreateDay(Long createDay) {
		this.createDay = createDay;
	}

	public Long getLoginDay() {
		return LoginDay;
	}

	public void setLoginDay(Long loginDay) {
		LoginDay = loginDay;
	}

	public Long getSettingDay() {
		return SettingDay;
	}

	public void setSettingDay(Long settingDay) {
		SettingDay = settingDay;
	}

	public Long getPageDay() {
		return PageDay;
	}

	public void setPageDay(Long pageDay) {
		PageDay = pageDay;
	}

	public Long getLastActiveDay() {
		return LastActiveDay;
	}

	public void setLastActiveDay(Long lastActiveDay) {
		LastActiveDay = lastActiveDay;
	}

	public Long getAdDay() {
		return AdDay;
	}

	public void setAdDay(Long adDay) {
		AdDay = adDay;
	}

	public Long getAdFlagDay() {
		return AdFlagDay;
	}

	public void setAdFlagDay(Long adFlagDay) {
		AdFlagDay = adFlagDay;
	}

	public Long getAdFlagContactDay() {
		return AdFlagContactDay;
	}

	public void setAdFlagContactDay(Long adFlagContactDay) {
		AdFlagContactDay = adFlagContactDay;
	}

	public Long getAdPassDay() {
		return AdPassDay;
	}

	public void setAdPassDay(Long adPassDay) {
		AdPassDay = adPassDay;
	}

	public Long getAdDeliveryDay() {
		return AdDeliveryDay;
	}

	public void setAdDeliveryDay(Long adDeliveryDay) {
		AdDeliveryDay = adDeliveryDay;
	}

	public Long getInvalidDay() {
		return InvalidDay;
	}

	public void setInvalidDay(Long invalidDay) {
		InvalidDay = invalidDay;
	}

	public Long getStatus() {
		return status;
	}

	public void setStatus(Long status) {
		this.status = status;
	}

	public String getCookies() {
		return cookies;
	}

	public void setCookies(String cookies) {
		this.cookies = cookies;
	}

	public String getPageUrl() {
		return pageUrl;
	}

	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}

	public String getPixel() {
		return pixel;
	}

	public void setPixel(String pixel) {
		this.pixel = pixel;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public Long getCategoryId() {
		return CategoryId;
	}

	public void setCategoryId(Long categoryId) {
		CategoryId = categoryId;
	}

	public Long getUaId() {
		return UaId;
	}

	public void setUaId(Long uaId) {
		UaId = uaId;
	}

	public Boolean getCookieUpdate() {
		return CookieUpdate;
	}

	public void setCookieUpdate(Boolean cookieUpdate) {
		CookieUpdate = cookieUpdate;
	}

	// public Long getMachineId() {
	// return machineId;
	// }
	//
	// public void setMachineId(Long machineId) {
	// this.machineId = machineId;
	// }
	//
	// public BigInteger getMachineUserId() {
	// return machineUserId;
	// }
	//
	// public void setMachineUserId(BigInteger machineUserId) {
	// this.machineUserId = machineUserId;
	// }

	public BigInteger getLastOpTime() {
		return lastOpTime;
	}

	public void setLastOpTime(BigInteger lastOpTime) {
		this.lastOpTime = lastOpTime;
	}

	public Machine getMachine() {
		return machine;
	}

	public void setMachine(Machine machine) {
		this.machine = machine;
	}

	public MachineUser getMachineUser() {
		return machineUser;
	}

	public void setMachineUser(MachineUser machineUser) {
		this.machineUser = machineUser;
	}

	public Proxy getProxy() {
		return proxy;
	}

	public void setProxy(Proxy proxy) {
		this.proxy = proxy;
	}

	public Long getTaskNum() {
		return taskNum;
	}

	public void setTaskNum(Long taskNum) {
		this.taskNum = taskNum;
	}
}
