package com.jxt.pay.appclient.service.dynamic.pojo;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("gameParam")
public class GameParam {
	@XStreamAlias("gameType")
	private int gameType;
	@XStreamAlias("url")
	private String url;
	@XStreamAlias("data")
	private String data;

	public int getGameType() {
		return gameType;
	}

	public void setGameType(int gameType) {
		this.gameType = gameType;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}
