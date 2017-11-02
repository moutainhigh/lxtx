package com.lxtx.settlement.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Random;

import com.lxtx.settlement.config.SettlementQueueConfig;
import com.lxtx.settlement.dbmodel.DiceSettlementGroup;
import com.lxtx.settlement.dbmodel.DiceSettlementHistory;
import com.lxtx.settlement.dbmodel.StatisticsSettlement;
import com.lxtx.settlement.handler.AnimalDialSettlementGroupHandler;
import com.lxtx.settlement.handler.DiceSettlementGroupHandler;
import com.lxtx.settlement.handler.DiceSettlementHistoryHandler;
import com.lxtx.settlement.handler.StatisticsHandler;
import com.lxtx.settlement.handler.StatisticsSettlementHandler;
import com.lxtx.settlement.model.DiceGameResult;
import com.lxtx.settlement.utils.DiceSettlement;

public class DiceSettlementService
		extends BaseSettlementService<DiceGameResult, DiceSettlementGroup, DiceSettlementHistory> {

	public DiceSettlementService() {
		super("dice_pre_time_robot_max_lose", "dice_pre_day_robot_max_lose", "dice_enforcement_intervention",
				"dice_enforcement_intervention_pre_time_max_lose", "dice_enforcement_intervention_lose_random_bound",
				"dice_robot_banker_pre_time_system_max_lose", "dice_normal_banker_pre_day_system_max_lose",
				SettlementQueueConfig.DICE_SETTLEMENT_CTRL_QUEUE, SettlementQueueConfig.DICE_SETTLEMENT_OPER_QUEUE);
	}

	@Override
	protected DiceGameResult makeGameResult() {
		Random rand = new Random();
		int[] dices = new int[3];
		for (int i = 0; i < dices.length; i++) {
			dices[i] = rand.nextInt(6) + 1;
		}
		DiceGameResult diceGameResult = new DiceGameResult();
		diceGameResult.dices = dices;
		return diceGameResult;
	}

	@Override
	protected double[] getOptionMultiples(DiceSettlementGroup group, DiceGameResult gameResult) {
		return DiceSettlement.getAllTypeMultiples(gameResult.dices);
	}

	@Override
	protected long selectSumSystemSettlementResult(long beginTimestamp, long endTimestamp) throws SQLException {
		return DiceSettlementGroupHandler.selectSumSystemSettlementResult(beginTimestamp, endTimestamp);
	}

	@Override
	protected long selectNormalBankerSumSystemSettlementResult(long beginTimestamp, long endTimestamp)
			throws SQLException {
		return DiceSettlementGroupHandler.selectNormalBankerSumSystemSettlementResult(beginTimestamp, endTimestamp);
	}

	@Override
	protected void updateStatistics(DiceGameResult gameResult, int groupId) throws SQLException {
		int[] dices = gameResult.dices;
		int total = dices[0] + dices[1] + dices[2];
		if (total >= 4 && total <= 17) {
			StatisticsHandler.updateTotal(total, groupId);
		}
		if (dices[0] == dices[1] && dices[1] == dices[2]) {
			StatisticsHandler.updateTripple(dices[0], groupId);
			StatisticsHandler.updateDouble(dices[0], groupId);
		} else if (dices[0] == dices[1] || dices[0] == dices[2]) {
			StatisticsHandler.updateDouble(dices[0], groupId);
		} else if (dices[1] == dices[2]) {
			StatisticsHandler.updateDouble(dices[1], groupId);
		}
	}

	@Override
	protected DiceSettlementGroup getSettlementGroup(int groupId) throws SQLException {
		return DiceSettlementGroupHandler.getSettlementGroup(groupId);
	}

	@Override
	protected List<DiceSettlementHistory> querySettlementHistorys(int groupId) throws SQLException {
		return DiceSettlementHistoryHandler.querySettlementHistorys(groupId);
	}

	@Override
	protected void updateHistroySettlement(DiceSettlementHistory history) throws SQLException {
		DiceSettlementHistoryHandler.updateHistroySettlement(history);
	}

	@Override
	protected void updateGroupSettlement(DiceSettlementGroup group) throws SQLException {
		DiceSettlementGroupHandler.updateGroupSettlement(group);
	}

	@Override
	protected void insertStatisticsSettlement(StatisticsSettlement statisticsSettlement) throws SQLException {
		StatisticsSettlementHandler.insertDiceStatisticsSettlement(statisticsSettlement);
	}

	@Override
	protected void updateLosedHistroySettlements(List<Integer> ids) throws SQLException {
		DiceSettlementHistoryHandler.updateLosedHistroySettlements(ids);
	}

}
