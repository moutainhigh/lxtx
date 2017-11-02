package com.lxtx.settlement.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Random;

import com.lxtx.settlement.config.SettlementQueueConfig;
import com.lxtx.settlement.dbmodel.AnimalDialSettlementGroup;
import com.lxtx.settlement.dbmodel.AnimalDialSettlementHistory;
import com.lxtx.settlement.dbmodel.StatisticsSettlement;
import com.lxtx.settlement.handler.AnimalDialSettlementGroupHandler;
import com.lxtx.settlement.handler.AnimalDialSettlementHistoryHandler;
import com.lxtx.settlement.handler.StatisticsSettlementHandler;
import com.lxtx.settlement.model.AnimalGameResult;
import com.lxtx.settlement.utils.AnimalDialSettlement;

public class AnimalDialSettlementService
		extends BaseSettlementService<AnimalGameResult, AnimalDialSettlementGroup, AnimalDialSettlementHistory> {

	public AnimalDialSettlementService() {
		super("animal_dial_pre_time_robot_max_lose", "animal_dial_pre_day_robot_max_lose",
				"animal_dial_enforcement_intervention", "animal_dial_enforcement_intervention_pre_time_max_lose",
				"animal_dial_enforcement_intervention_lose_random_bound",
				"animal_dial_robot_banker_pre_time_system_max_lose",
				"animal_dial_normal_banker_pre_day_system_max_lose", SettlementQueueConfig.ANIMAL_SETTLEMENT_CTRL_QUEUE,
				SettlementQueueConfig.ANIMAL_SETTLEMENT_OPER_QUEUE);
	}

	@Override
	protected AnimalGameResult makeGameResult() {
		Random rand = new Random();
		AnimalGameResult gameResult = new AnimalGameResult();
		gameResult.resultTiandi = rand.nextInt(AnimalDialSettlement.OPTION_COUNT_FOR_TIAN_DI)
				+ AnimalDialSettlement.OPTION_TYPE_TIAN_DI_BEGIN;
		gameResult.resultWuxing = rand.nextInt(AnimalDialSettlement.OPTION_COUNT_FOR_WU_XING)
				+ AnimalDialSettlement.OPTION_TYPE_WU_XING_BEGIN;
		gameResult.resultAnimal = rand.nextInt(AnimalDialSettlement.OPTION_COUNT_FOR_ANIMAL)
				+ AnimalDialSettlement.OPTION_TYPE_ANIMAL_BEGIN;
		return gameResult;
	}

	@Override
	protected double[] getOptionMultiples(AnimalDialSettlementGroup group, AnimalGameResult gameResult) {
		int[] combinedTwo = new int[2];
		combinedTwo[0] = group.getCombined_two_tiandi() - AnimalDialSettlement.OPTION_TYPE_TIAN_DI_BEGIN;
		combinedTwo[1] = group.getCombined_two_animal() - AnimalDialSettlement.OPTION_TYPE_ANIMAL_BEGIN;

		int[] combinedThree = new int[3];
		combinedThree[0] = group.getCombined_three_tiandi() - AnimalDialSettlement.OPTION_TYPE_TIAN_DI_BEGIN;
		combinedThree[1] = group.getCombined_three_wuxing() - AnimalDialSettlement.OPTION_TYPE_WU_XING_BEGIN;
		combinedThree[2] = group.getCombined_three_animal() - AnimalDialSettlement.OPTION_TYPE_ANIMAL_BEGIN;

		double[] optionMultiples = AnimalDialSettlement.getAllTypeMultiples(combinedTwo, combinedThree,
				gameResult.resultTiandi - AnimalDialSettlement.OPTION_TYPE_TIAN_DI_BEGIN,
				gameResult.resultWuxing - AnimalDialSettlement.OPTION_TYPE_WU_XING_BEGIN,
				gameResult.resultAnimal - AnimalDialSettlement.OPTION_TYPE_ANIMAL_BEGIN);
		return optionMultiples;
	}

	@Override
	protected AnimalDialSettlementGroup getSettlementGroup(int groupId) throws SQLException {
		return AnimalDialSettlementGroupHandler.getSettlementGroup(groupId);
	}

	@Override
	protected List<AnimalDialSettlementHistory> querySettlementHistorys(int groupId) throws SQLException {
		return AnimalDialSettlementHistoryHandler.querySettlementHistorys(groupId);
	}

	@Override
	protected long selectSumSystemSettlementResult(long beginTimestamp, long endTimestamp) throws SQLException {
		return AnimalDialSettlementGroupHandler.selectSumSystemSettlementResult(beginTimestamp, endTimestamp);
	}

	@Override
	protected long selectNormalBankerSumSystemSettlementResult(long beginTimestamp, long endTimestamp)
			throws SQLException {
		return AnimalDialSettlementGroupHandler.selectNormalBankerSumSystemSettlementResult(beginTimestamp,
				endTimestamp);
	}

	@Override
	protected void updateHistroySettlement(AnimalDialSettlementHistory history) throws SQLException {
		AnimalDialSettlementHistoryHandler.updateHistroySettlement(history);
	}

	@Override
	protected void updateStatistics(AnimalGameResult gameResult, int groupId) throws SQLException {
	}

	@Override
	protected void updateGroupSettlement(AnimalDialSettlementGroup group) throws SQLException {
		AnimalDialSettlementGroupHandler.updateGroupSettlement(group);
	}

	@Override
	protected void insertStatisticsSettlement(StatisticsSettlement statisticsSettlement) throws SQLException {
		StatisticsSettlementHandler.insertAnimalDialStatisticsSettlement(statisticsSettlement);
	}

	@Override
	protected void updateLosedHistroySettlements(List<Integer> ids) throws SQLException {
		AnimalDialSettlementHistoryHandler.updateLosedHistroySettlements(ids);
	}

}
