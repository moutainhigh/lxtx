package com.lxtx.fb.pojo;

//用户
public class User {
	
	private long id;
	
	private String userName;
	
	private String userPass;
	
	private String country;//国家
	
	private String birthDay;
	
	private int friends;//好友人数
	
	private long proxyId;
	
	private int createDay;//初始化日期
	
	private int loginDay;//初次登陆日期
	
	private int settingDay;//初始化设置日期
	
	private int pageDay;//创建主页日期
	
	private int lastActiveDay;//上一次主页活跃日期
	
	private int adDay;//创建广告日期
	
	private int adFlagDay;//广告禁止日期
	
	private int adFlagContactDay;//广告申诉日期
	
	private int adPassDay;//广告通过日期
	
	private int adDeliveryDay;//广告投放日期
	
	private int invalidDay;//账号失效日期
	
	private int status;

	private String cookies;
	
	private String pageUrl;//主页链接
	
	private String pixel;//像素code

	private int categoryId;

	private String language;//setting
	
	private int uaId = 0;
	
	private boolean cookieUpdate;
	
	//
	private Proxy proxy;
	
	private Languages languages;//ua
	
	private FilterWords filterWords;
	
	private Category category;
	
	private Ua ua;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public int getFriends() {
		return friends;
	}

	public void setFriends(int friends) {
		this.friends = friends;
	}

	public long getProxyId() {
		return proxyId;
	}

	public void setProxyId(long proxyId) {
		this.proxyId = proxyId;
	}

	public int getCreateDay() {
		return createDay;
	}

	public void setCreateDay(int createDay) {
		this.createDay = createDay;
	}

	public int getPageDay() {
		return pageDay;
	}

	public void setPageDay(int pageDay) {
		this.pageDay = pageDay;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getCookies() {
		return cookies;
	}

	public void setCookies(String cookies) {
		this.cookies = cookies;
	}

	public Proxy getProxy() {
		return proxy;
	}

	public void setProxy(Proxy proxy) {
		this.proxy = proxy;
	}

	public Languages getLanguages() {
		return languages;
	}

	public void setLanguages(Languages languages) {
		this.languages = languages;
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
	
	public String getPageUrl() {
		return pageUrl;
	}

	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}

	public int getSettingDay() {
		return settingDay;
	}

	public void setSettingDay(int settingDay) {
		this.settingDay = settingDay;
	}

	public int getLastActiveDay() {
		return lastActiveDay;
	}

	public void setLastActiveDay(int lastActiveDay) {
		this.lastActiveDay = lastActiveDay;
	}

	public int getAdDay() {
		return adDay;
	}

	public void setAdDay(int adDay) {
		this.adDay = adDay;
	}

	public int getAdFlagDay() {
		return adFlagDay;
	}

	public void setAdFlagDay(int adFlagDay) {
		this.adFlagDay = adFlagDay;
	}

	public int getAdFlagContactDay() {
		return adFlagContactDay;
	}

	public void setAdFlagContactDay(int adFlagContactDay) {
		this.adFlagContactDay = adFlagContactDay;
	}

	public int getAdPassDay() {
		return adPassDay;
	}

	public void setAdPassDay(int adPassDay) {
		this.adPassDay = adPassDay;
	}

	public int getAdDeliveryDay() {
		return adDeliveryDay;
	}

	public void setAdDeliveryDay(int adDeliveryDay) {
		this.adDeliveryDay = adDeliveryDay;
	}

	public int getLoginDay() {
		return loginDay;
	}

	public void setLoginDay(int loginDay) {
		this.loginDay = loginDay;
	}

	public int getInvalidDay() {
		return invalidDay;
	}

	public void setInvalidDay(int invalidDay) {
		this.invalidDay = invalidDay;
	}

	public FilterWords getFilterWords() {
		return filterWords;
	}

	public void setFilterWords(FilterWords filterWords) {
		this.filterWords = filterWords;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public int getUaId() {
		return uaId;
	}

	public void setUaId(int uaId) {
		this.uaId = uaId;
	}

	public Ua getUa() {
		return ua;
	}

	public void setUa(Ua ua) {
		this.ua = ua;
	}

	public boolean isCookieUpdate() {
		return cookieUpdate;
	}

	public void setCookieUpdate(boolean cookieUpdate) {
		this.cookieUpdate = cookieUpdate;
	}
	
	
}
