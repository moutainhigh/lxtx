package com.lxtx.service.order;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lxtx.dao.CloudFundHistoryMapper;
import com.lxtx.dao.CloudUserMapper;
import com.lxtx.dao.LotteryCqsscDataMapper;
import com.lxtx.dao.LotteryOrderMapper;
import com.lxtx.model.CloudFundHistory;
import com.lxtx.model.CloudOrder;
import com.lxtx.model.CloudUser;
import com.lxtx.model.LotteryCqsscData;
import com.lxtx.model.LotteryOrder;
import com.lxtx.util.Constant;

@Service
public class LotteryOrderService {
	private static boolean _test_ = false;
	private static final int PRE_TIME_MAX_TOTAL_MONEY = 500 * 10 * 100;//每一轮最大下注金额
	private static final int PRE_TIME_MONEY = 10 * 100;

	static final int snMin = 1;
	static final int snMax = 119;

	private static Map<Integer, int[]> serialNumberValidMinuteOfDay;
	
	//10s正在获取时时彩数据，切换下一期
	//出结果之前，正在开奖
	//公告
	
	@Autowired
	private LotteryOrderMapper lotteryOrderMapper;
	
	@Autowired
	private LotteryCqsscDataMapper lotteryCqsscDataMapper;
	
	@Autowired
	private CloudUserMapper userMapper;
	
	@Autowired
	private CloudFundHistoryMapper refillHistoryMapper;

	private static final Logger log = LoggerFactory.getLogger(OrderService.class);

	static{
		if(serialNumberValidMinuteOfDay == null){
			serialNumberValidMinuteOfDay = new HashMap<>();
			for(int i = snMin; i <= snMax; i++){
				serialNumberValidMinuteOfDay.put(i, new int[]{getBeginMinuteOfDay(i), getEndMinuteOfDay(i)});
			}
		}
	}
	
	@Transactional
	public Map<String, Object> createOrder(HttpServletRequest request, LotteryOrder order) throws Exception {
		Map<String, Object> map = new HashMap<>();
		if(_test_){
			Map<String, Object> placeOrderState = this.getPlaceOrderState(false);
			order.setCode(1);
			order.setDirectDate((Integer)placeOrderState.get("date"));
			order.setDirectSn((Integer)placeOrderState.get("sn"));
			order.setMoney(1000);
		}
		HttpSession session = request.getSession();
		CloudUser ssuser = (CloudUser) session.getAttribute(Constant.SESSION_USER);
		CloudUser sessionUser = userMapper.selectByPrimaryKey(ssuser.getId());
		
		if (sessionUser.getCarryAmount().intValue() < order.getMoney() * 100) {//
			map.put("data", 101);
			return map;// 充值
		}
		
		Map<String, Object> placeOrderState = this.getPlaceOrderState(false);
		if(!placeOrderState.get("date").equals(order.getDirectDate()) || !placeOrderState.get("sn").equals(order.getDirectSn())){
			/*map.put("ErrCode", -104);
			map.put("Message", "您选择的期号已经不允许下单，请重新下单");
			map.put("status", "1");
			map.put("errorMsg", "您选择的期号已经不允许下单，请重新下单");*/
			map.put("data", 102);
			return map;//目标期号和当前系统不是同一单
		}
		
		Integer sumOrderMoney = lotteryOrderMapper.selectSumOrderMoney(order.getDirectDate(), order.getDirectSn());
		if ((null == sumOrderMoney ? 0 : sumOrderMoney) + order.getMoney() > PRE_TIME_MAX_TOTAL_MONEY) {//查询当前已经下单金额
/*			map.put("ErrCode", -104);
			map.put("Message", "超过系统最大下单金额限制");
			map.put("status", "1");
			map.put("errorMsg", "超过系统最大下单金额限制");*/
			map.put("data", 104);
			return map;
		}

		CloudUser user = new CloudUser();
//		user.setBalance(new BigDecimal(order.getMoney() / 100));
		//hecm update order.getMoney为分，应/100*10000;最终为乘以100
		user.setCarryAmount(-order.getMoney()*100l);
		user.setId(sessionUser.getId());
		userMapper.updateCarryAmountById(user);
		
		// 生成订单并入库
		order.setUserId(sessionUser.getId());
		order.setOrderTime(new Date(System.currentTimeMillis()));
		
		lotteryOrderMapper.insert(order);
		//map.put("status", "0");
		map.put("data", 200);
		return map;
	}
	
	public List<LotteryCqsscData> getCqsscTodayDatas(){
		return this.lotteryCqsscDataMapper.selectByDate(getSelfDefineDate(System.currentTimeMillis()));
	}
	
	public LotteryCqsscData getLatestLotteryData() {
		return this.lotteryCqsscDataMapper.getLatestLotteryData(getSelfDefineDate(System.currentTimeMillis()));
	}
	
	public Map<String, Object> getTodayOrders(HttpServletRequest request){
		Map<String, Object> map = new HashMap<>();
		HttpSession session = request.getSession();
		CloudUser sessionUser = (CloudUser) session.getAttribute(Constant.SESSION_USER);
		List<LotteryOrder> orders = this.lotteryOrderMapper.selectByUserIdAndDate(sessionUser.getId(), this.getSelfDefineDate(System.currentTimeMillis()));
		map.put("orders", orders);
		int count = 0;
		int winMoney = 0;
		for(LotteryOrder order : orders){
			count += order.getMoney() / PRE_TIME_MONEY;
			winMoney += null == order.getSettlementResult() ? 0 : order.getSettlementResult();
		}
		map.put("order_count", count);
		map.put("win_money", winMoney);
		return map;
	}
	
	public List<LotteryOrder> getCurrentWinOrders(){
		int date = 0;
		int preSn = 0;
		{
			long millis = System.currentTimeMillis();
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(millis);
			int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
			int minute = calendar.get(Calendar.MINUTE);
			int minuteOfDay = hourOfDay * 60 + minute;
			if(minuteOfDay > serialNumberValidMinuteOfDay.get(snMax)[1]){//大于最后一期的结束时间
				date = getSelfDefineDate(millis + 1000 * 60 *60 *24);
				preSn = snMax;
			}else{
				for(int i = snMin; i <= snMax; i++){
					int[] validMinute = serialNumberValidMinuteOfDay.get(i);
					if(minuteOfDay >= validMinute[0] && minuteOfDay < validMinute[1]){
						if(i == 1){
							date = getSelfDefineDate(millis - 1000 * 60 *60 *24);
							preSn = snMax;
						}else{
							date = getSelfDefineDate(millis);
							preSn = i - 1;
						}
					}
				}
			}
		}
		if(date !=0 && preSn != 0){
			return this.lotteryOrderMapper.selectWinOrdersByDateAndSn(date, preSn);
		}
		return null;
	}

	public Map<String, Object> getPlaceOrderState(boolean hasPreCode){//获得下单状态
		return getPlaceOrderState(hasPreCode, System.currentTimeMillis());
	}
	
	public Map<String, Object> getPlaceOrderState(boolean hasPreCode, long millis){
		Map<String, Object> map = new HashMap<>();
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millis);
		int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		int minuteOfDay = hourOfDay * 60 + minute;
		if(minuteOfDay > serialNumberValidMinuteOfDay.get(snMax)[1]){//大于最后一期的结束时间
			int date = getSelfDefineDate(millis + 1000 * 60 *60 *24);
			map.put("date", date);
			map.put("sn", 1);
			int remainMillis = (24 * 60 + serialNumberValidMinuteOfDay.get(1)[1] - minuteOfDay) * 60 * 1000 - (calendar.get(Calendar.SECOND) * 1000 + calendar.get(Calendar.MILLISECOND));
			map.put("remain_millis", remainMillis);
			if(hasPreCode){
				map.put("pre_sn", snMax);
				map.put("pre_code", queryCode(date, snMax));
			}
			return map;
		}
		for(int i = snMin; i <= snMax; i++){
			int[] validMinute = serialNumberValidMinuteOfDay.get(i);
			if(minuteOfDay >= validMinute[0] && minuteOfDay < validMinute[1]){
				int date = getSelfDefineDate(millis);
				map.put("date", date);
				map.put("sn", i);
				int remainMillis = (validMinute[1] - minuteOfDay) * 60 * 1000 - (calendar.get(Calendar.SECOND) * 1000 + calendar.get(Calendar.MILLISECOND));
				map.put("remain_millis", remainMillis);
				if(hasPreCode){
					if(i == 1){
						int preDate = getSelfDefineDate(millis - 1000 * 60 *60 *24);
						map.put("pre_sn", snMax);
						map.put("pre_code", queryCode(preDate, snMax));
					}else{
						map.put("pre_sn", i - 1);
						map.put("pre_code", queryCode(date, i - 1));
					}
				}
				return map;
			}
		}
		return map;
	}
	
	private int getSelfDefineDate(long millis){
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millis);
		
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH) + 1;
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int date = year * 10000 + month * 100 + day;
		return date;
	}
	
	private String queryCode(int date, int sn){
		LotteryCqsscData data = lotteryCqsscDataMapper.selectByDateAndSn(date, sn);
		if(data != null){
			return data.getOpenCode();
		}
		return null;
	}
	
	private static int getContinueMinute(int serialNumber){//下单持续时间，也就是开奖间隔时间
		if(serialNumber == 1){
			return 10;
		}else if(serialNumber == 24){
			return 4 * 60;
		} else if(serialNumber <= 23 || serialNumber >= 97){
			return 5;
		}else{
			return 10;
		}
	}
	
	private static int getSettlementMinuteOfDay(int serialNumber){//结算时刻对应当天的分钟数
		if(serialNumber <= 23){
			return 6 + 5 * (serialNumber - 1);//0点6分结算第一期，之后每隔5分钟结算一期
		}else if(serialNumber > 23 && serialNumber < 97){
			return 10 * 60 + 1 + 10 * (serialNumber - 24);//10点1分结算第24期， 每10分钟结算一次
		}else{
			return 22 * 60 + 6 + 5 * (serialNumber - 97);
		}
	}
	
	private static int getBeginMinuteOfDay(int serialNumber){//下单开始时间
		return getEndMinuteOfDay(serialNumber) - getContinueMinute(serialNumber);
	}
	
	private static int getEndMinuteOfDay(int serialNumber){//下单结束时间
		return getSettlementMinuteOfDay(serialNumber) - 3;//下单截止时间相对结算时间的提前量;
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
	
	public static void main(String[] args) {
		for (Integer i: serialNumberValidMinuteOfDay.keySet()) {
			int[] ints = serialNumberValidMinuteOfDay.get(i);
			System.out.println(ints[0] + "  " + ints[1]);
		}
	}

	public List<LotteryOrder> getOrdersByUid(Integer id, Integer id2) {
		return lotteryOrderMapper.selectOrderByUid(id, id2);
	}
	
}
