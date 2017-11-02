package com.lxtech.dao;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxtech.model.CloudHumanControlConfig;
import com.lxtech.model.CloudOrder;
import com.lxtech.model.CloudTarget;
import com.lxtech.model.CloudUser;
import com.lxtech.util.IOUtil;
import com.lxtech.util.JdbcUtils;
import com.lxtech.util.wx.WeixinUtil;

public class CloudHumanAdjuster {
	private static final Logger logger = LoggerFactory.getLogger(CloudHumanAdjuster.class);

	private static List<Integer> availableMoneyOptions;
	private static List<Integer> availableNumOptions;
	private static List<String> availableSubjectOptions;

	static {
		availableMoneyOptions = new ArrayList<Integer>();
		availableMoneyOptions.add(10);
		availableMoneyOptions.add(100);
		availableMoneyOptions.add(500);
		availableNumOptions = new ArrayList<Integer>();
		availableNumOptions.add(1);
		availableNumOptions.add(2);
		availableNumOptions.add(3);
		availableNumOptions.add(4);
		availableNumOptions.add(5);
		availableNumOptions.add(6);
		availableSubjectOptions = new ArrayList<String>();
		availableSubjectOptions.add("BU");
		availableSubjectOptions.add("CU");
		availableSubjectOptions.add("AG");
	}

	// 处理充值逻辑
	public static void refill(String uidList) throws SQLException {
		List<Long> uids = IOUtil.praseLongList(uidList);
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "insert into cloud_fund_history(uid, amount, time, wx_trade_no, status, notify_status, `type`) values(?,?,?,?,?,?,?)";
		String updateSql = "update cloud_user set balance = balance + ? where id = ?";

		int money = 1000;
		// int money = 2000 + (int)(Math.random()*3)*1000;
		for (Long uid : uids) {
			String tradeNo = "wx" + System.currentTimeMillis() + (int) (Math.random() * 9);
			Object[] params = { uid.longValue(), money, new Date(), tradeNo, 1, 9, 1 };
			qr.update(sql, params);
			// update balance
			Object[] updateParams = { money * 0.996, uid.longValue() };
			qr.update(updateSql, updateParams);
		}
	}

	// 提现
	public static void repay(Map<Integer, Integer> amountMap) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "insert into cloud_fund_history(uid, amount, time, wx_trade_no, status, notify_status ,`type`) values(?,?,?,?,?,?,?)";

		for (Integer uid : amountMap.keySet()) {
			String tradeNo = WeixinUtil.orderNum();
			int money = amountMap.get(uid);
			Object[] params = { uid.longValue(), money, new Date(), tradeNo, 1, 9, 2 };
			qr.update(sql, params);
		}
	}

	public static List<CloudHumanControlConfig> queryHumanControlConfig() throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		String sql = "select * from cloud_human_control_config where status = 0";
		Object params[] = {};
		return qr.query(sql, new BeanListHandler<CloudHumanControlConfig>(CloudHumanControlConfig.class), params);
	}

	public static List<CloudUser> queryAvaliableUser(String begin, String end, String chnno) throws SQLException {
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		// 订单数大于3的用户
		String sql = "select count(1) as count,uid as id from cloud_order where  clear_time between ? and ? and chnno=? group by uid HAVING count(1)>8 order by count desc";
		Object params[] = { begin, end, chnno };
		return qr.query(sql, new BeanListHandler<CloudUser>(CloudUser.class), params);
	}

	public static void updateHumanControlConfig(CloudHumanControlConfig config) throws SQLException {
		String sql = "update cloud_human_control_config set status=?, runed_count = runed_count + 1 where id = ?";
		Object[] params = { config.getStatus(), config.getId() };
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		qr.update(sql, params);
	}

	public static void placeOrder(int uid, int money, int num, String chnno, float feeRate, boolean isHuman,
			List<String> subjects) {
		Random r = new Random();
		if (!availableMoneyOptions.contains(money)) {
			logger.info("invalid money:" + money);
			return;
		}
		if (!availableNumOptions.contains(num)) {
			logger.info("invalid num:" + num);
			return;
		}
		String subject = availableSubjectOptions.get(r.nextInt(availableSubjectOptions.size()));
		if (subjects != null && subjects.size() > 0) {
			subject = subjects.get(r.nextInt(subjects.size()));
		}
		// now place the order
		try {
			List<CloudTarget> targetList = CloudTargetDao.queryTarget();
			for (CloudTarget target : targetList) {
				if (target.getName().equals(subject)) {
					if (isHuman) {
						CloudOrder order1 = generateOrder(true, uid, money, num, subject, target.getCurrent_index(),
								chnno, feeRate, true);
						CloudOrder order2 = generateOrder(false, uid, money, num, subject, target.getCurrent_index(),
								chnno, feeRate, true);
						placeOrder(order1);
						placeOrder(order2);
						// now update the user
						// BigDecimal variance = new BigDecimal(amount *2);
						// changeBalance(uid, variance);
					}

					break;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void changeBalance(int uid, BigDecimal variance) throws SQLException {
		String sql = "update cloud_user set balance = balance - ? , contract_amount = contract_amount + ? where id = ?";
		Object[] params = { variance, variance, uid };
		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		qr.update(sql, params);
	}

	private static void placeOrder(CloudOrder order) throws SQLException {
		String sql = "insert into cloud_order(uid, chnno, `subject`, direction, money, amount, contract_money, `limit`, cleared, order_time, order_index, commission, `status`, clear_upper_limit, clear_down_limit, coupon_commission, cash, sys_profit, "
				+ "sys_loss, coupou_money, human) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		QueryRunner qr = new QueryRunner(JdbcUtils.getDataSource());
		Object[] params = { order.getUid(), order.getChnno(), order.getSubject(), order.getDirection(),
				order.getMoney(), order.getAmount(), order.getContractMoney(), order.getLimit(), order.getCleared(),
				order.getOrderTime(), order.getOrderIndex(), order.getCommission(), order.getStatus(),
				order.getClearUpperLimit(), order.getClearDownLimit(), order.getCoupon_commission(), order.getCash(),
				order.getSys_profit(), order.getSys_loss(), order.getCoupou_money(), order.getHuman() };
		qr.update(sql, params);
	}

	private static int getLimitBySubject(String subject) {
		if (subject.equals("BU")) {
			return 5;
		} else if (subject.equals("AG")) {
			return 4;
		} else {
			return 30;
		}
	}

	private static BigDecimal calculateBigDecimal(double dval) {
		return new BigDecimal(dval).setScale(2, BigDecimal.ROUND_HALF_UP);
	}

	private static CloudOrder generateOrder(boolean pricePromote, int uid, int money, int num, String subject,
			int orderIndex, String chnno, double feeRate, boolean isHuman) {
		CloudOrder order = new CloudOrder();
		order.setUid(uid);
		order.setChnno(chnno);
		order.setAmount(num);
		order.setMoney(new BigDecimal(money));
		BigDecimal contractMoney = new BigDecimal(money * num);
		order.setContractMoney(contractMoney);
		order.setSubject(subject);
		int limit = getLimitBySubject(subject);
		order.setLimit(limit);
		order.setOrderIndex(orderIndex);
		order.setClearUpperLimit(orderIndex + limit);
		order.setClearDownLimit(orderIndex - limit);
		order.setCleared(0);
		order.setStatus(0);
		order.setCommission(calculateBigDecimal((contractMoney.doubleValue() * feeRate)));
		order.setHuman(isHuman ? 1 : 0);
		order.setOrderTime(new Date());
		order.setDirection(pricePromote ? 1 : 2);
		order.setCash(contractMoney);
		order.setSys_profit(calculateBigDecimal(contractMoney.doubleValue() * (1.0 - feeRate)));
		order.setSys_loss(calculateBigDecimal(contractMoney.doubleValue() * (feeRate - 1.0)));
		order.setCoupou_money(BigDecimal.ZERO);
		order.setCoupon_commission(BigDecimal.ZERO);
		return order;
	}

	private static void repayInBatch() throws SQLException, InterruptedException {
		Map<Integer, Integer> amountMap = new HashMap<Integer, Integer>();
		// amountMap.put(50, 3000);
		// amountMap.put(66, 3000);
		// amountMap.put(1217, 3000);
		// amountMap.put(1228, 3000);
		repay(amountMap);
	}

	public static void repayInBatch(int uid, int money) throws SQLException, InterruptedException {
		Map<Integer, Integer> amountMap = new HashMap<Integer, Integer>();
		amountMap.put(uid, money);
		repay(amountMap);
	}

	public static void main(String[] args) throws SQLException, InterruptedException {
		// 生成人工订单
		// 如果任务可以执行，则获取渠道号，并随机获取当天订单量大于3的该渠道用户id；查询用户的余额，根据账户余额确定订单笔数2、3、4；随机获取产品
		// placeOrder(10740, 100, 3, "1001", 0.15f, "CU", true);
		// CloudHumanAdjuster.refill("50,66,1217,1228");
		// 提取当前用户的盈利金额
		// repayInBatch(10740,(new BigDecimal(100*3*0.7)).intValue());
	}

	public static List<CloudUser> queryAvaliableUser() {
		// TODO Auto-generated method stub
		return null;
	}

}
