package com.lxtx.settlement.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Random;

import com.lxtx.settlement.config.SettlementQueueConfig;
import com.lxtx.settlement.dbmodel.CarDialSettlementGroup;
import com.lxtx.settlement.dbmodel.CarDialSettlementHistory;
import com.lxtx.settlement.dbmodel.StatisticsSettlement;
import com.lxtx.settlement.handler.AnimalDialSettlementGroupHandler;
import com.lxtx.settlement.handler.CarDialSettlementGroupHandler;
import com.lxtx.settlement.handler.CarDialSettlementHistoryHandler;
import com.lxtx.settlement.handler.StatisticsSettlementHandler;
import com.lxtx.settlement.model.CarDialGameResult;
import com.lxtx.settlement.utils.CarDialSettlement;

public class CarDialSettlementService
		extends BaseSettlementService<CarDialGameResult, CarDialSettlementGroup, CarDialSettlementHistory> {

	public CarDialSettlementService() {
		super("car_dial_pre_time_robot_max_lose", "car_dial_pre_day_robot_max_lose",
				"car_dial_enforcement_intervention", "car_dial_enforcement_intervention_pre_time_max_lose",
				"car_dial_enforcement_intervention_lose_random_bound", "car_dial_robot_banker_pre_time_system_max_lose",
				"car_dial_normal_banker_pre_day_system_max_lose", SettlementQueueConfig.CAR_SETTLEMENT_CTRL_QUEUE,
				SettlementQueueConfig.CAR_SETTLEMENT_OPER_QUEUE);
	}

	@Override
	protected CarDialGameResult makeGameResult() {
		Random rand = new Random();
		CarDialGameResult gameResult = new CarDialGameResult();
		gameResult.result = CarDialSettlement.sumOptions[rand.nextInt(CarDialSettlement.sumOptions.length)];

		List<Integer> optionFlags = CarDialSettlement.getOptionFlags(gameResult.result);
		int result_flag;
		if (optionFlags.size() > 1) {
			int index = rand.nextInt(optionFlags.size());
			result_flag = optionFlags.get(index);
		} else {
			result_flag = optionFlags.get(0);
		}
		gameResult.result_flag = result_flag;
		return gameResult;
	}

	@Override
	protected double[] getOptionMultiples(CarDialSettlementGroup group, CarDialGameResult gameResult) {
		return CarDialSettlement.getAllTypeMultiples(gameResult.result - 1);
	}

	@Override
	protected long selectSumSystemSettlementResult(long beginTimestamp, long endTimestamp) throws SQLException {
		return CarDialSettlementGroupHandler.selectSumSystemSettlementResult(beginTimestamp, endTimestamp);
	}

	@Override
	protected long selectNormalBankerSumSystemSettlementResult(long beginTimestamp, long endTimestamp)
			throws SQLException {
		return CarDialSettlementGroupHandler.selectNormalBankerSumSystemSettlementResult(beginTimestamp, endTimestamp);
	}

	@Override
	protected void updateStatistics(CarDialGameResult gameResult, int groupId) throws SQLException {

	}

	@Override
	protected CarDialSettlementGroup getSettlementGroup(int groupId) throws SQLException {
		return CarDialSettlementGroupHandler.getSettlementGroup(groupId);
	}

	@Override
	protected List<CarDialSettlementHistory> querySettlementHistorys(int groupId) throws SQLException {
		return CarDialSettlementHistoryHandler.querySettlementHistorys(groupId);
	}

	@Override
	protected void updateHistroySettlement(CarDialSettlementHistory history) throws SQLException {
		CarDialSettlementHistoryHandler.updateHistroySettlement(history);
	}

	@Override
	protected void updateGroupSettlement(CarDialSettlementGroup group) throws SQLException {
		CarDialSettlementGroupHandler.updateGroupSettlement(group);
	}

	@Override
	protected void insertStatisticsSettlement(StatisticsSettlement statisticsSettlement) throws SQLException {
		StatisticsSettlementHandler.insertCarDialStatisticsSettlement(statisticsSettlement);
	}

	@Override
	protected void updateLosedHistroySettlements(List<Integer> ids) throws SQLException {
		CarDialSettlementHistoryHandler.updateLosedHistroySettlements(ids);
	}

}
