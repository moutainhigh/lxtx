package com.jxt.synch.pojo;

public class DuohSync {

	private long id;
	
	private String weima;
	
	private String linkId;
	
	private String channel;
	
	private String channelCode;
	
	private long mobileTaskId;
	
	private int createDay;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getWeima() {
		return weima;
	}

	public void setWeima(String weima) {
		this.weima = weima;
	}

	public String getLinkId() {
		return linkId;
	}

	public void setLinkId(String linkId) {
		this.linkId = linkId;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getChannelCode() {
		return channelCode;
	}

	public void setChannelCode(String channelCode) {
		this.channelCode = channelCode;
	}

	public long getMobileTaskId() {
		return mobileTaskId;
	}

	public void setMobileTaskId(long mobileTaskId) {
		this.mobileTaskId = mobileTaskId;
	}

	public int getCreateDay() {
		return createDay;
	}

	public void setCreateDay(int createDay) {
		this.createDay = createDay;
	}
}
