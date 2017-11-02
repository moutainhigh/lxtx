package com.lxtx.service.order;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Strings;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.lxtx.dao.CloudChnBackCommissionMapper;
import com.lxtx.dao.CloudCouponMapper;
import com.lxtx.dao.CloudFundHistoryMapper;
import com.lxtx.dao.CloudOrderMapper;
import com.lxtx.dao.CloudSystemConfigMapper;
import com.lxtx.dao.CloudTargetMapper;
import com.lxtx.dao.CloudUserLimitMapper;
import com.lxtx.dao.CloudUserMapper;
import com.lxtx.model.CloudChnBackCommission;
import com.lxtx.model.CloudCoupon;
import com.lxtx.model.CloudFundHistory;
import com.lxtx.model.CloudOrder;
import com.lxtx.model.CloudSystemConfig;
import com.lxtx.model.CloudTarget;
import com.lxtx.model.CloudUser;
import com.lxtx.model.CloudUserLimit;
import com.lxtx.model.vo.PersonalOrderStat;
import com.lxtx.service.cache.PersonalOrderCache;
import com.lxtx.service.cache.SystemConfigCache;
import com.lxtx.util.BizException;
import com.lxtx.util.BusinessUtil;
import com.lxtx.util.Constant;
import com.lxtx.util.DayLimitConfig;
import com.lxtx.util.StringUtil;
import com.lxtx.util.SystemSwitchConfig;
import com.lxtx.util.tencent.WXPayConfig;

@Service
public class OrderService {
	@Autowired
	private CloudUserMapper cloudUserMapper;
	@Autowired
	private CloudOrderMapper cloudOrderMapper;
	@Autowired
	private CloudTargetMapper targetMapper;
	@Autowired
	private CloudFundHistoryMapper refillHistoryMapper;

	@Autowired
	private CloudUserLimitMapper cloudUserLimitMapper;

	@Autowired
	private CloudChnBackCommissionMapper chnBackCommissionMapper;

	@Autowired
	private CloudCouponMapper cloudCouponMapper;
	@Autowired
	private CloudSystemConfigMapper cloudSystemConfigMapper;
	@Autowired
	private PersonalOrderCache orderCache;
	@Autowired
	private SystemConfigCache systemConfigCache;

	private static final Logger log = LoggerFactory.getLogger(OrderService.class);
	
	//used for cache the action someone has placed an order upon a target
	//for instance, a player has placed an order on BTC, then there will be a key $uid_BTC within this cache 
	//and it will exist for only 60 seconds
	private static Cache<String, String> cache = CacheBuilder.newBuilder()
			// 设置cache的初始大小为10，要合理设置该值
			.initialCapacity(10)
			// 设置并发数为5，即同一时间最多只能有5个线程往cache执行写入操作
			.concurrencyLevel(5)
			// 设置cache中的数据在写入之后的存活时间为10秒
			.expireAfterWrite(60, TimeUnit.SECONDS)
			// 构建cache实例
			.build();
	
	public static void setupCache(Cache<String, String> outerCache) {
		cache = outerCache;
	}
	
	@Transactional
	public Map<String, Object> createOrder(HttpServletRequest request, CloudOrder order) throws Exception {
		Map<String, Object> map = new HashMap<>();
		HttpSession session = request.getSession();
		CloudUser sessionUser = (CloudUser) session.getAttribute(Constant.SESSION_USER);
		
		//check whether there is ordering operation upon current target within 60 seconds
		String key = sessionUser.getId().intValue() + "_" + order.getSubject();
		if (cache.getIfPresent(key) != null) {
			map.put("ErrCode", -105);
			map.put("Message", "请控制下单频率");
			map.put("status", "1");
			map.put("errorMsg", "请控制下单频率");
			return map;
		} else {
			cache.put(key, "hitfm");
		}
 		
		if (order.getLimit() <= 0 || order.getLimit() > 200 || order.getMoney().doubleValue() < 0) {
			map.put("ErrCode", -103);
			map.put("Message", "invalid parameters");
			map.put("status", "1");
			map.put("errorMsg", "invalid parameters");
			return map;
		}
		
		//判断数据库开闭市控制
		CloudSystemConfig openCloseCtr = cloudSystemConfigMapper.selectByProperty("open.close.trade");
		if(null != openCloseCtr && openCloseCtr.getValue().equals("0")){
			map.put("ErrCode", -104);
			map.put("Message", "商品休市");
			map.put("status", "1");
			map.put("errorMsg", "商品休市");
			return map;
		}
		// 判断时间 04:00:00 至 09:00:00
		if (BusinessUtil.validTimeLimit()) {
			map.put("ErrCode", -104);
			map.put("Message", "交易时间:9:00~4:00");
			map.put("status", "1");
			map.put("errorMsg", "交易时间:9:00~4:00");
			return map;
		}
		
		if (SystemSwitchConfig.getInstance().get("subject_count").equals("2")) {
			// 填写手机号 开关控制
			CloudSystemConfig sconfig = cloudSystemConfigMapper.selectByProperty("need.input.mbl.on");
			if (null != sconfig && sconfig.getValue().equals("1")) {
				CloudUser cUser = cloudUserMapper.selectByPrimaryKey(sessionUser.getId());
				if (StringUtils.isEmpty(cUser.getMobile())) {
					int orderCount = cloudOrderMapper.selectCountByUid(sessionUser.getId());
					if (orderCount > 3) {
						map.put("status", "9");
						map.put("ErrCode", -104);
						map.put("Message", "为了您的账户安全，请先设置手机号");
						map.put("errorMsg", "为了您的账户安全，请先设置手机号");
						return map;
					}
				}
			}
		}

		// 检查单品订单是否超过限制
		int maxHoldCount = Integer.parseInt(DayLimitConfig.getInstance().get("max_hold_count"));
		int holdedCount = cloudOrderMapper.selectCountByUidAndSubAndStatus(sessionUser.getId(), order.getSubject(),
				Constant.ORDER_STAT_UNTREAT);
		if (holdedCount >= maxHoldCount) {
			map.put("ErrCode", -104);
			map.put("Message", "建仓失败：单品持仓数量超限，不能超过" + maxHoldCount + "笔");
			map.put("status", "1");
			map.put("errorMsg", "建仓失败：单品持仓数量超限，不能超过" + maxHoldCount + "笔");
			return map;
		}

		// 生成订单并入库
		order.setUid(sessionUser.getId());
		// 订单总额
		order.setContractMoney(order.getMoney().multiply(new BigDecimal(order.getAmount())));
		
		// 获取当前用户 账户信息
		CloudUser currUser = cloudUserMapper.selectByPrimaryKey(sessionUser.getId());
		
		String now = StringUtil.formatDate(new Date(), "yyyy-MM-dd");

		// 是否用折扣券标志
		boolean isUsedRateCoupon = false;
		// 是否用现金券标志
		boolean isUsedCoupon = false;
		// 判断是否能用折扣券
		CloudCoupon coupon = new CloudCoupon();
		BigDecimal allBalance = currUser.getBalance();
		if (order.getContractMoney().intValue() >= 100) {
			coupon = cloudCouponMapper.queryRateCouponByUidAndDate(currUser.getId(), now);
			if (coupon != null) {
				isUsedRateCoupon = true;
				allBalance = allBalance.add(new BigDecimal(10 - coupon.getRate()).divide(new BigDecimal(10))
						.multiply(order.getContractMoney()));
			}
		}
		if (!isUsedRateCoupon) {
			// 判断是否有可以使用的现金券
			coupon = cloudCouponMapper.queryCloudCouponByUidAndDate(currUser.getId(), now);
			if (coupon != null) {
				isUsedCoupon = true;
				allBalance = allBalance.add(new BigDecimal(coupon.getCouponAmount()));
			}
		}
		
		// 判断资金是否充足
		if (allBalance.intValue() < order.getContractMoney().intValue()) {
			map.put("ErrCode", -104);
			map.put("Message", "资金不足(需保证金" + order.getContractMoney() + "元)");
			map.put("status", "1");
			map.put("errorMsg", "资金不足(需保证金" + order.getContractMoney() + "元)");
			return map;
		}
		CloudTarget target = targetMapper.selectByName(order.getSubject());
		if (null == target) {
			map.put("ErrCode", -104);
			map.put("Message", "建仓失败");
			map.put("status", "1");
			map.put("errorMsg", "建仓失败");
			log.error("not found the cloudTarget where subject is " + order.getSubject());
			return map;
		}
		
		try {
			order.setChnno(currUser.getChnno());
			order.setOrderTime(new Date());
			float rate = BusinessUtil.getCommissionRate(target, order.getLimit());
			BigDecimal commission = order.getContractMoney().multiply(new BigDecimal(rate));
			// 手续费
			order.setCommission(commission);
			if (isUsedCoupon) {
				// 用现金券部分手续费
				order.setCouponCommission(new BigDecimal(coupon.getCouponAmount()).multiply(new BigDecimal(rate)));
				// coupon_money
				order.setCoupouMoney(new BigDecimal(coupon.getCouponAmount()));
			} else if (isUsedRateCoupon) {
				// 用折扣券部分手续费 (10-折扣)/10*money*rate
				order.setCouponCommission(new BigDecimal(10 - coupon.getRate()).divide(new BigDecimal(10))
						.multiply(order.getContractMoney()).multiply(new BigDecimal(rate)));
				// coupon_money
				order.setCoupouMoney(order.getContractMoney()
						.multiply(new BigDecimal(10 - coupon.getRate()).divide(new BigDecimal(10))));
			} else {
				order.setCouponCommission(BigDecimal.ZERO);
				// coupon_money
				order.setCoupouMoney(BigDecimal.ZERO);
			}
			// 用户本金
			order.setCash(order.getContractMoney().subtract(order.getCoupouMoney()));
			// 设置 sys_profit：本金*(1-rate) 系统赢
			order.setSysProfit(order.getCash().multiply(new BigDecimal(1).subtract(new BigDecimal(rate))));
			// sys_loss:(本金+2券金)*(1-rate) 系统输
			order.setSysLoss(
					BigDecimal.ZERO.subtract((order.getCash().add(order.getCoupouMoney().multiply(new BigDecimal(2))))
							.multiply(new BigDecimal(1).subtract(new BigDecimal(rate)))));

			order.setStatus(0);
			// 获取当前服务器的最新index;
			CloudTarget lastTarget = targetMapper.selectByName(order.getSubject());
			order.setOrderIndex(lastTarget.getCurrentIndex());
			// 起止平仓点
			order.setClearUpperLimit(order.getOrderIndex() + order.getLimit());
			order.setClearDownLimit(order.getOrderIndex() - order.getLimit());
			cloudOrderMapper.insertSelective(order);
			if (isUsedCoupon || isUsedRateCoupon) {
				cloudCouponMapper.updateStatusById(coupon.getId(), Constant.COUPON_STAT_USEED, order.getId());
			}
			// 修改账户
			CloudUser user = new CloudUser();
			user.setId(currUser.getId());
			if (isUsedCoupon) {
				user.setBalance(new BigDecimal(0).subtract(order.getContractMoney())
						.add(new BigDecimal(coupon.getCouponAmount())));
			} else if (isUsedRateCoupon) {
				user.setBalance(new BigDecimal(0).subtract(new BigDecimal(coupon.getRate()).divide(new BigDecimal(10))
						.multiply(order.getContractMoney())));
			} else {
				user.setBalance(new BigDecimal(0).subtract(order.getContractMoney()));
			}
			user.setContractAmount(order.getContractMoney());
			
			if (shouldHideCurrentOrder(order)) {
				order.setHidden(1);
			}
			
			int affectedRow = cloudUserMapper.updateBalanceAndContractById(user);
			if (affectedRow == 0) {
				log.info("affected row equals 0");
				throw new BizException("invalid order");
			}
			
			// 修改用户日限额表
			CloudUserLimit upUserLimit = new CloudUserLimit();
			upUserLimit.setUid(sessionUser.getId());
			upUserLimit.setDayTranAmount(order.getContractMoney());
			cloudUserLimitMapper.updateTranAmountById(upUserLimit);

			// 返回全部当前产品的order
			List<CloudOrder> orders = cloudOrderMapper.selectAllOrderByUIdAndStatus(sessionUser.getId(),
					order.getSubject(), 0);
			map.put("orderList", orders);
			map.put("ErrCode", 0);
			String msg = "建仓成功";
			if (isUsedCoupon) {
				msg += "：自动为您使用金额为" + coupon.getCouponAmount() + "元的优惠券";
			}
			if (isUsedRateCoupon) {
				msg += "：自动为您使用折扣为" + coupon.getRate() + "折的优惠券";
			}
			map.put("Message", msg);
			map.put("status", "0");
			map.put("errorMsg", msg);
			map.put("seconds", "60");
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getMessage());
			throw new BizException(e);
		}

		return map;
	}

	/**
	 * evaluate if we needs to hide current order
	 * @param order
	 * @return
	 */
	private boolean shouldHideCurrentOrder(CloudOrder order) {
		//check the switch 
		CloudSystemConfig switchConfig = this.systemConfigCache.get(SystemConfigCache.ORDER_HIDE_ON);
		if (Strings.isNullOrEmpty(switchConfig.getValue()) || switchConfig.getValue().trim().equals("0")) {
			return false;
		}
		
		CloudSystemConfig chnlistConfig = this.systemConfigCache.get(SystemConfigCache.ORDER_TAG_CHN_LIST);
		//if we don't provide the channel list, all channels support channel hiding
		//if the channel list doesn't contain current channel no, we don't hide orders
		if (!chnlistConfig.getValue().contains(order.getChnno()) || Strings.isNullOrEmpty(chnlistConfig.getValue())) {
			return false;
		}
		
		int uid = order.getUid();
		String subject = order.getSubject();
		
		PersonalOrderStat stat = this.orderCache.getCache(uid, subject);
		if (stat != null) {
			String day = (new SimpleDateFormat("yyyy-MM-dd")).format(new Date());
			if (!stat.getDay().equals(day)) {
				stat = this.queryPersonalOrderStat(uid, subject);
				//this cache is for previous day
				this.orderCache.updateCache(uid, subject, stat);
			}
			int hideTimes = stat.getHide_times();
			CloudSystemConfig stepConfig = this.systemConfigCache.get(SystemConfigCache.ORDER_TAG_STEP);
			CloudSystemConfig amountConfig = this.systemConfigCache.get(SystemConfigCache.ORDER_TAG_INITIAL_AMOUNT);
			CloudSystemConfig orderCntConfig = this.systemConfigCache.get(SystemConfigCache.ORDER_TAG_INITIAL_ORDERS);
			
			int step = Strings.isNullOrEmpty(stepConfig.getValue()) ? 5 : Integer.valueOf(stepConfig.getValue()).intValue();
			
			if (hideTimes > 0) {
				stat.setTotalAmount(stat.getTotal_amount() + order.getContractMoney().intValue());
				stat.setTotalOrders(stat.getTotal_orders() + 1);
				
				if (stat.getTotal_orders() % step == 0) {
					//increase the hidden times
					log.info("set hide times :" + stat.getHide_times());
					stat.setHideTimes(stat.getHide_times() + 1);
					log.info("update cache 3:" + stat.toString());
					this.orderCache.updateCache(uid, subject, stat);
					return true;
				} else {
					log.info("update cache 4:" + stat.toString());
					this.orderCache.updateCache(uid, subject, stat);
				}
			} else {
				int totalAmount = Strings.isNullOrEmpty(amountConfig.getValue()) ? 300 : Integer.valueOf(amountConfig.getValue()).intValue();
				int totalOrders = Strings.isNullOrEmpty(orderCntConfig.getValue()) ? 5 : Integer.valueOf(orderCntConfig.getValue()).intValue(); 

				stat.setTotalAmount(stat.getTotal_amount() + order.getContractMoney().intValue());
				stat.setTotalOrders(stat.getTotal_orders() + 1);
				
				if (stat.getTotal_amount() > totalAmount || stat.getTotal_orders() > totalOrders) {
					log.info("total amount:" + stat.getTotal_amount() + " total order count:" + stat.getTotal_orders() + " step is: " + step);
					if (stat.getTotal_orders() % step == 0) {
						//increase the hidden times
						stat.setHideTimes(stat.getHide_times() + 1);
						log.info("update cache 1:" + stat.toString());
						this.orderCache.updateCache(uid, subject, stat);
						return true;
					} 
				} else {
					log.info("update cache 2:" + stat.toString());
					this.orderCache.updateCache(uid, subject, stat);
				}
			}
		} else {
			log.info("get order stat for " + uid + " " + subject + " is null!");
		}
		
		return false;
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

	public List<CloudChnBackCommission> getCommissionByDate(int date) {
		return chnBackCommissionMapper.getCommissionByDate(date);
	}

	public int selectCountByUid(Integer uid) {
		return cloudOrderMapper.selectCountByUid(uid);
	}
	
	public PersonalOrderStat queryPersonalOrderStat(int uid, String subject) {
		//we only care about the orders created today
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date now = new Date();
		String startDate = sdf.format(now);
		String endDate = sdf.format(new Date(now.getTime() + 86400* 1000));
		PersonalOrderStat stat = this.cloudOrderMapper.getOrderStat(uid, subject, startDate, endDate);
		stat.setDay(startDate);
		return stat;
	}
}
