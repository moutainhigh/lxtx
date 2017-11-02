package com.lxtx.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lxtx.dao.CloudClearDetailMapper;
import com.lxtx.dao.CloudCouponMapper;
import com.lxtx.dao.CloudDayCensusMapper;
import com.lxtx.dao.CloudFundHistoryMapper;
import com.lxtx.dao.CloudOrderMapper;
import com.lxtx.dao.CloudSystemTaskDetailMapper;
import com.lxtx.dao.CloudUserLimitMapper;
import com.lxtx.dao.CloudUserMapper;
import com.lxtx.model.CloudClearDetail;
import com.lxtx.model.CloudDayCensus;
import com.lxtx.model.CloudOrder;
import com.lxtx.model.CloudSystemTaskDetail;
import com.lxtx.model.CloudTarget;
import com.lxtx.model.CloudUser;
import com.lxtx.model.vo.SubjectDaySumVo;
import com.lxtx.service.target.TargetService;
import com.lxtx.util.Constant;
import com.lxtx.util.StringUtil;

@Service
public class DayClearService {
	private static final Logger logger = LoggerFactory.getLogger(DayClearService.class);
	private static final String dayClearTime = " 04:00:00";
	@Autowired
	private CloudOrderMapper cloudOrderMapper;
	@Autowired
	private CloudUserMapper cloudUserMapper;

	@Autowired
	private CloudDayCensusMapper cloudCensusMapper;

	@Autowired
	private TargetService targetService;

	@Autowired
	private CloudClearDetailMapper cloudClearDetailMapper;

	@Autowired
	private CloudUserLimitMapper cloudUserLimitMapper;

	@Autowired
	private CloudFundHistoryMapper cloudFundHistoryMapper;

	@Autowired
	private CloudSystemTaskDetailMapper cloudSystemTaskDetailMapper;

	@Autowired
	private CloudCouponMapper cloudCouponMapper;

	/**
	 * 日终清算任务
	 */
	public void dayClearTask() {
		Date begin_clear = new Date();
		String nowDate = StringUtil.formatDate(begin_clear);
		logger.info("dayClear Task begin:" + nowDate);
		// 获取日终日期区间
		String clearDate = StringUtil.formatDateCommon(begin_clear, "yyyy-MM-dd", 0);
		String preClearDate = StringUtil.formatDateCommon(begin_clear, "yyyy-MM-dd", -1).concat(dayClearTime);
		String nowClearDate = clearDate.concat(dayClearTime);

		closeDayTask(preClearDate, nowClearDate);
		// markLog(begin_clear, "【1】closeDay Task");

		CloudDayCensus dayCensus = new CloudDayCensus();
		dayCensus.setOrderDate(StringUtil.parseStringToDate10(clearDate));
		dayCensus = sumDayProfitTask(dayCensus, preClearDate, nowClearDate);
		// markLog(begin_clear, "【2】sumDayProfit Task");

		dayCensus = sumDayOrderTask(dayCensus, preClearDate, nowClearDate);
		logger.debug(dayCensus.toString());
		// 入库
		cloudCensusMapper.insertSelective(dayCensus);
		// markLog(begin_clear, "【3】sumDayOrder Task");

		// 清空用户表的限额数据
		cloudUserLimitMapper.clearAllUserData();
		// markLog(begin_clear, "【4】clearAllUserData Task");

		markLog(begin_clear, "dayClear");
	}

	/**
	 * 轮巡结算任务
	 */
	public void closeOrderTask() {
		Date begin_clear = new Date();
		String nowDate = StringUtil.formatDate(begin_clear);
		logger.info("closeOrder Task begin:" + nowDate);
		// 查询全部为状态为处理中的订单
		// 批量平仓
		List<CloudOrder> orderList = cloudOrderMapper.selectByStatus(Constant.ORDER_STAT_TREATING);
		CloudClearDetail cloudClearDetail = new CloudClearDetail();
		// 系统赢的笔数及金额
		int profitCount = 0;
		BigDecimal profitAmount = new BigDecimal(0);
		// 系统亏的笔数及金额
		int lossCount = 0;
		BigDecimal lossAmount = new BigDecimal(0);
		if (null != orderList && orderList.size() > 0) {
			boolean gainMoney = false;
			Map<String, CloudTarget> targetMap = targetService.listTargets();
			// update the status for these orders
			for (CloudOrder order : orderList) {
				order.setCleared(1);
				if (order.getDirection() == 1 && order.getClearIndex() >= order.getClearUpperLimit()) {
					gainMoney = true;
					// 赢钱 做多的情况下，达到止盈点
					order.setStatus(Constant.ORDER_STAT_PROFIT);
					order.setClearIndex(order.getClearUpperLimit());
					order.setfProfit(order.getSysLoss());
					lossCount += 1;
					lossAmount = lossAmount.subtract(order.getSysLoss());
				} else if (order.getDirection() == 2 && order.getClearIndex() <= order.getClearDownLimit()) {
					// 做空的情况下，达到止损点
					gainMoney = true;
					order.setStatus(Constant.ORDER_STAT_PROFIT);
					order.setClearIndex(order.getClearDownLimit());
					order.setfProfit(order.getSysLoss());
					lossCount += 1;
					lossAmount = lossAmount.subtract(order.getSysLoss());
				} else {
					// 亏钱
					gainMoney = false;
					order.setfProfit(order.getSysProfit());
					if (null != order.getHuman() && order.getHuman().intValue() == 1) {
						order.setStatus(Constant.ORDER_STAT_HUMAN);
					} else {
						order.setStatus(Constant.ORDER_STAT_LOSS);
						profitCount += 1;
						profitAmount = profitAmount.add(order.getSysProfit());
					}
				}
				order.setClearTime(new Date());
				CloudTarget target = targetMap.get(order.getSubject());
				if (target == null) {
					continue;
				}
				try {
					closeSingleOrder(targetMap, order, gainMoney);
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e.getMessage());
				}
			}

		}
		// 组装每次平仓的统计数据 并入库
		packageClearDetail(orderList, cloudClearDetail, profitCount, profitAmount, lossCount, lossAmount);
		markLog(begin_clear, "closeOrder");
	}

	/**
	 * 组装每次平仓的统计数据 并入库
	 * 
	 * @param orderList
	 * @param cloudClearDetail
	 * @param profitCount
	 * @param profitAmount
	 * @param lossCount
	 * @param lossAmount
	 */
	private void packageClearDetail(List<CloudOrder> orderList, CloudClearDetail cloudClearDetail, int profitCount,
			BigDecimal profitAmount, int lossCount, BigDecimal lossAmount) {
		if (orderList.size() > 0) {
			cloudClearDetail.setClearCount(orderList.size());
			cloudClearDetail.setProfitCount(profitCount);
			cloudClearDetail.setProfitAmount(profitAmount);
			cloudClearDetail.setLossCount(lossCount);
			cloudClearDetail.setLossAmount(lossAmount);
			cloudClearDetail.setSysAmount(profitAmount.subtract(lossAmount));
			cloudClearDetail.setCleartTm(new Date());
			cloudClearDetailMapper.insert(cloudClearDetail);
		}
	}

	/**
	 * 订单管理及账户处理
	 * 
	 * @param targetMap
	 * @param order
	 * @param gainMoney
	 */
	@Transactional
	private void closeSingleOrder(Map<String, CloudTarget> targetMap, CloudOrder order, boolean gainMoney) {
		BigDecimal contractAmount = new BigDecimal(0).subtract(order.getContractMoney());
		CloudUser user = new CloudUser();
		user.setId(order.getUid());
		user.setContractAmount(contractAmount);
		if (gainMoney) {
			if(null != order.getHuman() && order.getHuman().intValue() == 1){
				//如果是赢
			}else{
				// 返回钱 （订单合约金-手续费）*2
				BigDecimal backIncome = (order.getContractMoney().subtract(order.getCommission()))
						.multiply(new BigDecimal(2));
				user.setBalance(backIncome);
				cloudUserMapper.updateBalanceAndContractById(user);
			}
		} else {
			if (null != order.getHuman() && order.getHuman().intValue() == 1) {
				// 如果是输
			} else {
				// 输 减去合约金
				cloudUserMapper.updateContractAmountById(user);
			}
		}
		cloudOrderMapper.updateByPrimaryKey(order);
	}

	/**
	 * 日终平仓任务
	 * 
	 * @param nowClearDate
	 * @param preClearDate
	 */
	private void closeDayTask(String begin, String end) {
		List<CloudOrder> orders = cloudOrderMapper.selectNeedDayClearOrder(Constant.ORDER_STAT_UNTREAT, begin, end);
		for (CloudOrder order : orders) {
			try {
				singleClearOrder(order);
			} catch (Exception e) {
				logger.error("order " + order.getId() + " closeDayTask failed! errMsg:" + e.getMessage());
			}
		}
		/**
		 * 日终平仓添加平仓条数
		 */
		CloudClearDetail closeDayTaskCount = new CloudClearDetail();
		closeDayTaskCount.setClearCount(orders.size());
		closeDayTaskCount.setCleartTm(StringUtil.parseStringToDate(end));
		closeDayTaskCount.setLossAmount(new BigDecimal(0));
		closeDayTaskCount.setLossCount(0);
		closeDayTaskCount.setProfitAmount(new BigDecimal(0));
		closeDayTaskCount.setProfitCount(0);
		closeDayTaskCount.setSysAmount(new BigDecimal(0));
		cloudClearDetailMapper.insertSelective(closeDayTaskCount);
	}

	/**
	 * 单笔订单平仓处理
	 * 
	 * @param order
	 */
	@Transactional
	private void singleClearOrder(CloudOrder order) {
		CloudUser user = new CloudUser();
		user.setId(order.getUid());
		// 1.修改账户的合约金和可用余额
		// 退还金额为订单合约金-订单手续费
		BigDecimal backAmout = order.getContractMoney().subtract(order.getCommission());
		user.setContractAmount(new BigDecimal(0).subtract(order.getContractMoney()));
		user.setBalance(backAmout);
		cloudUserMapper.updateBalanceAndContractById(user);
		// 2.修改订单状态
		order.setStatus(Constant.ORDER_STAT_DAYCLEAR);
		order.setCleared(1);
		order.setClearTime(new Date());
		cloudOrderMapper.updateStateById(order);
	}

	/**
	 * 日终盈亏统计
	 * 
	 * @param dayCentus
	 * @param preClearDate
	 * @param nowClearDate
	 * @return
	 */
	private CloudDayCensus sumDayProfitTask(CloudDayCensus dayCentus, String begin, String end) {
		/* 查询盈利 */
		// 客户输钱
		BigDecimal profit = cloudOrderMapper.queryDayProfit(Constant.ORDER_STAT_LOSS, begin, end);
		dayCentus.setProfit(profit);
		// 查询亏损 客户赢钱
		BigDecimal loss = cloudOrderMapper.queryDayLoss(Constant.ORDER_STAT_PROFIT, begin, end);
		dayCentus.setLoss(loss == null ? new BigDecimal(0) : new BigDecimal(0).subtract(loss));
		// 查询提现数
		int repayCount = cloudFundHistoryMapper.selectRePayCount(2, begin, end);
		BigDecimal commissionProfit = new BigDecimal(2 * repayCount);
		dayCentus.setCommissionProfit(commissionProfit);
		BigDecimal profitLoss = dayCentus.getProfit().add(commissionProfit).subtract(dayCentus.getLoss());
		dayCentus.setProfitLoss(profitLoss == null ? new BigDecimal(0) : profitLoss);
		// 查询手续费
		BigDecimal chnCommission = cloudOrderMapper.queryChnDayCommission(begin, end);
		dayCentus.setChnCommission(chnCommission);
		return dayCentus;
	}

	/**
	 * 日终订单数量统计
	 * 
	 * @param dayCentus
	 * @param preClearDate
	 * @param nowClearDate
	 * @return
	 */
	private CloudDayCensus sumDayOrderTask(CloudDayCensus dayCentus, String begin, String end) {
		// 查询标的物的订单总量
		List<SubjectDaySumVo> sumVos = cloudOrderMapper.queryDaySum(begin, end);
		int allSum = 0;
		if (null != sumVos && sumVos.size() > 0) {
			for (SubjectDaySumVo sumVo : sumVos) {
				if (sumVo.getSubject().equals("BTC")) {
					dayCentus.setSub1Sum(sumVo.getCount() == 0 ? 0 : sumVo.getCount());
				} else if (sumVo.getSubject().equals("LTC")) {
					dayCentus.setSub2Sum(sumVo.getCount());
				} else if (sumVo.getSubject().equals("CU")) {
					dayCentus.setSub3Sum(sumVo.getCount());
				}
				allSum += sumVo.getCount();
			}
		} else {
			dayCentus.setSub1Sum(0);
			dayCentus.setSub2Sum(0);
			dayCentus.setSub3Sum(0);
		}
		if (dayCentus.getSub1Sum() == null) {
			dayCentus.setSub1Sum(0);
		}
		if (dayCentus.getSub2Sum() == null) {
			dayCentus.setSub2Sum(0);
		}
		if (dayCentus.getSub3Sum() == null) {
			dayCentus.setSub3Sum(0);
		}
		dayCentus.setAllSum(allSum);
		return dayCentus;
	}

	/**
	 * 任务日志记录
	 * 
	 * @param begin_clear
	 * @param taskNm
	 */
	private void markLog(Date begin_clear, String taskNm) {
		Date close_end = new Date();
		long closeCom = (close_end.getTime() - begin_clear.getTime());
		CloudSystemTaskDetail taskDetail = new CloudSystemTaskDetail();
		taskDetail.setExecDay(StringUtil.formatDate(close_end, "yyyy-MM-dd"));
		taskDetail.setExecTime(StringUtil.formatDate(close_end, "HH:mm:ss"));
		taskDetail.setTaskNm(taskNm);
		int i = cloudSystemTaskDetailMapper.updateByDayAndTaskNm(taskDetail);
		if (i == 0) {
			cloudSystemTaskDetailMapper.insertSelective(taskDetail);
		}
		logger.info(taskNm + " Task end:" + StringUtil.formatDate(close_end) + " used time:" + closeCom + " ms");
	}

	public void couponStatusClearTask() {
		Date begin_clear = new Date();
		String nowDate = StringUtil.formatDate(begin_clear);
		logger.info("couponStatusClear Task begin:" + nowDate);
		String clearDate = StringUtil.formatDateCommon(begin_clear, "yyyy-MM-dd", -1);
		cloudCouponMapper.updateStatusByDateAndStatus(clearDate, Constant.COUPON_STAT_UNUSEED,
				Constant.COUPON_STAT_OVERDUE);
		markLog(begin_clear, "couponStatusClear");
	}

}
