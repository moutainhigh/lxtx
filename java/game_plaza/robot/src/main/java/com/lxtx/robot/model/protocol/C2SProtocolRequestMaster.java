package com.lxtx.robot.model.protocol;

import com.lxtx.robot.service.ProtocolUtil;

public class C2SProtocolRequestMaster extends BaseProtocol {
	private int userId;
	private int up;

	public C2SProtocolRequestMaster(int userId, int up) {
		super(ProtocolUtil.C2S_PROTOCOL_REQUEST_MASTER);
		this.userId = userId;
		this.up = up;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getUp() {
		return up;
	}

	public void setUp(int up) {
		this.up = up;
	}
}