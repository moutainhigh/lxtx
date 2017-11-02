package com.lxtx.robot.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxtech.cloud.net.KestrelConnector;
import com.lxtx.robot.dbmodel.UserData;
import com.lxtx.robot.model.BetTarget;
import com.lxtx.robot.model.Robot;
import com.lxtx.robot.model.protocol.BaseProtocol;
import com.lxtx.robot.model.protocol.S2CProtocolGameState;
import com.lxtx.robot.model.protocol.S2CProtocolMasterList;
import com.lxtx.robot.model.protocol.S2CProtocolRequestMaster;
import com.lxtx.robot.model.protocol.S2CProtocolSetChips;
import com.lxtx.robot.model.protocol.S2CProtocolSettedChips;
import com.lxtx.robot.model.protocol.S2CUserInfo;
import com.lxtx.robot.scheduler.GlobalScheduler;
import com.lxtx.robot.utils.Tools;

public class BaseRoom {
	private final static Logger logger = LoggerFactory.getLogger(BaseRoom.class);

	public static enum RoomState {
		ROOM_STATE_WAITING_FOR_MASTER, // 空闲
		ROOM_STATE_WAITING_FOR_START, // 等待开始
		ROOM_STATE_WAITING_FOR_SET_CHIPS, // 下注
		ROOM_STATE_WAITING_FOR_CACLULATE, // 结算
		ROOM_STATE_WAITING_FOR_SERVER,
	}

	public static enum RequestMasterState {
		REQEST_MASTER_STATE_NOT_IN_START_STATE, // 0
		REQEST_MASTER_STATE_BE_SETTED_BY_OTHER, // 1
		REQEST_MASTER_STATE_NOT_ENOUGH_CHIPS, // 2
		REQEST_MASTER_STATE_SUCCESS, // 3
		REQEST_MASTER_STATE_INFOMATION, // 4
		REQEST_MASTER_STATE_DOWN_SUCCESS,//
	}

	public BaseRoom(RoomConfig roomConfig) {
		super();
		this.roomConfig = roomConfig;
		this.roomState = RoomState.ROOM_STATE_WAITING_FOR_SERVER;
		// this.users = new LinkedList<UserData>();
		this.banker = null;
		this.bankerContinuousCount = 0;
		this.waitBankers = new LinkedList<>();
		this.betTargetProbability = null;
		this.betTotalInfos = new int[this.roomConfig.betTargets.length];
		this.robotsUsedGameCount = 0;
		this.rand = new Random();
	}

	public void calculateBetTargetProbability() {
		this.roomConfig.loadBetTargets();
		BetTarget[] betTargets = this.roomConfig.betTargets;
		Set<Integer> multiplesSet = new HashSet<>();
		for (BetTarget betTarget : betTargets) {
			multiplesSet.add((int) (betTarget.multiple + 0.5));
		}
		List<Integer> multiples = new ArrayList<>();
		for (Integer m : multiplesSet) {
			multiples.add(m);
		}
		Collections.sort(multiples);
		long sumMultiple = 1;
		for (int i = multiples.size() - 1; i >= 0; i--) {
			int m = multiples.get(i);
			long temp = sumMultiple;
			for (int j = 2; j <= m; j++) {
				if (m % j == 0 && temp % j == 0) {
					m /= j;
					temp /= j;
					j--;
				}
			}
			sumMultiple *= m;
		}
		List<Integer> options = new ArrayList<>();
		for (BetTarget betTarget : betTargets) {
			long count = sumMultiple / (int) (betTarget.multiple + 0.5);
			for (int i = 0; i < count; i++) {
				options.add(betTarget.option);
			}
		}
		this.betTargetProbability = options.toArray(new Integer[options.size()]);
		Tools.shuffle(this.betTargetProbability);
	}

	public void dealProtocol(BaseProtocol protocol) {
		// set state banker userlist
		switch (protocol.getProtocol()) {
		// case ProtocolUtil.S2C_PROTOCOL_LOGIN:
		// S2CProtocolLogin s2cProtocolLogin = (S2CProtocolLogin) protocol;
		// if (s2cProtocolLogin.getCode() == 0) {
		// this.refreshUserList(s2cProtocolLogin.getUser());
		// }
		// break;
		case ProtocolUtil.S2C_PROTOCOL_SETTED_CHIPS:
			S2CProtocolSettedChips s2cProtocolSettedChips = (S2CProtocolSettedChips) protocol;
			this.setBetTotalInfos(s2cProtocolSettedChips);
			break;
		case ProtocolUtil.S2C_PROTOCOL_GAME_STATE:
			S2CProtocolGameState s2cProtocolGameState = (S2CProtocolGameState) protocol;
			this.setRoomState(s2cProtocolGameState.getState(), s2cProtocolGameState.getInterval() * 1000);
			break;
		case ProtocolUtil.S2C_PROTOCOL_REQUEST_MASTER:
			S2CProtocolRequestMaster s2cProtocolRequestMaster = (S2CProtocolRequestMaster) protocol;
			// this.setRoomState(s2cProtocolRequestMaster.getState(), 0);
			this.requestMasterResult(s2cProtocolRequestMaster);
			break;
		case ProtocolUtil.S2C_PROTOCOL_SET_CHIPS:
			S2CProtocolSetChips s2cProtocolSetChips = (S2CProtocolSetChips) protocol;
			this.setBetInfo(s2cProtocolSetChips);
			break;
		case ProtocolUtil.S2C_PROTOCOL_MASTER_LIST:
			S2CProtocolMasterList s2cProtocolMasterList = (S2CProtocolMasterList) protocol;
			this.refreshMasterList(s2cProtocolMasterList.getUsers());
			break;
		// case ProtocolUtil.S2C_PROTOCOL_HINT:
		// S2CProtocolHint s2cProtocolHint = (S2CProtocolHint) protocol;
		// break;
		}
	}

	public void setRoomState(int state, int interval) {
		RoomState oldRoomState = this.roomState;
		this.roomState = RoomState.values()[state];
		this.finishTime = System.currentTimeMillis() + interval;
		if (this.roomState != oldRoomState) {
			KestrelConnector.enqueue(ProtocolUtil.makeGetMasterList());
		}
		if (this.roomState == RoomState.ROOM_STATE_WAITING_FOR_SET_CHIPS && this.roomState != oldRoomState) {
			this.calculateBetTargetProbability();
			this.roomConfig.loadBankerMinGold();
			this.roomConfig.loadBetNumConfigs();
			this.roomConfig.loadBetConfig();
			if (0 != this.robotsUsedGameCount) {
				refreshUsedRobotsInfo();
			}
			this.robotsUsedGameCount++;
		}
	}

	private void refreshUsedRobotsInfo() {
		Set<Integer> ids = new HashSet<>();
		if (null != this.usedRobots) {
			for (Robot robot : this.usedRobots) {
				ids.add(robot.userData.getId());
			}
		}
		if (banker != null) {
			ids.add(banker.getId());
		}
		ids.addAll(this.waitBankers);
		GlobalScheduler.getInstance().refreshUsedRobotsInfo(ids);
	}

	/*
	 * public void refreshUserList(List<S2CUserInfo> userInfos) { for (int i =
	 * 0; i < this.users.size(); i++) {// remove unused UserData userData =
	 * this.users.get(i); boolean find = false; for (int j = 0; j <
	 * userInfos.size(); j++) { S2CUserInfo info = userInfos.get(j); if
	 * (userData.getId().intValue() == info.getId()) { find = true; break; } }
	 * if (!find) {
	 * GlobalScheduler.getInstance().makeRobotIdle(userData.getId());
	 * this.users.remove(i); i--; } } for (int j = 0; j < userInfos.size(); j++)
	 * {// add new S2CUserInfo userInfo = userInfos.get(j); boolean find =
	 * false; for (int i = 0; i < this.users.size(); i++) { UserData userData =
	 * this.users.get(i); if (userData.getId().intValue() == userInfo.getId()) {
	 * find = true; break; } } if (!find) { this.enterRoom(userInfo); } } }
	 */
	public void requestMasterResult(S2CProtocolRequestMaster s2cProtocolRequestMaster) {
		if (s2cProtocolRequestMaster.getState() == RequestMasterState.REQEST_MASTER_STATE_INFOMATION.ordinal()) {
			if (null == s2cProtocolRequestMaster.getUser() || s2cProtocolRequestMaster.getUser().getId() == 0) {
				this.banker = null;
			} else {
				this.banker = UserData.conver(s2cProtocolRequestMaster.getUser());
			}
		}
	}

	public void refreshMasterList(List<S2CUserInfo> userInfos) {
		this.waitBankers.clear();
		for (S2CUserInfo userInfo : userInfos) {
			this.waitBankers.add(userInfo.getId());
		}
	}
	/*
	 * public void enterRoom(S2CUserInfo userInfo) { Robot robot =
	 * GlobalScheduler.getInstance().getRobot(userInfo.getId()); UserData
	 * userData; if (null != robot) {
	 * GlobalScheduler.getInstance().makeRobotBusy(userInfo.getId(), this);
	 * userData = robot.userData; } else { userData = UserData.conver(userInfo);
	 * } this.users.add(userData); }
	 * 
	 * public void exitRoom(int userId) { Robot robot =
	 * GlobalScheduler.getInstance().makeRobotIdle(userId); for (int i = 0; i <
	 * this.users.size(); i++) { if (this.users.get(i).getId().intValue() ==
	 * userId) { this.users.remove(i); break; } } }
	 */

	public void setBetInfo(S2CProtocolSetChips protocolSetChips) {
		this.betTotalInfos[protocolSetChips.getLotteryIndex() - 1] += protocolSetChips.getNum();
	}

	public void setBetTotalInfos(S2CProtocolSettedChips s2cProtocolSettedChips) {
		for (int i = 0; i < this.betTotalInfos.length; i++) {
			this.betTotalInfos[i] = 0;
		}
		for (Entry<String, Integer> entry : s2cProtocolSettedChips.getTotal().entrySet()) {
			this.betTotalInfos[Integer.parseInt(entry.getKey()) - 1] = entry.getValue();
		}
	}

	public void tick() {
		long currentTime = System.currentTimeMillis();
		switch (this.roomState) {
		case ROOM_STATE_WAITING_FOR_MASTER:
			if (this.isTriggerApplyBanker(currentTime)) {
				this.scheduleApplyBanker();
			}
			if (this.isTriggerApplyCancelBanker(currentTime)) {
				this.scheduleApplyCancelBanker();
			}
			break;
		case ROOM_STATE_WAITING_FOR_START:
			if (this.isTriggerApplyBanker(currentTime)) {
				this.scheduleApplyBanker();
			}
			break;
		case ROOM_STATE_WAITING_FOR_SET_CHIPS:
			if (this.finishTime > currentTime && this.isTriggerBet(currentTime)) {
				this.scheduleBet();
			}
			if (this.isTriggerApplyBanker(currentTime)) {
				this.scheduleApplyBanker();
			}
			if (this.isTriggerApplyCancelBanker(currentTime)) {
				this.scheduleApplyCancelBanker();
			}
			break;
		case ROOM_STATE_WAITING_FOR_CACLULATE:
		case ROOM_STATE_WAITING_FOR_SERVER:
			break;
		}
	}

	private void scheduleApplyBanker() {
		if (this.waitBankers.size() > 5) {
			return;
		}
		int count = 1;
		switch (this.roomState) {
		case ROOM_STATE_WAITING_FOR_MASTER:
			count = rand.nextInt(2) + 1;
			break;
		case ROOM_STATE_WAITING_FOR_START:
			if (this.waitBankers.size() >= 1) {//已经有人在排桩的时候，降低上庄频次
				count = (rand.nextInt(4) == 0) ? 1 : 0;
			}
			break;
		case ROOM_STATE_WAITING_FOR_SET_CHIPS:
			if (this.waitBankers.size() >= 1) {
				count = (rand.nextInt(5) == 0) ? 1 : 0;
			}
			break;
		default:
			break;
		}
		if (count <= 0) {
			logger.info("skip scheduleApplyBanker");
			return;
		}
		for (Robot robot : this.getRandomRobots(count * 5)) {
			// Robot robot =
			// GlobalScheduler.getInstance().getRobot(userData.getId());
			if (null != robot && robot.userData.getCarry_amount() >= this.roomConfig.bankerMinGold) {
				// send msg
				KestrelConnector.enqueue(ProtocolUtil.makeRequestMasterMessage(robot.userData.getId(), true));
				if (--count <= 0) {
					break;
				}
			}
		}
	}

	private boolean isTriggerApplyBanker(long currentTime) {
		boolean trigger = (0 != applyBankerTriggerTime) && (currentTime >= applyBankerTriggerTime);
		if (trigger || 0 == applyBankerTriggerTime) {
			int delay = rand.nextInt(
					this.roomConfig.apply_banker_delay_time_scope[1] - this.roomConfig.apply_banker_delay_time_scope[0])
					+ this.roomConfig.apply_banker_delay_time_scope[0];
			applyBankerTriggerTime = currentTime + delay;
		}
		return trigger;
	}

	private void scheduleApplyCancelBanker() {
		int count = 0;
		List<Integer> sumBankerIds = new ArrayList<>(this.waitBankers);
		if (null != banker) {
			sumBankerIds.add(banker.getId());
		}

		int sumBankerCount = sumBankerIds.size();
		if (sumBankerCount > 5) {
			count = sumBankerCount / 2;
		} else if (sumBankerCount > 3) {
			count = 1;
		} else if (sumBankerCount == 1) {
			count = 0;
		} else {
			count = rand.nextInt(2);
		}

		if (count > 0) {
			for (Integer id : sumBankerIds) {
				Robot robot = GlobalScheduler.getInstance().getRobot(id);
				if (null != robot) {
					// send msg
					KestrelConnector.enqueue(ProtocolUtil.makeRequestMasterMessage(robot.userData.getId(), false));
					if (--count <= 0) {
						break;
					}
				}
			}
		}
	}

	private boolean isTriggerApplyCancelBanker(long currentTime) {
		boolean trigger = (0 != applyCancelBankerTriggerTime) && (currentTime >= applyCancelBankerTriggerTime);
		if (trigger || 0 == applyCancelBankerTriggerTime) {
			int delay = rand
					.nextInt(this.roomConfig.apply_cancel_banker_delay_time_scope[1]
							- this.roomConfig.apply_cancel_banker_delay_time_scope[0])
					+ this.roomConfig.apply_cancel_banker_delay_time_scope[0];
			applyCancelBankerTriggerTime = currentTime + delay;
		}
		return trigger;
	}

	private void scheduleBet() {
		int betCount = this.roomConfig.getRandomBetCount(System.currentTimeMillis());
		int multi = 1;
		if (this.roomConfig.betNumsDynamic) {
			if (null == banker) {
				logger.info("error");
			} else {
				multi = (int) (banker.getCarry_amount() / this.roomConfig.bankerMinGold);
				multi = multi < 1 ? 1 : multi;
				multi = multi > this.roomConfig.betNumsMultiMax ? this.roomConfig.betNumsMultiMax : multi;
			}
		}
		for (Robot robot : this.getRandomRobots(betCount * 5)) {
			if (null == banker || !robot.userData.getId().equals(banker.getId())) {
				// Robot robot =
				// GlobalScheduler.getInstance().getRobot(userData.getId());
				if (null != robot && robot.userData.getCarry_amount() >= (this.roomConfig.betNums[0] * multi)) {
					int targetOption = this.betTargetProbability[rand.nextInt(this.betTargetProbability.length)];
					double multiple = 1;
					for (BetTarget betTarget : this.roomConfig.betTargets) {
						if (betTarget.option == targetOption) {
							multiple = betTarget.multiple;
						}
					}

					int betNumOptionCount = 0;
					for (int i = 0; i < this.roomConfig.betNums.length; i++) {
						if (robot.userData.getCarry_amount() >= this.roomConfig.betNums[i]) {
							betNumOptionCount = i + 1;
						} else {
							break;
						}
					}
					if (betNumOptionCount <= 0) {
						continue;
					}
					if (multiple >= 100) {// 概率大的强制下小注
						betNumOptionCount = 1;
					} else if (multiple >= 50) {
						betNumOptionCount = betNumOptionCount > 2 ? 2 : betNumOptionCount;
					} else if (multiple >= 5) {
						betNumOptionCount = betNumOptionCount > 3 ? 3 : betNumOptionCount;
					}
					// send msg: targetOption this.roomConfig.betOptions[index]
					List<Integer> betNumOptions = new ArrayList<>();
					for (int i = 0; i < betNumOptionCount; i++) {
						for (int j = 0; j < this.roomConfig.betNumProbabilitys[i]; j++) {
							betNumOptions.add(this.roomConfig.betNums[i]);
						}
					}
					int betNum = betNumOptions.get(rand.nextInt(betNumOptions.size()));
					betNum *= multi;
					KestrelConnector
							.enqueue(ProtocolUtil.makeSetChipsMessage(robot.userData.getId(), targetOption, betNum));
					robot.userData.setCarry_amount(robot.userData.getCarry_amount() - betNum);// 不管下注是否成功直接扣除，下一轮开始的时候刷新机器人身上的金币数量
					if (--betCount <= 0) {
						break;
					}
				}
			}
		}
	}

	private boolean isTriggerBet(long currentTime) {
		boolean trigger = (0 != betTriggerTime) && (currentTime >= betTriggerTime);
		if (trigger || 0 == betTriggerTime) {
			int delay = this.roomConfig.getRandomBetDelayTime(System.currentTimeMillis());
			betTriggerTime = currentTime + delay;
		}
		return trigger;
	}

	private List<Robot> getRandomRobots(int count) {
		List<Robot> usedRobots = getUsedRobots();
		if (count > usedRobots.size()) {
			return new ArrayList<>(usedRobots);
		}
		Map<Integer, Robot> randRobots = new HashMap<>();
		int failedCount = 0;
		while (count > 0) {
			int index = rand.nextInt(usedRobots.size());
			Robot robot = usedRobots.get(index);
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

	private List<Robot> getUsedRobots() {
		if (null == this.usedRobots || this.usedRobots.size() <= 0
				|| this.robotsUsedGameCount > ROBOTS_USED_MAX_GAME_COUNT) {
			this.usedRobots = GlobalScheduler.getInstance().getRandomRobots(PRE_TIME_OBTAIN_ROBOT_COUNT);
			this.robotsUsedGameCount = 0;
		}
		return this.usedRobots;
	}

	private static final int PRE_TIME_OBTAIN_ROBOT_COUNT = 200;// 每次获得使用机器人的数量
	private static final int ROBOTS_USED_MAX_GAME_COUNT = 10;// 每批机器人使用的局数

	private long applyBankerTriggerTime;
	private long applyCancelBankerTriggerTime;
	private long betTriggerTime;
	private Integer[] betTargetProbability;
	private Random rand;

	public int id;
	protected RoomConfig roomConfig;
	private int[] betTotalInfos;
	public RoomState roomState;
	public long finishTime;
	public List<Robot> usedRobots;
	public int robotsUsedGameCount;
	// public List<UserData> users;
	public UserData banker;
	public int bankerContinuousCount;// 连庄次数
	public List<Integer> waitBankers;// 上庄列表，
}
