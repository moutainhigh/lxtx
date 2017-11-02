package com.lxtx.robot.model.protocol;

import java.util.List;

public class S2CProtocolLogin extends BaseProtocol {
	private int code;
	private String errMsg;
	private List<S2CUserInfo> user;//当前用户信息

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public List<S2CUserInfo> getUser() {
		return user;
	}

	public void setUser(List<S2CUserInfo> user) {
		this.user = user;
	}
}
