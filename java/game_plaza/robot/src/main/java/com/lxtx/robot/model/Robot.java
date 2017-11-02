package com.lxtx.robot.model;

import com.lxtx.robot.dbmodel.UserData;
import com.lxtx.robot.service.BaseRoom;

public class Robot {
	public Robot(UserData userData) {
		this.userData = userData;
	}

	public boolean isInRoom() {
		return this.room != null;
	}

	public void enterRoom(BaseRoom room) {
		this.enterRoomTime = System.currentTimeMillis();
		this.room = room;
	}

	public void exitRoom() {
		this.room = null;
		this.enterRoomTime = 0;
	}

	public UserData userData;
	public BaseRoom room;
	public long enterRoomTime;
	public long idleTime;
}
