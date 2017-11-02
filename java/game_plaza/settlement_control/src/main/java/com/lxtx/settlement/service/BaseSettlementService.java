package com.lxtx.settlement.service;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxtech.cloud.net.KestrelConnector;
import com.lxtx.settlement.cache.SystemConfigCache;
import com.lxtx.settlement.dbmodel.BaseSettlementGroup;
import com.lxtx.settlement.dbmodel.BaseSettlementHistory;
import com.lxtx.settlement.dbmodel.StatisticsSettlement;
import com.lxtx.settlement.handler.UserDataHandler;
import com.lxtx.settlement.model.BaseGameResult;
import com.lxtx.settlement.model.protocol.BaseProtocol;
import com.lxtx.settlement.model.protocol.ProtocolSettlement;

public abstract class BaseSettlementService<T extends BaseGameResult, G extends BaseSettlementGroup<T>, H extends BaseSettlementHistory> {
	private final static Logger logger = LoggerFactory.getLogger(BaseSettlementService.class);
	private static final boolean IGNORE_ROBOT_STATISTICS_SETTLEMENT = true;
	public static boolean __TEST__ = false;

	private String pre_time_max_lose_config_key;
	private String pre_day_max_lose_config_key;
	private String enforcement_intervention_key;
	private String enforcement_intervention_pre_time_max_lose_key;
	private String enforcement_intervention_lose_random_bound_key;
	private String robot_banker_pre_time_system_max_lose_key;
	private String normal_banker_pre_day_system_max_lose_key;

	private String settlement_ctrl_queue;
	private String settlement_oper_queue;

	private String currentDay;
	private long systemSumSettlementPreDay;
	private long normalBankerSystemSumSettlementPreDay;

	public BaseSettlementService(String pre_time_max_lose_config_key, String pre_day_max_lose_config_key,
			String enforcement_intervention_key, String enforcement_intervention_pre_time_max_lose_key,
			String enforcement_intervention_lose_random_bound_key, String robot_banker_pre_time_system_max_lose_key,
			String normal_banker_pre_day_system_max_lose_key, String settlement_ctrl_queue,
			String settlement_oper_queue) {
		super();
		this.pre_time_max_lose_config_key = pre_time_max_lose_config_key;
		this.pre_day_max_lose_config_key = pre_day_max_lose_config_key;
		this.enforcement_intervention_key = enforcement_intervention_key;
		this.enforcement_intervention_pre_time_max_lose_key = enforcement_intervention_pre_time_max_lose_key;
		this.enforcement_intervention_lose_random_bound_key = enforcement_intervention_lose_random_bound_key;
		this.robot_banker_pre_time_system_max_lose_key = robot_banker_pre_time_system_max_lose_key;
		this.normal_banker_pre_day_system_max_lose_key = normal_banker_pre_day_system_max_lose_key;
		this.settlement_ctrl_queue = settlement_ctrl_queue;
		this.settlement_oper_queue = settlement_oper_queue;
	}

	public void load() throws Exception {
		currentDay = new SimpleDateFormat("yyyyMMdd").format(new Date());
		Date date = new SimpleDateFormat("yyyyMMdd HH:mm:ss").parse(currentDay + " 00:00:00");
		long dayMin = date.getTime();
		long dayMax = dayMin + 24 * 60 * 60 * 1000;
		logger.info("load sum system settlement result:[{}, {}]", dayMin / 1000, dayMax / 1000);
		systemSumSettlementPreDay = selectSumSystemSettlementResult(dayMin, dayMax);
		normalBankerSystemSumSettlementPreDay = selectNormalBankerSumSystemSettlementResult(dayMin, dayMax);
	}

	public void tick() {
		String msg = KestrelConnector.dequeue(settlement_ctrl_queue);
		if (null != msg && msg.length() > 0) {
			BaseProtocol baseProtocol = ProtocolUtil.parseProtocal(msg);
			switch (baseProtocol.getProtocol()) {
			case ProtocolUtil.PROTOCOL_SETTLEMENT:
				ProtocolSettlement protocolSettlement = (ProtocolSettlement) baseProtocol;
				if (protocolSettlement.getState() == 3) {
					int groupId = protocolSettlement.getGroupId();
					boolean success = settlement(groupId);
					if (success) {
						String successMsg = "{\"msg\":3003, \"code\":" + (success ? 0 : 1) + ", \"groupId\":" + groupId
								+ "}";
						KestrelConnector.enqueue(settlement_oper_queue, successMsg);
					}
				}
				break;
			}
		}
	}

	protected long getRobotsSumSettlement(double[] optionMultiples, int bankerId, List<H> historys) {
		long robotSumSettlement = 0;
		long bankerSettlement = 0;
		for (BaseSettlementHistory history : historys) {
			int option = history.getTarget();
			long settlement = (long) (optionMultiples[option - 1] * history.getAmount());
			if (settlement == 0) {// 金额为0，庄家收益为玩家下注金额
				bankerSettlement += history.getAmount();
			} else {// 庄家收益为玩家最终收益，减去玩家本金
				bankerSettlement -= settlement - history.getAmount();
			}
			if (RobotManager.getInstance().isRobot(history.getUser_id())) {
				robotSumSettlement += settlement - history.getAmount();
			}
		}
		if (RobotManager.getInstance().isRobot(bankerId)) {
			robotSumSettlement += bankerSettlement;
		}
		return robotSumSettlement;
	}

	public boolean settlement(int groupId) {
		try {
			G group = getSettlementGroup(groupId);
			logger.info("load group success");
			if (!__TEST__ && group.getState() == 2) {
				return false;
			}
			boolean bankerIsRobot = RobotManager.getInstance().isRobot(group.getBanker_id());
			long currentMillis = System.currentTimeMillis();
			String currentDay = new SimpleDateFormat("yyyyMMdd").format(new Date(currentMillis));
			if (!this.currentDay.equals(currentDay)) {
				this.currentDay = currentDay;
				systemSumSettlementPreDay = 0;
				normalBankerSystemSumSettlementPreDay = 0;
			}

			List<H> historys = querySettlementHistorys(groupId);
			logger.info("load history success");
			boolean enforcement_intervention = SystemConfigCache.getInstance().getBoolean(enforcement_intervention_key);
			long maxLosePreTime;
			long maxLosePreDay;
			long robot_banker_pre_time_system_max_lose;
			long normal_banker_pre_day_system_max_lose;
			boolean forceLose = false;
			if (enforcement_intervention) {
				robot_banker_pre_time_system_max_lose = Integer.MAX_VALUE;
				normal_banker_pre_day_system_max_lose = Integer.MAX_VALUE;
				int enforcement_intervention_lose_random_bound = SystemConfigCache.getInstance()
						.getInt(enforcement_intervention_lose_random_bound_key);
				Random rand = new Random(currentMillis);
				forceLose = (0 == rand.nextInt(enforcement_intervention_lose_random_bound));
				if (forceLose) {
					maxLosePreTime = SystemConfigCache.getInstance()
							.getLong(enforcement_intervention_pre_time_max_lose_key);
					maxLosePreDay = Integer.MAX_VALUE;
				} else {// 设置赢钱
					maxLosePreTime = 0;
					if (systemSumSettlementPreDay > 0) {
						maxLosePreDay = 0;
					} else {
						maxLosePreDay = -systemSumSettlementPreDay;
					}
				}
			} else {
				maxLosePreTime = SystemConfigCache.getInstance().getLong(pre_time_max_lose_config_key);
				maxLosePreDay = SystemConfigCache.getInstance().getLong(pre_day_max_lose_config_key);

				robot_banker_pre_time_system_max_lose = SystemConfigCache.getInstance()
						.getLong(robot_banker_pre_time_system_max_lose_key);
				normal_banker_pre_day_system_max_lose = SystemConfigCache.getInstance()
						.getLong(normal_banker_pre_day_system_max_lose_key);
			}
			T gameResult;
			double[] optionMultiples;
			long robotSumSettlement;

			T maxGameResult = null;
			double[] maxOptionMultiples = null;
			long maxRobotSumSettlement = Long.MIN_VALUE;
			int makeCount = 0;
			do {
				gameResult = this.makeGameResult();
				optionMultiples = getOptionMultiples(group, gameResult);
				makeCount++;
				robotSumSettlement = getRobotsSumSettlement(optionMultiples, group.getBanker_id(), historys);
				if (robotSumSettlement > maxRobotSumSettlement) {
					maxGameResult = gameResult;
					maxOptionMultiples = optionMultiples;
					maxRobotSumSettlement = robotSumSettlement;
				}
				logger.info("make settlements result:[{}], sellement:{}",
						new Object[] { gameResult.toString(), robotSumSettlement });
				if (robotSumSettlement > -maxLosePreTime// 大于单次系统输的最大值
						&& this.systemSumSettlementPreDay + robotSumSettlement > -maxLosePreDay// 大于当天系统输的最大值
				) {
					boolean success = false;
					if (bankerIsRobot) {
						if (robotSumSettlement > -robot_banker_pre_time_system_max_lose) {// 机器人坐庄单次输赢大于设置的最大值
							success = true;
						}
					} else {
						if (this.normalBankerSystemSumSettlementPreDay
								+ robotSumSettlement > -normal_banker_pre_day_system_max_lose) {// 非机器人坐庄，当天所有普通人坐庄的输赢总和大于设置的最大值
							success = true;
						}
					}
					if (success) {
						if (!forceLose || (forceLose && robotSumSettlement < 0)) {// 如果不是强制输，则退出；如果是强制输并且确实输钱了，则退出，赢钱则不退出
							break;
						}
					}
				}
				if (makeCount >= 30) {// >30次，不再计算
					forceLose = false;
					break;
				}
			} while (true);// 一局游戏输100W或者当日累计输2000W，则重新查找输钱最少的结果
			logger.info("makeSettlements count:" + makeCount);
			if (!forceLose) {// 如果是强制输，则当前结果一定是输钱的结果。因为因为次数达到而退出的时候，已经把状态设置为false
				gameResult = maxGameResult;
				optionMultiples = maxOptionMultiples;
				robotSumSettlement = maxRobotSumSettlement;
			}

			Map<Integer, Long> userSettlements = new HashMap<>();
			Map<Integer, Long> userSumAmounts = new HashMap<>();
			long bankerSettlement = 0;
			List<Integer> losedHistoryIds = new ArrayList<>();
			List<H> robotWinHistorys = new ArrayList<>();
			List<H> normalWinHistorys = new ArrayList<>();
			for (H history : historys) {
				int option = history.getTarget();
				long settlement = (long) (optionMultiples[option - 1] * history.getAmount());
				if (settlement == 0) {// 金额为0，庄家收益为玩家下注金额
					bankerSettlement += history.getAmount();
				} else {// 庄家收益为玩家最终收益，减去玩家本金
					bankerSettlement -= settlement - history.getAmount();
				}
				history.setSettlement_result(settlement);

				Long userSettlement = userSettlements.get(history.getUser_id());
				if (null == userSettlement) {
					userSettlement = 0L;
				}
				userSettlements.put(history.getUser_id(), userSettlement.longValue() + settlement);// 玩家本金下注时已经被扣除了，所以收益应该带着本金

				Long userSumAmount = userSumAmounts.get(history.getUser_id());
				if (null == userSumAmount) {
					userSumAmount = 0L;
				}
				userSumAmounts.put(history.getUser_id(), userSumAmount.longValue() + history.getAmount().longValue());
				if (settlement == 0) {
					losedHistoryIds.add(history.getId());
				} else if (RobotManager.getInstance().isRobot(history.getUser_id())) {
					robotWinHistorys.add(history);
				} else {
					normalWinHistorys.add(history);
				}
			}
			assert (losedHistoryIds.size() + robotWinHistorys.size() + normalWinHistorys.size() == historys.size());
			logger.info("update history begin");
			if (!__TEST__) {
				for (H history : normalWinHistorys) {
					try {
						updateHistroySettlement(history);
					} catch (SQLException e) {
						logger.error("", e);
						e.printStackTrace();
					}
				}
				logger.info("update normal win end");
				if (losedHistoryIds.size() > 0) {
					updateLosedHistroySettlements(losedHistoryIds);
				}
				logger.info("update lose end");
				new Thread(new Runnable() {
					@Override
					public void run() {
						for (H history : robotWinHistorys) {
							try {
								updateHistroySettlement(history);
							} catch (SQLException e) {
								logger.error("", e);
								e.printStackTrace();
							}
						}
						logger.info("update robot win end");
					}
				}).start();
			}
			logger.info("update history end");
			group.setEnd_time(new Date());
			group.setGameResult(gameResult);
			group.setState(2);
			group.setBanker_settlerment_result(bankerSettlement);
			group.setSystem_settlement_result(robotSumSettlement);
			if (!__TEST__) {
				updateGroupSettlement(group);
			}
			logger.info("update group end");

			UserDataHandler.addCarryAmount((int) group.getBanker_id(),
					group.getBanker_carry_amount() + bankerSettlement);
			logger.info("update banker info end");

			for (Entry<Integer, Long> entry : userSettlements.entrySet()) {
				Integer uid = entry.getKey();
				Long userSettlement = entry.getValue();
				// add players money
				UserDataHandler.addCarryAmount(uid, userSettlement);

				if (!IGNORE_ROBOT_STATISTICS_SETTLEMENT || !RobotManager.getInstance().isRobot(uid)) {
					// add settlement statistics
					StatisticsSettlement statisticsSettlement = new StatisticsSettlement();
					statisticsSettlement.setGroup_id(group.getId());
					statisticsSettlement.setUser_id(uid);
					statisticsSettlement.setResult(userSettlement - userSumAmounts.get(uid));
					statisticsSettlement.setCreate_time(new Date());
					if (!__TEST__) {
						insertStatisticsSettlement(statisticsSettlement);
					}
				}
			}
			logger.info("update player info end");
			if (!__TEST__) {
				this.updateStatistics(gameResult, groupId);
			}
			logger.info("update statistics end");
			systemSumSettlementPreDay += robotSumSettlement;
			if (!bankerIsRobot) {// 非机器人庄的时候统计输赢
				normalBankerSystemSumSettlementPreDay += robotSumSettlement;
			}
		} catch (SQLException e) {
			logger.error("", e);
			e.printStackTrace();
			return false;
		}
		return true;
	}

	protected abstract T makeGameResult();

	protected abstract double[] getOptionMultiples(G group, T gameResult);

	protected abstract G getSettlementGroup(int groupId) throws SQLException;

	protected abstract List<H> querySettlementHistorys(int groupId) throws SQLException;

	protected abstract long selectSumSystemSettlementResult(long beginTimestamp, long endTimestamp) throws SQLException;

	protected abstract long selectNormalBankerSumSystemSettlementResult(long beginTimestamp, long endTimestamp)
			throws SQLException;

	protected abstract void updateHistroySettlement(H history) throws SQLException;

	protected abstract void updateLosedHistroySettlements(List<Integer> ids) throws SQLException;

	protected abstract void updateStatistics(T gameResult, int groupId) throws SQLException;

	protected abstract void updateGroupSettlement(G group) throws SQLException;

	protected abstract void insertStatisticsSettlement(StatisticsSettlement statisticsSettlement) throws SQLException;
}
