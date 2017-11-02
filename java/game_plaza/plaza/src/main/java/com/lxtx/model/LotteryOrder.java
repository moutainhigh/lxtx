package com.lxtx.model;

import java.io.Serializable;
import java.util.Date;

public class LotteryOrder implements Serializable {

	private Integer id;
	private Integer userId;
	private Date orderTime;
	private Integer money;
	private Integer state;
	private Date settlementTime;
	private Integer settlementResult;
	private Integer directDate;
	private Integer directSn;
	private Integer code;
	private String announcement;
	private Integer position;//当前订单的头寸
	private String dealkind;
	private String perStatus;
	private String perStyle;
	private String orderTime1; 
	private String settlementTime1;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Date getOrderTime() {
		return orderTime;
	}

	public void setOrderTime(Date orderTime) {
		this.orderTime = orderTime;
	}

	public Integer getMoney() {
		return money;
	}

	public void setMoney(Integer money) {
		this.money = money;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Date getSettlementTime() {
		return settlementTime;
	}

	public void setSettlementTime(Date settlementTime) {
		this.settlementTime = settlementTime;
	}

	public Integer getSettlementResult() {
		return settlementResult;
	}

	public void setSettlementResult(Integer settlementResult) {
		this.settlementResult = settlementResult;
	}

	public Integer getDirectDate() {
		return directDate;
	}

	public void setDirectDate(Integer directDate) {
		this.directDate = directDate;
	}

	public Integer getDirectSn() {
		return directSn;
	}

	public void setDirectSn(Integer directSn) {
		this.directSn = directSn;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getAnnouncement() {
		return announcement;
	}

	public void setAnnouncement(String announcement) {
		this.announcement = announcement;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	public void setDealkind(String dealkind) {
		this.dealkind = dealkind;
	}

	public String getDealkind() {
		return dealkind;
	}

	public void setPerStatus(String perStatus) {
		this.perStatus = perStatus;
	}

	public String getPerStatus() {
		return perStatus;
	}

	public void setPerStyle(String perStyle) {
		this.perStyle = perStyle;
	}

	public String getPerStyle() {
		return perStyle;
	}

	public String getOrderTime1() {
		return orderTime1;
	}

	public void setOrderTime1(String orderTime1) {
		this.orderTime1 = orderTime1;
	}

	public String getSettlementTime1() {
		return settlementTime1;
	}

	public void setSettlementTime1(String settlementTime1) {
		this.settlementTime1 = settlementTime1;
	}
	
}