package com.lxtx.robot.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.lxtx.robot.cache.GameCacheUtil;
import com.lxtx.robot.model.BetRateConfig;
import com.lxtx.robot.model.BetTarget;
import com.lxtx.robot.model.BetTimeRateConfig;

public abstract class RoomConfig {
	private final static Logger logger = LoggerFactory.getLogger(RoomConfig.class);
	// config for dice
	private static final String dice_extra_multiple_with_little = "dice_extra_multiple_with_little";
	private static final String dice_extra_multiple_with_large = "dice_extra_multiple_with_large";
	private static final String dice_extra_multiple_with_baozi_total = "dice_extra_multiple_with_baozi_total";
	private static final String dice_extra_multiple_with_baozi_single = "dice_extra_multiple_with_baozi_single";
	private static final String dice_extra_multiple_with_duizi_single = "dice_extra_multiple_with_duizi_single";
	private static final String dice_extra_multiple_with_sum_value_single = "dice_extra_multiple_with_sum_value_single";
	private static final String dice_extra_multiple_with_single = "dice_extra_multiple_with_single";
	private static final String dice_banker_min_gold = "dice_banker_min_gold";
	private static final String dice_bet_nums = "dice_bet_nums";
	private static final String dice_bet_num_probabilitys = "dice_bet_num_probabilitys";
	private static final String dice_bet_config = "dice_bet_config";
	private static final String dice_bet_nums_dynamic = "dice_bet_nums_dynamic";
	private static final String dice_bet_nums_multi_max = "dice_bet_nums_multi_max";

	private static final String car_dial_extra_multiple_with_normal = "car_dial_extra_multiple_with_normal";
	private static final String car_dial_extra_multiple_with_luxury = "car_dial_extra_multiple_with_luxury";
	private static final String car_dial_banker_min_gold = "car_dial_banker_min_gold";
	private static final String car_dial_bet_nums = "car_dial_bet_nums";
	private static final String car_dial_bet_num_probabilitys = "car_dial_bet_num_probabilitys";
	private static final String car_dial_bet_config = "car_dial_bet_config";
	private static final String car_dial_bet_nums_dynamic = "car_dial_bet_nums_dynamic";
	private static final String car_dial_bet_nums_multi_max = "car_dial_bet_nums_multi_max";

	private static final String animal_dial_extra_multiple_with_tiandi = "animal_dial_extra_multiple_with_tiandi";
	private static final String animal_dial_extra_multiple_with_wuxing = "animal_dial_extra_multiple_with_wuxing";
	private static final String animal_dial_extra_multiple_with_animal = "animal_dial_extra_multiple_with_animal";
	private static final String animal_dial_extra_multiple_with_combined_two = "animal_dial_extra_multiple_with_combined_two";
	private static final String animal_dial_extra_multiple_with_combined_three = "animal_dial_extra_multiple_with_combined_three";
	private static final String animal_dial_banker_min_gold = "animal_dial_banker_min_gold";
	private static final String animal_dial_bet_nums = "animal_dial_bet_nums";
	private static final String animal_dial_bet_num_probabilitys = "animal_dial_bet_num_probabilitys";
	private static final String animal_dial_bet_config = "animal_dial_bet_config";
	private static final String animal_dial_bet_nums_dynamic = "animal_dial_bet_nums_dynamic";
	private static final String animal_dial_bet_nums_multi_max = "animal_dial_bet_nums_multi_max";

	private static double[] DICE_OPTION_MULTIPLES = { 1.96, 1.96, // 大小
			36, // 豹子通选
			200, 200, 200, 200, 200, 200, // 豹子单选
			10, 10, 10, 10, 10, 10, // 对子单选
			50, 18, 14, 12, 8, 6, 6, 6, 6, 8, 12, 14, 18, 50, // 和值单选
			3, 3, 3, 3, 3, 3 };// 单筛子

	// config for car
	private static final double CAR_DIAL_MULTIPLE_FOR_NORMAL = 5;
	private static final double CAR_DIAL_MULTIPLE_FOR_LUXURY = 20;
	private static final int CAR_DIAL_OPTION_TYPE_COUNT = 8;

	// config for animal
	public static final int ANIMAL_DIAL_OPTION_TYPE_ANIMAL_BEGIN = 1;// 十二生肖开始索引
	public static final int ANIMAL_DIAL_OPTION_COUNT_FOR_ANIMAL = 12;
	public static final int ANIMAL_DIAL_OPTION_TYPE_WU_XING_BEGIN = ANIMAL_DIAL_OPTION_TYPE_ANIMAL_BEGIN
			+ ANIMAL_DIAL_OPTION_COUNT_FOR_ANIMAL;// 五行开始索引,13
	public static final int ANIMAL_DIAL_OPTION_COUNT_FOR_WU_XING = 5;
	public static final int ANIMAL_DIAL_OPTION_TYPE_TIAN_DI_BEGIN = ANIMAL_DIAL_OPTION_TYPE_WU_XING_BEGIN
			+ ANIMAL_DIAL_OPTION_COUNT_FOR_WU_XING;// 天地开始索引,18
	public static final int ANIMAL_DIAL_OPTION_COUNT_FOR_TIAN_DI = 2;
	public static final int ANIMAL_DIAL_OPTION_TYPE_COMBINED_TWO = ANIMAL_DIAL_OPTION_TYPE_TIAN_DI_BEGIN
			+ ANIMAL_DIAL_OPTION_COUNT_FOR_TIAN_DI;// 20,二串一
	public static final int ANIMAL_DIAL_OPTION_TYPE_COMBINED_THREE = ANIMAL_DIAL_OPTION_TYPE_COMBINED_TWO + 1;// 21,三串一
	public static final int ANIMAL_DIAL_OPTION_TYPE_COUNT = ANIMAL_DIAL_OPTION_TYPE_COMBINED_THREE;

	public static final double ANIMAL_DIAL_MULTIPLE_FOR_ANIMAL = 12;
	public static final double ANIMAL_DIAL_MULTIPLE_FOR_TIAN_DI = 1.96;
	public static final double ANIMAL_DIAL_MULTIPLE_FOR_WUXING = 5;
	public static final double ANIMAL_DIAL_MULTIPLE_FOR_COMBINED_TWO = 24;
	public static final double ANIMAL_DIAL_MULTIPLE_FOR_COMBINED_THREE = 120;

	private static double getCarDialMultiple(int index) {
		return (index % 2 == 0)
				? (CAR_DIAL_MULTIPLE_FOR_LUXURY
						* GameCacheUtil.getSystemConfigCache().getInt(car_dial_extra_multiple_with_luxury))
				: (CAR_DIAL_MULTIPLE_FOR_NORMAL
						* GameCacheUtil.getSystemConfigCache().getInt(car_dial_extra_multiple_with_normal));
	}

	private static double getAnimalDialMultiples(int option) {//
		assert (option >= ANIMAL_DIAL_OPTION_TYPE_ANIMAL_BEGIN && option <= ANIMAL_DIAL_OPTION_TYPE_COUNT);
		if (option < ANIMAL_DIAL_OPTION_TYPE_WU_XING_BEGIN) {
			return ANIMAL_DIAL_MULTIPLE_FOR_ANIMAL
					* GameCacheUtil.getSystemConfigCache().getInt(animal_dial_extra_multiple_with_animal);
		} else if (option < ANIMAL_DIAL_OPTION_TYPE_TIAN_DI_BEGIN) {
			return ANIMAL_DIAL_MULTIPLE_FOR_WUXING
					* GameCacheUtil.getSystemConfigCache().getInt(animal_dial_extra_multiple_with_wuxing);
		} else if (option < ANIMAL_DIAL_OPTION_TYPE_COMBINED_TWO) {
			return ANIMAL_DIAL_MULTIPLE_FOR_TIAN_DI
					* GameCacheUtil.getSystemConfigCache().getInt(animal_dial_extra_multiple_with_tiandi);
		} else if (option == ANIMAL_DIAL_OPTION_TYPE_COMBINED_TWO) {
			return ANIMAL_DIAL_MULTIPLE_FOR_COMBINED_TWO
					* GameCacheUtil.getSystemConfigCache().getInt(animal_dial_extra_multiple_with_combined_two);
		} else if (option == ANIMAL_DIAL_OPTION_TYPE_COMBINED_THREE) {
			return ANIMAL_DIAL_MULTIPLE_FOR_COMBINED_THREE
					* GameCacheUtil.getSystemConfigCache().getInt(animal_dial_extra_multiple_with_combined_three);
		}
		return 0;
	}

	public static RoomConfig getDiceRoomConfig() {
		RoomConfig config = new RoomConfig(dice_banker_min_gold, dice_bet_nums, dice_bet_nums_dynamic,
				dice_bet_nums_multi_max, dice_bet_num_probabilitys, dice_bet_config) {
			@Override
			public void loadBetTargets() {
				this.betTargets = new BetTarget[DICE_OPTION_MULTIPLES.length];
				for (int i = 0; i < DICE_OPTION_MULTIPLES.length; i++) {
					int extraMulti = 1;
					if (0 == i) {
						extraMulti = GameCacheUtil.getSystemConfigCache().getInt(dice_extra_multiple_with_little);
					} else if (1 == i) {
						extraMulti = GameCacheUtil.getSystemConfigCache().getInt(dice_extra_multiple_with_large);
					} else if (i == 2) {
						extraMulti = GameCacheUtil.getSystemConfigCache().getInt(dice_extra_multiple_with_baozi_total);
					} else if (i < 3 + 6) {
						extraMulti = GameCacheUtil.getSystemConfigCache().getInt(dice_extra_multiple_with_baozi_single);
					} else if (i < 3 + 6 + 6) {
						extraMulti = GameCacheUtil.getSystemConfigCache().getInt(dice_extra_multiple_with_duizi_single);
					} else if (i < 3 + 6 + 6 + 14) {
						extraMulti = GameCacheUtil.getSystemConfigCache()
								.getInt(dice_extra_multiple_with_sum_value_single);
					} else {
						extraMulti = GameCacheUtil.getSystemConfigCache().getInt(dice_extra_multiple_with_single);
					}
					this.betTargets[i] = new BetTarget();
					this.betTargets[i].option = i + 1;
					this.betTargets[i].multiple = DICE_OPTION_MULTIPLES[i] * extraMulti;
				}
			}
		};
		config.loadBetTargets();
		config.loadBankerMinGold();
		config.loadBetNumConfigs();
		config.loadBetConfig();

		config.apply_banker_delay_time_scope = new int[] { 30 * 1000, 60 * 1000 };
		config.apply_cancel_banker_delay_time_scope = new int[] { 60 * 1000, 180 * 1000 };
		// config.bet_delay_time_scope = new int[] { 500, 1 * 1000 };
		// config.pre_time_schedule_bet_count_scope = new int[] { 10, 20 };
		return config;
	}

	public static RoomConfig getCarDialRoomConfig() {
		RoomConfig config = new RoomConfig(car_dial_banker_min_gold, car_dial_bet_nums, car_dial_bet_nums_dynamic,
				car_dial_bet_nums_multi_max, car_dial_bet_num_probabilitys, car_dial_bet_config) {
			@Override
			public void loadBetTargets() {
				this.betTargets = new BetTarget[CAR_DIAL_OPTION_TYPE_COUNT];
				for (int i = 0; i < CAR_DIAL_OPTION_TYPE_COUNT; i++) {
					this.betTargets[i] = new BetTarget();
					this.betTargets[i].option = i + 1;
					this.betTargets[i].multiple = getCarDialMultiple(i);
				}
			}
		};
		config.loadBetTargets();
		config.loadBankerMinGold();
		config.loadBetNumConfigs();
		config.loadBetConfig();

		config.apply_banker_delay_time_scope = new int[] { 30 * 1000, 60 * 1000 };
		config.apply_cancel_banker_delay_time_scope = new int[] { 60 * 1000, 180 * 1000 };
		// config.bet_delay_time_scope = new int[] { 500, 1 * 1000 };
		// config.pre_time_schedule_bet_count_scope = new int[] { 10, 20 };
		return config;
	}

	public static RoomConfig getAnimalDialRoomConfig() {
		RoomConfig config = new RoomConfig(animal_dial_banker_min_gold, animal_dial_bet_nums,
				animal_dial_bet_nums_dynamic, animal_dial_bet_nums_multi_max, animal_dial_bet_num_probabilitys,
				animal_dial_bet_config) {
			@Override
			public void loadBetTargets() {
				this.betTargets = new BetTarget[ANIMAL_DIAL_OPTION_TYPE_COUNT];
				for (int i = 0; i < ANIMAL_DIAL_OPTION_TYPE_COUNT; i++) {
					this.betTargets[i] = new BetTarget();
					this.betTargets[i].option = i + 1;
					this.betTargets[i].multiple = getAnimalDialMultiples(i + 1);
				}
			}
		};
		config.loadBetTargets();
		config.loadBankerMinGold();
		config.loadBetNumConfigs();
		config.loadBetConfig();

		config.apply_banker_delay_time_scope = new int[] { 30 * 1000, 60 * 1000 };
		config.apply_cancel_banker_delay_time_scope = new int[] { 60 * 1000, 180 * 1000 };
		// config.bet_delay_time_scope = new int[] { 500, 1 * 1000 };
		// config.pre_time_schedule_bet_count_scope = new int[] { 10, 20 };
		return config;
	}

	private RoomConfig(String banker_min_gold_key, String bet_nums_key, String bet_nums_dynamic_key,
			String bet_nums_multi_max_key, String bet_num_probabilitys_key, String bet_config_key) {
		this.banker_min_gold_key = banker_min_gold_key;
		this.bet_nums_key = bet_nums_key;
		this.bet_nums_dynamic_key = bet_nums_dynamic_key;
		this.bet_nums_multi_max_key = bet_nums_multi_max_key;
		this.bet_num_probabilitys_key = bet_num_probabilitys_key;
		this.bet_config_key = bet_config_key;
	}

	public abstract void loadBetTargets();

	public void loadBankerMinGold() {
		this.bankerMinGold = GameCacheUtil.getSystemConfigCache().getInt(banker_min_gold_key);
	}

	public void loadBetNumConfigs() {
		String betNumsConfig = GameCacheUtil.getSystemConfigCache().get(bet_nums_key);
		String[] bns = betNumsConfig.split(",");
		this.betNums = new int[bns.length];
		for (int i = 0; i < bns.length; i++) {
			this.betNums[i] = Integer.parseInt(bns[i].trim());
		}
		this.betNumsDynamic = GameCacheUtil.getSystemConfigCache().getBoolean(bet_nums_dynamic_key);
		this.betNumsMultiMax = GameCacheUtil.getSystemConfigCache().getInt(bet_nums_multi_max_key);
		String betNumProbabilitysConfig = GameCacheUtil.getSystemConfigCache().get(bet_num_probabilitys_key);
		String[] bnps = betNumProbabilitysConfig.split(",");
		this.betNumProbabilitys = new int[bnps.length];
		for (int i = 0; i < bnps.length; i++) {
			this.betNumProbabilitys[i] = Integer.parseInt(bnps[i].trim());
		}
	}

	public void loadBetConfig() {
		String betConfig = GameCacheUtil.getSystemConfigCache().get(bet_config_key);
		if (!betConfig.equals(lastBetRateConfig)) {
			Gson gson = new Gson();
			this.betRateConfig = gson.fromJson(betConfig, BetRateConfig.class);
			this.lastBetRateConfig = betConfig;
		}
	}

	public int getRandomBetCount(long currentTime) {
		int bet_count_min = this.betRateConfig.getBet_count_min();
		int bet_count_max = this.betRateConfig.getBet_count_max();
		String currentDay = new SimpleDateFormat("yyyyMMdd").format(new Date(currentTime));
		try {
			for (BetTimeRateConfig betTimeRateConfig : this.betRateConfig.getSpecial()) {
				Date beginDate = new SimpleDateFormat("yyyyMMdd HH:mm:ss")
						.parse(currentDay + " " + betTimeRateConfig.getTime_begin());
				Date endDate = new SimpleDateFormat("yyyyMMdd HH:mm:ss")
						.parse(currentDay + " " + betTimeRateConfig.getTime_end());
				if (currentTime >= beginDate.getTime() && currentTime <= endDate.getTime()) {
					bet_count_min = betTimeRateConfig.getBet_count_min();
					bet_count_max = betTimeRateConfig.getBet_count_max();
					break;
				}
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		Random rand = new Random(currentTime);
		return rand.nextInt(bet_count_max - bet_count_min) + bet_count_min;
	}

	public int getRandomBetDelayTime(long currentTime) {
		int bet_delay_time_min = this.betRateConfig.getBet_delay_time_min();
		int bet_delay_time_max = this.betRateConfig.getBet_delay_time_max();
		String currentDay = new SimpleDateFormat("yyyyMMdd").format(new Date(currentTime));
		try {
			for (BetTimeRateConfig betTimeRateConfig : this.betRateConfig.getSpecial()) {
				Date beginDate = new SimpleDateFormat("yyyyMMdd HH:mm:ss")
						.parse(currentDay + " " + betTimeRateConfig.getTime_begin());
				Date endDate = new SimpleDateFormat("yyyyMMdd HH:mm:ss")
						.parse(currentDay + " " + betTimeRateConfig.getTime_end());
				if (currentTime >= beginDate.getTime() && currentTime <= endDate.getTime()) {
					bet_delay_time_min = betTimeRateConfig.getBet_delay_time_min();
					bet_delay_time_max = betTimeRateConfig.getBet_delay_time_max();
					break;
				}
			}
		} catch (Exception e) {
			logger.error("", e);
		}
		Random rand = new Random(currentTime);
		return rand.nextInt(bet_delay_time_max - bet_delay_time_min) + bet_delay_time_min;
	}

	private String banker_min_gold_key;
	private String bet_nums_key;
	private String bet_nums_dynamic_key;
	private String bet_num_probabilitys_key;
	private String bet_nums_multi_max_key;
	private String bet_config_key;
	// public int startWaitTime;// 开局等待时间ms
	// public int betTime;// 下注总时长ms
	// public int settlementTime;// 结算总时长ms
	// public int bankerContinuousMaxCount;// 连续坐庄次数
	public long bankerMinGold;// 最低上庄金额
	// public int deductPercent;// 手续费百分比
	public int[] betNums;// 下注金额选项
	public boolean betNumsDynamic;
	public int betNumsMultiMax;
	public int[] betNumProbabilitys;// 下注金额出现概率
	public BetTarget[] betTargets;

	public int[] apply_banker_delay_time_scope;// 执行申请上庄任务的延迟时间范围
	public int[] apply_cancel_banker_delay_time_scope;// 执行申请上庄任务的延迟时间范围
	private BetRateConfig betRateConfig;
	private String lastBetRateConfig;
	// public int[] bet_delay_time_scope;// 执行下注任务的延迟时间范围
	// public int[] pre_time_schedule_bet_count_scope;// 执行下注任务的下注次数
}
