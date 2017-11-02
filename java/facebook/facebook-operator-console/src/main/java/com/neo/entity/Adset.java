package com.neo.entity;

import java.math.BigInteger;
import java.util.stream.Stream;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;

@Entity
@Table(name = "adset")
public class Adset {

	@javax.persistence.Id
	@GeneratedValue
	private BigInteger Id;
	@Column
	private String name;
	@Column
	private String country;
	@Column(name = "minage")
	private Long minAge;
	@Column(name = "minrange")
	private Long minRange;
	@Column(name = "maxage")
	private Long maxAge;
	@Column(name = "maxrange")
	private Long maxRange;
	@Column
	private Integer gender;
	@Column
	private String interests;
	@Column(name = "showname")
	private String showName;
	@Column(name = "seemoreurl")
	private String seeMoreUrl;
	@Column(name = "alipayaccountid")
	private Long AlipayAccountId;
	@Column
	private Integer status;
	@Column
	private String domain;
	@Column(name = "picdir")
	private String picDir;
	@Column(name = "headlines")
	private String headLines;
	@Column
	private Long num;
	@Column
	private String text;

	public BigInteger getId() {
		return Id;
	}

	public void setId(BigInteger id) {
		Id = id;
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

	public Long getMinAge() {
		return minAge;
	}

	public void setMinAge(Long minAge) {
		this.minAge = minAge;
	}

	public Long getMinRange() {
		return minRange;
	}

	public void setMinRange(Long minRange) {
		this.minRange = minRange;
	}

	public Long getMaxAge() {
		return maxAge;
	}

	public void setMaxAge(Long maxAge) {
		this.maxAge = maxAge;
	}

	public Long getMaxRange() {
		return maxRange;
	}

	public void setMaxRange(Long maxRange) {
		this.maxRange = maxRange;
	}

	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}

	public String getInterests() {
		if (interests.indexOf("||") > 0) { 
			this.interests = String.join("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;", interests.split("\\|\\|"));			
		} else {
			this.interests = interests;
		}
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

	public String getSeeMoreUrl() {
		return seeMoreUrl;
	}

	public void setSeeMoreUrl(String seeMoreUrl) {
		this.seeMoreUrl = seeMoreUrl;
	}

	public Long getAlipayAccountId() {
		return AlipayAccountId;
	}

	public void setAlipayAccountId(Long alipayAccountId) {
		AlipayAccountId = alipayAccountId;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
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
		if (headLines.indexOf("||") > 0) { 
			this.headLines = String.join("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;", headLines.split("\\|\\|"));			
		} 
		return headLines;
	}

	public void setHeadLines(String headLines) {
		this.headLines = headLines;
	}

	public Long getNum() {
		return num;
	}

	public void setNum(Long num) {
		this.num = num;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
