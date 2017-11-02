package com.lxtx.util.tencent;

public class UserInfo {
	private String openid;

	private String nickname;

	private String headimgurl;

	private String chnno;
	
	private Integer wxProviderId;

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getHeadimgurl() {
		return headimgurl;
	}

	public void setHeadimgurl(String headimgurl) {
		this.headimgurl = headimgurl;
	}

	public String getChnno() {
		return chnno;
	}

	public void setChnno(String chnno) {
		this.chnno = chnno;
	}

	public Integer getWxProviderId() {
		return wxProviderId;
	}

	public void setWxProviderId(Integer wxProviderId) {
		this.wxProviderId = wxProviderId;
	}
}
