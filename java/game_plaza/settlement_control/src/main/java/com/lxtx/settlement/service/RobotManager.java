package com.lxtx.settlement.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxtx.settlement.dbmodel.UserData;
import com.lxtx.settlement.handler.UserDataHandler;

public class RobotManager {
	private final static Logger logger = LoggerFactory.getLogger(RobotManager.class);
	private static RobotManager inst;

	public static RobotManager getInstance() {
		if (null == inst) {
			inst = new RobotManager();
			inst.loadRobots();
		}
		return inst;
	}

	private RobotManager() {

	}

	public void loadRobots() {

		try {
			logger.info("load robots");
			List<UserData> userDatas = UserDataHandler.queryRobots();
			logger.info("load robots. success count:" + userDatas.size());
			this.robots.clear();
			this.robotMap.clear();
			for (UserData data : userDatas) {
				this.robots.add(data);
				this.robotMap.put(data.getId(), data);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			logger.error("", e);
		}
	}

	public UserData getRobot(int userId) {
		return this.robotMap.get(userId);
	}

	public boolean isRobot(int id) {
		return this.robotMap.get(id) != null;
	}

	private List<UserData> robots = new ArrayList<>();
	private Map<Integer, UserData> robotMap = new HashMap<>();
}
