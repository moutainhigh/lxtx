package com.baidu.alipay;

/**
 * 任务统计
 * @author leoliu
 *
 */
public class AppTaskStat {
	
	public static final String TOTALFEE = "totalfee";
	public static final String TOTALNUM = "totalnum";
	public static final String SUCCFEE = "succfee";
	public static final String SUCCNUM = "succnum";
	
	private int totalFee = 0;
	
	private int totalNum = 0;
	
	private int succFee = 0;
	
	private int succNum = 0;

	public int getTotalFee() {
		return totalFee;
	}

	public void setTotalFee(int totalFee) {
		this.totalFee = totalFee;
	}

	public int getTotalNum() {
		return totalNum;
	}

	public void setTotalNum(int totalNum) {
		this.totalNum = totalNum;
	}

	public int getSuccFee() {
		return succFee;
	}

	public void setSuccFee(int succFee) {
		this.succFee = succFee;
	}

	public int getSuccNum() {
		return succNum;
	}

	public void setSuccNum(int succNum) {
		this.succNum = succNum;
	}
	
	
}
