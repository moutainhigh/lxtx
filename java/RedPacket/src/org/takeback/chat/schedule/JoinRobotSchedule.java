package org.takeback.chat.schedule;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.chat.controller.RobotUserController;
import org.takeback.chat.store.room.Room;
import org.takeback.chat.store.room.RoomStore;
import org.takeback.chat.store.user.RobotUser;
import org.takeback.chat.store.user.UserStore;
import org.takeback.util.httpclient.HttpClientUtils;

public class JoinRobotSchedule {
	@Autowired
	private RoomStore roomStore;
	@Autowired
	private UserStore userStore;
	@Autowired
	private RobotUserController robotController;
	
	private static String url = "http://hb.guimicheng.com/joinRobot";

	public void init() {
		try {
			//sleep for 8 seconds first
			TimeUnit.SECONDS.sleep(20);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		final List<Room> rooms = this.roomStore.getByCatalog("");
		long count = 0;// this.userStore.size();
		if (rooms != null && !rooms.isEmpty()) {
			for (final Room room : rooms) {
				int roomUserCnt = room.getUsers().keySet().size();
				count += roomUserCnt;
			}
		}
		try {
			if (count < 30) {
				System.out.println("robots are joining .......");
				robotController.join(null);				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
