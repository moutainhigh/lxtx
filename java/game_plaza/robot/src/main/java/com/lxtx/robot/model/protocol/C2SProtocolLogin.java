package com.lxtx.robot.model.protocol;

import com.lxtx.robot.service.ProtocolUtil;

public class C2SProtocolLogin extends BaseProtocol {
	private int userId;

	public C2SProtocolLogin(int userId) {
		//super(ProtocolUtil.C2S_PROTOCOL_LOGIN);
		this.userId = userId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}
}