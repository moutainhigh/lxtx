package com.lxtech.biz;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxtech.dao.CloudHumanAdjuster;
import com.lxtech.dao.CloudSystemConfigDao;
import com.lxtech.dao.CloudUserDao;
import com.lxtech.model.CloudHumanControlConfig;
import com.lxtech.model.CloudUser;
import com.lxtech.util.TimeUtil;

public class HumanControlService {
	private final static Logger logger = LoggerFactory.getLogger(HumanControlService.class);

	public static void humanAdjuster() {
		Date now = new Date();

		String begin = TimeUtil.formatDate(now, "yyyy-MM-dd");
		String end = TimeUtil.formatDateCommon(now, "yyyy-MM-dd", 1);

		try {
			// 如果任务可以执行，则获取渠道号，并随机获取当天订单量大于3的该渠道用户id；随机获取产品
			// 1.查询所有待下订单的渠道
			List<CloudHumanControlConfig> humanConfigs = CloudHumanAdjuster.queryHumanControlConfig();
			// 获取标的物的subjects
			String subVals = CloudSystemConfigDao.getSystemConfig("active.subject");
			List<String> subjects = new ArrayList<>();
			if (StringUtils.isNotEmpty(subVals)) {
				subjects = Arrays.asList(subVals.split(","));
			}
			if (null != humanConfigs && humanConfigs.size() > 0) {
				for (CloudHumanControlConfig humanConfig : humanConfigs) {
					// 当前时间大于等于配置的时间
					if (TimeUtil.compareDate(now, humanConfig.getRun_time())) {
						if (null != humanConfig.getNeed_run_count() && null != humanConfig.getRuned_count()
								&& humanConfig.getNeed_run_count().intValue() > humanConfig.getRuned_count()
										.intValue()) {
							if (null != humanConfig.getUid() && humanConfig.getUid() != 0) {
								CloudUser u = CloudUserDao.queryUserByUid(humanConfig.getUid());
								if (u != null) {
									// 3.下单
									CloudHumanAdjuster.placeOrder(u.getId(), humanConfig.getOrder_money(),
											humanConfig.getOrder_num(), humanConfig.getChnno(), 0.15f, true, subjects);
									int repayAmount = (new BigDecimal(
											humanConfig.getOrder_money() * humanConfig.getOrder_num() * 0.7))
													.intValue();
									// 更新runed_count及状态
									if (humanConfig.getNeed_run_count()
											.intValue() == humanConfig.getRuned_count().intValue() + 1) {
										humanConfig.setStatus(1);
									}
									CloudHumanAdjuster.updateHumanControlConfig(humanConfig);
									// 提现
									CloudHumanAdjuster.repayInBatch(u.getId(), repayAmount);
								} else {
									logger.info("not fount the user with id=" + humanConfig.getUid());
								}
							} else {
								// 2.查询符合的用户
								List<CloudUser> users = CloudHumanAdjuster.queryAvaliableUser(begin, end,
										humanConfig.getChnno());
								if (null != users && users.size() > 0) {
									Random rand = new Random();
									int index = rand.nextInt(users.size());
									CloudUser u = users.get(index);
									// 3.下单
									CloudHumanAdjuster.placeOrder(u.getId(), humanConfig.getOrder_money(),
											humanConfig.getOrder_num(), humanConfig.getChnno(), 0.15f, true, subjects);
									int repayAmount = (new BigDecimal(
											humanConfig.getOrder_money() * humanConfig.getOrder_num() * 0.7))
													.intValue();
									// 更新runed_count及状态
									if (humanConfig.getNeed_run_count()
											.intValue() == humanConfig.getRuned_count().intValue() + 1) {
										humanConfig.setStatus(1);
									}
									CloudHumanAdjuster.updateHumanControlConfig(humanConfig);
									// 提现
									CloudHumanAdjuster.repayInBatch(u.getId(), repayAmount);
								} else {
									logger.info("not found avaliable user!");
								}
							}
						}
					}
				}
			} else {
				logger.info("no need human controlConfig to run!");
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}

	}

	private void humanAdjuster(Date now) {

		String begin = TimeUtil.formatDate(now, "yyyy-MM-dd");
		String end = TimeUtil.formatDateCommon(now, "yyyy-MM-dd", 1);

		try {
			// 如果任务可以执行，则获取渠道号，并随机获取当天订单量大于3的该渠道用户id；随机获取产品
			// 1.查询所有待下订单的渠道
			List<CloudHumanControlConfig> humanConfigs = CloudHumanAdjuster.queryHumanControlConfig();
			// 获取标的物的subjects
			String subVals = CloudSystemConfigDao.getSystemConfig("active.subject");
			List<String> subjects = new ArrayList<>();
			if (StringUtils.isNotEmpty(subVals)) {
				subjects = Arrays.asList(subVals.split(","));
			}
			if (null != humanConfigs && humanConfigs.size() > 0) {
				for (CloudHumanControlConfig humanConfig : humanConfigs) {
					// 当前时间大于等于配置的时间
					if (TimeUtil.compareDate(now, humanConfig.getRun_time())) {
						if (null != humanConfig.getNeed_run_count() && null != humanConfig.getRuned_count()
								&& humanConfig.getNeed_run_count().intValue() > humanConfig.getRuned_count()
										.intValue()) {
							if (null != humanConfig.getUid() && humanConfig.getUid() != 0) {
								CloudUser u = CloudUserDao.queryUserByUid(humanConfig.getUid());
								if (u != null) {
									// 3.下单
									// CloudHumanAdjuster.placeOrder(u.getId(),
									// humanConfig.getOrder_money(),
									// humanConfig.getOrder_num(),
									// humanConfig.getChnno(), 0.15f,
									// true,subjects);
									// int repayAmount = (new BigDecimal(
									// humanConfig.getOrder_money() *
									// humanConfig.getOrder_num() * 0.7))
									// .intValue();
									// 更新runed_count及状态
									if (humanConfig.getNeed_run_count()
											.intValue() == humanConfig.getRuned_count().intValue() + 1) {
										humanConfig.setStatus(1);
									}
									CloudHumanAdjuster.updateHumanControlConfig(humanConfig);
									// 提现
									// CloudHumanAdjuster.repayInBatch(u.getId(),
									// repayAmount);
								} else {
									logger.info("not fount the user with id=" + humanConfig.getUid());
								}
							} else {
								// 2.查询符合的用户
								List<CloudUser> users = CloudHumanAdjuster.queryAvaliableUser(begin, end,
										humanConfig.getChnno());
								if (null != users && users.size() > 0) {
									Random rand = new Random();
									int index = rand.nextInt(users.size());
									CloudUser u = users.get(index);
									// 3.下单
									// CloudHumanAdjuster.placeOrder(u.getId(),
									// humanConfig.getOrder_money(),
									// humanConfig.getOrder_num(),
									// humanConfig.getChnno(), 0.15f,
									// true,subjects);
									// int repayAmount = (new BigDecimal(
									// humanConfig.getOrder_money() *
									// humanConfig.getOrder_num() *
									// 0.7)).intValue();
									// 更新runed_count及状态
									if (humanConfig.getNeed_run_count()
											.intValue() == humanConfig.getRuned_count().intValue() + 1) {
										humanConfig.setStatus(1);
									}
									CloudHumanAdjuster.updateHumanControlConfig(humanConfig);
									// 提现
									// CloudHumanAdjuster.repayInBatch(u.getId(),
									// repayAmount);
								} else {
									logger.info("not found avaliable user!");
								}
							}
						}
					}
				}
			} else {
				logger.info("no need human controlConfig to run!");
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		// HumanControlService s = new HumanControlService();
		// s.humanAdjuster(TimeUtil.formatDateCommon(new Date(), 0));
		String subVals;
		try {
			subVals = CloudSystemConfigDao.getSystemConfig("active.subject");
			List<String> subjects = new ArrayList<>();
			if (StringUtils.isNotEmpty(subVals)) {
				subjects = Arrays.asList(subVals.split(","));
			}
			System.out.println(subjects.size());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
