package com.lxtx.task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.lxtx.service.ChnFillPayService;
import com.lxtx.service.ChnPerHalfHourProfitService;
import com.lxtx.service.CommissionAndUCountService;
import com.lxtx.service.DayClearService;
import com.lxtx.util.tencent.WXPayConfig;

@Component
public class CloudTask {

	/**
	 * 日终清算任务
	 */
	@Autowired
	private DayClearService dayClearService;

	@Autowired
	private CommissionAndUCountService commissionAndUCountService;
	@Autowired
	private ChnPerHalfHourProfitService chnPerHalfHourProfitService;

	@Autowired
	private ChnFillPayService chnFillPayService;

	@Scheduled(cron = "0 0 4 * * ?")
	public void dayClearTask() {
		String taskSwitch = WXPayConfig.getInstance().get("task_switch");
		if ("1".equals(taskSwitch)) {
			dayClearService.dayClearTask();
		}
	}

	/**
	 * 轮巡结算任务
	 */
	@Scheduled(cron = "0/2 * * * * ?")
	public void orderSquareTask() {
		String taskSwitch = WXPayConfig.getInstance().get("task_switch");
		if ("1".equals(taskSwitch)) {
			dayClearService.closeOrderTask();
		}
	}

	/**
	 * 轮巡统计渠道用户及手续费任务
	 */
	@Scheduled(cron = "0 0/5 * * * ?")
	public void commissonAndUCountTask() {
		String taskSwitch = WXPayConfig.getInstance().get("task_switch");
		if ("1".equals(taskSwitch)) {
			commissionAndUCountService.commissionAndUCountTask();
		}
	}

	/**
	 * 轮巡统计渠道盈亏
	 */
	@Scheduled(cron = "0 0/30 * * * ?")
	public void chnPerHalfHourProfit() {
		String taskSwitch = WXPayConfig.getInstance().get("task_switch");
		if ("1".equals(taskSwitch)) {
			chnPerHalfHourProfitService.chnPerHalfHourProfitTask();
		}
	}

	/**
	 * 日统计渠道盈亏
	 */
	@Scheduled(cron = "0 10 0 * * ?")
	public void chnDayProfit() {
		String taskSwitch = WXPayConfig.getInstance().get("task_switch");
		if ("1".equals(taskSwitch)) {
			chnPerHalfHourProfitService.chnDayProfitTask();
		}
	}

	/**
	 * 优惠券定时过期清理
	 */
	@Scheduled(cron = "0 5 0 * * ?")
	public void couponStatusClearTask() {
		String taskSwitch = WXPayConfig.getInstance().get("task_switch");
		if ("1".equals(taskSwitch)) {
			dayClearService.couponStatusClearTask();
		}
	}

	/**
	 * 半小时统计 渠道充值提现、手续费、新增用户分时统计
	 */
	@Scheduled(cron = "0 0/30 * * * ?")
	public void chnFillPayTask() {
		String taskSwitch = WXPayConfig.getInstance().get("task_switch");
		if ("1".equals(taskSwitch)) {
			chnFillPayService.chnFillPayTask();
		}
	}

	/**
	 * 3分钟 日统计 渠道充值提现、手续费、新增用户日统计
	 */
	@Scheduled(cron = "0 0/3 * * * ?")
	public void chnDayFill() {
		String taskSwitch = WXPayConfig.getInstance().get("task_switch");
		if ("1".equals(taskSwitch)) {
			chnFillPayService.chnDayFillPayTask();
		}
	}

	/**
	 * 0点前执行 渠道充值提现、手续费、新增用户日统计
	 */
	@Scheduled(cron = "59 59 23 * * ?")
	public void chnDayFill1() {
		String taskSwitch = WXPayConfig.getInstance().get("task_switch");
		if ("1".equals(taskSwitch)) {
			chnFillPayService.chnDayFillPayTask();
		}
	}

	/**
	 * 用户日交易、盈亏统计
	 */
	@Scheduled(cron = "0 0/3 * * * ?")
	public void chnUserDayProfitTask() {
		String taskSwitch = WXPayConfig.getInstance().get("task_switch");
		if ("1".equals(taskSwitch)) {
			chnFillPayService.chnUserDayProfitTask();
		}
	}

	/**
	 * 0点前执行 用户日交易、盈亏统计
	 */
	@Scheduled(cron = "59 59 23 * * ?")
	public void chnUserDayProfitTask1() {
		String taskSwitch = WXPayConfig.getInstance().get("task_switch");
		if ("1".equals(taskSwitch)) {
			chnFillPayService.chnUserDayProfitTask();
		}
	}

}
