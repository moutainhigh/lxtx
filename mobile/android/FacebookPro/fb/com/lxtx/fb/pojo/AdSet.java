package com.lxtx.fb.pojo;

import java.util.List;
import java.util.Random;

public class AdSet {
	
	private long id;
	
	private String name;
	
	private String country;

	private int minAge;
	
	private int minRange;
	
	private int maxAge;
	
	private int maxRange;
	
	private int gender;//0 female; 1 male ; 2 all 

	private String interests;
	
	private String showName;

	private String seeMoreUrl;
	
	private AlipayAccount alipayAccount;
	
	private String domain;
	
	private String picDir;
	
	private String headLines;
	
	private int num;
	
	private String text;
	
	//wrap to fill
	private List<Ad> adList;
	
	private int alipayAccountId;
	
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public int getMinAge() {
		return minAge;
	}

	public void setMinAge(int minAge) {
		this.minAge = minAge;
	}

	public int getMaxAge() {
		return maxAge;
	}

	public void setMaxAge(int maxAge) {
		this.maxAge = maxAge;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public String getInterests() {
		return interests;
	}

	public void setInterests(String interests) {
		this.interests = interests;
	}

	public String getShowName() {
		return showName;
	}

	public void setShowName(String showName) {
		this.showName = showName;
	}

	public List<Ad> getAdList() {
		return adList;
	}

	public void setAdList(List<Ad> adList) {
		this.adList = adList;
	}

	public AlipayAccount getAlipayAccount() {
		return alipayAccount;
	}

	public void setAlipayAccount(AlipayAccount alipayAccount) {
		this.alipayAccount = alipayAccount;
	}

	public String getSeeMoreUrl() {
		return seeMoreUrl;
	}

	public void setSeeMoreUrl(String seeMoreUrl) {
		this.seeMoreUrl = seeMoreUrl;
	}

	public int getAlipayAccountId() {
		return alipayAccountId;
	}

	public void setAlipayAccountId(int alipayAccountId) {
		this.alipayAccountId = alipayAccountId;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getPicDir() {
		return picDir;
	}

	public void setPicDir(String picDir) {
		this.picDir = picDir;
	}

	public String getHeadLines() {
		return headLines;
	}

	public void setHeadLines(String headLines) {
		this.headLines = headLines;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getMinRange() {
		return minRange;
	}

	public void setMinRange(int minRange) {
		this.minRange = minRange;
	}

	public int getMaxRange() {
		return maxRange;
	}

	public void setMaxRange(int maxRange) {
		this.maxRange = maxRange;
	}
	
	public int getMinAge1(){
		return (minAge - minRange) + new Random().nextInt(2 * minRange + 1);
	}
	
	public int getMaxAge1(){
		return (maxAge - maxRange) + new Random().nextInt(2 * maxRange + 1);
	}
	
}
