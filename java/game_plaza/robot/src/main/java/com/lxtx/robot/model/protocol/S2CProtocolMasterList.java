package com.lxtx.robot.model.protocol;

import java.util.List;

public class S2CProtocolMasterList extends BaseProtocol {
	private List<S2CUserInfo> users;

	public List<S2CUserInfo> getUsers() {
		return users;
	}

	public void setUsers(List<S2CUserInfo> users) {
		this.users = users;
	}

}
