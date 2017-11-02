package com.neo.entity;

import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;

@Entity
@Table(name = "page")
public class Page {
	@javax.persistence.Id
	@GeneratedValue
	private BigInteger Id;

	@Column(name = "name")
	private String Name;
	@Column(name = "pic1")
	private String Pic1;
	@Column(name = "pic2")
	private String Pic2;
	@Column(name = "link")
	private String Link;
	@Column(name = "Country")
	private String country;
	@Column(name = "status")
	private Integer status;
	@Column(name = "categoryid")
	private Long categoryId;

	public BigInteger getId() {
		return Id;
	}

	public void setId(BigInteger id) {
		Id = id;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getPic1() {
		return Pic1;
	}

	public void setPic1(String pic1) {
		Pic1 = pic1;
	}

	public String getPic2() {
		return Pic2;
	}

	public void setPic2(String pic2) {
		Pic2 = pic2;
	}

	public String getLink() {
		return Link;
	}

	public void setLink(String link) {
		Link = link;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

}
