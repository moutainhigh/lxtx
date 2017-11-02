package com.lxtx.robot.model.protocol;

public class S2CUserInfo {
	private int id;
	private String name;
	private int chips;
	private int money;
	private int reliefCount;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getChips() {
		return chips;
	}

	public void setChips(int chips) {
		this.chips = chips;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public int getReliefCount() {
		return reliefCount;
	}

	public void setReliefCount(int reliefCount) {
		this.reliefCount = reliefCount;
	}
}
