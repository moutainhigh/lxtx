package com.lxtech.biz;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lxtech.dao.CloudOrderDao;
import com.lxtech.dao.CloudUserDayTranSumDao;
import com.lxtech.model.CloudUserDayTranSum;
import com.lxtech.util.TimeUtil;

public class TranDayAnalyse {
	private final static Logger logger = LoggerFactory.getLogger(TranDayAnalyse.class);

	public void analyseDayTran() {
		Date now = new Date();

		String date = TimeUtil.formatDateCommon(now, "yyyy-MM-dd", -1);

		try {
			CloudUserDayTranSum sum = new CloudUserDayTranSum();
			// 交易总人数
			int tranUCount = CloudOrderDao.getTranUCountByDate(date);
			int tranUOldCount = CloudOrderDao.getTranUOldCountByDate(date);
			// 交易总金额、交易总笔数、总手续费
			sum = CloudOrderDao.getTranAmountByDate(date);
			sum.setTran_u_count(tranUCount);
			sum.setTran_u_old_count(tranUOldCount);
			// 老用户量、老用户交易金额、老用户手续费
			CloudUserDayTranSum sum_o = CloudOrderDao.getOldUserTranAmountByDate(date);
			sum.setTran_u_old_order_count(sum_o.getTran_u_old_order_count());
			sum.setTran_u_old_commission(sum_o.getTran_u_old_commission());
			sum.setTran_u_old_amount(sum_o.getTran_u_old_amount());
			// 平均
			if (sum.getTran_u_count() > 0) {
				sum.setTran_u_mean_count(new BigDecimal(sum.getTran_order_count().intValue())
						.divide(new BigDecimal(sum.getTran_u_count()), 2, BigDecimal.ROUND_HALF_UP));
				sum.setTran_mean_amount(sum.getTran_amount().divide(new BigDecimal(sum.getTran_u_count()), 2,
						BigDecimal.ROUND_HALF_UP));
				sum.setSingle_tran_amount(sum.getTran_amount().divide(new BigDecimal(sum.getTran_order_count()), 2,
						BigDecimal.ROUND_HALF_UP));
			} else {
				sum.setTran_u_mean_count(BigDecimal.ZERO);
				sum.setTran_mean_amount(BigDecimal.ZERO);
				sum.setSingle_tran_amount(BigDecimal.ZERO);
			}
			// 占比
			if (sum.getTran_amount().intValue() > 0) {
				sum.setTran_u_old_amount_prop(
						sum.getTran_u_old_amount().divide(sum.getTran_amount(), 2, BigDecimal.ROUND_HALF_UP));
			} else {
				sum.setTran_u_old_amount_prop(BigDecimal.ZERO);
			}
			if (sum.getTran_u_count() > 0) {
				sum.setTran_u_old_count_prop(new BigDecimal(sum.getTran_u_old_count())
						.divide(new BigDecimal(sum.getTran_u_count()), 2, BigDecimal.ROUND_HALF_UP));
			} else {
				sum.setTran_u_old_count_prop(BigDecimal.ZERO);
			}
			if (sum.getTran_commission().intValue() > 0) {
				sum.setTran_u_old_commission_prop(
						sum.getTran_u_old_commission().divide(sum.getTran_commission(), 2, BigDecimal.ROUND_HALF_UP));
			} else {
				sum.setTran_u_old_commission_prop(BigDecimal.ZERO);
			}

			sum.setDate(date);
			int count = CloudUserDayTranSumDao.updateData(sum);
			if (count == 0) {
				CloudUserDayTranSumDao.saveData(sum);
			}

		} catch (SQLException e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		TranDayAnalyse s = new TranDayAnalyse();
		s.analyseDayTran();
	}

}
