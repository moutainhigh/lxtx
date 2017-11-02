package com.neo.entity;

import java.math.BigInteger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;

@Entity
@Table(name = "proxy")
public class Proxy {

	@javax.persistence.Id
	@GeneratedValue
	private BigInteger Id;
	@Column
	private String ip;
	@Column
	private String port1;
	@Column
	private String port2;
	@Column(name = "username")
	private String userName;
	@Column(name = "userpass")
	private String userPass;
	@Column
	private String country;
	@Column
	private long status;
	@Column(name = "endday")
	private long endDay;

	public BigInteger getId() {
		return Id;
	}

	public void setId(BigInteger id) {
		Id = id;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPort1() {
		return port1;
	}

	public void setPort1(String port1) {
		this.port1 = port1;
	}

	public String getPort2() {
		return port2;
	}

	public void setPort2(String port2) {
		this.port2 = port2;
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

	public long getStatus() {
		return status;
	}

	public void setStatus(long status) {
		this.status = status;
	}

	public long getEndDay() {
		return endDay;
	}

	public void setEndDay(long endDay) {
		this.endDay = endDay;
	}

}
