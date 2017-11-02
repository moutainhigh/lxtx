package com.lxtx.service.order;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lxtx.dao.CloudFundHistoryMapper;
import com.lxtx.dao.CloudOrderMapper;
import com.lxtx.dao.CloudUserMapper;
import com.lxtx.model.CloudFundHistory;
import com.lxtx.model.CloudOrder;
import com.lxtx.model.CloudUser;
import com.lxtx.util.BizException;
import com.lxtx.util.BusinessUtil;
import com.lxtx.util.Constant;
import com.lxtx.util.DayLimitConfig;
import com.lxtx.util.StringUtil;
import com.lxtx.util.SystemSwitchConfig;

@Service
public class OrderService {
	@Autowired
	private CloudUserMapper cloudUserMapper;
	@Autowired
	private CloudOrderMapper cloudOrderMapper;
	@Autowired
	private CloudFundHistoryMapper refillHistoryMapper;

	private static final Logger log = LoggerFactory.getLogger(OrderService.class);

	@Transactional
	public Map<String, Object> createOrder(HttpServletRequest request, CloudOrder order) throws Exception {
		Map<String, Object> map = new HashMap<>();
		HttpSession session = request.getSession();
		CloudUser sessionUser = (CloudUser) session.getAttribute(Constant.SESSION_USER);
		

		// 判断时间 04:00:00 至 09:00:00
		if (BusinessUtil.validTimeLimit()) {
			map.put("ErrCode", -104);
			map.put("Message", "交易时间：周一~周五9:00~4:00");
			map.put("status", "1");
			map.put("errorMsg", "交易时间：周一~周五9:00~4:00");
			return map;
		}

		// 生成订单并入库
		order.setUid(sessionUser.getId());
		// 订单总额
		order.setContractMoney(order.getMoney().multiply(new BigDecimal(order.getAmount())));
		// 获取当前用户 账户信息
		CloudUser currUser = cloudUserMapper.selectByPrimaryKey(sessionUser.getId());
		// 判断是否超限
		// BigDecimal dayTranAmount = new
		// BigDecimal(DayLimitConfig.getInstance().get("day_tran_amount"));
		// BigDecimal nowAmount =
		// cloudUserLimitMapper.selectByPrimaryKey(sessionUser.getId()).getDayTranAmount()
		// .add(order.getContractMoney());
		// int compResult = nowAmount.compareTo(dayTranAmount);
		// if (compResult == 1) {
		// map.put("ErrCode", -104);
		// map.put("Message", "建仓失败：日交易累计超限，不能超过" + dayTranAmount + "元");
		// map.put("status", "1");
		// map.put("errorMsg", "建仓失败：日交易累计超限，不能超过" + dayTranAmount + "元");
		// return map;
		// }
		String now = StringUtil.formatDate(new Date(), "yyyy-MM-dd");

		// 是否用折扣券标志
		boolean isUsedRateCoupon = false;
		// 是否用现金券标志
		boolean isUsedCoupon = false;
		// 判断是否能用折扣券
		BigDecimal allBalance = currUser.getBalance();
		// 判断资金是否充足
		if (allBalance.intValue() < order.getContractMoney().intValue()) {
			map.put("ErrCode", -104);
			map.put("Message", "资金不足(需保证金" + order.getContractMoney() + "元)");
			map.put("status", "1");
			map.put("errorMsg", "资金不足(需保证金" + order.getContractMoney() + "元)");
			return map;
		}

		return map;
	}

	public List<CloudOrder> listOrderByUidAndStatus(int uid, String subject, int status) {
		List<CloudOrder> orders = cloudOrderMapper.selectAllOrderByUIdAndStatus(uid, subject, status);
		return orders;
	}

	public List<CloudOrder> getOrdersByUid(int uid, Integer id) {
		List<CloudOrder> orders = cloudOrderMapper.selectAllOrderByUId(uid, id);
		for (CloudOrder cloudOrder : orders) {
			switch (cloudOrder.getStatus()) {
			case 0:
				cloudOrder.setDealkind("等待成交");
				break;
			case 1:
				cloudOrder.setDealkind("处理中");
				cloudOrder.setPerStatus("处");
				cloudOrder.setClearTypeNm("处理中");
				cloudOrder.setPerStyle("earn-zorn");
				cloudOrder.setProfit(new BigDecimal(0));
				break;
			case 2:
				cloudOrder.setDealkind("已平仓");
				cloudOrder.setPerStatus("赚");
				cloudOrder.setClearTypeNm("止盈");
				cloudOrder.setPerStyle("earn-up");
				cloudOrder.setProfit(cloudOrder.getContractMoney().subtract(cloudOrder.getCommission()));
				break;
			case 3:
				cloudOrder.setDealkind("已平仓");
				cloudOrder.setPerStatus("亏");
				cloudOrder.setClearTypeNm("止损");
				cloudOrder.setPerStyle("earn-down");
				cloudOrder.setProfit(cloudOrder.getCommission().subtract(cloudOrder.getContractMoney()));
				break;
			case 4:
				cloudOrder.setDealkind("已平仓");
				cloudOrder.setPerStatus("退");
				cloudOrder.setClearTypeNm("日终平仓");
				cloudOrder.setPerStyle("earn-zorn");
				cloudOrder.setCommission(new BigDecimal(0));
				cloudOrder.setProfit(cloudOrder.getContractMoney());
				break;
			default:
				cloudOrder.setDealkind("其它");
				cloudOrder.setPerStatus("它");
				cloudOrder.setClearTypeNm("其它");
				cloudOrder.setPerStyle("earn-zorn");
				cloudOrder.setCommission(new BigDecimal(0));
				cloudOrder.setProfit(new BigDecimal(0));
				break;
			}
			switch (cloudOrder.getDirection()) {
			case 1:
				cloudOrder.setOrderType("看涨");
				break;
			case 2:
				cloudOrder.setOrderType("看跌");
				break;
			default:
				break;
			}
		}
		return orders;
	}

	public List<CloudFundHistory> getFundByUid(Integer uid, Integer id) {
		List<CloudFundHistory> refills = refillHistoryMapper.selectAllFundByUid(uid, id);
		return refills;
	}

	public CloudFundHistory getFundByOrderId(String orderId) {
		return refillHistoryMapper.selectByOrderId(orderId);
	}

	public int saveRefillRecord(CloudFundHistory history) {
		return refillHistoryMapper.insert(history);
	}

	public int updateRefillRecord(String orderId) {
		return refillHistoryMapper.updateByOrderId(orderId);
	}

	public int getNewOrderAmount(Integer id, String subject) {
		return cloudOrderMapper.countNewOrder(id, subject);
	}

	public void markOrderInProcess(String code, int v) {
		cloudOrderMapper.markOrderInProcess(code, v);
	}
}
