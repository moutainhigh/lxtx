package com.lxtx.robot.scheduler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxtech.cloud.net.KestrelConnector;
import com.lxtx.robot.dbmodel.UserData;
import com.lxtx.robot.handler.UserDataHandler;
import com.lxtx.robot.model.Robot;
import com.lxtx.robot.model.protocol.BaseProtocol;
import com.lxtx.robot.model.protocol.S2CProtocolGameState;
import com.lxtx.robot.model.protocol.S2CProtocolHint;
import com.lxtx.robot.model.protocol.S2CProtocolLogin;
import com.lxtx.robot.model.protocol.S2CProtocolMasterList;
import com.lxtx.robot.model.protocol.S2CProtocolRequestMaster;
import com.lxtx.robot.model.protocol.S2CProtocolSetChips;
import com.lxtx.robot.model.protocol.S2CProtocolSettedChips;
import com.lxtx.robot.service.BaseRoom;
import com.lxtx.robot.service.ProtocolUtil;
import com.lxtx.robot.service.RoomConfig;

public class GlobalScheduler {
	private final static Logger logger = LoggerFactory.getLogger(GlobalScheduler.class);

	public static enum State {
		STATE_IDLE, STATE_INIT, STATE_CONNECT_SERVER, STATE_RUNNING
	};


	private static GlobalScheduler inst = null;
	
	public static GlobalScheduler getInstance(){
		return inst;
	}

	public static GlobalScheduler makeInstance(RoomConfig roomConfig) {
		if (null == inst) {
			inst = new GlobalScheduler(roomConfig);
		}
		return inst;
	}

	private GlobalScheduler(RoomConfig roomConfig) {
		this.roomConfig = roomConfig;
	}

	private void clear() {
		this.exitRoomTriggerTime = 0;
		this.enterRoomTriggerTime = 0;
		this.state = State.STATE_IDLE;
		this.room = null;
		this.robots.clear();
		this.robotMap.clear();
	}

	private void init() {
		// load android and config
		try {
			this.room = new BaseRoom(this.roomConfig);
			List<UserData> userDatas = UserDataHandler.queryRobots();
			this.robots.clear();
			this.robotMap.clear();
			for (UserData data : userDatas) {
				Robot robot = new Robot(data);
				this.robots.add(robot);
				this.robotMap.put(data.getId(), robot);
			}

			logger.info("load robot success:{}.", this.robots.size());
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("global init error", e);
		}
	}

	public void tick() {
		long currentTime = System.currentTimeMillis();
		switch (this.state) {
		case STATE_IDLE:
			this.clear();
			this.state = State.STATE_INIT;
			// break;
		case STATE_INIT:
			this.init();
			this.state = State.STATE_CONNECT_SERVER;
			// break;
		case STATE_CONNECT_SERVER:
		// if (Net.getInstance().connectServer())
		{
			KestrelConnector.deleteBotCtrlQueue();
			String msg = ProtocolUtil.makeRequestGameStateMessage();
			KestrelConnector.enqueue(msg);
			this.state = State.STATE_RUNNING;
		}
			break;
		case STATE_RUNNING:
			String protocolStr;
			do {
				protocolStr = KestrelConnector.dequeue();
				if (protocolStr != null && protocolStr.length() > 0) {
					BaseProtocol baseProtocol = ProtocolUtil.parseProtocal(protocolStr);
					room.dealProtocol(baseProtocol);
				} else {
					break;
				}
			} while (true);
			/*
			 * / 用户行为调度：进出房间 if (isTriggerExitRoom(currentTime)) {
			 * logger.info("trigger exit room");
			 * this.scheduleExitRoom(currentTime); } if
			 * (isTriggerEnterRoom(currentTime)) {
			 * logger.info("trigger enter room");
			 * this.scheduleEnterRoom(currentTime); }
			 */
			// 房间行为调度：上庄，下庄，下注
			room.tick();
			break;
		default:
			break;
		}
	}

	public List<Robot> getRandomRobots(int count) {
		Random rand = new Random();
		if (count > this.robots.size()) {
			return new ArrayList<>(this.robots);
		}
		Map<Integer, Robot> randRobots = new HashMap<>();
		int failedCount = 0;
		while (count > 0) {
			int index = rand.nextInt(this.robots.size());
			Robot robot = this.robots.get(index);
			if (randRobots.containsKey(robot.userData.getId())) {
				failedCount++;
				if (failedCount > 10) {
					break;
				}
			} else {
				randRobots.put(robot.userData.getId(), robot);
				failedCount = 0;
				count--;
			}
		}
		return new ArrayList<>(randRobots.values());
	}

	public Robot makeRobotBusy(int userId, BaseRoom room) {
		if (null == this.robotMap.get(userId)) {
			return null;
		}
		for (int i = 0; i < this.robots.size(); i++) {
			if (this.robots.get(i).userData.getId().intValue() == userId) {
				Robot robot = this.robots.remove(i);
				robot.enterRoom(room);
				this.robots.add(robot);
				return robot;
			}
		}
		return null;
	}

	public void refreshUsedRobotsInfo(Collection<Integer> ids) {
		long currentTime = System.currentTimeMillis();
		try {
			List<UserData> userDatas = UserDataHandler.queryUserData(ids);
			for (UserData userData : userDatas) {
				Robot robot = this.robotMap.get(userData.getId());
				if (null != robot) {
					robot.userData = userData;
				}
			}
		} catch (SQLException e) {
			logger.error("refreshUsedRobotsInfo", e);
			e.printStackTrace();
		}
		logger.info("refreshUsedRobotsInfo used:{}", (System.currentTimeMillis() - currentTime));
	}

	public Robot makeRobotIdle(int userId) {
		Robot robot = this.robotMap.get(userId);
		if (null != robot && robot.isInRoom()) {
			robot.exitRoom();
			// this.robots.add(robot);
			return robot;
		}
		return null;
	}

	public Robot getRobot(int userId) {
		return this.robotMap.get(userId);
	}

	public boolean isRobot(int id) {
		return this.robotMap.get(id) != null;
	}

	/*
	 * private void scheduleExitRoom(long currentTime) { Random rand = new
	 * Random(currentTime); int playerCount = room.users.size(); for (int[]
	 * config : PRE_TIME_SCHEDULE_EXIT_ROBOT_COUNT_SCOPE) { if (playerCount <
	 * config[0]) { int userCountMin = config[1]; int userCountMax = config[2];
	 * int userCount = rand.nextInt(userCountMax - userCountMin) + userCountMin;
	 * logger.
	 * info("schedule exit room:{}. player count:{}, user count:{},scope:[{}-{}]"
	 * , new Object[] { room.id, playerCount, userCount, userCountMin,
	 * userCountMax }); for (UserData data : room.users) { Robot robot =
	 * this.robotMap.get(data.getId()); if (null != robot && robot.enterRoomTime
	 * - currentTime > 1 * 60 * 1000) { // send msg to server
	 * KestrelConnector.enqueue(ProtocolUtil.makeLogoutMessage(robot.userData.
	 * getId())); if (--userCount <= 0) { break; } } } break; } } }
	 * 
	 * private void scheduleEnterRoom(long currentTime) { Random rand = new
	 * Random(currentTime); int playerCount = room.users.size(); for (int[]
	 * config : PRE_TIME_SCHEDULE_ENTER_ROBOT_COUNT_SCOPE) { if (playerCount <
	 * config[0]) { int userCountMin = config[1]; int userCountMax = config[2];
	 * int userCount = rand.nextInt(userCountMax - userCountMin) + userCountMin;
	 * logger.
	 * info("schedule enter room:{}. player count:{}, user count:{},scope:[{}-{}]"
	 * , new Object[] { room.id, playerCount, userCount, userCountMin,
	 * userCountMax }); for (Robot robot : this.robots) { if (!robot.isInRoom()
	 * && currentTime - robot.idleTime > 1 * 60 * 1000) { // send msg to server
	 * KestrelConnector.enqueue(ProtocolUtil.makeLoginMessage(robot.userData.
	 * getId())); if (--userCount <= 0) { break; } } } break; } } }
	 */
	private boolean isTriggerExitRoom(long currentTime) {
		boolean trigger = (0 != exitRoomTriggerTime) && (currentTime >= exitRoomTriggerTime);
		if (trigger || 0 == exitRoomTriggerTime) {
			Random rand = new Random(currentTime);
			int delay = rand.nextInt(EXIT_ROOM_DELAY_TIME_SCOPE[1] - EXIT_ROOM_DELAY_TIME_SCOPE[0])
					+ EXIT_ROOM_DELAY_TIME_SCOPE[0];
			exitRoomTriggerTime = currentTime + delay;
		}
		return trigger;
	}

	private boolean isTriggerEnterRoom(long currentTime) {
		boolean trigger = (0 != enterRoomTriggerTime) && (currentTime >= enterRoomTriggerTime);
		if (trigger || 0 == enterRoomTriggerTime) {
			Random rand = new Random(currentTime);
			int delay = rand.nextInt(ENTER_ROOM_DELAY_TIME_SCOPE[1] - ENTER_ROOM_DELAY_TIME_SCOPE[0])
					+ ENTER_ROOM_DELAY_TIME_SCOPE[0];
			enterRoomTriggerTime = currentTime + delay;
		}
		return trigger;
	}

	private static final int[] EXIT_ROOM_DELAY_TIME_SCOPE = { 60 * 1000, 2 * 60 * 1000 };// 执行退出房间任务的延迟时间范围
	private static final int[] ENTER_ROOM_DELAY_TIME_SCOPE = { 10 * 1000, 1 * 60 * 1000 };// 执行进入房间任务的延迟时间范围
	private static int[][] PRE_TIME_SCHEDULE_EXIT_ROBOT_COUNT_SCOPE = { { 30, 5, 10 }, { 50, 4, 8 }, { 100, 3, 8 },
			{ 200, 2, 4 }, { Integer.MAX_VALUE, 1, 2 } };// 每次触发执行退出房间操作时，最终执行退出操作的机器人数量。如：当房间在线用户数小于3时，1-2个机器人执行操作
	private static int[][] PRE_TIME_SCHEDULE_ENTER_ROBOT_COUNT_SCOPE = { { 30, 1, 2 }, { 50, 2, 4 }, { 100, 3, 8 },
			{ 200, 4, 8 }, { Integer.MAX_VALUE, 5, 10 } };
	private long exitRoomTriggerTime;
	private long enterRoomTriggerTime;
	private State state = State.STATE_IDLE;
	private RoomConfig roomConfig;
	private BaseRoom room;
	private List<Robot> robots = new ArrayList<>();
	private Map<Integer, Robot> robotMap = new HashMap<>();
}
